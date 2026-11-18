package br.com.agendanails.service;

import br.com.agendanails.dto.*;
import br.com.agendanails.exception.ConflitoHorarioException;
import br.com.agendanails.exception.RecursoNaoEncontradoException;
import br.com.agendanails.exception.RegraNegocioException;
import br.com.agendanails.model.*;
import br.com.agendanails.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AgendamentoService {

    private static final int PASSO_MINUTOS = 30;

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final HorarioTrabalhoRepository horarioRepository;
    private final ClienteService clienteService;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              ServicoRepository servicoRepository,
                              ProfissionalRepository profissionalRepository,
                              HorarioTrabalhoRepository horarioRepository,
                              ClienteService clienteService) {
        this.agendamentoRepository = agendamentoRepository;
        this.servicoRepository = servicoRepository;
        this.profissionalRepository = profissionalRepository;
        this.horarioRepository = horarioRepository;
        this.clienteService = clienteService;
    }


    @Transactional
    public AgendamentoResponse agendar(AgendamentoRequest req) {
        Profissional profissional = profissionalRepository.buscarComTrava(req.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrada."));
        Servico servico = buscarServico(req.servicoId());
        validarProfissionalEServico(profissional, servico);

        LocalDateTime inicio = LocalDateTime.of(req.data(), req.horario());
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        validarNaoPassado(inicio);
        validarExpediente(profissional.getId(), inicio, fim);
        validarConflito(profissional.getId(), inicio, fim, null);

        Cliente cliente = clienteService.buscarOuCriar(req.cliente());

        Agendamento a = new Agendamento();
        a.setCliente(cliente);
        a.setServico(servico);
        a.setProfissional(profissional);
        a.setInicio(inicio);
        a.setFim(fim);
        a.setStatus(StatusAgendamento.PENDENTE);
        a.setObservacoes(req.observacoes());

        return AgendamentoMapper.paraResponse(agendamentoRepository.save(a));
    }


    @Transactional(readOnly = true)
    public List<LocalTime> horariosDisponiveis(Long servicoId, Long profissionalId, LocalDate data) {
        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrada."));
        Servico servico = buscarServico(servicoId);
        validarProfissionalEServico(profissional, servico);

        int duracao = servico.getDuracaoMinutos();
        LocalDateTime inicioDoDia = data.atStartOfDay();
        List<Agendamento> ocupados = agendamentoRepository
                .buscarConflitos(profissionalId, inicioDoDia, inicioDoDia.plusDays(1));
        List<HorarioTrabalho> turnos =
                horarioRepository.findByProfissionalIdAndDiaSemana(profissionalId, data.getDayOfWeek());

        LocalDateTime agora = LocalDateTime.now();
        List<LocalTime> livres = new ArrayList<>();

        for (HorarioTrabalho turno : turnos) {
            LocalTime t = turno.getHoraInicio();
            while (true) {
                LocalTime fimDoSlot = t.plusMinutes(duracao);
                if (fimDoSlot.isBefore(t) || fimDoSlot.isAfter(turno.getHoraFim())) {
                    break;
                }

                LocalDateTime ini = LocalDateTime.of(data, t);
                LocalDateTime fim = ini.plusMinutes(duracao);
                if (ini.isAfter(agora) && !temConflito(ocupados, ini, fim) && !livres.contains(t)) {
                    livres.add(t);
                }

                LocalTime proximo = t.plusMinutes(PASSO_MINUTOS);
                if (proximo.isBefore(t)) {
                    break;
                }
                t = proximo;
            }
        }

        Collections.sort(livres);
        return livres;
    }


    @Transactional
    public AgendamentoResponse alterarStatus(Long id, StatusRequest req) {
        Agendamento a = buscarEntidade(id);
        StatusAgendamento novo = req.status();

        if (!a.getStatus().podeIrPara(novo)) {
            throw new RegraNegocioException(
                    "Não é possível mudar de " + a.getStatus() + " para " + novo + ".");
        }
        a.setStatus(novo);
        return AgendamentoMapper.paraResponse(agendamentoRepository.save(a));
    }

    @Transactional
    public AgendamentoResponse reagendar(Long id, ReagendarRequest req) {
        Agendamento a = buscarEntidade(id);

        if (a.getStatus() != StatusAgendamento.PENDENTE && a.getStatus() != StatusAgendamento.CONFIRMADO) {
            throw new RegraNegocioException("Só é possível reagendar agendamentos pendentes ou confirmados.");
        }

        Long profissionalId = a.getProfissional().getId();
        profissionalRepository.buscarComTrava(profissionalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrada."));

        LocalDateTime inicio = LocalDateTime.of(req.data(), req.horario());
        LocalDateTime fim = inicio.plusMinutes(a.getServico().getDuracaoMinutos());

        validarNaoPassado(inicio);
        validarExpediente(profissionalId, inicio, fim);
        validarConflito(profissionalId, inicio, fim, a.getId());
        a.setInicio(inicio);
        a.setFim(fim);
        return AgendamentoMapper.paraResponse(agendamentoRepository.save(a));
    }


    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarPorDia(LocalDate data) {
        List<Agendamento> lista = agendamentoRepository
                .buscarDoPeriodo(data.atStartOfDay(), data.plusDays(1).atStartOfDay());
        return converter(lista);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> historicoDoCliente(Long clienteId) {
        clienteService.buscarEntidade(clienteId);
        return converter(agendamentoRepository.findByClienteIdOrderByInicioDesc(clienteId));
    }



    private void validarProfissionalEServico(Profissional profissional, Servico servico) {
        if (!servico.isAtivo()) {
            throw new RegraNegocioException("Este serviço não está disponível.");
        }
        if (!profissional.isAtivo()) {
            throw new RegraNegocioException("Esta profissional não está disponível.");
        }
        boolean realiza = false;
        for (Servico s : profissional.getServicos()) {
            if (s.getId().equals(servico.getId())) {
                realiza = true;
            }
        }
        if (!realiza) {
            throw new RegraNegocioException("Esta profissional não realiza o serviço escolhido.");
        }
    }

    private void validarNaoPassado(LocalDateTime inicio) {
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Este horário já passou.");
        }
    }


    private void validarExpediente(Long profissionalId, LocalDateTime inicio, LocalDateTime fim) {
        if (!fim.toLocalDate().equals(inicio.toLocalDate())) {
            throw new RegraNegocioException("O atendimento precisa terminar no mesmo dia.");
        }
        List<HorarioTrabalho> turnos =
                horarioRepository.findByProfissionalIdAndDiaSemana(profissionalId, inicio.getDayOfWeek());
        for (HorarioTrabalho turno : turnos) {
            boolean comecaDentro = !inicio.toLocalTime().isBefore(turno.getHoraInicio());
            boolean terminaDentro = !fim.toLocalTime().isAfter(turno.getHoraFim());
            if (comecaDentro && terminaDentro) {
                return;
            }
        }
        throw new RegraNegocioException("Horário fora do expediente da profissional.");
    }

    private void validarConflito(Long profissionalId, LocalDateTime inicio, LocalDateTime fim, Long ignorarId) {
        List<Agendamento> conflitos = agendamentoRepository.buscarConflitos(profissionalId, inicio, fim);
        for (Agendamento existente : conflitos) {
            if (ignorarId == null || !existente.getId().equals(ignorarId)) {
                throw new ConflitoHorarioException("Horário indisponível para esta profissional.");
            }
        }
    }

    private boolean temConflito(List<Agendamento> ocupados, LocalDateTime inicio, LocalDateTime fim) {
        for (Agendamento a : ocupados) {
            if (a.getInicio().isBefore(fim) && a.getFim().isAfter(inicio)) {
                return true;
            }
        }
        return false;
    }

    private Servico buscarServico(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado."));
    }

    private Agendamento buscarEntidade(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));
    }

    private List<AgendamentoResponse> converter(List<Agendamento> lista) {
        List<AgendamentoResponse> resposta = new ArrayList<>();
        for (Agendamento a : lista) {
            resposta.add(AgendamentoMapper.paraResponse(a));
        }
        return resposta;
    }
}
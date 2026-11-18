package br.com.agendanails.service;

import br.com.agendanails.dto.*;
import br.com.agendanails.exception.RecursoNaoEncontradoException;
import br.com.agendanails.exception.RegraNegocioException;
import br.com.agendanails.model.HorarioTrabalho;
import br.com.agendanails.model.Profissional;
import br.com.agendanails.model.Servico;
import br.com.agendanails.repository.HorarioTrabalhoRepository;
import br.com.agendanails.repository.ProfissionalRepository;
import br.com.agendanails.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProfissionalService {

    private final ProfissionalRepository repository;
    private final ServicoRepository servicoRepository;
    private final HorarioTrabalhoRepository horarioRepository;

    public ProfissionalService(ProfissionalRepository repository,
                               ServicoRepository servicoRepository,
                               HorarioTrabalhoRepository horarioRepository) {
        this.repository = repository;
        this.servicoRepository = servicoRepository;
        this.horarioRepository = horarioRepository;
    }


    @Transactional(readOnly = true)
    public List<ProfissionalResponse> listarAtivas(Long servicoId) {
        List<Profissional> lista;
        if (servicoId == null) {
            lista = repository.findByAtivoTrue();
        } else {
            lista = repository.findByAtivoTrueAndServicosId(servicoId);
        }
        return converter(lista);
    }

    @Transactional(readOnly = true)
    public List<ProfissionalResponse> listarTodas() {
        return converter(repository.findAll());
    }

    @Transactional(readOnly = true)
    public ProfissionalResponse buscar(Long id) {
        return ProfissionalMapper.paraResponse(buscarEntidade(id));
    }

    @Transactional
    public ProfissionalResponse criar(ProfissionalRequest req) {
        Profissional p = new Profissional();
        ProfissionalMapper.copiar(req, p);
        p.setServicos(buscarServicos(req.servicoIds()));
        return ProfissionalMapper.paraResponse(repository.save(p));
    }

    @Transactional
    public ProfissionalResponse atualizar(Long id, ProfissionalRequest req) {
        Profissional p = buscarEntidade(id);
        ProfissionalMapper.copiar(req, p);
        p.setServicos(buscarServicos(req.servicoIds()));
        return ProfissionalMapper.paraResponse(repository.save(p));
    }

    @Transactional
    public void excluir(Long id) {
        Profissional p = buscarEntidade(id);
        horarioRepository.deleteAll(horarioRepository.findByProfissionalId(id));
        repository.delete(p);
        repository.flush();
    }

    @Transactional(readOnly = true)
    public List<HorarioTrabalhoResponse> listarHorarios(Long profissionalId) {
        buscarEntidade(profissionalId);
        List<HorarioTrabalhoResponse> resposta = new ArrayList<>();
        for (HorarioTrabalho h : horarioRepository.findByProfissionalId(profissionalId)) {
            resposta.add(HorarioTrabalhoMapper.paraResponse(h));
        }
        return resposta;
    }

    @Transactional
    public HorarioTrabalhoResponse adicionarHorario(Long profissionalId, HorarioTrabalhoRequest req) {
        Profissional p = buscarEntidade(profissionalId);

        if (!req.horaFim().isAfter(req.horaInicio())) {
            throw new RegraNegocioException("A hora de fim deve ser depois da hora de início.");
        }

        List<HorarioTrabalho> doDia =
                horarioRepository.findByProfissionalIdAndDiaSemana(profissionalId, req.diaSemana());
        for (HorarioTrabalho existente : doDia) {
            if (req.horaInicio().isBefore(existente.getHoraFim())
                    && req.horaFim().isAfter(existente.getHoraInicio())) {
                throw new RegraNegocioException("Este turno se sobrepõe a outro já cadastrado.");
            }
        }

        HorarioTrabalho h = new HorarioTrabalho();
        h.setProfissional(p);
        h.setDiaSemana(req.diaSemana());
        h.setHoraInicio(req.horaInicio());
        h.setHoraFim(req.horaFim());
        return HorarioTrabalhoMapper.paraResponse(horarioRepository.save(h));
    }

    @Transactional
    public void removerHorario(Long horarioId) {
        HorarioTrabalho h = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Horário não encontrado."));
        horarioRepository.delete(h);
    }

    private Profissional buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrada."));
    }

    private Set<Servico> buscarServicos(List<Long> ids) {
        Set<Servico> servicos = new HashSet<>();
        for (Long servicoId : ids) {
            Servico s = servicoRepository.findById(servicoId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Serviço " + servicoId + " não encontrado."));
            servicos.add(s);
        }
        return servicos;
    }

    private List<ProfissionalResponse> converter(List<Profissional> lista) {
        List<ProfissionalResponse> resposta = new ArrayList<>();
        for (Profissional p : lista) {
            resposta.add(ProfissionalMapper.paraResponse(p));
        }
        return resposta;
    }
}
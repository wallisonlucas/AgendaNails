package br.com.agendanails.service;

import br.com.agendanails.dto.ServicoMapper;
import br.com.agendanails.dto.ServicoRequest;
import br.com.agendanails.dto.ServicoResponse;
import br.com.agendanails.exception.RecursoNaoEncontradoException;
import br.com.agendanails.model.Servico;
import br.com.agendanails.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listarAtivos() {
        return converter(repository.findByAtivoTrue());
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listarTodos() {
        return converter(repository.findAll());
    }

    @Transactional(readOnly = true)
    public ServicoResponse buscar(Long id) {
        return ServicoMapper.paraResponse(buscarEntidade(id));
    }

    @Transactional
    public ServicoResponse criar(ServicoRequest req) {
        Servico servico = new Servico();
        ServicoMapper.copiar(req, servico);
        return ServicoMapper.paraResponse(repository.save(servico));
    }

    @Transactional
    public ServicoResponse atualizar(Long id, ServicoRequest req) {
        Servico servico = buscarEntidade(id);
        ServicoMapper.copiar(req, servico);
        return ServicoMapper.paraResponse(repository.save(servico));
    }

    @Transactional
    public void excluir(Long id) {
        Servico servico = buscarEntidade(id);
        repository.delete(servico);
        repository.flush();
    }

    private Servico buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado."));
    }

    private List<ServicoResponse> converter(List<Servico> servicos) {
        List<ServicoResponse> resposta = new ArrayList<>();
        for (Servico s : servicos) {
            resposta.add(ServicoMapper.paraResponse(s));
        }
        return resposta;
    }
}
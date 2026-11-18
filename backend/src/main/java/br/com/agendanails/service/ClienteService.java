package br.com.agendanails.service;

import br.com.agendanails.dto.ClienteMapper;
import br.com.agendanails.dto.ClienteRequest;
import br.com.agendanails.dto.ClienteResponse;
import br.com.agendanails.exception.RecursoNaoEncontradoException;
import br.com.agendanails.exception.RegraNegocioException;
import br.com.agendanails.model.Cliente;
import br.com.agendanails.repository.AgendamentoRepository;
import br.com.agendanails.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final AgendamentoRepository agendamentoRepository;

    public ClienteService(ClienteRepository repository, AgendamentoRepository agendamentoRepository) {
        this.repository = repository;
        this.agendamentoRepository = agendamentoRepository;
    }


    @Transactional
    public Cliente buscarOuCriar(ClienteRequest req) {
        String telefone = normalizarTelefone(req.telefone());
        Optional<Cliente> existente = repository.findByTelefone(telefone);
        if (existente.isPresent()) {
            return existente.get();
        }
        return salvarNovo(req, telefone);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        List<ClienteResponse> resposta = new ArrayList<>();
        for (Cliente c : repository.findAll()) {
            resposta.add(ClienteMapper.paraResponse(c));
        }
        return resposta;
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
        return ClienteMapper.paraResponse(buscarEntidade(id));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest req) {
        String telefone = normalizarTelefone(req.telefone());
        if (repository.findByTelefone(telefone).isPresent()) {
            throw new RegraNegocioException("Já existe um cliente com este telefone.");
        }
        return ClienteMapper.paraResponse(salvarNovo(req, telefone));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente cliente = buscarEntidade(id);
        String telefone = normalizarTelefone(req.telefone());

        Optional<Cliente> outro = repository.findByTelefone(telefone);
        if (outro.isPresent() && !outro.get().getId().equals(id)) {
            throw new RegraNegocioException("Já existe outro cliente com este telefone.");
        }

        ClienteMapper.copiar(req, cliente);
        cliente.setTelefone(telefone);
        return ClienteMapper.paraResponse(repository.save(cliente));
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscarEntidade(id);
        if (agendamentoRepository.existsByClienteId(id)) {
            throw new RegraNegocioException("Não é possível excluir: o cliente possui agendamentos.");
        }
        repository.delete(cliente);
    }

    public Cliente buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }

    private Cliente salvarNovo(ClienteRequest req, String telefone) {
        Cliente cliente = new Cliente();
        ClienteMapper.copiar(req, cliente);
        cliente.setTelefone(telefone);
        return repository.save(cliente);
    }

    private String normalizarTelefone(String telefone) {
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 13) {
            throw new RegraNegocioException("Telefone inválido.");
        }
        return digitos;
    }
}
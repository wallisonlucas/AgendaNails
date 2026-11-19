package br.com.agendanails.controller;

import br.com.agendanails.dto.AgendamentoResponse;
import br.com.agendanails.dto.ClienteRequest;
import br.com.agendanails.dto.ClienteResponse;
import br.com.agendanails.service.AgendamentoService;
import br.com.agendanails.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clientes")
public class AdminClienteController {

    private final ClienteService service;
    private final AgendamentoService agendamentoService;

    public AdminClienteController(ClienteService service, AgendamentoService agendamentoService) {
        this.service = service;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/historico")
    public List<AgendamentoResponse> historico(@PathVariable Long id) {
        return agendamentoService.historicoDoCliente(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criar(@Valid @RequestBody ClienteRequest req) {
        return service.criar(req);
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
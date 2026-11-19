package br.com.agendanails.controller;

import br.com.agendanails.dto.*;
import br.com.agendanails.service.ProfissionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/profissionais")
public class AdminProfissionalController {

    private final ProfissionalService service;

    public AdminProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProfissionalResponse> listar() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ProfissionalResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfissionalResponse criar(@Valid @RequestBody ProfissionalRequest req) {
        return service.criar(req);
    }

    @PutMapping("/{id}")
    public ProfissionalResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProfissionalRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @GetMapping("/{id}/horarios")
    public List<HorarioTrabalhoResponse> listarHorarios(@PathVariable Long id) {
        return service.listarHorarios(id);
    }

    @PostMapping("/{id}/horarios")
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioTrabalhoResponse adicionarHorario(@PathVariable Long id,
                                                    @Valid @RequestBody HorarioTrabalhoRequest req) {
        return service.adicionarHorario(id, req);
    }

    @DeleteMapping("/horarios/{horarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerHorario(@PathVariable Long horarioId) {
        service.removerHorario(horarioId);
    }
}
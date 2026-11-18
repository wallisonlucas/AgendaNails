package br.com.agendanails.controller;

import br.com.agendanails.dto.*;
import br.com.agendanails.service.AgendamentoService;
import br.com.agendanails.service.ProfissionalService;
import br.com.agendanails.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final ServicoService servicoService;
    private final ProfissionalService profissionalService;
    private final AgendamentoService agendamentoService;

    public PublicController(ServicoService servicoService, ProfissionalService profissionalService, AgendamentoService agendamentoService) {
        this.servicoService = servicoService;
        this.profissionalService = profissionalService;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping("/servicos")
    public List<ServicoResponse> servicos() {
        return servicoService.listarAtivos();
    }

    @GetMapping("/profissionais")
    public List<ProfissionalResponse> profissionais(@RequestParam(required = false) Long servicoId) {
        return profissionalService.listarAtivas(servicoId);
    }

    @GetMapping("/disponibilidade")
    public List<LocalTime> disponibilidade(
            @RequestParam Long servicoId,
            @RequestParam Long profissionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return agendamentoService.horariosDisponiveis(servicoId, profissionalId, data);
    }

    @PostMapping("/agendamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse agendar(@Valid @RequestBody AgendamentoRequest req) {
        return agendamentoService.agendar(req);
    }
}
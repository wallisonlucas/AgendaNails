package br.com.agendanails.controller;

import br.com.agendanails.dto.AgendamentoResponse;
import br.com.agendanails.dto.ReagendarRequest;
import br.com.agendanails.dto.StatusRequest;
import br.com.agendanails.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/agendamentos")
public class AdminAgendamentoController {

    private final AgendamentoService service;

    public AdminAgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AgendamentoResponse> listarPorDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        LocalDate dia = (data != null) ? data : LocalDate.now();
        return service.listarPorDia(dia);
    }

    @PatchMapping("/{id}/status")
    public AgendamentoResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return service.alterarStatus(id, req);
    }

    @PatchMapping("/{id}/reagendar")
    public AgendamentoResponse reagendar(@PathVariable Long id, @Valid @RequestBody ReagendarRequest req) {
        return service.reagendar(id, req);
    }
}
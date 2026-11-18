package br.com.agendanails.dto;

import br.com.agendanails.model.StatusAgendamento;
import java.time.LocalDateTime;

public record AgendamentoResponse(
        Long id, String cliente, String servico, String profissional,
        LocalDateTime inicio, LocalDateTime fim, StatusAgendamento status) {}

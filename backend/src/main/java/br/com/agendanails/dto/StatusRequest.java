package br.com.agendanails.dto;

import br.com.agendanails.model.StatusAgendamento;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @NotNull(message = "Informe o novo status") StatusAgendamento status) {}

package br.com.agendanails.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequest(
        @NotNull(message = "Escolha o serviço")
        Long servicoId,

        @NotNull(message = "Escolha a profissional")
        Long profissionalId,

        @NotNull(message = "Escolha a data")
        @FutureOrPresent(message = "A data não pode estar no passado")
        LocalDate data,

        @NotNull(message = "Escolha o horário")
        LocalTime horario,

        @Valid
        @NotNull(message = "Informe os dados do cliente")
        ClienteRequest cliente,

        @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres")
        String observacoes) {}
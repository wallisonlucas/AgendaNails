package br.com.agendanails.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReagendarRequest(
        @NotNull(message = "Escolha a data")
        @FutureOrPresent(message = "A data não pode estar no passado")
        LocalDate data,

        @NotNull(message = "Escolha o horário")
        LocalTime horario) {}
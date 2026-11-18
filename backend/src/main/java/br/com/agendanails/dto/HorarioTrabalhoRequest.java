package br.com.agendanails.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioTrabalhoRequest(
        @NotNull(message = "Informe o dia da semana") DayOfWeek diaSemana,
        @NotNull(message = "Informe a hora de início") LocalTime horaInicio,
        @NotNull(message = "Informe a hora de fim") LocalTime horaFim) {}

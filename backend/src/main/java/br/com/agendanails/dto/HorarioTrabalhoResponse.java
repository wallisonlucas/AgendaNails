package br.com.agendanails.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioTrabalhoResponse(
        Long id, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {}

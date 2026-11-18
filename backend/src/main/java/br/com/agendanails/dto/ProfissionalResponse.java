package br.com.agendanails.dto;

import java.util.List;

public record ProfissionalResponse(
        Long id, String nome, String especialidade,
        String fotoUrl, boolean ativo, List<Long> servicoIds) {}

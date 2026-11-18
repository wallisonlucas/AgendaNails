package br.com.agendanails.dto;

import java.math.BigDecimal;

public record ServicoResponse(
        Long id, String nome, String descricao,
        BigDecimal preco, Integer duracaoMinutos, boolean ativo) {}

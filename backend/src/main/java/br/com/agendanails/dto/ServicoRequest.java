package br.com.agendanails.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ServicoRequest(
    @NotBlank(message = "Informe o nome do serviço")
    @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
    String nome,

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    String descricao,

    @NotNull(message = "Informe o preço")
    @Positive(message = "O preço deve ser maior que zero")
    BigDecimal preco,

    @NotNull(message = "Informe a duração em minutos")
    @Positive(message = "A duração deve ser maior que zero")
    Integer duracaoMinutos,

    Boolean ativo) {}
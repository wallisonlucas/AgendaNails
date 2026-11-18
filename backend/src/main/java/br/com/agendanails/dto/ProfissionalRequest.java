package br.com.agendanails.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record ProfissionalRequest(
        @NotBlank(message = "informe o nome da profissional")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @Size(max = 80, message = "A especialidade deve ter no máximo 80 caracteres")
        String especialidade,

        @Size(max = 255, message = "A URL da foto é muito longa")
        String fotoUrl,

        Boolean ativo,

        @NotNull(message = "Informe os serviços que ela realiza")
        List<Long> servicoIds) {}
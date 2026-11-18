package br.com.agendanails.dto;

import jakarta.validation.constraints.*;

public record ClienteRequest(
        @NotBlank(message = "Informe o nome")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "Informe o telefone")
        @Pattern(regexp = "^[0-9()+\\-\\s]{10,20}$", message = "Telefone inválido")
        String telefone,

        @Email(message = "E-mail inválido")
        @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres")
        String email) {}

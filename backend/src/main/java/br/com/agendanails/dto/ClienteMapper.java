package br.com.agendanails.dto;

import br.com.agendanails.model.Cliente;

public final class ClienteMapper {

    private ClienteMapper() {}

    public static ClienteResponse paraResponse(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNome(), c.getTelefone(), c.getEmail());
    }

    public static void copiar(ClienteRequest req, Cliente destino) {
        destino.setNome(req.nome());
        destino.setTelefone(req.telefone());
        destino.setEmail(req.email());
    }
}

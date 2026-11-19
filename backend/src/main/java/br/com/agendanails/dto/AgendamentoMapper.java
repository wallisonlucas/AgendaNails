package br.com.agendanails.dto;

import br.com.agendanails.model.Agendamento;

public final class AgendamentoMapper {

    private AgendamentoMapper() {}

    public static AgendamentoResponse paraResponse(Agendamento a) {
        return new AgendamentoResponse(
                a.getId(),
                a.getCliente().getNome(),
                a.getServico().getNome(),
                a.getProfissional().getNome(),
                a.getInicio(),
                a.getFim(),
                a.getStatus());
    }
}
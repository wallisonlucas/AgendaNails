package br.com.agendanails.dto;

import br.com.agendanails.model.Servico;

public final class ServicoMapper {

    private ServicoMapper() {}

    public static ServicoResponse paraResponse(Servico s) {
        return new ServicoResponse(s.getId(), s.getNome(), s.getDescricao(),
                s.getPreco(), s.getDuracaoMinutos(), s.isAtivo());
    }

    public static void copiar(ServicoRequest req, Servico destino) {
        destino.setNome(req.nome());
        destino.setDescricao(req.descricao());
        destino.setPreco(req.preco());
        destino.setDuracaoMinutos(req.duracaoMinutos());
        destino.setAtivo(req.ativo() == null || req.ativo());
    }
}
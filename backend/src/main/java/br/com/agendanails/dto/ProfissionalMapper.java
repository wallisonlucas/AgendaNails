package br.com.agendanails.dto;

import br.com.agendanails.model.Profissional;
import br.com.agendanails.model.Servico;
import java.util.ArrayList;
import java.util.List;

public final class ProfissionalMapper {

    private ProfissionalMapper() {}

    public static ProfissionalResponse paraResponse(Profissional p) {
        List<Long> servicoIds = new ArrayList<>();
        for (Servico s : p.getServicos()) {
            servicoIds.add(s.getId());
        }
        return new ProfissionalResponse(p.getId(), p.getNome(), p.getEspecialidade(),
                p.getFotoUrl(), p.isAtivo(), servicoIds);
    }

    public static void copiar(ProfissionalRequest req, Profissional destino) {
        destino.setNome(req.nome());
        destino.setEspecialidade(req.especialidade());
        destino.setFotoUrl(req.fotoUrl());
        destino.setAtivo(req.ativo() == null || req.ativo());
    }
}

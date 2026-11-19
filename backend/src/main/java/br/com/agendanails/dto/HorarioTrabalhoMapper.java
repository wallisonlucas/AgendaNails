package br.com.agendanails.dto;

import br.com.agendanails.model.HorarioTrabalho;

public final class HorarioTrabalhoMapper {

    private HorarioTrabalhoMapper() {}

    public static HorarioTrabalhoResponse paraResponse(HorarioTrabalho h) {
        return new HorarioTrabalhoResponse(h.getId(), h.getDiaSemana(),
                h.getHoraInicio(), h.getHoraFim());
    }
}

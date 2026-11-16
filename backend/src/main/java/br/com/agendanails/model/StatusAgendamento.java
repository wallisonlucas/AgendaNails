package br.com.agendanails.model;

public enum StatusAgendamento {
    PENDENTE, CONFIRMADO, CONCLUIDO, CANCELADO;

    public boolean podeIrPara(StatusAgendamento novo) {
        switch (this) {
            case PENDENTE:
                return novo == CONFIRMADO || novo == CANCELADO;
            case CONFIRMADO:
                return novo == CONCLUIDO || novo == CANCELADO;
            default:
                return false;
        }
    }
}
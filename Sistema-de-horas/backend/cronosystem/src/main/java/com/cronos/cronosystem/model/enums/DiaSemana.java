package com.cronos.cronosystem.model.enums;

public enum DiaSemana { // seg, ter, qua, qui, sex, sab

    SEG("Segunda"),
    TER("Terça"),
    QUA("Quarta"),
    QUI("Quinta"),
    SEX("Sexta"),
    SAB("Sabado");

    private final String descrição;

    DiaSemana(String descrição){this.descrição = descrição; }

    public String getDescrição() {return  descrição; }
}

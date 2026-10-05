package com.cronos.cronosystem.model.enums;

public enum Turmasep { // junto, turmaA, turmaB
    JUNTO("Junto"),
    TURMAA("TurmaA"),
    TURMAB("TurmaB");

    private final String descrição;

    Turmasep(String descrição) {this.descrição = descrição; }

    public  String getDescrição() { return descrição; }
}

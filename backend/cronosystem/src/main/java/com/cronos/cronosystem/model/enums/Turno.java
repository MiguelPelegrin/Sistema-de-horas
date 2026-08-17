package com.cronos.cronosystem.model.enums;

public enum Turno { // manha, tarde, integral, noite

    MANHA("Manha"),
    TARDE("Tarde"),
    INTEGRAL("Integral"),
    NOITE("Noite");

    private final String descrição;

    Turno(String descrição) { this.descrição = descrição; }

    public String getDescrição() { return descrição; }
    
}

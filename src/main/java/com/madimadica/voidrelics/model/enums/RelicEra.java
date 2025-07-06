package com.madimadica.voidrelics.model.enums;

public enum RelicEra {
    LITH(1),
    MESO(2),
    NEO(3),
    AXI(4),
    REQUIEM(5);

    private final int id;
    private final String label;

    RelicEra(int id) {
        this.id = id;
        String name = this.name();
        this.label = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}

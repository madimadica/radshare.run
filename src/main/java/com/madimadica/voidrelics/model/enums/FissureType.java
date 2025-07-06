package com.madimadica.voidrelics.model.enums;

import java.util.List;

public enum FissureType {
    LITH(1),
    MESO(2),
    NEO(3),
    AXI(4),
    REQUIEM(5),
    OMNIA(6);

    private final int id;
    private final String label;

    FissureType(int id) {
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

    public List<RelicEra> getAllowedEras() {
        return switch (this) {
            case LITH -> List.of(RelicEra.LITH);
            case MESO -> List.of(RelicEra.MESO);
            case NEO -> List.of(RelicEra.NEO);
            case AXI -> List.of(RelicEra.AXI);
            case REQUIEM -> List.of(RelicEra.REQUIEM);
            case OMNIA -> List.of(RelicEra.values());
        };
    }

    @Override
    public String toString() {
        return label;
    }
}

package com.madimadica.voidrelics.model.enums;

public enum RelicRefinement {
    INTACT(1),
    EXCEPTIONAL(2),
    FLAWLESS(3),
    RADIANT(4);

    private final int id;
    private final String label;

    RelicRefinement(int id) {
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

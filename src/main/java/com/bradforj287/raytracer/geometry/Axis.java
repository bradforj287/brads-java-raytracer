package com.bradforj287.raytracer.geometry;

public enum Axis {
    X("x"),
    Y("y"),
    Z("z");

    private String axis;
    Axis(String axis) {
        this.axis = axis;
    }

    @Override
    public String toString() {
        return axis;
    }

    public String toCoordinateName() {
        return toString();
    }
}

package com.bradforj287.raytracer.geometry;

public class Surface {
    private boolean isReflective;
    private int color;

    public boolean isReflective() {
        return isReflective;
    }

    public void setReflective(boolean reflective) {
        isReflective = reflective;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

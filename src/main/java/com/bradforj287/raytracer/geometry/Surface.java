package com.bradforj287.raytracer.geometry;

public class Surface {
    private boolean isReflective;
    private int color;
    private Double iof;

    public boolean isRefractive() {
        return iof != null;
    }

    public Double getIof() {
        return iof;
    }

    public void setIof(Double iof) {
        this.iof = iof;
    }

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

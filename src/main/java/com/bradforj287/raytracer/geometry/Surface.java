package com.bradforj287.raytracer.geometry;

public class Surface {
    private boolean isReflective;
    private Double iof;
    private RgbColor color;
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

    public RgbColor getColor() {
        return color;
    }

    public void setColor(RgbColor color) {
        this.color = color;
    }
}

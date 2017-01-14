package com.bradforj287.raytracer.geometry;

public class Surface {
    private boolean isReflective;
    private boolean isLightSource;
    private Double iof;
    private RgbColor color;
    public boolean isRefractive() {
        return iof != null;
    }

    public Surface() {
    }

    public boolean isLightSource() {
        return isLightSource;
    }

    public void setLightSource(boolean lightSource) {
        isLightSource = lightSource;
    }

    public Surface(RgbColor color) {
        this.color = color;
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

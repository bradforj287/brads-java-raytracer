package com.bradforj287.raytracer.geometry;

public class PointLight {
    private final Vector3d position;
    private final RgbColor color;
    private final double intensity;

    public PointLight(Vector3d position, double intensity, RgbColor color) {
        this.intensity = intensity;
        this.position = position;
        this.color = color;
    }

    public RgbColor getContribution(Vector3d hitPoint, Surface hitSurface) {
        double distance = position.subtract(hitPoint).getMagnitude();

        double r2 = distance * distance;

        RgbColor blendedColor = hitSurface.getColor().blend(this.color);
        if (r2 == 0) {
            return blendedColor.scale(Double.MAX_VALUE);
        } else {
            return blendedColor.scale(this.intensity * (1 / (4 * Math.PI * r2)));
        }
    }

    public Vector3d getPosition() {
        return position;
    }

    public double getIntensity() {
        return intensity;
    }

    public RgbColor getColor() {
        return color;
    }
}

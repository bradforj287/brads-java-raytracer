package com.bradforj287.raytracer.geometry;

public abstract class Shape3d {
    public abstract double minX();
    public abstract double minY();
    public abstract double minZ();
    public abstract double maxX();
    public abstract double maxY();
    public abstract double maxZ();
    public abstract Vector3D getCentroid();
}

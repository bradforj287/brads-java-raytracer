package com.bradforj287.raytracer.model.kdtree;

import com.bradforj287.raytracer.geometry.Ray3d;

public interface KDTree {
    void visitPossibleMatches(Ray3d ray, ShapeVisitor visitor);
}

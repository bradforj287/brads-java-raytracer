package com.bradforj287.raytracer.model;

import com.bradforj287.raytracer.geometry.AxisAlignedBoundingBox3d;
import com.bradforj287.raytracer.geometry.Ray3d;
import com.bradforj287.raytracer.model.kdtree.ShapeVisitor;

public interface SpacialStructure {
    void visitPossibleIntersections(Ray3d ray, ShapeVisitor visitor);

    AxisAlignedBoundingBox3d getBounds();
}

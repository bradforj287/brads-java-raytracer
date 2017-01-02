package com.bradforj287.raytracer.model;

import com.bradforj287.raytracer.geometry.AxisAlignedBoundingBox3d;
import com.bradforj287.raytracer.geometry.Ray3d;

public interface SpacialStructure {
    SpacialStructureQueryStats visitPossibleIntersections(Ray3d ray, ShapeVisitor visitor);

    AxisAlignedBoundingBox3d getBounds();
}

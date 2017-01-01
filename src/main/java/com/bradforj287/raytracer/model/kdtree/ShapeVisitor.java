package com.bradforj287.raytracer.model.kdtree;

import com.bradforj287.raytracer.geometry.Shape3d;

public interface ShapeVisitor {
    void visit(Shape3d shape);
}

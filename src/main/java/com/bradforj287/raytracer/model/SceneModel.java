package com.bradforj287.raytracer.model;

import java.util.List;
import com.bradforj287.raytracer.geometry.AxisAlignedBoundingBox3d;
import com.bradforj287.raytracer.geometry.Ray3d;
import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.model.kdtree.KDTree;
import com.google.common.base.Preconditions;

public class SceneModel {

    private SpacialStructure shapesTree;

    public SceneModel(final List<Shape3d> shapes) {
        Preconditions.checkNotNull(shapes);
        Preconditions.checkArgument(!shapes.isEmpty());

        shapesTree = new KDTree(shapes);
    }

    public void visitPossibleIntersections(Ray3d ray, ShapeVisitor visitor) {
        shapesTree.visitPossibleIntersections(ray, visitor);
    }

    public AxisAlignedBoundingBox3d getBounds() {
        return shapesTree.getBounds();
    }

}

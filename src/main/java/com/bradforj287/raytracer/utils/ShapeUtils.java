package com.bradforj287.raytracer.utils;

import java.util.List;
import com.bradforj287.raytracer.geometry.Bounds3D;
import com.bradforj287.raytracer.geometry.Shape3D;
import com.bradforj287.raytracer.geometry.Vector3D;
import com.google.common.base.Preconditions;

public class ShapeUtils {

    public static Bounds3D getBoundsForShapes(List<Shape3D> shapes) {
        Preconditions.checkNotNull(shapes);
        Preconditions.checkArgument(!shapes.isEmpty());

        Shape3D firstShape  = shapes.get(0);
        Bounds3D bounds = new Bounds3D();
        bounds.minX = firstShape.minX();
        bounds.minY = firstShape.minY();
        bounds.minZ = firstShape.minZ();
        bounds.maxX = firstShape.maxX();
        bounds.maxY = firstShape.maxY();
        bounds.maxZ = firstShape.maxZ();

        for (int i = 1; i < shapes.size(); i++) {
            Shape3D shape = shapes.get(i);
            if (shape.minX() < bounds.minX) {
                bounds.minX = shape.minX();
            }
            if (shape.minY() < bounds.minY) {
                bounds.minY = shape.minY();
            }
            if (shape.minZ() < bounds.minZ) {
                bounds.minZ = shape.minZ();
            }

            if (shape.maxX() > bounds.maxX) {
                bounds.maxX = shape.maxX();
            }
            if (shape.maxY() > bounds.maxY) {
                bounds.maxY = shape.maxY();
            }
            if (shape.maxZ() > bounds.maxZ) {
                bounds.maxZ = shape.maxZ();
            }
        }

        return bounds;
    }

    public static Vector3D getMidpoint(List<Shape3D> shapes) {
        double x = 0;
        double y = 0;
        double z = 0;
        double numShapes = shapes.size();
        for (Shape3D s : shapes) {
            Vector3D center = s.getCentroid();
            x += center.x;
            y += center.y;
            z += center.z;
        }
        return new Vector3D(x/numShapes, y/numShapes, z/numShapes);
    }
}

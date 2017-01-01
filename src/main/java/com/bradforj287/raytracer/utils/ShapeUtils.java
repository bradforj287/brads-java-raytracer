package com.bradforj287.raytracer.utils;

import java.util.List;
import com.bradforj287.raytracer.geometry.AxisAlignedBoundingBox3D;
import com.bradforj287.raytracer.geometry.Shape3D;
import com.bradforj287.raytracer.geometry.Vector3D;
import com.google.common.base.Preconditions;

public class ShapeUtils {

    public static AxisAlignedBoundingBox3D getBoundsForShapes(List<Shape3D> shapes) {
        Preconditions.checkNotNull(shapes);
        Preconditions.checkArgument(!shapes.isEmpty());

        Shape3D firstShape = shapes.get(0);
        double minX = firstShape.minX();
        double minY = firstShape.minY();
        double minZ = firstShape.minZ();
        double maxX = firstShape.maxX();
        double maxY = firstShape.maxY();
        double maxZ = firstShape.maxZ();

        for (int i = 1; i < shapes.size(); i++) {
            Shape3D shape = shapes.get(i);
            if (shape.minX() < minX) {
                minX = shape.minX();
            }
            if (shape.minY() < minY) {
                minY = shape.minY();
            }
            if (shape.minZ() < minZ) {
                minZ = shape.minZ();
            }

            if (shape.maxX() > maxX) {
                maxX = shape.maxX();
            }
            if (shape.maxY() > maxY) {
                maxY = shape.maxY();
            }
            if (shape.maxZ() > maxZ) {
                maxZ = shape.maxZ();
            }
        }
        Vector3D minPoint = new Vector3D(minX, minY, minZ);
        Vector3D maxPoint = new Vector3D(maxX, maxY, maxZ);

        return new AxisAlignedBoundingBox3D(minPoint, maxPoint);
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
        return new Vector3D(x / numShapes, y / numShapes, z / numShapes);
    }
}

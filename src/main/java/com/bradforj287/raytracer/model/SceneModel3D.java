package com.bradforj287.raytracer.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.bradforj287.raytracer.geometry.Matrix3D;
import com.bradforj287.raytracer.geometry.Triangle3D;
import com.bradforj287.raytracer.geometry.Vector3D;

public class SceneModel3D {
    private List<Triangle3D> shapes = new ArrayList<Triangle3D>();

    public void convertAllToUnitVector() {

        for (Triangle3D t : shapes) {
            t.convertVerticiesToUnitVectors();
        }
    }

    public void scaleScene(double scaleFactor) {
        Iterator<Vector3D> itr = getVertexIterator();

        while (itr.hasNext()) {
            itr.next().scaleBy(scaleFactor);
        }
    }

    public void convertToUnitCube(double scaleFactor) {
        double xWidth = getXWidth();
        double yWidth = getYWidth();
        double zWidth = getZWidth();

        Matrix3D scaleMatrix = new Matrix3D();

        scaleMatrix.matrix[0][0] = scaleFactor * (1 / 1);
        scaleMatrix.matrix[1][1] = scaleFactor * (1 / 1);
        scaleMatrix.matrix[2][2] = scaleFactor * (1 / 1);
        scaleMatrix.matrix[3][3] = 1;

        for (Triangle3D t : shapes) {
            //	t.multiplyByMatrix(scaleMatrix);

            t.v1.scaleBy(scaleFactor);
            t.v2.scaleBy(scaleFactor);
            t.v3.scaleBy(scaleFactor);
        }
    }

    private double getYWidth() {
        VertexIterator vItr = getVertexIterator();

        double max_y = vItr.next().y;
        double min_y = max_y;

        while (vItr.hasNext()) {
            double y = vItr.next().y;

            if (y > max_y) {
                max_y = y;
            }

            if (y < min_y) {
                min_y = y;
            }
        }

        double ywidth = max_y - min_y;
        return ywidth;
    }

    private double getZWidth() {
        VertexIterator vItr = getVertexIterator();

        double max_z = vItr.next().z;
        double min_z = max_z;

        while (vItr.hasNext()) {
            double z = vItr.next().z;

            if (z > max_z) {
                max_z = z;
            }

            if (z < min_z) {
                min_z = z;
            }
        }

        double zwidth = max_z - min_z;
        return zwidth;
    }

    private double getXWidth() {

        VertexIterator vItr = getVertexIterator();

        double max_x = vItr.next().x;
        double min_x = max_x;

        while (vItr.hasNext()) {
            double x = vItr.next().x;

            if (x > max_x) {
                max_x = x;
            }

            if (x < min_x) {
                min_x = x;
            }
        }

        double xwidth = max_x - min_x;
        return xwidth;
    }

    public List<Triangle3D> getShapes() {
        return shapes;
    }

    public void addShape(Triangle3D triangle3d) {
        shapes.add(triangle3d);
    }

    public Triangle3D getShape(int i) {
        return shapes.get(i);
    }

    public int size() {
        return shapes.size();
    }

    public void addShapes(SceneModel3D s) {
        shapes.addAll(s.getShapes());
    }

    public VertexIterator getVertexIterator() {
        return new VertexIterator(this);
    }

}

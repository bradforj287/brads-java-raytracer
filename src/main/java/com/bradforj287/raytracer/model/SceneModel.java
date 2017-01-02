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

    public SpacialStructureQueryStats visitPossibleIntersections(Ray3d ray, ShapeVisitor visitor) {
        return shapesTree.visitPossibleIntersections(ray, visitor);
    }

    public AxisAlignedBoundingBox3d getBounds() {
        return shapesTree.getBounds();
    }

   /** public void scaleScene(double scaleFactor) {
        Iterator<Vector3d> itr = getVertexIterator();

        while (itr.hasNext()) {
            itr.next().scaleBy(scaleFactor);
        }
    }

    public void convertToUnitCube(double scaleFactor) {
        double xWidth = getXWidth();
        double yWidth = getYWidth();
        double zWidth = getZWidth();

        Matrix3d scaleMatrix = new Matrix3d();

        scaleMatrix.matrix[0][0] = scaleFactor * (1 / 1);
        scaleMatrix.matrix[1][1] = scaleFactor * (1 / 1);
        scaleMatrix.matrix[2][2] = scaleFactor * (1 / 1);
        scaleMatrix.matrix[3][3] = 1;

        for (Triangle3d t : shapes) {
            //	t.multiplyByMatrix(scaleMatrix);

            t.v1.scaleBy(scaleFactor);
            t.v2.scaleBy(scaleFactor);
            t.v3.scaleBy(scaleFactor);
        }
    } **/
}

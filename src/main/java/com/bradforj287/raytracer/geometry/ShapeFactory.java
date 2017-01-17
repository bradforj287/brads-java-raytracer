package com.bradforj287.raytracer.geometry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class ShapeFactory {
    private ShapeFactory() {

    }

    public static List<Triangle3d> buildAxisAlignedTriangleBox(AxisAlignedBoundingBox3d bb, Surface surface) {
        List<Triangle3d> r = new ArrayList<>();

        Vector3d min = bb.getMin();
        Vector3d max = bb.getMax();

        //back
        r.add(new Triangle3d(min, new Vector3d(min.x, min.y, max.z), new Vector3d(min.x, max.y, max.z), surface));
        r.add(new Triangle3d(min, new Vector3d(min.x, max.y, max.z), new Vector3d(min.x, max.y, min.z), surface));

        // front
        r.add(new Triangle3d(new Vector3d(max.x, min.y, min.z), new Vector3d(max.x, min.y, max.z), new Vector3d(max.x, max.y, max.z), surface));
        r.add(new Triangle3d(new Vector3d(max.x, min.y, min.z), new Vector3d(max.x, max.y, max.z), new Vector3d(max.x, max.y, min.z), surface));

        // top
        r.add(new Triangle3d(new Vector3d(min.x, min.y, max.z), new Vector3d(max.x, min.y, max.z), max, surface));
        r.add(new Triangle3d(new Vector3d(min.x, min.y, max.z), new Vector3d(min.x, max.y, max.z), max, surface));

        // bottom
        r.add(new Triangle3d(new Vector3d(min.x, min.y, min.z), new Vector3d(max.x, min.y, min.z), new Vector3d(max.x, max.y, min.z), surface));
        r.add(new Triangle3d(new Vector3d(min.x, min.y, min.z), new Vector3d(min.x, max.y, min.z), new Vector3d(max.x, max.y, min.z), surface));

        // rhs
        r.add(new Triangle3d(new Vector3d(min.x, max.y, min.z), new Vector3d(max.x, max.y, min.z), max, surface));
        r.add(new Triangle3d(new Vector3d(min.x, max.y, min.z), new Vector3d(min.x, max.y, max.z), max, surface));

        // lhs
        r.add(new Triangle3d(new Vector3d(min.x, min.y, min.z), new Vector3d(max.x, min.y, min.z), new Vector3d(max.x, min.y, max.z), surface));
        r.add(new Triangle3d(new Vector3d(min.x, min.y, min.z), new Vector3d(min.x, min.y, max.z), new Vector3d(max.x, min.y, max.z), surface));

        return r.parallelStream().map(tri -> correctNormal(tri)).collect(Collectors.toList());
    }

    private static class MengerIteration {
        private AxisAlignedBoundingBox3d boundingBox;
        private int level;

        public MengerIteration(AxisAlignedBoundingBox3d bb, int level) {
            this.boundingBox = bb;
            this.level = level;
        }
    }

    public static List<AxisAlignedBoundingBox3d> mengerSplit(AxisAlignedBoundingBox3d bbox) {
        List<AxisAlignedBoundingBox3d> r = new ArrayList<>();

        double xinc = bbox.xLength() / 3;
        double yinc = bbox.yLength() / 3;
        double zinc = bbox.zLength() / 3;

        Vector3d xshiftv = new Vector3d(xinc, 0, 0);
        Vector3d yshiftv = new Vector3d(0, yinc, 0);
        Vector3d zshiftv = new Vector3d(0, 0, zinc);
        Vector3d twiceZshift = zshiftv.multiply(2);
        Vector3d twiceYshift = yshiftv.multiply(2);

        Vector3d deltav = new Vector3d(bbox.xLength() / 3, bbox.yLength() / 3,bbox.zLength() / 3);
        //back
        AxisAlignedBoundingBox3d b = new AxisAlignedBoundingBox3d(bbox.getMin(), bbox.getMin().add(deltav));
        r.add(b);
        r.add(b.translate(yshiftv));
        r.add(b.translate(twiceYshift));

        r.add(b.translate(zshiftv));
        r.add(b.translate(zshiftv).translate(twiceYshift));

        r.add(b.translate(twiceZshift));
        r.add(b.translate(yshiftv).translate(twiceZshift));
        r.add(b.translate(twiceYshift.add(twiceZshift)));

        // mid
        b = b.translate(xshiftv);
        r.add(b);
        r.add(b.translate(twiceYshift));

        r.add(b.translate(twiceZshift));
        r.add(b.translate(twiceYshift.add(twiceZshift)));

        // front
        b = b.translate(xshiftv);
        r.add(b);
        r.add(b.translate(yshiftv));
        r.add(b.translate(twiceYshift));

        r.add(b.translate(zshiftv));
        r.add(b.translate(zshiftv).translate(twiceYshift));

        r.add(b.translate(twiceZshift));
        r.add(b.translate(yshiftv).translate(twiceZshift));
        r.add(b.translate(twiceYshift.add(twiceZshift)));

        return r;
    }

    public static List<Triangle3d> buildMengerSponge(AxisAlignedBoundingBox3d bb, Surface surface, int level) {
        List<Triangle3d> r = new ArrayList<>();

        Queue<MengerIteration> queue = new ArrayDeque<>();
        queue.add(new MengerIteration(bb, 0));

        while (!queue.isEmpty()) {
            MengerIteration mi = queue.remove();
            if (mi.level >= level) {
                r.addAll(buildAxisAlignedTriangleBox(mi.boundingBox, surface));
            } else {
                for (AxisAlignedBoundingBox3d bbox : mengerSplit(mi.boundingBox)) {
                    MengerIteration ni = new MengerIteration(bbox, mi.level + 1);
                    queue.add(ni);
                }
            }
        }

        return r;
    }

    private static Triangle3d correctNormal(Triangle3d tri) {
        Vector3d fromOriginToTriangle = tri.v1.toUnitVector();
        Vector3d normal = tri.getNormalVector();
        if (normal.dot(fromOriginToTriangle) < 0) {
            return tri.getFlippedNormal();
        } else {
            return tri;
        }
    }
}

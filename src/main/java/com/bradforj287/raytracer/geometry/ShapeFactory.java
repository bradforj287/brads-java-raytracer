package com.bradforj287.raytracer.geometry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ShapeFactory {
    private ShapeFactory() {

    }

    // with outward normals
    public static List<Triangle3d> buildAxisAlignedTriangleBox(AxisAlignedBoundingBox3d bb, Surface surface) {
        List<Triangle3d> r = new ArrayList<>();

        Vector3d min = bb.getMin();
        Vector3d max = bb.getMax();

        Vector3d minminmax = new Vector3d(min.x, min.y, max.z);
        Vector3d minmaxmax = new Vector3d(min.x, max.y, max.z);
        Vector3d minmaxmin = new Vector3d(min.x, max.y, min.z);
        Vector3d maxminmax = new Vector3d(max.x, min.y, max.z);
        Vector3d maxmaxmin = new Vector3d(max.x, max.y, min.z);
        Vector3d maxminmin = new Vector3d(max.x, min.y, min.z);
        //back
        r.add(new Triangle3d(min, minminmax, minmaxmax, surface));
        r.add(new Triangle3d(min,minmaxmax, minmaxmin, surface));

        // front
        r.add(new Triangle3d(maxminmin, max,maxminmax, surface));
        r.add(new Triangle3d(maxminmin, maxmaxmin,max, surface));

        // top
        r.add(new Triangle3d(minminmax, maxminmax,max, surface));
        r.add(new Triangle3d(minminmax, max,minmaxmax, surface));

        // bottom
        r.add(new Triangle3d(min, maxmaxmin, maxminmin, surface));
        r.add(new Triangle3d(min, minmaxmin, maxmaxmin, surface));

        // rhs
        r.add(new Triangle3d(minmaxmin, max, maxmaxmin, surface));
        r.add(new Triangle3d(minmaxmin, minmaxmax, max, surface));

        // lhs
        r.add(new Triangle3d(min,  maxminmin, maxminmax, surface));
        r.add(new Triangle3d(min, maxminmax, minminmax, surface));

        return r;
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

        Vector3d deltav = new Vector3d(bbox.xLength() / 3, bbox.yLength() / 3, bbox.zLength() / 3);
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

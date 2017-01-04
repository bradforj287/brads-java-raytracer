package com.bradforj287.raytracer.geometry;

public class Triangle3d extends Shape3d {

    public final Vector3d v1;
    public final Vector3d v2;
    public final Vector3d v3;

    private final Surface surface;
    private Vector3d normal; // cached for performance

    public Triangle3d(Vector3d a, Vector3d b, Vector3d c, Surface surface) {
        v1 = a;
        v2 = b;
        v3 = c;
        this.surface = surface;
    }

    public Triangle3d getFlippedNormal() {
        return new Triangle3d(v2, v1, v3, this.surface);
    }

    @Override
    public Vector3d normalAtSurfacePoint(Vector3d intersectPoint) {
        return getNormalVector();
    }

    public Vector3d getNormalVector() {
        if (normal == null) {
            Vector3d a = v2.subtract(v1);
            Vector3d b = v3.subtract(v1);
            Vector3d nn = a.cross(b);
            this.normal = nn.toUnitVector();
        }
        return this.normal;
    }

    @Override
    public boolean isHitByRay(Ray3d ray, double t1, RayCastArguments returnArgs) {

        Vector3d eye = ray.getPoint();
        Vector3d dir = ray.getDirection();

        Vector3d v_a = this.v1;
        Vector3d v_b = this.v2;
        Vector3d v_c = this.v3;

        double a = v_a.x - v_b.x;
        double b = v_a.y - v_b.y;
        double c = v_a.z - v_b.z;
        double d = v_a.x - v_c.x;
        double e = v_a.y - v_c.y;
        double f = v_a.z - v_c.z;
        double g = dir.x;
        double h = dir.y;
        double i = dir.z;

        double j = v_a.x - eye.x;
        double k = v_a.y - eye.y;
        double l = v_a.z - eye.z;

        // compute M
        double M = a * (e * i - h * f) + b * (g * f - d * i) + c
                * (d * h - e * g);
        // compute t
        double t = (-1 * (f * (a * k - j * b) + e * (j * c - a * l) + d
                * (b * l - k * c)))
                / M;

        if (t < 0 || t > t1) {
            return false;
        }

        // compute gamma
        double gamma = (i * (a * k - j * b) + h * (j * c - a * l) + g
                * (b * l - k * c))
                / M;

        if (gamma < 0 || gamma > 1) {
            return false;
        }
        // compute beta
        double beta = (j * (e * i - h * f) + k * (g * f - d * i) + l
                * (d * h - e * g))
                / M;

        if (beta < 0 || beta > 1 - gamma) {
            return false;
        }

        // set return arguments
        if (returnArgs != null) {
            returnArgs.t = t;
            returnArgs.gamma = gamma;
            returnArgs.beta = beta;
        }
        return true;
    }

    private Vector3d[] toVertexArray() {
        Vector3d[] r = new Vector3d[3];
        r[0] = v1;
        r[1] = v2;
        r[2] = v3;
        return r;
    }

    private double getMin(final Axis axis) {
        final String coord = axis.toCoordinateName();
        Vector3d[] r = toVertexArray();
        double min = r[0].getCoordiateByName(coord);
        for (int i = 1; i < r.length; i++) {
            double val = r[i].getCoordiateByName(coord);
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    private double getMax(final Axis axis) {
        final String coord = axis.toCoordinateName();
        Vector3d[] r = toVertexArray();
        double max = r[0].getCoordiateByName(coord);
        for (int i = 1; i < r.length; i++) {
            double val = r[i].getCoordiateByName(coord);
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    @Override
    public AxisAlignedBoundingBox3d getBoundingBox() {
        Vector3d min = new Vector3d(getMin(Axis.X), getMin(Axis.Y), getMin(Axis.Z));
        Vector3d max = new Vector3d(getMax(Axis.X), getMax(Axis.Y), getMax(Axis.Z));
        return new AxisAlignedBoundingBox3d(min, max);
    }

    @Override
    public Vector3d getCentroid() {
        return new Vector3d((v1.x + v2.x + v3.x) / 3,
                (v1.y + v2.y + v3.y) / 3,
                (v1.z + v2.z + v3.z) / 3);
    }

    @Override
    public Surface getSurface() {
        return this.surface;
    }
}

package com.bradforj287.raytracer.geometry;

public class Triangle3d extends Shape3d {

    public Vector3d v1;
    public Vector3d v2;
    public Vector3d v3;
    public int color;

    public Triangle3d() {
        color = 0;

        flipNormal();
    }

    /**
     * This is important: we make a new fresh copy of all
     * vertices. No two triangles should share the same Vector3d object
     * they will need separate objects. This makes performing transformations
     * on triangle meshes much easier.
     *
     * @param a
     * @param b
     * @param c
     * @param color1
     */
    public Triangle3d(Vector3d a, Vector3d b, Vector3d c, int color1) {
        v1 = new Vector3d(a);
        v2 = new Vector3d(b);
        v3 = new Vector3d(c);
        color = color1;
        flipNormal();
    }

    public void convertVerticiesToUnitVectors() {
        v1 = v1.getUnitVector();
        v2 = v2.getUnitVector();
        v3 = v3.getUnitVector();
    }

    public void flipNormal() {
        Vector3d temp = v2;
        v2 = v1;
        v1 = temp;
    }

    public Vector3d getNormalVector() {
        Vector3d a = com.bradforj287.raytracer.geometry.Vector3d.vectorSubtract(v2, v1);
        Vector3d b = com.bradforj287.raytracer.geometry.Vector3d.vectorSubtract(v3, v1);
        Vector3d normal = com.bradforj287.raytracer.geometry.Vector3d.vectorCross(a, b);
        return normal.getUnitVector();
    }

    public double signedAreaXY() {
        double x1 = v3.x - v1.x;
        double y1 = v3.y - v1.y;
        double x2 = v2.x - v1.x;
        double y2 = v2.y - v1.y;

        double crossProd = x2 * y1 - x1 * y2;
        return (crossProd * .5);
    }

    void baryXY(Vector3d v, double alpha, double beta, double gamma) {
        double x1;
        double y1;
        double x2;
        double y2;

        // get areaA
        double areaB;
        x1 = v2.x - v1.x;
        y1 = v2.y - v1.y;
        x2 = v.x - v1.x;
        y2 = v.y - v1.y;

        areaB = .5 * (x1 * y2 - x2 * y1);

        // get areaB
        double areaA;
        x1 = v2.x - v.x;
        y1 = v2.y - v.y;
        x2 = v3.x - v.x;
        y2 = v3.y - v.y;
        areaA = .5 * (x1 * y2 - x2 * y1);

        // get areaY
        double areaY;
        x1 = v3.x - v.x;
        y1 = v3.y - v.y;
        x2 = v1.x - v.x;
        y2 = v1.y - v.y;
        areaY = .5 * (x1 * y2 - x2 * y1);

        // get Total area;
        double totalArea = signedAreaXY();

        alpha = areaA / totalArea;
        beta = areaB / totalArea;
        gamma = areaY / totalArea;
    }

    public void multiplyByMatrix(Matrix3d mat) {
        v1.multiplyByMatrix(mat);
        v2.multiplyByMatrix(mat);
        v3.multiplyByMatrix(mat);
    }

    public boolean isHitByRay(Ray3d ray,  double t0, double t1,
                              RayCastArguments returnArgs) {

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

        if (t < t0 || t > t1) {
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

    private double getMin(final String coord) {
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

    private double getMax(final String coord) {
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
    public double minX() {
        return getMin("x");
    }

    @Override
    public double minY() {
        return getMin("y");
    }

    @Override
    public double minZ() {
        return getMin("z");
    }

    @Override
    public double maxX() {
        return getMax("x");
    }

    @Override
    public double maxY() {
        return getMax("y");
    }

    @Override
    public double maxZ() {
        return getMax("z");
    }

    @Override
    public Vector3d getCentroid() {
        return new Vector3d((v1.x + v2.x + v3.x) / 3,
                (v1.y + v2.y + v3.y) / 3,
                (v1.z + v2.z + v3.z) / 3);
    }
}

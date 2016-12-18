package com.bradforj287.raytracer.shapes;

public class Triangle3D extends Shape3D {

    public Vector3D v1;
    public Vector3D v2;
    public Vector3D v3;
    public int color;

    public Triangle3D() {
        color = 0;

        flipNormal();
    }

    /**
     * This is important: we make a new fresh copy of all
     * vertices. No two triangles should share the same Vector3D object
     * they will need separate objects. This makes performing transformations
     * on triangle meshes much easier.
     *
     * @param a
     * @param b
     * @param c
     * @param color1
     */
    public Triangle3D(Vector3D a, Vector3D b, Vector3D c, int color1) {
        v1 = new Vector3D(a);
        v2 = new Vector3D(b);
        v3 = new Vector3D(c);
        color = color1;
        flipNormal();
    }

    public void convertVerticiesToUnitVectors() {
        v1 = v1.getUnitVector();
        v2 = v2.getUnitVector();
        v3 = v3.getUnitVector();
    }

    public void flipNormal() {
        Vector3D temp = v2;
        v2 = v1;
        v1 = temp;
    }

    public Vector3D getNormalVector() {
        Vector3D a = Vector3D.vectorSubtract(v2, v1);
        Vector3D b = Vector3D.vectorSubtract(v3, v1);
        Vector3D normal = Vector3D.vectorCross(a, b);
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

    void baryXY(Vector3D v, double alpha, double beta, double gamma) {
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

    public void multiplyByMatrix(Matrix3D mat) {
        v1.multiplyByMatrix(mat);
        v2.multiplyByMatrix(mat);
        v3.multiplyByMatrix(mat);
    }

    public boolean isHitByRay(Vector3D eye, Vector3D dir, double t0, double t1,
                              RayCastArguments returnArgs) {
        Vector3D v_a = this.v1;
        Vector3D v_b = this.v2;
        Vector3D v_c = this.v3;

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
}

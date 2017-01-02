package com.bradforj287.raytracer.geometry;

import java.lang.reflect.Field;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Class for 3d vector
 *
 * @author brad
 */
public class Vector3d {

    public double x;
    public double y;
    public double z;

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3d(Vector3d v) {

        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3d subtract(Vector3d v) {
        double x1 = x - v.x;
        double y1 = y - v.y;
        double z1 = z - v.z;
        return new Vector3d(x1, y1, z1);
    }

    public double dot(Vector3d b) {
        return (this.x * b.x + this.y * b.y + this.z * b.z);
    }

    public Vector3d cross(Vector3d b) {
        // From Shirley p.27: a x b = (ay*bz - az*by, az*bx - ax*zb, ax*by-ay*bx)
        return new Vector3d(this.y * b.z - this.z * b.y, this.z * b.x - this.x * b.z, this.x
                * b.y - this.y * b.x);
    }

    public Double getMagnitude() {
        double sum = x * x + y * y + z * z;
        return Math.sqrt(sum);
    }

    public Vector3d getUnitVector() {
        double m = getMagnitude();
        double x1 = x / m;
        double y1 = y / m;
        double z1 = z / m;
        return new Vector3d(x1, y1, z1);
    }

    public void multiplyByMatrix(Matrix3d a) {
        double[] result = new double[3];
        double[] h = new double[4];
        h[0] = this.x;
        h[1] = this.y;
        h[2] = this.z;
        h[3] = 1;

        for (int i = 0; i < 3; i++) {
            double sum = 0;
            for (int j = 0; j < 4; j++) {
                sum = sum + h[j] * a.matrix[i][j];
            }
            result[i] = sum;
        }
        x = result[0];
        y = result[1];
        z = result[2];
    }

    public double getCoordiateByName(final String coord) {
        Field coordField = FieldUtils.getField(Vector3d.class, coord);
        try {
            return coordField.getDouble(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

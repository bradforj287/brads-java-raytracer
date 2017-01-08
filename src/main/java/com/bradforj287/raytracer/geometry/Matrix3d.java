package com.bradforj287.raytracer.geometry;

public class Matrix3d {
    public static final Matrix3d IDENTITY = getIdentityMatrix();
    public double[][] matrix = new double[4][4];

    public Matrix3d() {
        zero();
    }

    public void zero() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    public static Matrix3d getIdentityMatrix() {
        Matrix3d matrix3d = new Matrix3d();
        matrix3d.identity();
        return matrix3d;
    }

    public void identity() {
        zero();
        for (int i = 0; i < 4; i++) {
            matrix[i][i] = 1;
        }
    }

    public static Matrix3d matrixMultiply(Matrix3d a, Matrix3d b) {
        Matrix3d result = new Matrix3d();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum = sum + a.matrix[k][j] * b.matrix[i][k];
                }
                result.matrix[i][j] = sum;
            }
        }
        return result;
    }

    public static Vector3d matrixMultiply(Matrix3d a, Vector3d v) {
        double[] result = new double[3];
        double[] h = new double[4];
        h[0] = v.x;
        h[1] = v.y;
        h[2] = v.z;
        h[3] = 1;

        for (int i = 0; i < 3; i++) {
            double sum = 0;
            for (int j = 0; j < 4; j++) {
                sum = sum + h[j] * a.matrix[i][j];
            }
            result[i] = sum;
        }
        Vector3d res = new Vector3d(result[0], result[1], result[2]);
        return res;
    }

    public static Matrix3d getXRotationMatrix(double theta) {
        Matrix3d result = new Matrix3d();
        result.identity();
        result.matrix[1][1] = Math.cos(theta);
        result.matrix[1][2] = -Math.sin(theta);
        result.matrix[2][1] = Math.sin(theta);
        result.matrix[2][2] = Math.cos(theta);
        return result;
    }

    public static Matrix3d getYRotationMatrix(double theta) {
        Matrix3d result = new Matrix3d();
        result.identity();
        result.matrix[0][0] = Math.cos(theta);
        result.matrix[0][2] = Math.sin(theta);
        result.matrix[2][0] = -Math.sin(theta);
        result.matrix[2][2] = Math.cos(theta);
        return result;
    }

    public static Matrix3d getZRotationMatrix(double theta) {
        Matrix3d result = new Matrix3d();
        result.identity();
        result.matrix[0][0] = Math.cos(theta);
        result.matrix[0][1] = -Math.sin(theta);
        result.matrix[1][0] = Math.sin(theta);
        result.matrix[1][1] = Math.cos(theta);
        return result;
    }

    public static Matrix3d getRotationMatrix(double tx, double ty, double tz) {
        Matrix3d xRot = Matrix3d.getXRotationMatrix(tx);
        Matrix3d yRot = Matrix3d.getYRotationMatrix(ty);
        Matrix3d zRot = Matrix3d.getZRotationMatrix(tz);
        Matrix3d rot = Matrix3d.matrixMultiply(xRot, yRot);
        return Matrix3d.matrixMultiply(rot, zRot);
    }
};
package com.bradforj287.raytracer.shapes;

public class Matrix3D {
    public double[][] matrix = new double[4][4];

    public Matrix3D() {
        zero();
    }

    public void zero() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    public void identity() {
        zero();
        for (int i = 0; i < 4; i++) {
            matrix[i][i] = 1;
        }
    }

    public static Matrix3D matrixMultiply(Matrix3D a, Matrix3D b) {
        Matrix3D result = new Matrix3D();
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

    public static Vector3D matrixMultiply(Matrix3D a, Vector3D v) {
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
        Vector3D res = new Vector3D(result[0], result[1], result[2]);
        return res;
    }

    public static Matrix3D getXRotationMatrix(double theta) {
        Matrix3D result = new Matrix3D();
        result.identity();
        result.matrix[1][1] = Math.cos(theta);
        result.matrix[1][2] = -Math.sin(theta);
        result.matrix[2][1] = Math.sin(theta);
        result.matrix[2][2] = Math.cos(theta);
        return result;
    }

    public static Matrix3D getYRotationMatrix(double theta) {
        Matrix3D result = new Matrix3D();
        result.identity();
        result.matrix[0][0] = Math.cos(theta);
        result.matrix[0][2] = Math.sin(theta);
        result.matrix[2][0] = -Math.sin(theta);
        result.matrix[2][2] = Math.cos(theta);
        return result;

    }

    public static Matrix3D getZRotationMatrix(double theta) {
        Matrix3D result = new Matrix3D();
        result.identity();
        result.matrix[0][0] = Math.cos(theta);
        result.matrix[0][1] = -Math.sin(theta);
        result.matrix[1][0] = Math.sin(theta);
        result.matrix[1][1] = Math.cos(theta);
        return result;
    }

};
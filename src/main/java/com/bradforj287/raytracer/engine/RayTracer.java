package com.bradforj287.raytracer.engine;

import com.bradforj287.raytracer.ProgramArguments;
import com.bradforj287.raytracer.geometry.*;
import com.bradforj287.raytracer.model.SceneModel;
import com.bradforj287.raytracer.model.SpacialStructureQueryStats;

public class RayTracer implements Tracer {
    private SceneModel scene;
    /**private DataPointBuffer kdTreeNodeVisitsBuffer = new DataPointBuffer(100);
    private DataPointBuffer shapeVisitsBuffer = new DataPointBuffer(100);*/

    private final static int MAX_RECURSE_DEPTH = 16;

    public RayTracer(SceneModel model) {
        this.scene = model;
    }

    private static double clamp(final double val, final double min, final double max) {
        return Math.max(min, Math.min(max, val));
    }

    private Vector3d getRefractionVector(final Vector3d I, final Vector3d N, final double ior) {
        double cosi = clamp(-1, 1, I.dot(N));
        double etai = 1, etat = ior;
        Vector3d n = N;
        if (cosi < 0) {
            cosi = -cosi;
        } else {
            double temp = etai;
            etai = etat;
            etat = temp;
            n = N.multiply(-1);
        }
        double eta = etai / etat;
        double k = 1 - (eta * eta) * (1 - (cosi * cosi));
        if (k <= 0) {
            return Vector3d.ZERO;
        } else {
            return I.multiply(eta).add(n.multiply(((eta * cosi) - Math.sqrt(k))));
        }
    }

    private Vector3d getReflectionVector(final Ray3d ray, final Vector3d normalToShape) {
       return getReflectionVector(ray.getDirection(), normalToShape);
    }

    private Vector3d getReflectionVector(final Vector3d dir, final Vector3d normalToShape) {
        double dDotN = dir.dot(normalToShape) * 2;
        return dir.subtract(normalToShape.multiply(dDotN));
    }

    private class FresnelResult {
        private double kt;
        private double kr;
    }

    private FresnelResult fresnel(final Vector3d I, final Vector3d N, final double ior) {
        double cosi = clamp(-1, 1, I.dot(N));
        double etai = 1, etat = ior;
        if (cosi > 0) {
            double temp = etai;
            etai = etat;
            etat = temp;
        }
        // Compute sini using Snell's law
        double sint = etai / etat * Math.sqrt(Math.max(0.f, 1 - cosi * cosi));
        // Total internal reflection
        FresnelResult result = new FresnelResult();
        if (sint >= 1) {
            result.kr = 1;
        } else {
            double cost = Math.sqrt(Math.max(0.f, 1 - sint * sint));
            cosi = Math.abs(cosi);
            double Rs = ((etat * cosi) - (etai * cost)) / ((etat * cosi) + (etai * cost));
            double Rp = ((etai * cosi) - (etat * cost)) / ((etai * cosi) + (etat * cost));
            result.kr = (Rs * Rs + Rp * Rp) / 2;
        }
        // As a consequence of the conservation of energy, transmittance is given by:
        result.kt = 1 - result.kr;
        return result;
    }

    private RgbColor getColorShadowAdusted(Shape3d intersectShape, Vector3d intersectLoc, Vector3d normalToShape, int depth) {
        boolean canRecurseFurther = depth < MAX_RECURSE_DEPTH;
        RgbColor color = intersectShape.getSurface().getColor();
        Vector3d vectorToLight = ProgramArguments.LIGHT_LOCATION.subtract(intersectLoc).toUnitVector();

        double angleBetweenNormalAndLight = normalToShape.dot(vectorToLight);

        if (angleBetweenNormalAndLight < 0) {
            angleBetweenNormalAndLight = 0;
        } else if (canRecurseFurther && isInShadow(intersectLoc, ProgramArguments.LIGHT_LOCATION)) {
            angleBetweenNormalAndLight = 0;
        }

        double colorscalar = ProgramArguments.AMBIENT_LIGHT + (1 - ProgramArguments.AMBIENT_LIGHT)
                * (angleBetweenNormalAndLight * ProgramArguments.LIGHT_INTENSITY);
        return color.scale(colorscalar);
    }

    @Override
    public RgbColor getColorForRay(final Ray3d ray) {
        return getColorForRay(ray, 0);
    }

    private RgbColor getColorForRay(final Ray3d ray, int depth) {
        final boolean canRecurseFurther = depth < MAX_RECURSE_DEPTH;
        RayHitResult rayHitResult = doesRayHitAnyShape(ray);

        if (!rayHitResult.didHitShape()) {
            return RgbColor.BLACK; // doesn't hit anything.
        }

        Shape3d intersectShape = rayHitResult.getShape();
        Surface intersectSurface = intersectShape.getSurface();
        final double t = rayHitResult.getT();

        Vector3d intersectLoc = ray.getPoint().add(ray.getDirection().multiply(t));
        Vector3d normalToShape = intersectShape.normalAtSurfacePoint(intersectLoc);

        if (canRecurseFurther) {
            if (intersectSurface.isRefractive() && intersectSurface.isReflective()) {
                RgbColor refractionColor = RgbColor.BLACK;
                // compute fresnel
                Vector3d dir = ray.getDirection().toUnitVector();
                float kr;
                FresnelResult fresnelResult = fresnel(dir, normalToShape, intersectSurface.getIof());
                // compute refraction if it is not a case of total internal reflection
                if (fresnelResult.kr < 1) {
                    Vector3d refractionDirection = getRefractionVector(dir, normalToShape, intersectSurface.getIof()).toUnitVector();
                    Ray3d fresnelRay = Ray3d.createShiftedRay(intersectLoc, refractionDirection);
                    refractionColor = getColorForRay(fresnelRay, depth + 1);
                }

                Vector3d reflectionDirection = getReflectionVector(dir, normalToShape).toUnitVector();
                Ray3d reflectionRay = Ray3d.createShiftedRay(intersectLoc, reflectionDirection);
                RgbColor reflectionColor = getColorForRay(reflectionRay, depth + 1);

                RgbColor reflectColorVec = reflectionColor.scale(fresnelResult.kr);
                RgbColor refractColorVec = refractionColor.scale(1 -fresnelResult.kr);

                return reflectColorVec.add(refractColorVec);
            } else if (intersectSurface.isRefractive()) {
                double iof = intersectSurface.getIof();
                Vector3d refractDir = getRefractionVector(ray.getDirection().toUnitVector(), normalToShape, iof);
                Ray3d refractRay = Ray3d.createShiftedRay(intersectLoc, refractDir);
                return getColorForRay(refractRay, depth + 1);
            } else if (intersectSurface.isReflective()) {
                Vector3d reflectDir = getReflectionVector(ray, normalToShape);
                Ray3d reflectRay = Ray3d.createShiftedRay(intersectLoc, reflectDir);
                return getColorForRay(reflectRay, depth + 1);
            }
        }
        return getColorShadowAdusted(intersectShape, intersectLoc, normalToShape, depth);
    }

    private boolean isInShadow(Vector3d hitLoc, Vector3d lightLocation) {
        Vector3d directionToLight = lightLocation.subtract(hitLoc);
        Ray3d shadowRay = Ray3d.createShiftedRay(hitLoc, directionToLight);
        double tThatHitsLight = (lightLocation.x - hitLoc.x) / directionToLight.x;
        RayHitResult hitResult = doesRayHitAnyShape(shadowRay, tThatHitsLight);
        return hitResult.didHitShape();
    }

    private RayHitResult doesRayHitAnyShape(final Ray3d ray) {
        return doesRayHitAnyShapeHelper(ray, Double.MAX_VALUE);
    }

    private RayHitResult doesRayHitAnyShape(final Ray3d ray, final double maxT) {
        return doesRayHitAnyShapeHelper(ray, maxT);
    }

    private RayHitResult doesRayHitAnyShapeHelper(final Ray3d theRay, final double maxT) {
        final RayHitResult results = new RayHitResult();
        results.setT(maxT);
        final RayCastArguments rayCastArgs = new RayCastArguments();

        SpacialStructureQueryStats queryStats = scene.visitPossibleIntersections(theRay, shape -> {
            if (shape.isHitByRay(theRay, results.getT(),
                    rayCastArgs)) {
                if (rayCastArgs.t < results.getT()) {
                    results.setT(rayCastArgs.t);
                    results.setShape(shape);
                }
            }
        });

        //log some stats
      /*  kdTreeNodeVisitsBuffer.addToBuffer(queryStats.getNodesVisited());
        shapeVisitsBuffer.addToBuffer(queryStats.getShapesVisited()); */

        results.setQueryStats(queryStats);
        return results;
    }
}

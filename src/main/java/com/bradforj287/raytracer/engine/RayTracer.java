package com.bradforj287.raytracer.engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import com.bradforj287.raytracer.ProgramArguments;
import com.bradforj287.raytracer.geometry.*;
import com.bradforj287.raytracer.model.SceneModel;
import com.bradforj287.raytracer.model.SpacialStructureQueryStats;
import com.bradforj287.raytracer.utils.DataPointBuffer;
import com.bradforj287.raytracer.utils.VideoDataPointBuffer;

public class RayTracer {
    private SceneModel scene;
    private Dimension sceneResolution;
    private VideoDataPointBuffer fpsBuffer = new VideoDataPointBuffer();
    private DataPointBuffer kdTreeNodeVisitsBuffer = new DataPointBuffer(100);
    private DataPointBuffer shapeVisitsBuffer = new DataPointBuffer(100);

    private final static int MAX_RECURSE_DEPTH = 16;

    public RayTracer(SceneModel model, Dimension sceneResolution) {
        this.sceneResolution = sceneResolution;
        this.scene = model;
    }

    public BufferedImage traceScene(final double thetax, final double thetay, final double thetaz) {
        final BufferedImage image = new BufferedImage(sceneResolution.width,
                sceneResolution.height, BufferedImage.TYPE_INT_RGB);

        // first clear the screen to the background color
        clearImage(image);

        final int numCores = Runtime.getRuntime().availableProcessors();

        // create some arguments for the trace call
        final double xIncrement = ProgramArguments.SCREEN_WIDTH / sceneResolution.getWidth();
        final double yIncrement = ProgramArguments.SCREEN_HEIGHT / sceneResolution.getHeight();
        final double xstart = -1 * ProgramArguments.SCREEN_WIDTH / 2;
        final double ystart = -1 * ProgramArguments.SCREEN_HEIGHT / 2;
        final int threadWidth = sceneResolution.width / numCores;

        ArrayList<Thread> tracePool = new ArrayList<>();

        // create the threads
        for (int i = 0; i < numCores; i++) {
            // calculate width and height for this thread
            int width = threadWidth;
            int height = sceneResolution.height;

            if (i == numCores - 1) {
                width += sceneResolution.width % numCores;
            }

            // pass this to thread
            final Rectangle threadRect = new Rectangle(i * threadWidth, 0,
                    width, height);

            // create the worker thread
            Thread traceThread = new Thread(() -> {
                iterateOverScreenRegion(image, threadRect, xIncrement,
                        yIncrement, xstart, ystart, thetax, thetay, thetaz);
            });

            tracePool.add(traceThread);

            // start up the thread we just created
            traceThread.start();
        }

        // make sure all threads have finished before we return!
        for (Thread t : tracePool) {
            try {
                t.join();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        fpsBuffer.addToBuffer(System.currentTimeMillis());

        printStatsToImage(image, thetax, thetay, thetaz);
        return image;
    }

    private void printStatsToImage(BufferedImage image, double thetax, double thetay, double thetaz) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // print FPS
        String fpsString = Double.toString(fpsBuffer.getFramesPerSecond());
        drawString(g2d, "FPS=" + fpsString, 10, 10);

        // print rotation
        drawString(g2d, "rX=" + Double.toString(thetax), 10, 20);
        drawString(g2d, "rY=" + Double.toString(thetay), 10, 30);
        drawString(g2d, "rZ=" + Double.toString(thetaz), 10, 40);

        // print spacial structure query stats
        String avgNodeVisits = Double.toString(kdTreeNodeVisitsBuffer.getAvg());
        drawString(g2d, "N_VST=" + avgNodeVisits, 10, 50);

        String avgShapeVisits = Double.toString(shapeVisitsBuffer.getAvg());
        drawString(g2d, "S_VST=" + avgShapeVisits, 10, 60);
    }

    private void drawString(Graphics2D g2d, String s, int i, int j) {
        if (s.length() > 11) {
            s = s.substring(0, 11);
        }
        char[] charA = s.toCharArray();
        g2d.drawChars(charA, 0, charA.length, i, j);
    }

    /**
     * Iterates over a subset of the screen resolution specified by Region. The
     * purpose of this is to distribute across threads.
     */
    private void iterateOverScreenRegion(BufferedImage image, Rectangle region,
                                         double xIncrement, double yIncrement, double xStart, double yStart,
                                         double thetax, double thetay, double thetaz) {

        Matrix3d xRot = Matrix3d.getXRotationMatrix(thetax);
        Matrix3d yRot = Matrix3d.getYRotationMatrix(thetay);
        Matrix3d zRot = Matrix3d.getZRotationMatrix(thetaz);

        Random rand = new Random();

        Matrix3d rot = Matrix3d.matrixMultiply(xRot, yRot);
        rot = Matrix3d.matrixMultiply(rot, zRot);

        // iterate over all pixels in resolution
        for (int i = region.x; i < region.x + region.width; i++) {
            for (int j = 0; j < region.height; j++) {

                int aveR = 0;
                int aveG = 0;
                int aveB = 0;
                int aveA = 0;
                for (int a = 0; a < ProgramArguments.ANTIALIASING_SAMPLES; a++) {
                    double xOffset;
                    double yOffset;

                    if (ProgramArguments.ANTIALIASING_SAMPLES > 1) {
                        xOffset = rand.nextDouble() * xIncrement;
                        yOffset = rand.nextDouble() * yIncrement;
                    } else {
                        xOffset = .5 * xIncrement;
                        yOffset = .5 * yIncrement;
                    }

                    // calculate pointOnScreen and eyePoint
                    Vector3d pointOnScreen = new Vector3d(xStart + i * xIncrement + xOffset,
                            yStart + j * yIncrement + yOffset, ProgramArguments.SCREEN_POSITION.z);
                    Vector3d eyePosition = new Vector3d(ProgramArguments.EYE_POSITION);

                    // rotate both points according to theta
                    pointOnScreen = pointOnScreen.multiplyByMatrix(rot);
                    eyePosition = eyePosition.multiplyByMatrix(rot);

                    // calculate view ray
                    Vector3d eyeDirection = pointOnScreen.subtract(eyePosition);

                    Ray3d ray = new Ray3d(eyePosition, eyeDirection);
                    int color = getColorForRay(ray, 0).asInt();

                    Color c = new Color(color);
                    aveR = aveR + c.getRed();
                    aveG = aveG + c.getGreen();
                    aveB = aveB + c.getBlue();
                    aveA = aveA + c.getAlpha();

                }

                aveR = aveR / ProgramArguments.ANTIALIASING_SAMPLES;
                aveG = aveG / ProgramArguments.ANTIALIASING_SAMPLES;
                aveB = aveB / ProgramArguments.ANTIALIASING_SAMPLES;
                aveA = aveA / ProgramArguments.ANTIALIASING_SAMPLES;

                Color c1 = new Color(aveR, aveG, aveB, aveA);
                image.setRGB(i, j, c1.getRGB());
            }
        }
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
        kdTreeNodeVisitsBuffer.addToBuffer(queryStats.getNodesVisited());
        shapeVisitsBuffer.addToBuffer(queryStats.getShapesVisited());

        results.setQueryStats(queryStats);
        return results;
    }

    private void clearImage(BufferedImage image) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setBackground(Color.red);
        g.setColor(Color.black);
        g.fillRect(0, 0, sceneResolution.width, sceneResolution.height);
    }
}

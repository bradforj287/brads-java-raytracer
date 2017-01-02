package com.bradforj287.raytracer.engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import com.bradforj287.raytracer.ProgramArguments;
import com.bradforj287.raytracer.geometry.*;
import com.bradforj287.raytracer.model.SceneModel;
import com.bradforj287.raytracer.utils.VideoDataPointBuffer;

public class RayTracer {
    private SceneModel scene;
    private Dimension sceneResolution;
    private VideoDataPointBuffer fpsBuffer = new VideoDataPointBuffer();

    public RayTracer(SceneModel model, Dimension sceneResolution) {
        this.sceneResolution = sceneResolution;
        this.scene = model;
    }

    public BufferedImage traceScene(final double thetax, final double thetay, final double thetaz)
            throws InterruptedException {

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

        ArrayList<Thread> tracePool = new ArrayList<Thread>();

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
            Thread traceThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    iterateOverScreenRegion(image, threadRect, xIncrement,
                            yIncrement, xstart, ystart, thetax, thetay, thetaz);
                }
            }
            );

            tracePool.add(traceThread);

            // start up the thread we just created
            traceThread.start();
        }

        // make sure all threads have finished before we return!
        for (Thread t : tracePool) {
            t.join();
        }

        fpsBuffer.addToBuffer(System.currentTimeMillis());

        printStatsToImage(image, thetax, thetay, thetaz);
        return image;
    }

    private void printStatsToImage(BufferedImage image, double thetax, double thetay, double thetaz) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // print FPS
        String fpsString = Double.toString(fpsBuffer.getFramesPerSecond());
        drawString(g2d, "FPS = " + fpsString, 10, 10);

        // print rotation
        drawString(g2d, "rX= " + Double.toString(thetax), 10, 20);
        drawString(g2d, "rY = " + Double.toString(thetay), 10, 30);
        drawString(g2d, "rZ = " + Double.toString(thetaz), 10, 40);

    }

    private void drawString(Graphics2D g2d, String s, int i, int j) {
        if (s.length() > 10) {
            s = s.substring(0, 10);
        }
        char[] charA = s.toCharArray();

        g2d.drawChars(charA, 0, charA.length, i, j);
    }

    /**
     * Iterates over a subset of the screen resolution specified by Region. The
     * purpose of this is to distribute across threads.
     *
     * @param region
     * @param xIncrement
     * @param yIncrement
     * @param xStart
     * @param yStart
     * @param theta
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
                    pointOnScreen.multiplyByMatrix(rot);
                    eyePosition.multiplyByMatrix(rot);

                    // calculate view ray
                    Vector3d eyeDirection = pointOnScreen.subtract(eyePosition);

                    Ray3d ray = new Ray3d(eyePosition, eyeDirection);

                    int color = getColorForRay(ray, i, j);

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

    private int getColorForRay(final Ray3d ray, int i, int j) {

        RayCastArguments returnArgs = new RayCastArguments();

        Shape3d intersectShape = doesRayHitAnyShape(ray, returnArgs);
        if (intersectShape != null) {

            double t = returnArgs.t;

            //todo: make this more elegant
            Vector3d eyePosition = ray.getPoint();
            Vector3d eyeDirection = ray.getDirection();

            Vector3d intersectLoc = new Vector3d(eyePosition.x + t
                    * eyeDirection.x, eyePosition.y + t * eyeDirection.y,
                    eyePosition.z + t * eyeDirection.z);
            Vector3d normalToShape = intersectShape.normalAtSurfacePoint(intersectLoc);

            int color = intersectShape.getColor();

            Vector3d lightVector = ProgramArguments.LIGHT_LOCATION.subtract(intersectLoc).getUnitVector();

            double angleBetweenNormalAndLight = normalToShape.dot(lightVector);

            lightVector = intersectLoc.subtract(ProgramArguments.LIGHT_LOCATION).getUnitVector();
            if (angleBetweenNormalAndLight < 0) {
                angleBetweenNormalAndLight = 0;
            } else if (isInShadow(intersectShape, ProgramArguments.LIGHT_LOCATION, lightVector)) {
                angleBetweenNormalAndLight = 0;
            }

            /**
             * Set the color for the pixel
             */
            double colorscalar = ProgramArguments.AMBIENT_LIGHT + (1 - ProgramArguments.AMBIENT_LIGHT)
                    * angleBetweenNormalAndLight;

            int mask = 0x00FFFFFF;
            int redChannel = color & mask;
            redChannel = redChannel >> 16;

            mask = 0x0000FF00;
            int greenChannel = color & mask;

            greenChannel = greenChannel >> 8;

            mask = 0x000000FF;
            int blueChannel = color & mask;

            double nRed = colorscalar * ((double) redChannel);
            double nGreen = colorscalar * ((double) greenChannel);
            double nBlue = colorscalar * ((double) blueChannel);

            redChannel = (int) nRed;
            greenChannel = (int) nGreen;
            blueChannel = (int) nBlue;

            redChannel = redChannel << 16;
            greenChannel = greenChannel << 8;

            int colorToAssign = redChannel + greenChannel + blueChannel;

            return colorToAssign;
        } else {
            return 0;
        }

    }

    private boolean isInShadow(Shape3d hitShape, Vector3d intersectLoc,
                               Vector3d lightDir) {
        Ray3d ray = new Ray3d(intersectLoc, lightDir);
        Shape3d intersectShape = doesRayHitAnyShape(ray, new RayCastArguments());

        if (intersectShape == null) {
            return false;
        } else if (intersectShape != hitShape) {
            return true;
        } else {
            return false;
        }
    }

    private class VisitingResults {
        double t1 = Double.MAX_VALUE;
        Shape3d visibleShape;
    }

    private Shape3d doesRayHitAnyShape(final Ray3d ray, final RayCastArguments returnArgs) {
        final double t0 = .0001;
        final VisitingResults results = new VisitingResults();

        scene.visitPossibleIntersections(ray, shape -> {
            if (shape.isHitByRay(ray, t0, results.t1,
                    returnArgs)) {
                results.t1 = returnArgs.t;
                results.visibleShape = shape;
            }
        });
        return results.visibleShape;
    }

    private void clearImage(BufferedImage image) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setBackground(Color.red);
        g.setColor(Color.black);
        g.fillRect(0, 0, sceneResolution.width, sceneResolution.height);
    }
}

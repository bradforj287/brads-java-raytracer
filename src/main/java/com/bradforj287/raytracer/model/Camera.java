package com.bradforj287.raytracer.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import com.bradforj287.raytracer.ProgramArguments;
import com.bradforj287.raytracer.engine.Tracer;
import com.bradforj287.raytracer.geometry.Matrix3d;
import com.bradforj287.raytracer.geometry.Ray3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.utils.VideoDataPointBuffer;

public class Camera {
    final private Dimension screenResolution;
    private Vector3d screenPosition;
    final private Tracer tracer;
    private Matrix3d rotation;
    private double thetax = 0;
    private double thetay = 0;
    private double thetaz = 0;

    private VideoDataPointBuffer fpsBuffer = new VideoDataPointBuffer();

    public Camera(Dimension screenResolution, Tracer tracer) {
        this.screenResolution = screenResolution;
        this.tracer = tracer;
        this.screenPosition = Vector3d.ZERO;
        this.rotation = Matrix3d.IDENTITY;
    }

    public void setRotation(final double thetax, final double thetay, final double thetaz) {
        this.thetax = thetax;
        this.thetay = thetay;
        this.thetaz = thetaz;
        Matrix3d xRot = Matrix3d.getXRotationMatrix(thetax);
        Matrix3d yRot = Matrix3d.getYRotationMatrix(thetay);
        Matrix3d zRot = Matrix3d.getZRotationMatrix(thetaz);
        Matrix3d rot = Matrix3d.matrixMultiply(xRot, yRot);
        rotation = Matrix3d.matrixMultiply(rot, zRot);
    }

    public double getThetax() {
        return thetax;
    }

    public double getThetay() {
        return thetay;
    }

    public double getThetaz() {
        return thetaz;
    }

    public BufferedImage captureImage() {
        return traceScene();
    }

    private void clearImage(BufferedImage image) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setBackground(Color.red);
        g.setColor(Color.black);
        g.fillRect(0, 0, screenResolution.width, screenResolution.height);
    }

    private BufferedImage traceScene() {
        final BufferedImage image = new BufferedImage(screenResolution.width,
                screenResolution.height, BufferedImage.TYPE_INT_RGB);

        // first clear the screen to the background color
        clearImage(image);

        final int numCores = Runtime.getRuntime().availableProcessors();

        // create some arguments for the trace call
        final double xIncrement = ProgramArguments.SCREEN_WIDTH / screenResolution.getWidth();
        final double yIncrement = ProgramArguments.SCREEN_HEIGHT / screenResolution.getHeight();
        final double xstart = -1 * ProgramArguments.SCREEN_WIDTH / 2;
        final double ystart = -1 * ProgramArguments.SCREEN_HEIGHT / 2;
        final int threadWidth = screenResolution.width / numCores;

        ArrayList<Thread> tracePool = new ArrayList<>();

        // create the threads
        for (int i = 0; i < numCores; i++) {
            // calculate width and height for this thread
            int width = threadWidth;
            int height = screenResolution.height;

            if (i == numCores - 1) {
                width += screenResolution.width % numCores;
            }

            // pass this to thread
            final Rectangle threadRect = new Rectangle(i * threadWidth, 0,
                    width, height);

            // create the worker thread
            Thread traceThread = new Thread(() -> {
                iterateOverScreenRegion(image, threadRect, xIncrement,
                        yIncrement, xstart, ystart, screenPosition, rotation);
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

        printStatsToImage(image);
        return image;
    }

    private void printStatsToImage(BufferedImage image) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // print FPS
        String fpsString = Double.toString(fpsBuffer.getFramesPerSecond());
        drawString(g2d, "FPS=" + fpsString, 10, 10);

        // print rotation
         drawString(g2d, "rX=" + Double.toString(thetax), 10, 20);
        drawString(g2d, "rY=" + Double.toString(thetay), 10, 30);
        drawString(g2d, "rZ=" + Double.toString(thetaz), 10, 40);

        // print spacial structure query stats
        //String avgNodeVisits = Double.toString(kdTreeNodeVisitsBuffer.getAvg());
        //drawString(g2d, "N_VST=" + avgNodeVisits, 10, 50);

        //String avgShapeVisits = Double.toString(shapeVisitsBuffer.getAvg());
        //drawString(g2d, "S_VST=" + avgShapeVisits, 10, 60);
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
                                         double xIncrement, double yIncrement,
                                         double xStart, double yStart,
                                         Vector3d screenPos, Matrix3d screenRotation) {
        Random rand = new Random();

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
                            yStart + j * yIncrement + yOffset, 0);
                    Vector3d eyePosition = new Vector3d(0, 0, ProgramArguments.EYE_CAMERA_DISTANCE);

                    // rotate both points according to theta
                    pointOnScreen = pointOnScreen.multiplyByMatrix(screenRotation);
                    eyePosition = eyePosition.multiplyByMatrix(screenRotation);

                    // move to screen position
                    pointOnScreen = pointOnScreen.add(screenPos);
                    eyePosition = eyePosition.add(screenPos);

                    // calculate view ray
                    Vector3d eyeDirection = pointOnScreen.subtract(eyePosition);

                    Ray3d ray = new Ray3d(eyePosition, eyeDirection);
                    int color = this.tracer.getColorForRay(ray).asInt();

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
                //set screen coordiates. Need to mirror on y axis for output to buffered image
                int si = (screenResolution.width -1) - i;
                image.setRGB(si, j, c1.getRGB());
            }
        }
    }

    public Dimension getScreenResolution() {
        return screenResolution;
    }

    public Vector3d getScreenPosition() {
        return screenPosition;
    }

    public void setScreenPosition(Vector3d screenPosition) {
        this.screenPosition = screenPosition;
    }

    public Tracer getTracer() {
        return tracer;
    }

    public VideoDataPointBuffer getFpsBuffer() {
        return fpsBuffer;
    }

    public void setFpsBuffer(VideoDataPointBuffer fpsBuffer) {
        this.fpsBuffer = fpsBuffer;
    }
}

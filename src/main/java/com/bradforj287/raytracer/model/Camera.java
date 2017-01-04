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
    private Vector3d eye;
    private Dimension screenResolution;
    private Vector3d screenPosition;
    private Tracer tracer;

    private VideoDataPointBuffer fpsBuffer = new VideoDataPointBuffer();


    public Camera(Vector3d eye, Dimension screenResolution, Vector3d screenPosition, Tracer tracer) {
        this.eye = eye;
        this.screenResolution = screenResolution;
        this.screenPosition = screenPosition;
        this.tracer = tracer;
    }

    public void rotateCamera(final double thetax, final double thetay, final double thetaz) {
        return;
    }

    public BufferedImage captureImage(final double thetax, final double thetay, final double thetaz) {
        return traceScene(thetax, thetay, thetaz);
    }

    private void clearImage(BufferedImage image) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setBackground(Color.red);
        g.setColor(Color.black);
        g.fillRect(0, 0, screenResolution.width, screenResolution.height);
    }

    private BufferedImage traceScene(final double thetax, final double thetay, final double thetaz) {
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
                image.setRGB(i, j, c1.getRGB());
            }
        }
    }


    public Vector3d getEye() {
        return eye;
    }

    public void setEye(Vector3d eye) {
        this.eye = eye;
    }

    public Dimension getScreenResolution() {
        return screenResolution;
    }

    public void setScreenResolution(Dimension screenResolution) {
        this.screenResolution = screenResolution;
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

    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    public VideoDataPointBuffer getFpsBuffer() {
        return fpsBuffer;
    }

    public void setFpsBuffer(VideoDataPointBuffer fpsBuffer) {
        this.fpsBuffer = fpsBuffer;
    }
}

package com.bradforj287.raytracer.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Future;
import com.bradforj287.raytracer.Globals;
import com.bradforj287.raytracer.ProgramArguments;
import com.bradforj287.raytracer.engine.RayTracer;
import com.bradforj287.raytracer.engine.Tracer;
import com.bradforj287.raytracer.geometry.Matrix3d;
import com.bradforj287.raytracer.geometry.Ray3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.kdtree.KdTreeQueryStats;

public class Camera {
    private static int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    final private Dimension screenResolution;
    private Vector3d screenPosition;
    private Matrix3d rotation;
    private final SceneModel sceneModel;
    private double thetax = 0;
    private double thetay = 0;
    private double thetaz = 0;

    public Camera(Dimension screenResolution, SceneModel sceneModel) {
        this.sceneModel = sceneModel;
        this.screenResolution = screenResolution;
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

    public CameraTraceResult captureImage() {
        return traceScene();
    }

    private void clearImage(BufferedImage image) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setBackground(Color.red);
        g.setColor(Color.black);
        g.fillRect(0, 0, screenResolution.width, screenResolution.height);
    }

    private CameraTraceResult traceScene() {
        final BufferedImage image = new BufferedImage(screenResolution.width,
                screenResolution.height, BufferedImage.TYPE_INT_RGB);

        // first clear the screen to the background color
        clearImage(image);

        // create some arguments for the trace call
        final double xIncrement = ProgramArguments.SCREEN_WIDTH / screenResolution.getWidth();
        final double yIncrement = ProgramArguments.SCREEN_HEIGHT / screenResolution.getHeight();
        final double xstart = -1 * ProgramArguments.SCREEN_WIDTH / 2;
        final double ystart = -1 * ProgramArguments.SCREEN_HEIGHT / 2;
        final int threadWidth = screenResolution.width / NUM_THREADS;

        ArrayList<Future<KdTreeQueryStats>> futures = new ArrayList<>();

        // create the tasks
        for (int i = 0; i < NUM_THREADS; i++) {
            // calculate width and height for this thread
            int width = threadWidth;
            int height = screenResolution.height;

            if (i == NUM_THREADS - 1) {
                width += screenResolution.width % NUM_THREADS;
            }

            // pass this to thread
            final Rectangle threadRect = new Rectangle(i * threadWidth, 0,
                    width, height);

            Future<KdTreeQueryStats> future = Globals.executorService.submit(() -> {
                return iterateOverScreenRegion(image, threadRect, xIncrement,
                        yIncrement, xstart, ystart, screenPosition, rotation);
            });

            futures.add(future);
        }

        KdTreeQueryStats total = new KdTreeQueryStats();
        // wait for all tasks to finish
        for (Future<KdTreeQueryStats> f : futures) {
            try {
                KdTreeQueryStats stats = f.get();
                total.add(stats);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return new CameraTraceResult(total, image);
    }

    /**
     * Iterates over a subset of the screen resolution specified by Region. The
     * purpose of this is to distribute across threads.
     */
    private KdTreeQueryStats iterateOverScreenRegion(BufferedImage image, Rectangle region,
                                                     double xIncrement, double yIncrement,
                                                     double xStart, double yStart,
                                                     Vector3d screenPos, Matrix3d screenRotation) {
        Random rand = new Random();

        Tracer tracer = new RayTracer(sceneModel);

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
                    int color = tracer.getColorForRay(ray).asInt();

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
                int si = (screenResolution.width - 1) - i;
                image.setRGB(si, j, c1.getRGB());
            }
        }

        return tracer.getKdTreeQueryStats();
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
}

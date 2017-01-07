package com.bradforj287.raytracer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import com.bradforj287.raytracer.model.Camera;
import com.google.common.base.Stopwatch;

public class RayTracerPanel extends JPanel {
    private Camera camera;
    private BufferedImage sceneFrame;

    private Object paintLock = new Object();

    public RayTracerPanel(Camera camera) {
        this.camera = camera;
        this.setPreferredSize(camera.getScreenResolution());
        final double rotationRate = -1 * 5 * Math.PI / 180; // rotation per
        // second
        Thread animationTimer = new Thread(new Runnable() {
            double theta = 0;
            long lastFrameTime = System.currentTimeMillis();

            @Override
            public void run() {
                while (true) {
                    long delta = System.currentTimeMillis() - lastFrameTime;
                    double dDeltaSec = ((double) delta) / 1000;
                    theta = theta + dDeltaSec * rotationRate;

                    lastFrameTime = System.currentTimeMillis();
                    camera.setRotation(-1 * Math.PI/2, 0, theta);
                    BufferedImage img = camera.captureImage();
                    synchronized (paintLock) {
                        sceneFrame = img;
                    }

                    repaint();
                }
            }
        });

        if (ProgramArguments.ROTATION_ON) {
            animationTimer.start();
        } else {
            boolean perfTest = false;
            if (perfTest) {
                DescriptiveStatistics ds = new DescriptiveStatistics();
                for (int i = 1; i < 10; i++) {
                    Stopwatch sw = Stopwatch.createStarted();
                    camera.setRotation(0, 0, 0);
                    sceneFrame = camera.captureImage();
                    ds.addValue(sw.elapsed(TimeUnit.MILLISECONDS));
                    System.out.println("Frame render time (ms): " + sw.elapsed(TimeUnit.MILLISECONDS));
                }
                System.out.println(ds);
            } else {
                camera.setRotation(-1 * Math.PI/2, 0, 0);
                sceneFrame = camera.captureImage();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (sceneFrame == null) {
            return;
        }

        Rectangle paintRect = calculateTargetRectangle(camera.getScreenResolution());
        Graphics2D g2d = (Graphics2D) g.create(paintRect.x, paintRect.y,
                paintRect.width, paintRect.height);

        double scalex = ((double) paintRect.getWidth())
                / ((double) sceneFrame.getWidth());
        double scaley = ((double) paintRect.getHeight())
                / ((double) sceneFrame.getHeight());

        AffineTransform xform = AffineTransform
                .getScaleInstance(scalex, scaley);

        // we don't want to be painting this while
        // it is being rendered underneath us
        synchronized (paintLock) {
            g2d.drawImage(sceneFrame, xform, null);
        }
    }

    /**
     * This function takes a Dimension object representing the width and height
     * of the video data to be displayed. This function returns the largest
     * Rectangle such that it has the same aspect ratio of the image data and
     * fits inside of the screen bounds.
     *
     * @param rawImage
     * @return
     */
    private Rectangle calculateTargetRectangle(Dimension rawImage) {
        Rectangle theRect = getVisibleRect();

        double contentPanelHeight = theRect.getHeight();
        double contentPanelWidth = theRect.getWidth();
        double imageHeight = rawImage.getHeight();
        double imageWidth = rawImage.getWidth();
        double aspectRatioOfImage = imageHeight / imageWidth;

        double newImageHeight = 0;
        double newImageWidth = 0;

        if (aspectRatioOfImage >= 1) // longer than it is wide
        {
            newImageHeight = contentPanelHeight;
            newImageWidth = (1 / aspectRatioOfImage) * newImageHeight;

            if (newImageWidth > contentPanelWidth) {
                newImageWidth = contentPanelWidth;
                newImageHeight = aspectRatioOfImage * newImageWidth;
            }
        } else {
            newImageWidth = contentPanelWidth;
            newImageHeight = aspectRatioOfImage * newImageWidth;

            if (newImageHeight > contentPanelHeight) {
                newImageHeight = contentPanelHeight;
                newImageWidth = (1 / aspectRatioOfImage) * newImageHeight;
            }
        }

        int newHeight = (int) newImageHeight;
        int newWidth = (int) newImageWidth;
        Rectangle returnRect = new Rectangle();

        if (newImageHeight != 0 && newImageWidth != 0) {

            if (newWidth == theRect.width) {
                returnRect.x = 0;
                returnRect.y = (theRect.height - newHeight) / 2;
            } else {
                returnRect.y = 0;
                returnRect.x = (theRect.width - newWidth) / 2;
            }
            returnRect.width = newWidth;
            returnRect.height = newHeight;
            return returnRect;
        } else {
            return new Rectangle(0, 0, rawImage.width, rawImage.height);
        }
    }
}

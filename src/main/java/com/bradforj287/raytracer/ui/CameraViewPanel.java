package com.bradforj287.raytracer.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import com.bradforj287.raytracer.model.Camera;
import com.bradforj287.raytracer.model.CameraTraceResult;
import com.bradforj287.raytracer.model.kdtree.KdTreeQueryStats;
import com.bradforj287.raytracer.utils.MathUtils;
import com.bradforj287.raytracer.utils.VideoDataPointBuffer;

/**
 * renders the ray tracing camera to a canvas
 */
public class CameraViewPanel extends JPanel {
    final private Object paintLock = new Object();
    final private Camera camera;

    private BufferedImage sceneFrame;
    private VideoDataPointBuffer fpsBuffer = new VideoDataPointBuffer();

    public CameraViewPanel(Camera camera) {
        this.camera = camera;
        this.setPreferredSize(camera.getScreenResolution());
    }

    public BufferedImage getSceneFrame() {
        return sceneFrame;
    }

    public void captureImageWriteToFile(File outputfile) {
        CameraTraceResult result = camera.captureImage();
        try {
            ImageIO.write(result.getImage(), "png", outputfile);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        synchronized (paintLock) {
            sceneFrame = result.getImage();
        }
    }

    public void renderFrame() {
        CameraTraceResult result = camera.captureImage();

        synchronized (paintLock) {
            sceneFrame = result.getImage();
        }
        fpsBuffer.addToBuffer(System.currentTimeMillis());

        printStatsToImage(sceneFrame, result.getQueryStats());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (sceneFrame == null) {
            return;
        }

        Rectangle paintRect = MathUtils.calculateTargetRectangle(camera.getScreenResolution(), getVisibleRect());
        Graphics2D g2d = (Graphics2D) g.create(paintRect.x, paintRect.y,
                paintRect.width, paintRect.height);

        double scalex = (paintRect.getWidth())
                / ((double) sceneFrame.getWidth());
        double scaley = (paintRect.getHeight())
                / ((double) sceneFrame.getHeight());

        AffineTransform xform = AffineTransform
                .getScaleInstance(scalex, scaley);

        // we don't want to be painting this while
        // it is being rendered underneath us
        synchronized (paintLock) {
            g2d.drawImage(sceneFrame, xform, null);
        }
    }

    private void printStatsToImage(BufferedImage image, KdTreeQueryStats stats) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // print FPS
        String fpsString = Double.toString(fpsBuffer.getFramesPerSecond());
        drawString(g2d, "FPS=" + fpsString, 10, 10);

        // print rotation
        drawString(g2d, "rX=" + Double.toString(camera.getThetax()), 10, 20);
        drawString(g2d, "rY=" + Double.toString(camera.getThetay()), 10, 30);
        drawString(g2d, "rZ=" + Double.toString(camera.getThetaz()), 10, 40);

        long nodesPerRay = stats.getNodesVisited() / stats.getRaysCast();
        long shapeVisitsPerRay = stats.getShapesVisited() / stats.getRaysCast();
        String krays = Long.toString(stats.getRaysCast() / 1000);
        drawString(g2d, "#KRAYS=" + krays, 10, 50);

        String shapeVisits = Long.toString(shapeVisitsPerRay);
        drawString(g2d, "#SVST=" + shapeVisits, 10, 60);

        String nodeVisits = Long.toString(nodesPerRay);
        drawString(g2d, "#NVST=" + nodeVisits, 10, 70);
    }

    private void drawString(Graphics2D g2d, String s, int i, int j) {
        if (s.length() > 12) {
            s = s.substring(0, 11);
        }
        char[] charA = s.toCharArray();
        g2d.drawChars(charA, 0, charA.length, i, j);
    }

}

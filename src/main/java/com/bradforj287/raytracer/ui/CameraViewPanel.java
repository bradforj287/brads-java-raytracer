package com.bradforj287.raytracer.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.*;
import com.bradforj287.raytracer.model.Camera;
import com.bradforj287.raytracer.utils.DataPointBuffer;
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
    private DataPointBuffer kdTreeNodeVisitsBuffer = new DataPointBuffer(100);
    private DataPointBuffer shapeVisitsBuffer = new DataPointBuffer(100);

    public CameraViewPanel(Camera camera) {
        this.camera = camera;
        this.setPreferredSize(camera.getScreenResolution());
    }

    public void renderFrame() {
        BufferedImage newImg = camera.captureImage();
        synchronized (paintLock) {
            sceneFrame = newImg;
        }
        fpsBuffer.addToBuffer(System.currentTimeMillis());

        printStatsToImage(newImg);
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

    private void printStatsToImage(BufferedImage image) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // print FPS
        String fpsString = Double.toString(fpsBuffer.getFramesPerSecond());
        drawString(g2d, "FPS=" + fpsString, 10, 10);

        // print rotation
        drawString(g2d, "rX=" + Double.toString(camera.getThetax()), 10, 20);
        drawString(g2d, "rY=" + Double.toString(camera.getThetay()), 10, 30);
        drawString(g2d, "rZ=" + Double.toString(camera.getThetaz()), 10, 40);

        // print spacial structure query stats
        // node visits per ray
        String avgNodeVisits = Double.toString(kdTreeNodeVisitsBuffer.getAvg());
        drawString(g2d, "N_VST=" + avgNodeVisits, 10, 50);

        // shape visits per ray
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

}

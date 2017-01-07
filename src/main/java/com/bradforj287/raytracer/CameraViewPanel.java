package com.bradforj287.raytracer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.*;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.Camera;
import com.bradforj287.raytracer.utils.MathUtils;

/**
 * renders the ray tracing camera to a canvas
 */
public class CameraViewPanel extends JPanel {
    final private Object paintLock = new Object();
    final private Camera camera;

    private BufferedImage sceneFrame;

    public CameraViewPanel(Camera camera) {
        this.camera = camera;
        this.setPreferredSize(camera.getScreenResolution());
    }

    public void setCameraPosition(Vector3d cameraPosition) {
        this.camera.setScreenPosition(cameraPosition);
    }

    public void rotate(double tx, double ty, double tz) {
        camera.setRotation(tx, ty, tz);
    }

    public void renderFrame() {
        BufferedImage newImg = camera.captureImage();
        synchronized (paintLock) {
            sceneFrame = newImg;
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

        Rectangle paintRect = MathUtils.calculateTargetRectangle(camera.getScreenResolution(), getVisibleRect());
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
}

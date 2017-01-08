package com.bradforj287.raytracer.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import com.bradforj287.raytracer.ProgramArguments;
import com.bradforj287.raytracer.engine.Tracer;
import com.bradforj287.raytracer.geometry.Matrix3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.Camera;

public class CameraControlPanel extends JPanel {
    private Camera camera;
    private CameraViewPanel cameraViewPanel;
    private double velocity = 20;
    private Vector3d direction = new Vector3d(0, -1, 0);

    final double rotationRate = -1 * 5 * Math.PI / 180;

    public CameraControlPanel(Tracer rayTracer) {
        this.setLayout(new BorderLayout());
        this.setFocusable(true);
        this.requestFocusInWindow();

        // build camera
        Dimension sceneRes = ProgramArguments.SIZE_OF_SCENE;

        this.camera = new Camera(sceneRes, rayTracer);
        camera.setScreenPosition(ProgramArguments.DEFAULT_SCREEN_POSITION);
        camera.setRotation(-1 * Math.PI/2, 0, 0);

        cameraViewPanel = new CameraViewPanel(camera);
        cameraViewPanel.renderFrame();

        this.add(cameraViewPanel, BorderLayout.CENTER);

        registerKbListeners();
    }

    private void rerender() {
        cameraViewPanel.renderFrame();
        cameraViewPanel.repaint();
    }

    private void movePositionForward(double delta) {
        Vector3d dd = direction.multiply(delta);
        Vector3d newPos = camera.getScreenPosition().add(dd);
        camera.setScreenPosition(newPos);
        rerender();
    }

    private void rotateByDelta(double tx, double ty, double tz) {
        //rotate directional vector
        Matrix3d rot = Matrix3d.getRotationMatrix(tx, ty, tz);
        direction = direction.multiplyByMatrix(rot);
        double ntx = camera.getThetax() + tx;
        double nty = camera.getThetay() + ty;
        double ntz = camera.getThetaz() + tz;
        camera.setRotation(ntx, nty, ntz);
        rerender();
    }

    private void rotateLeft() {
        rotateByDelta(0, 0, -1*rotationRate);
    }

    private void rotateRight() {
        rotateByDelta(0, 0, rotationRate);
    }

    private void registerKbListeners() {
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                char key = e.getKeyChar();
                if (key == 'w') {
                    movePositionForward(velocity);
                } else if (key == 's') {
                    movePositionForward(-1*velocity);
                } else if (key == 'a') {
                    rotateLeft();
                } else if (key == 'd')  {
                    rotateRight();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
}

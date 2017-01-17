package com.bradforj287.raytracer.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import com.bradforj287.raytracer.ProgramArguments;
import com.bradforj287.raytracer.geometry.Matrix3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.Camera;
import com.bradforj287.raytracer.model.SceneModel;

public class CameraControlPanel extends JPanel {
    private Camera camera;
    private CameraViewPanel cameraViewPanel;
    private double velocity = 20;
    private Vector3d direction = new Vector3d(0, -1, 0);

    private Timer moveForwardTimer;
    private Timer moveBackwardTimer;
    private Timer turnLeftTimer;
    private Timer turnRightTimer;
    private Timer lookUpTimer;
    private Timer lookDownTimer;

    final double rotationRate = -1 * 5 * Math.PI / 180;

    public CameraControlPanel(SceneModel sceneModel) {
        this.setLayout(new BorderLayout());
        this.setFocusable(true);
        this.requestFocusInWindow();

        // build camera
        Dimension sceneRes = ProgramArguments.SIZE_OF_SCENE;

        this.camera = new Camera(sceneRes, sceneModel);
        camera.setScreenPosition(ProgramArguments.DEFAULT_SCREEN_POSITION);
        camera.setRotation(-1 * Math.PI / 2, 0, 0);
        //camera.setScreenPosition(new Vector3d(264.47257159,190.827145105804, 137.471873229963));
        //camera.setRotation(-1.22173, 0, -0.95993);
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
        //System.out.println(newPos.toString());
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
        rotateByDelta(0, 0, -1 * rotationRate);
    }

    private void rotateUp() {
        rotateByDelta(rotationRate, 0, 0);
    }

    private void rotateDown() {
        rotateByDelta(-1 * rotationRate, 0, 0);
    }

    private void rotateRight() {
        rotateByDelta(0, 0, rotationRate);
    }

    private void registerKbListeners() {
        final int delay = 10;
        this.moveForwardTimer = new Timer(delay, a -> movePositionForward(velocity));
        this.moveBackwardTimer = new Timer(delay, a -> movePositionForward(-1 * velocity));
        this.turnLeftTimer = new Timer(delay, a -> rotateLeft());
        this.turnRightTimer = new Timer(delay, a -> rotateRight());
        this.lookUpTimer = new Timer(delay, a -> rotateUp());
        this.lookDownTimer = new Timer(delay, a -> rotateDown());

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                char key = e.getKeyChar();
                int keycode = e.getKeyCode();
                if (key == 'w') {
                    moveForwardTimer.start();
                } else if (key == 's') {
                    moveBackwardTimer.start();
                } else if (key == 'a') {
                    turnLeftTimer.start();
                } else if (key == 'd') {
                    turnRightTimer.start();
                } else if (keycode == KeyEvent.VK_UP) {
                    lookUpTimer.start();
                } else if (keycode == KeyEvent.VK_DOWN) {
                    lookDownTimer.start();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char key = e.getKeyChar();
                int keycode = e.getKeyCode();

                if (key == 'w') {
                    moveForwardTimer.stop();
                } else if (key == 's') {
                    moveBackwardTimer.stop();
                } else if (key == 'a') {
                    turnLeftTimer.stop();
                } else if (key == 'd') {
                    turnRightTimer.stop();
                } else if (keycode == KeyEvent.VK_UP) {
                    lookUpTimer.stop();
                } else if (keycode == KeyEvent.VK_DOWN) {
                    lookDownTimer.stop();
                }
            }
        });
    }
}

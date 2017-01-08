package com.bradforj287.raytracer.ui;

import java.awt.*;
import javax.swing.*;
import com.bradforj287.raytracer.ProgramArguments;
import com.bradforj287.raytracer.engine.Tracer;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.Camera;

public class CameraControlPanel extends JPanel {
    private Camera camera;

    public CameraControlPanel(Tracer rayTracer) {
        this.setLayout(new BorderLayout());

        // build camera
        Dimension sceneRes = ProgramArguments.SIZE_OF_SCENE;
        Vector3d screenPos = ProgramArguments.DEFAULT_SCREEN_POSITION;

        this.camera = new Camera(sceneRes, rayTracer);
        camera.setScreenPosition(screenPos);
        camera.setRotation(-1 * Math.PI/2, 0, 0);

        CameraViewPanel cameraViewPanel = new CameraViewPanel(camera);
        cameraViewPanel.renderFrame();

        this.add(cameraViewPanel, BorderLayout.CENTER);
    }
}

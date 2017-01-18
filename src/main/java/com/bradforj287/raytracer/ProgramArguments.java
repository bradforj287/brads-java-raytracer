package com.bradforj287.raytracer;

import java.awt.*;
import com.bradforj287.raytracer.geometry.Vector3d;

public class ProgramArguments {
    public static final boolean ROTATION_ON = false;

    // UI Arguments for window sizing
    public static final Dimension SIZE_OF_WINDOW = new Dimension(320, 240);
    public static final Dimension SIZE_OF_SCENE = new Dimension(3200, 2400);

    // Ray tracing arguments
    public final static double EYE_CAMERA_DISTANCE = 5;
    public final static Vector3d DEFAULT_SCREEN_POSITION = new Vector3d(0, 500, 0);
    public final static double SCREEN_WIDTH = 4;
    public final static double SCREEN_HEIGHT = 3;
    public final static double AMBIENT_LIGHT = .20;
    public final static double LIGHT_INTENSITY = 1.0;
    public final static int ANTIALIASING_SAMPLES = 16;
    public final static Vector3d LIGHT_LOCATION = new Vector3d(400, 400, 400);
}

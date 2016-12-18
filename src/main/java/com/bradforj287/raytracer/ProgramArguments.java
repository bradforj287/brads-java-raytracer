package com.bradforj287.raytracer;

import java.awt.*;
import com.bradforj287.raytracer.shapes.Vector3D;

public class ProgramArguments {
    public static final boolean ROTATION_ON = true;

    // UI Arguments for window sizing
    public static final Dimension SIZE_OF_WINDOW = new Dimension(320, 240);
    public static final Dimension SIZE_OF_SCENE = new Dimension(320, 240);

    // Ray tracing arguments
    public final static Vector3D EYE_POSITION = new Vector3D(0, 0, 300);
    public final static Vector3D SCREEN_POSITION = new Vector3D(0, 0, 290);
    public final static double SCREEN_WIDTH = 8;
    public final static double SCREEN_HEIGHT = 6;
    public final static double AMBIENT_LIGHT = .20;
    public final static int ANTIALIASING_SAMPLES = 1;
    public final static Vector3D LIGHT_LOCATION = new Vector3D(100, 100, 100);
}

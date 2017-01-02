package com.bradforj287.raytracer;

import java.awt.*;
import com.bradforj287.raytracer.geometry.Vector3d;

public class ProgramArguments {
    public static final boolean ROTATION_ON = false;

    // UI Arguments for window sizing
    public static final Dimension SIZE_OF_WINDOW = new Dimension(320, 240);
    public static final Dimension SIZE_OF_SCENE = new Dimension(320, 240);

    // Ray tracing arguments
    public final static Vector3d EYE_POSITION = new Vector3d(0, 0, 300);
    public final static Vector3d SCREEN_POSITION = new Vector3d(0, 0, 290);
    public final static double SCREEN_WIDTH = 8;
    public final static double SCREEN_HEIGHT = 6;
    public final static double AMBIENT_LIGHT = .10;
    public final static int ANTIALIASING_SAMPLES = 1;
    public final static Vector3d LIGHT_LOCATION = new Vector3d(100, 100, 100);
}

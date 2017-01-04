package com.bradforj287.raytracer.engine;

import com.bradforj287.raytracer.geometry.Ray3d;
import com.bradforj287.raytracer.geometry.RgbColor;

public interface Tracer {
    RgbColor getColorForRay(final Ray3d ray);
}

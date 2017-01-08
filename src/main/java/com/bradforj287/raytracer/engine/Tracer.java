package com.bradforj287.raytracer.engine;

import com.bradforj287.raytracer.geometry.Ray3d;
import com.bradforj287.raytracer.geometry.RgbColor;
import com.bradforj287.raytracer.model.kdtree.KdTreeQueryStats;

public interface Tracer {
    RgbColor getColorForRay(final Ray3d ray);
    KdTreeQueryStats getKdTreeQueryStats();
}

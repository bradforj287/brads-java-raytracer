package com.bradforj287.raytracer.model;

import java.awt.image.BufferedImage;
import com.bradforj287.raytracer.model.kdtree.KdTreeQueryStats;

public class CameraTraceResult {
    final private KdTreeQueryStats queryStats;
    final private BufferedImage image;

    public CameraTraceResult(KdTreeQueryStats queryStats, BufferedImage image) {
        this.queryStats = queryStats;
        this.image = image;
    }

    public KdTreeQueryStats getQueryStats() {
        return queryStats;
    }

    public BufferedImage getImage() {
        return image;
    }
}

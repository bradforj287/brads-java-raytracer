package com.bradforj287.raytracer.geometry;

import com.google.common.base.Preconditions;

public class Box3D {
    Plane3D[] planes;

    public Box3D(Plane3D[] planes) {
        Preconditions.checkNotNull(planes);
        Preconditions.checkArgument(planes.length == 6);
        this.planes = planes;
    }
}

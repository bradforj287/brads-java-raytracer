package com.bradforj287.raytracer.model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import com.bradforj287.raytracer.geometry.Triangle3d;
import com.bradforj287.raytracer.geometry.Vector3D;

public class VertexIterator implements Iterator<Vector3D> {
    private final SceneModel scene;
    private int index = 0;
    private int vIndex = 0;

    VertexIterator(SceneModel scene) {
        this.scene = scene;
    }

    @Override
    public boolean hasNext() {
        return (index < scene.size());
    }

    @Override
    public Vector3D next() {

        Triangle3d tri = scene.getShape(index);

        Vector3D retVal = null;
        if (vIndex == 0) {
            retVal = tri.v1;
        } else if (vIndex == 1) {
            retVal = tri.v2;
        } else {
            retVal = tri.v3;
        }

        vIndex = vIndex % 3;
        if (vIndex == 0) {
            index++;
        }
        return retVal;
    }

    @Override
    public void remove() {
        throw new NotImplementedException();
    }

}

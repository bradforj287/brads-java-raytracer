package com.bradforj287.raytracer.utils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import com.bradforj287.raytracer.geometry.AxisAlignedBoundingBox3d;
import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.google.common.collect.MinMaxPriorityQueue;

/**
 * queue of Shapes. Insertion and removal is O(logN) but getting a bounding box at
 * any time is O(1).
 *
 * Non thread safe
 */
public class ShapeBoundsQueue {
    private ArrayDeque<Shape3d> shapes;
    private MinMaxPriorityQueue<Shape3d> xQueue;
    private MinMaxPriorityQueue<Shape3d> yQueue;
    private MinMaxPriorityQueue<Shape3d> zQueue;

    public ShapeBoundsQueue() {
        shapes = new ArrayDeque<>();
        xQueue = MinMaxPriorityQueue.
                orderedBy(new Comparator<Shape3d>() {
                    @Override
                    public int compare(Shape3d v1, Shape3d v2) {
                        Double aa = v1.getCentroid().x;
                        Double bb = v2.getCentroid().x;
                        return aa.compareTo(bb);
                    }
                }).create();
        yQueue = MinMaxPriorityQueue.
                orderedBy(new Comparator<Shape3d>() {
                    @Override
                    public int compare(Shape3d v1, Shape3d v2) {
                        Double aa = v1.getCentroid().y;
                        Double bb = v2.getCentroid().y;
                        return aa.compareTo(bb);
                    }
                }).create();
        zQueue = MinMaxPriorityQueue.
                orderedBy(new Comparator<Shape3d>() {
                    @Override
                    public int compare(Shape3d v1, Shape3d v2) {
                        Double aa = v1.getCentroid().z;
                        Double bb = v2.getCentroid().z;
                        return aa.compareTo(bb);
                    }
                }).create();
    }

    public void add(Shape3d s) {
        shapes.add(s);
        xQueue.add(s);
        yQueue.add(s);
        zQueue.add(s);
    }

    public void addAll(Collection<Shape3d> shapes) {
        this.shapes.addAll(shapes);
        xQueue.addAll(shapes);
        yQueue.addAll(shapes);
        zQueue.addAll(shapes);
    }


    public Shape3d remove() {
        Shape3d s = shapes.remove();
        xQueue.remove(s);
        yQueue.remove(s);
        zQueue.remove(s);
        return s;
    }

    public Vector3d getMax() {
        double mx = xQueue.peekLast().getCentroid().x;
        double my = yQueue.peekLast().getCentroid().y;
        double mz = zQueue.peekLast().getCentroid().z;
        return new Vector3d(mx, my, mz);
    }

    public Vector3d getMin() {
        double mx = xQueue.peekFirst().getCentroid().x;
        double my = yQueue.peekFirst().getCentroid().y;
        double mz = zQueue.peekFirst().getCentroid().z;
        return new Vector3d(mx, my, mz);
    }

    public AxisAlignedBoundingBox3d getBoundingBox() {
        return new AxisAlignedBoundingBox3d(getMin(), getMax());
    }

    public int size() {
        return xQueue.size();
    }

    public boolean isEmpty() {
        return xQueue.isEmpty();
    }

}

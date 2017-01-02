package com.bradforj287.raytracer;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.geometry.Triangle3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.SceneModel;
import com.bradforj287.raytracer.parser.ObjFileParser;
import com.bradforj287.raytracer.utils.Utils;

public class MainEntry {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brad's Ray Tracer");

        final File objFile = Utils.tryGetResourceFile("teapot.obj");
        final List<Shape3d> shapes = ObjFileParser.parseObjFile(objFile);
        final List<Triangle3d> boundingBoxTriangles = getBoundingBox();

        //install bounding box
        shapes.addAll(boundingBoxTriangles);

        SceneModel model = new SceneModel(shapes);

        RayTracerPanel r = new RayTracerPanel(model, ProgramArguments.SIZE_OF_SCENE);

        frame.setContentPane(new JScrollPane(r));
        frame.setPreferredSize(ProgramArguments.SIZE_OF_WINDOW);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static List<Triangle3d> getBoundingBox() {

        List<Triangle3d> scene = new ArrayList<>();
        // create cube vertices

        double cubeWidth = 2000;

        int boundingColor = Color.blue.darker().getRGB();

        // top vertices
        Vector3d top1 = new Vector3d(0, 0, cubeWidth);
        Vector3d top2 = new Vector3d(cubeWidth, 0, cubeWidth);
        Vector3d top3 = new Vector3d(0, cubeWidth, cubeWidth);
        Vector3d top4 = new Vector3d(cubeWidth, cubeWidth, cubeWidth);

        // bottom vertices
        Vector3d bottom1 = new Vector3d(0, 0, 0);
        Vector3d bottom2 = new Vector3d(cubeWidth, 0, 0);
        Vector3d bottom3 = new Vector3d(0, cubeWidth, 0);
        Vector3d bottom4 = new Vector3d(cubeWidth, cubeWidth, 0);

        // top triangles
        scene.add(new Triangle3d(top1, top2, top3, boundingColor));
        scene.add(flipNormal(new Triangle3d(top2, top3, top4,
                boundingColor)));

        boundingColor = Color.blue.getRGB();
        // bottom triangles
        scene.add(new Triangle3d(bottom1, bottom2, bottom3,
                boundingColor));
        scene.add(flipNormal(new Triangle3d(bottom2, bottom3, bottom4,
                boundingColor)));

        boundingColor = Color.gray.getRGB();
        // left triangles
        scene.add(new Triangle3d(top1, top3, bottom1, boundingColor));
        scene.add(flipNormal(new Triangle3d(bottom1, bottom3, top3,
                boundingColor)));

        // right triangles
        scene.add(new Triangle3d(top2, top4, bottom2, boundingColor));
        scene.add(flipNormal(new Triangle3d(bottom2, bottom4, top4,
                boundingColor)));

        boundingColor = Color.green.getRGB();

        // back triangles
        scene.add(new Triangle3d(top1, top2, bottom1, boundingColor));
        scene.add(flipNormal(new Triangle3d(bottom1, bottom2, top2,
                boundingColor)));

        // front triangles
        scene.add(new Triangle3d(top3, top4, bottom3, boundingColor));
        scene.add(flipNormal(new Triangle3d(bottom3, bottom4, top4,
                boundingColor)));

        double offset = (cubeWidth / 2);
        Vector3d offsetV = new Vector3d(offset, offset, offset);

        scene = scene.stream().map(t -> {
            Vector3d v1 = t.v1.subtract(offsetV);
            Vector3d v2 =t.v2.subtract(offsetV);
            Vector3d v3 =t.v3.subtract(offsetV);
            return new Triangle3d(v1, v2, v3, t.getColor());
        }).collect(Collectors.toList());

        scene = scene.stream()
                .map(triangle3d -> correctNormal(triangle3d))
                .collect(Collectors.toList());

        scene = scene.stream()
                .map(triangle3d -> triangle3d.getFlippedNormal())
                .collect(Collectors.toList());

        return scene;
    }

    private static Triangle3d flipNormal(Triangle3d triangle3d) {
        return triangle3d.getFlippedNormal();
    }

    private static Triangle3d correctNormal(Triangle3d tri) {
        Vector3d fromOriginToTriangle = tri.v1.getUnitVector();
        Vector3d normal = tri.getNormalVector();
        if (normal.dot(fromOriginToTriangle) < 0) {
            return tri.getFlippedNormal();
        } else {
            return tri;
        }
    }
}

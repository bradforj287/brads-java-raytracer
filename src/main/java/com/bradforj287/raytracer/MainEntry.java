package com.bradforj287.raytracer;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.*;
import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.kdtree.KDTreeImpl;
import com.bradforj287.raytracer.model.SceneModel;
import com.bradforj287.raytracer.parser.ObjFileParser;
import com.bradforj287.raytracer.geometry.Triangle3d;
import com.bradforj287.raytracer.utils.Utils;

public class MainEntry {

    public static void main(String[] args) {

        File objFile = Utils.tryGetResourceFile("teapot.obj");

        JFrame frame = new JFrame("Brad's Ray Tracer");

        SceneModel model = null;
        try {
            model = ObjFileParser.parseObjFile(objFile);

            // quick test
            ArrayList<Shape3d> shapes = new ArrayList<>();
            model.getShapes().forEach(tri -> shapes.add(tri));

            KDTreeImpl kdtree = new KDTreeImpl(shapes);
            System.out.println("done test");


            // end test
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        installBoundingBox(model);

        RayTracerPanel r = new RayTracerPanel(model, ProgramArguments.SIZE_OF_SCENE);

        frame.setContentPane(new JScrollPane(r));
        frame.setPreferredSize(ProgramArguments.SIZE_OF_WINDOW);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static void installBoundingBox(SceneModel model) {

        SceneModel scene = new SceneModel();
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
        scene.addShape(new Triangle3d(top1, top2, top3, boundingColor));
        scene.addShape(flipNormal(new Triangle3d(top2, top3, top4,
                boundingColor)));

        boundingColor = Color.blue.getRGB();
        // bottom triangles
        scene
                .addShape(new Triangle3d(bottom1, bottom2, bottom3,
                        boundingColor));
        scene.addShape(flipNormal(new Triangle3d(bottom2, bottom3, bottom4,
                boundingColor)));

        boundingColor = Color.gray.getRGB();
        // left triangles
        scene.addShape(new Triangle3d(top1, top3, bottom1, boundingColor));
        scene.addShape(flipNormal(new Triangle3d(bottom1, bottom3, top3,
                boundingColor)));

        // right triangles
        scene.addShape(new Triangle3d(top2, top4, bottom2, boundingColor));
        scene.addShape(flipNormal(new Triangle3d(bottom2, bottom4, top4,
                boundingColor)));

        boundingColor = Color.green.getRGB();

        // back triangles
        scene.addShape(new Triangle3d(top1, top2, bottom1, boundingColor));
        scene.addShape(flipNormal(new Triangle3d(bottom1, bottom2, top2,
                boundingColor)));

        // front triangles
        scene.addShape(new Triangle3d(top3, top4, bottom3, boundingColor));
        scene.addShape(flipNormal(new Triangle3d(bottom3, bottom4, top4,
                boundingColor)));

        double offset = (cubeWidth / 2);
        Vector3d offsetV = new Vector3d(offset, offset, offset);
        for (int i = 0; i < scene.size(); i++) {
            Triangle3d t = scene.getShape(i);
            t.v1.subtract(offsetV);
            t.v2.subtract(offsetV);
            t.v3.subtract(offsetV);
        }

        getNormalsInCorrectDirection(scene);

        for (int i = 0; i < scene.size(); i++) {
            scene.getShape(i).flipNormal();
        }

        model.addShapes(scene);

    }

    private static Triangle3d flipNormal(Triangle3d t) {
        t.flipNormal();
        return t;
    }

    private static SceneModel createSceneForRayTrace() {

        SceneModel scene = new SceneModel();

        // define icosahedron
        Vector3d[] figurepoints = new Vector3d[12];

        figurepoints[0] = new Vector3d(-0.692, 0, 0.427);
        figurepoints[1] = new Vector3d(0.0, 0.427, -0.692);
        figurepoints[2] = new Vector3d(0.0, 0.427, 0.692);
        figurepoints[3] = new Vector3d(0.692, 0.0, -0.427);
        figurepoints[4] = new Vector3d(-0.427, -0.692, 0.0);
        figurepoints[5] = new Vector3d(-0.427, 0.692, 0.0);
        figurepoints[6] = new Vector3d(0.0, -0.427, 0.692);
        figurepoints[7] = new Vector3d(0.427, 0.692, 0.0);
        figurepoints[8] = new Vector3d(0.0, -0.427, -0.692);
        figurepoints[9] = new Vector3d(0.692, 0.0, 0.427);
        figurepoints[10] = new Vector3d(0.427, -0.692, 0.0);
        figurepoints[11] = new Vector3d(-0.692, 0.0, -0.427);
        double scaleFactor = 1;
        for (int i = 0; i < 12; i++) {
            figurepoints[i].x = figurepoints[i].x * scaleFactor;
            figurepoints[i].y = figurepoints[i].y * scaleFactor;
            figurepoints[i].z = figurepoints[i].z * scaleFactor;
        }

        int tempcolor = 123123;
        scene.addShape(new Triangle3d(figurepoints[9], figurepoints[2],
                figurepoints[6], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[1], figurepoints[5],
                figurepoints[11], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[11], figurepoints[1],
                figurepoints[8], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[0], figurepoints[11],
                figurepoints[4], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[3], figurepoints[7],
                figurepoints[1], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[3], figurepoints[1],
                figurepoints[8], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[9], figurepoints[3],
                figurepoints[7], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[0], figurepoints[2],
                figurepoints[6], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[4], figurepoints[6],
                figurepoints[10], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[1], figurepoints[7],
                figurepoints[5], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[7], figurepoints[2],
                figurepoints[5], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[8], figurepoints[10],
                figurepoints[3], tempcolor));

        scene.addShape(new Triangle3d(figurepoints[4], figurepoints[11],
                figurepoints[8], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[9], figurepoints[2],
                figurepoints[7], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[10], figurepoints[6],
                figurepoints[9], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[0], figurepoints[11],
                figurepoints[5], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[0], figurepoints[2],
                figurepoints[5], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[8], figurepoints[10],
                figurepoints[4], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[3], figurepoints[9],
                figurepoints[10], tempcolor));
        scene.addShape(new Triangle3d(figurepoints[6], figurepoints[4],
                figurepoints[0], tempcolor));

        getNormalsInCorrectDirection(scene);

        return scene;
    }

    private static void getNormalsInCorrectDirection(SceneModel scene) {

        // get normals to point in right direction
        for (int i = 0; i < scene.size(); i++) {
            Vector3d fromOriginToTriangle = scene.getShape(i).v1;
            fromOriginToTriangle = fromOriginToTriangle.getUnitVector();
            Vector3d normal = ((Triangle3d) scene.getShape(i))
                    .getNormalVector();
            if (com.bradforj287.raytracer.geometry.Vector3d.dotProduct(normal, fromOriginToTriangle) < 0) {
                // flip two verticies;

                scene.getShape(i).flipNormal();
            }
        }
    }
}

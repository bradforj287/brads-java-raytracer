package com.bradforj287.raytracer;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;
import com.bradforj287.raytracer.model.SceneModel3D;
import com.bradforj287.raytracer.parser.ObjFileParser;
import com.bradforj287.raytracer.geometry.Triangle3D;
import com.bradforj287.raytracer.geometry.Vector3D;
import com.bradforj287.raytracer.utils.Utils;

public class MainEntry {

    public static void main(String[] args) {

        File objFile = Utils.tryGetResourceFile("teapot.obj");

        JFrame frame = new JFrame("Brad's Ray Tracer");

        SceneModel3D model = null;
        try {
            model = ObjFileParser.parseObjFile(objFile);
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

    private static void installBoundingBox(SceneModel3D model) {

        SceneModel3D scene = new SceneModel3D();
        // create cube vertices

        double cubeWidth = 2000;

        int boundingColor = Color.blue.darker().getRGB();

        // top vertices
        Vector3D top1 = new Vector3D(0, 0, cubeWidth);
        Vector3D top2 = new Vector3D(cubeWidth, 0, cubeWidth);
        Vector3D top3 = new Vector3D(0, cubeWidth, cubeWidth);
        Vector3D top4 = new Vector3D(cubeWidth, cubeWidth, cubeWidth);

        // bottom vertices
        Vector3D bottom1 = new Vector3D(0, 0, 0);
        Vector3D bottom2 = new Vector3D(cubeWidth, 0, 0);
        Vector3D bottom3 = new Vector3D(0, cubeWidth, 0);
        Vector3D bottom4 = new Vector3D(cubeWidth, cubeWidth, 0);

        // top triangles
        scene.addShape(new Triangle3D(top1, top2, top3, boundingColor));
        scene.addShape(flipNormal(new Triangle3D(top2, top3, top4,
                boundingColor)));

        boundingColor = Color.blue.getRGB();
        // bottom triangles
        scene
                .addShape(new Triangle3D(bottom1, bottom2, bottom3,
                        boundingColor));
        scene.addShape(flipNormal(new Triangle3D(bottom2, bottom3, bottom4,
                boundingColor)));

        boundingColor = Color.gray.getRGB();
        // left triangles
        scene.addShape(new Triangle3D(top1, top3, bottom1, boundingColor));
        scene.addShape(flipNormal(new Triangle3D(bottom1, bottom3, top3,
                boundingColor)));

        // right triangles
        scene.addShape(new Triangle3D(top2, top4, bottom2, boundingColor));
        scene.addShape(flipNormal(new Triangle3D(bottom2, bottom4, top4,
                boundingColor)));

        boundingColor = Color.green.getRGB();

        // back triangles
        scene.addShape(new Triangle3D(top1, top2, bottom1, boundingColor));
        scene.addShape(flipNormal(new Triangle3D(bottom1, bottom2, top2,
                boundingColor)));

        // front triangles
        scene.addShape(new Triangle3D(top3, top4, bottom3, boundingColor));
        scene.addShape(flipNormal(new Triangle3D(bottom3, bottom4, top4,
                boundingColor)));

        double offset = (cubeWidth / 2);
        Vector3D offsetV = new Vector3D(offset, offset, offset);
        for (int i = 0; i < scene.size(); i++) {
            Triangle3D t = scene.getShape(i);
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

    private static Triangle3D flipNormal(Triangle3D t) {
        t.flipNormal();
        return t;
    }

    private static SceneModel3D createSceneForRayTrace() {

        SceneModel3D scene = new SceneModel3D();

        // define icosahedron
        Vector3D[] figurepoints = new Vector3D[12];

        figurepoints[0] = new Vector3D(-0.692, 0, 0.427);
        figurepoints[1] = new Vector3D(0.0, 0.427, -0.692);
        figurepoints[2] = new Vector3D(0.0, 0.427, 0.692);
        figurepoints[3] = new Vector3D(0.692, 0.0, -0.427);
        figurepoints[4] = new Vector3D(-0.427, -0.692, 0.0);
        figurepoints[5] = new Vector3D(-0.427, 0.692, 0.0);
        figurepoints[6] = new Vector3D(0.0, -0.427, 0.692);
        figurepoints[7] = new Vector3D(0.427, 0.692, 0.0);
        figurepoints[8] = new Vector3D(0.0, -0.427, -0.692);
        figurepoints[9] = new Vector3D(0.692, 0.0, 0.427);
        figurepoints[10] = new Vector3D(0.427, -0.692, 0.0);
        figurepoints[11] = new Vector3D(-0.692, 0.0, -0.427);
        double scaleFactor = 1;
        for (int i = 0; i < 12; i++) {
            figurepoints[i].x = figurepoints[i].x * scaleFactor;
            figurepoints[i].y = figurepoints[i].y * scaleFactor;
            figurepoints[i].z = figurepoints[i].z * scaleFactor;
        }

        int tempcolor = 123123;
        scene.addShape(new Triangle3D(figurepoints[9], figurepoints[2],
                figurepoints[6], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[1], figurepoints[5],
                figurepoints[11], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[11], figurepoints[1],
                figurepoints[8], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[0], figurepoints[11],
                figurepoints[4], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[3], figurepoints[7],
                figurepoints[1], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[3], figurepoints[1],
                figurepoints[8], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[9], figurepoints[3],
                figurepoints[7], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[0], figurepoints[2],
                figurepoints[6], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[4], figurepoints[6],
                figurepoints[10], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[1], figurepoints[7],
                figurepoints[5], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[7], figurepoints[2],
                figurepoints[5], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[8], figurepoints[10],
                figurepoints[3], tempcolor));

        scene.addShape(new Triangle3D(figurepoints[4], figurepoints[11],
                figurepoints[8], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[9], figurepoints[2],
                figurepoints[7], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[10], figurepoints[6],
                figurepoints[9], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[0], figurepoints[11],
                figurepoints[5], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[0], figurepoints[2],
                figurepoints[5], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[8], figurepoints[10],
                figurepoints[4], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[3], figurepoints[9],
                figurepoints[10], tempcolor));
        scene.addShape(new Triangle3D(figurepoints[6], figurepoints[4],
                figurepoints[0], tempcolor));

        getNormalsInCorrectDirection(scene);

        return scene;
    }

    private static void getNormalsInCorrectDirection(SceneModel3D scene) {

        // get normals to point in right direction
        for (int i = 0; i < scene.size(); i++) {
            Vector3D fromOriginToTriangle = scene.getShape(i).v1;
            fromOriginToTriangle = fromOriginToTriangle.getUnitVector();
            Vector3D normal = ((Triangle3D) scene.getShape(i))
                    .getNormalVector();
            if (Vector3D.dotProduct(normal, fromOriginToTriangle) < 0) {
                // flip two verticies;

                scene.getShape(i).flipNormal();
            }
        }
    }
}

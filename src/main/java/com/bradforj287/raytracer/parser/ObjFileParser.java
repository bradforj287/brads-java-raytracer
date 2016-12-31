package com.bradforj287.raytracer.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.bradforj287.raytracer.model.SceneModel3D;
import com.bradforj287.raytracer.geometry.Triangle3D;
import com.bradforj287.raytracer.geometry.Vector3D;

public class ObjFileParser {

    public static SceneModel3D parseObjFile(File file) throws FileNotFoundException {
        SceneModel3D m = new SceneModel3D();

        FileInputStream in = new FileInputStream(file);

        Scanner scanner = new Scanner(in);

        ArrayList<Vector3D> verticies = new ArrayList<Vector3D>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            StringTokenizer tok = new StringTokenizer(line, " ");
            if (!tok.hasMoreTokens()) {
                continue;
            }
            String firstToken = tok.nextToken();

            if (firstToken.equals("v")) {
                double x = Double.parseDouble(tok.nextToken());
                double y = Double.parseDouble(tok.nextToken());
                double z = Double.parseDouble(tok.nextToken());
                verticies.add(new Vector3D(x, y, z));

            } else if (firstToken.equals("f")) {
                int i = Integer.parseInt(tok.nextToken()) - 1;
                int j = Integer.parseInt(tok.nextToken()) - 1;
                int k = Integer.parseInt(tok.nextToken()) - 1;

                Vector3D v1 = new Vector3D(verticies.get(i));
                Vector3D v2 = new Vector3D(verticies.get(j));
                Vector3D v3 = new Vector3D(verticies.get(k));

                m.addShape(new Triangle3D(v1, v2, v3, 123123));
            }
        }
        return m;
    }
}

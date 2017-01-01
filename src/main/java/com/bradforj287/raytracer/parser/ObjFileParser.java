package com.bradforj287.raytracer.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.SceneModel;
import com.bradforj287.raytracer.geometry.Triangle3d;

public class ObjFileParser {

    public static SceneModel parseObjFile(File file) throws FileNotFoundException {
        SceneModel m = new SceneModel();

        FileInputStream in = new FileInputStream(file);

        Scanner scanner = new Scanner(in);

        ArrayList<Vector3d> verticies = new ArrayList<Vector3d>();
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
                verticies.add(new Vector3d(x, y, z));

            } else if (firstToken.equals("f")) {
                int i = Integer.parseInt(tok.nextToken()) - 1;
                int j = Integer.parseInt(tok.nextToken()) - 1;
                int k = Integer.parseInt(tok.nextToken()) - 1;

                Vector3d v1 = new Vector3d(verticies.get(i));
                Vector3d v2 = new Vector3d(verticies.get(j));
                Vector3d v3 = new Vector3d(verticies.get(k));

                m.addShape(new Triangle3d(v1, v2, v3, 123123));
            }
        }
        return m;
    }
}

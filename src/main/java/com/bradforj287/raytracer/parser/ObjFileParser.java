package com.bradforj287.raytracer.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.geometry.Surface;
import com.bradforj287.raytracer.geometry.Triangle3d;
import com.bradforj287.raytracer.geometry.Vector3d;

public class ObjFileParser {

    public static List<Shape3d> parseObjFile(File file) {
        try {
            List<Shape3d> r = new ArrayList<>();

            FileInputStream in = new FileInputStream(file);

            Scanner scanner = new Scanner(in);

            ArrayList<Vector3d> verticies = new ArrayList<>();
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
                    Surface surface = new Surface();
                    surface.setColor(123123);
                    surface.setReflective(true);
                    Triangle3d tri = new Triangle3d(v1, v2, v3, surface);
                    r.add(tri.getFlippedNormal());
                }
            }
            return r;
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}

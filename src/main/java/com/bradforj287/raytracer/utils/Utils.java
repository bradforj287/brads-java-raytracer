package com.bradforj287.raytracer.utils;

import java.io.File;
import java.net.URL;
import com.google.common.base.Preconditions;

public class Utils {
    public static File tryGetResourceFile(String path) {
        Preconditions.checkArgument(path != null);

        ClassLoader classLoader = Utils.class.getClassLoader();
        URL urlPath = classLoader.getResource(path);
        if (urlPath == null) {
            return null;
        }
        return new File(urlPath.getFile());
    }
}

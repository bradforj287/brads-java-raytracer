package com.bradforj287.raytracer.utils;

import java.awt.*;

public class MathUtils {

    /**
     * This function takes a Dimension object representing the width and height
     * of the video data to be displayed. This function returns the largest
     * Rectangle such that it has the same aspect ratio of the image data and
     * fits inside of the screen bounds.
     *
     * @param rawImage
     * @return
     */
    public static Rectangle calculateTargetRectangle(Dimension rawImage,  Rectangle theRect) {
        double contentPanelHeight = theRect.getHeight();
        double contentPanelWidth = theRect.getWidth();
        double imageHeight = rawImage.getHeight();
        double imageWidth = rawImage.getWidth();
        double aspectRatioOfImage = imageHeight / imageWidth;

        double newImageHeight = 0;
        double newImageWidth = 0;

        if (aspectRatioOfImage >= 1) // longer than it is wide
        {
            newImageHeight = contentPanelHeight;
            newImageWidth = (1 / aspectRatioOfImage) * newImageHeight;

            if (newImageWidth > contentPanelWidth) {
                newImageWidth = contentPanelWidth;
                newImageHeight = aspectRatioOfImage * newImageWidth;
            }
        } else {
            newImageWidth = contentPanelWidth;
            newImageHeight = aspectRatioOfImage * newImageWidth;

            if (newImageHeight > contentPanelHeight) {
                newImageHeight = contentPanelHeight;
                newImageWidth = (1 / aspectRatioOfImage) * newImageHeight;
            }
        }

        int newHeight = (int) newImageHeight;
        int newWidth = (int) newImageWidth;
        Rectangle returnRect = new Rectangle();

        if (newImageHeight != 0 && newImageWidth != 0) {

            if (newWidth == theRect.width) {
                returnRect.x = 0;
                returnRect.y = (theRect.height - newHeight) / 2;
            } else {
                returnRect.y = 0;
                returnRect.x = (theRect.width - newWidth) / 2;
            }
            returnRect.width = newWidth;
            returnRect.height = newHeight;
            return returnRect;
        } else {
            return new Rectangle(0, 0, rawImage.width, rawImage.height);
        }
    }
}

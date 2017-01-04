package com.bradforj287.raytracer.geometry;

public class RgbColor {
    public static RgbColor BLACK = new RgbColor(0, 0, 0);

    public final double r;
    public final double g;
    public final double b;

    public RgbColor(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int asInt() {
        int redChannel = (int) r;
        int greenChannel = (int) g;
        int blueChannel = (int) b;
        redChannel = redChannel << 16;
        greenChannel = greenChannel << 8;
        return redChannel + greenChannel + blueChannel;
    }

    public static RgbColor fromInt(int color) {
        int mask = 0x00FFFFFF;
        int redChannel = color & mask;
        redChannel = redChannel >> 16;
        mask = 0x0000FF00;
        int greenChannel = color & mask;
        greenChannel = greenChannel >> 8;
        mask = 0x000000FF;
        int blueChannel = color & mask;
        return new RgbColor(redChannel, greenChannel, blueChannel);
    }

    /**
     * scales the color by a number between 0 and 1
     *
     * @param scaleFactor - a double between 0 and 1
     * @return
     */
    public RgbColor scale(double scaleFactor) {
        double r1 = r * scaleFactor;
        double g1 = g * scaleFactor;
        double b1 = b * scaleFactor;
        return new RgbColor(r1, g1, b1);
    }

    public RgbColor add(RgbColor c1) {
        double r1 = r + c1.r;
        double g1 = g + c1.g;
        double b1 = b + c1.b;
        return new RgbColor(r1, g1, b1);
    }

    /**
     * returns a new color blended 50/50 with the passed in color
     *
     * @param c1
     * @return
     */
    public RgbColor blend(RgbColor c1) {
        return blend(c1, .5);
    }

    /**
     * returns a new color blended. THe new color is p% c1 and (1-p)% the other color
     *
     * @param c1 - double between 0 and 1 representing the percentage c1
     * @param p
     * @return
     */
    public RgbColor blend(RgbColor c1, double p) {
        RgbColor a = this.scale(1 - p);
        RgbColor b = c1.scale(p);
        return a.add(b);
    }
}

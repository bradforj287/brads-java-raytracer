package com.bradforj287.raytracer.geometry;

import com.bradforj287.raytracer.utils.MathUtils;

public class RgbColor {
    public static RgbColor BLACK = new RgbColor(0, 0, 0);

    private final double[] rgb;

    public RgbColor(int rgb) {
        this.rgb = new double[3];
        RgbColor c = fromInt(rgb);
        this.rgb[0] = c.rgb[0];
        this.rgb[1] = c.rgb[1];
        this.rgb[2] = c.rgb[2];
    }

    public RgbColor(double r, double g, double b) {
        this.rgb = new double[3];
        this.rgb[0] = r;
        this.rgb[1] = g;
        this.rgb[2] = b;
    }

    public int asInt() {
        double red = rgb[0];
        double green = rgb[1];
        double blue = rgb[2];

        // components need to be a max of 255. If greater bring it back down and scale
        // colors proportionally to maintain color.
        double max = MathUtils.max(red, green, blue);
        if (max > 255) {
            double d = max / 255;
            red = red / d;
            green = green / d;
            blue = blue / d;
        }

        int redChannel = (int) red;
        int greenChannel = (int) green;
        int blueChannel = (int) blue;
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
     * @param scaleFactor
     * @return
     */
    public RgbColor scale(double scaleFactor) {
        double r1 = rgb[0] * scaleFactor;
        double g1 = rgb[1] * scaleFactor;
        double b1 = rgb[2] * scaleFactor;

        return new RgbColor(r1, g1, b1);
    }

    public RgbColor add(RgbColor c1) {
        double r1 = rgb[0] + c1.rgb[0];
        double g1 = rgb[1] + c1.rgb[1];
        double b1 = rgb[2] + c1.rgb[2];
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

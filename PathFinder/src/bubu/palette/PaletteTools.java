/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bubu.palette;

import java.util.ArrayList;

/**
 *
 * @author Reuben
 */
public class PaletteTools {

    public static ArrayList<Integer[]> generateRandomPaletteRoute() {

        ArrayList<Integer[]> paletteRoute = new ArrayList<Integer[]>();

        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{0, 0, 0});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{100, 100, 100});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{0, 0, 0});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{100, 100, 100});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{0, 0, 0});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{100, 100, 100});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{0, 0, 0});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{100, 100, 100});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{0, 0, 0});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{100, 100, 100});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{0, 0, 0});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{100, 100, 100});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{0, 0, 0});
        paletteRoute.add(new Integer[]{(int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)});
        paletteRoute.add(new Integer[]{100, 100, 100});

        return paletteRoute;

    }

    public static int[] generatePalette(ArrayList<Integer[]> paletteRoute) {

        ArrayList<Integer> retArrayList = new ArrayList<Integer>();

        for (int i = 1; i < paletteRoute.size(); i++) {

            Integer[] startRoute = paletteRoute.get(i - 1);
            Integer[] endRoute = paletteRoute.get(i);

            int distance = (int) Math.pow(Math.pow(Math.abs(startRoute[0] - endRoute[0]), 2) + Math.pow(Math.abs(startRoute[1] - endRoute[1]), 2) + Math.pow(Math.abs(startRoute[2] - endRoute[2]), 2), 0.5);

            for (double z = 0; z <= distance; z++) {

                int r = startRoute[0], g = startRoute[1], b = startRoute[2];
                double temp = 0;

                for (int c = 0; c < 3; c++) {

                    if (startRoute[c] < endRoute[c]) {

                        temp = startRoute[c] + ((z / distance) * (endRoute[c] - startRoute[c]));

                    } else if (startRoute[c] > endRoute[c]) {

                        temp = startRoute[c] - ((z / distance) * (startRoute[c] - endRoute[c]));

                    } else {

                        temp = startRoute[c];

                    }

                    if (c == 0) {
                        r = (int) temp;
                    } else if (c == 1) {
                        g = (int) temp;
                    } else if (c == 2) {
                        b = (int) temp;
                    }



                }


                retArrayList.add(rgbToInt(r, g, b));

            }

        }


        int[] ret = new int[retArrayList.size()];

        int counter = 0;
        for (Integer current : retArrayList) {
            ret[counter] = current.intValue();
            counter++;
        }

        return ret;
    }

    public static int rgbToInt(int colour1, int colour2, int colour3) {

        int ret = 0;
        ret += colour1 + (colour2 * 256) + (colour3 * 256 * 256);
        return ret;

    }

    public static int[] intToRgb(int number) {

        int[] rgb = new int[3];

        rgb[0] = number % 256;
        rgb[1] = ((int) ((double) number / (double) 256)) % 256;
        rgb[2] = ((int) ((double) number / (double) (256 * 256))) % 256;

        return rgb;
    }
}

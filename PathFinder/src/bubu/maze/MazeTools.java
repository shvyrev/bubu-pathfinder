package bubu.maze;

import bubu.pathfinder.beans.Map;
import bubu.pathfinder.beans.Coordinate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MazeTools {

    private static final String baseDir = "C:\\";

    public static Map loadMapFromFile(String name) {

        String filename = baseDir + name + ".txt";

        File sourceFile = new File(filename);
        BufferedReader sourceFileReader = null;

        int mapWidth = 0;
        int mapHeigth = 0;

        Map mapObj = new Map();

        mapObj.setGrid(new int[0][0]);

        try {

            sourceFileReader = new BufferedReader(new FileReader(sourceFile));

            if (sourceFileReader != null) {

                String line = null;

                while ((line = sourceFileReader.readLine()) != null) {

                    mapHeigth++;
                    if (line.length() > mapWidth) {
                        mapWidth = line.length();
                    }
                }

                mapObj.setGrid(new int[mapWidth][mapHeigth]);

                sourceFileReader.close();
            }

            int x = 0;
            int y = mapHeigth - 1;

            sourceFileReader = new BufferedReader(new FileReader(sourceFile));

            if (sourceFileReader != null) {

                String line = null;

                while ((line = sourceFileReader.readLine()) != null) {

                    String character = "";

                    for (int i = 0; i < line.length(); i++) {

                        character = line.substring(i, i + 1);

                        if ("+-|#".indexOf(character) > -1) {
                            mapObj.getGrid()[x][y] = -2;
                        } else if ("A".equalsIgnoreCase(character)) {
                            mapObj.getGrid()[x][y] = -100;
                            mapObj.setStartLocation(new Coordinate(x, y));
                        } else if ("B".equalsIgnoreCase(character)) {
                            mapObj.getGrid()[x][y] = -200;
                            mapObj.setEndLocation(new Coordinate(x, y));
                        } else {
                            mapObj.getGrid()[x][y] = -1;
                        }

                        x++;

                    }
                    x = 0;
                    y--;

                }

                sourceFileReader.close();

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                sourceFileReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return mapObj;
    }

    public static Map getHugeEmptyMap(int width, int heigth) {

        Map map = new Map();

        int[][] grid = new int[width][heigth];

        for (int y = 0; y < heigth; y++) {
            for (int x = 0; x < width; x++) {

                if (x == 1 && y == (heigth - 1)) {
                    grid[x][y] = -100;
                } else if (x == (width - 1) && y == 1) {
                    grid[x][y] = -200;
                } else {
                    grid[x][y] = -1;
                }
            }
        }

        map.setGrid(grid);
        map.setStartLocation(new Coordinate(1, heigth - 1));
        map.setEndLocation(new Coordinate(width - 1, 1));

        return map;

    }

    public static Map enlargeMap(Map map, int resizeFactor) {

        Map ret = new Map();

        int width = map.getGrid().length;
        int heigth = map.getGrid()[0].length;

        boolean startLocationSet = false;
        boolean endLocationSet = false;

        int[][] enlargedMap = new int[width * resizeFactor][heigth * resizeFactor];

        for (int x = 0; x < width; x++) {

            for (int y = 0; y < heigth; y++) {

                for (int x2 = 0; x2 < resizeFactor; x2++) {

                    for (int y2 = 0; y2 < resizeFactor; y2++) {

                        int gridValue = map.getGrid()[x][y];

                        if (gridValue == -100 && !startLocationSet) {
                            enlargedMap[(x * resizeFactor) + x2][(y * resizeFactor) + y2] = gridValue;
                            startLocationSet = true;
                            ret.setStartLocation(new Coordinate((x * resizeFactor) + x2, (y * resizeFactor) + y2));
                        } else if (gridValue == -200 && !endLocationSet) {
                            enlargedMap[(x * resizeFactor) + x2][(y * resizeFactor) + y2] = gridValue;
                            endLocationSet = true;
                            ret.setEndLocation(new Coordinate((x * resizeFactor) + x2, (y * resizeFactor) + y2));
                        } else if (gridValue != -100 && gridValue != -200) {
                            enlargedMap[(x * resizeFactor) + x2][(y * resizeFactor) + y2] = gridValue;
                        }

                    }

                }

            }
            
        }

        ret.setGrid(enlargedMap);


        return ret;

    }
}

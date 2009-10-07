package bubu;

import bubu.pathfinder.PathFinder;
import bubu.pathfinder.beans.Map;
import bubu.pathfinder.beans.Coordinate;
import bubu.pathfinder.exception.CannotFindPathException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Reuben
 */
public class Main {

    private static final String baseDir = "F:\\Reuben\\My Documents\\PathFinderMaps\\";


    public static void main(String[] args) {
        try {

            PathFinder pathFinder = new PathFinder();

            String mazeName = "maze7";

            Map map = loadMapFromFile(mazeName);

            //pathFinder.drawMap(map.grid, map.startLocation, map.endLocation, new ArrayList<Coordinate>());

            long startTime = 0;
            long endTime = 0;

            int width = pathFinder.getMapWidth(map.getGrid());
            int heigth = pathFinder.getMapHeigth(map.getGrid());

            System.out.println("Finding Path for " + width +  " x " + heigth + " grid.............");
            startTime = System.currentTimeMillis();
            List<Coordinate> path = pathFinder.findPath(map, false);
            endTime = System.currentTimeMillis();

            pathFinder.saveMapImage(map, path, baseDir+mazeName, 3, false,"png");

            //pathFinder.drawMap(map, path);
            //pathFinder.drawArray();

            System.out.println((endTime - startTime) + " milliseconds, " + path.size() + " steps");

        } catch (CannotFindPathException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private static Map loadMapFromFile(String name) {

        
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
                            mapObj.setStartLocation(new Coordinate(x,y));
                        } else if ("B".equalsIgnoreCase(character)) {
                            mapObj.getGrid()[x][y] = -200;
                            mapObj.setEndLocation(new Coordinate(x,y));
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

    private static void benchmarkHugeMap() {

        try {

            PathFinder pathFinder = new PathFinder();

            int mapSize = 10;

            int incrementAmount = 1;

            while (mapSize + incrementAmount <= 1500) {

                mapSize = mapSize + incrementAmount;
                incrementAmount++;

                Map map = getHugeEmptyMap(mapSize, mapSize);

                long startTime = 0;
                long endTime = 0;

                System.out.println("Finding Path.............");
                startTime = System.currentTimeMillis();
                List<Coordinate> path = pathFinder.findPath(map, false);
                endTime = System.currentTimeMillis();

                System.out.println("Total number of squares : " + (mapSize * mapSize) + ", Time Taken: " + (endTime - startTime) + " milliseconds, Performance: " + (mapSize * mapSize) / (endTime - startTime) + " squares per millisecond");

            }

        } catch (CannotFindPathException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private static Map getHugeEmptyMap(int width, int heigth) {

        Map map = new Map();

        int[][] grid = new int[width][heigth];

        for (int y = 0; y < heigth; y++) {
            for (int x = 0; x < width; x++) {

                if (x == 0 && y == 0) {
                    grid[x][y] = -100;
                } else if (x == width - 1 && y == heigth - 1) {
                    grid[x][y] = -200;
                } else {
                    grid[x][y] = -1;
                }
            }
        }

        map.setGrid(grid);
        map.setStartLocation(new Coordinate(0,heigth-1));
        map.setEndLocation(new Coordinate(width-1,0));

        return map;

    }
}

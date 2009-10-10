package bubu.maze;

import bubu.astar.AStarPathFinder;
import bubu.astar.AStarResponse;
import bubu.pathfinder.PathFinder;
import bubu.pathfinder.beans.Coordinate;
import bubu.pathfinder.beans.Map;
import bubu.pathfinder.exception.CannotFindPathException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String args[]) {

        long startTime = 0;
        long endTime = 0;

        PathFinder pathFinder = new PathFinder();
        MazeGenerator mg = new MazeGenerator();

        System.out.println("Generating maze....");
        startTime = System.currentTimeMillis();



        int width = 1900 / 2;
        int heigth = 950 / 2;
        int linearFactor = Integer.MAX_VALUE;
        double horizontalVerticalBias = 0.5;
        int lineMinimumLength = 50;
        int lineMaximumLength = (int) (lineMinimumLength * 1.5);
        double complexity = 0.5;

        int resizeFactor = 1;

        String imageFormat = "png";

        String filename = "C:\\Maze-" + width + "x" + heigth + "-" + linearFactor + "-" + horizontalVerticalBias + "-" + lineMinimumLength + "-" + lineMaximumLength + "-" + complexity + "-" + resizeFactor;

        String filenameAStar = filename + "-AStar." + imageFormat;
        String filenameBuBu = filename + "-BuBu." + imageFormat;



        Map map = mg.generateMaze(width, heigth, linearFactor, horizontalVerticalBias, lineMinimumLength, lineMaximumLength, complexity, -1, -2);

        endTime = System.currentTimeMillis();

        width = pathFinder.getMapWidth(map.getGrid());
        heigth = pathFinder.getMapHeigth(map.getGrid());

        System.out.println("Generated maze of " + width + " x " + heigth + " in " + (endTime - startTime) + " milliseconds");

        boolean loadImage = false;

        try {

            List<Coordinate> path = new ArrayList<Coordinate>();


            if (true) {

                System.out.println("Finding path....");
                startTime = System.currentTimeMillis();
                path = pathFinder.findPath(map, false);
                endTime = System.currentTimeMillis();
                System.out.println("Found path in " + (endTime - startTime) + " milliseconds, " + path.size() + " steps");

                if (true) {
                    System.out.println("Saving image...." + filename);
                    pathFinder.saveMapImage(map,
                            path,
                            filenameBuBu,
                            resizeFactor,
                            true,
                            imageFormat);
                    loadImage = true;
                }
            }


            if (true) {

                System.out.println("Finding path....");
                AStarPathFinder aStarPathFinder = new AStarPathFinder();
                startTime = System.currentTimeMillis();
                AStarResponse aStarResponse = new AStarResponse();
                aStarResponse = aStarPathFinder.findPath(map);
                endTime = System.currentTimeMillis();
                System.out.println("Found path in " + (endTime - startTime) + " milliseconds, " + aStarResponse.getPath().size() + " steps");


                if (true) {
                    System.out.println("Saving image...." + filename);
                    aStarPathFinder.saveMapImage(map,
                            aStarResponse.getPath(),
                            filenameAStar,
                            resizeFactor,
                            true,
                            imageFormat,
                            aStarResponse.getMaxCost());
                    loadImage = true;
                }
            }

            if (loadImage) {
                Runtime.getRuntime().exec("rundll32.exe C:\\WINDOWS\\System32\\shimgvw.dll,ImageView_Fullscreen " + filenameBuBu);
                Runtime.getRuntime().exec("rundll32.exe C:\\WINDOWS\\System32\\shimgvw.dll,ImageView_Fullscreen " + filenameAStar);
            }

        } catch (CannotFindPathException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}

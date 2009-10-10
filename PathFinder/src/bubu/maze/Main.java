package bubu.maze;

import bubu.astar.AStarPathFinder;
import bubu.astar.AStarResponse;
import bubu.palette.PaletteTools;
import bubu.pathfinder.BuBuPathFinder;
import bubu.pathfinder.beans.Coordinate;
import bubu.pathfinder.beans.Map;
import bubu.pathfinder.exception.CannotFindPathException;
import bubu.tools.StopWatch;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String args[]) {

        BuBuPathFinder pathFinder = new BuBuPathFinder();
        MazeGenerator mg = new MazeGenerator();

        System.out.println("Generating maze....");

        StopWatch.startTimer();

        int width = 3000 / 2;
        int heigth = 3000 / 2;
        int linearFactor = Integer.MAX_VALUE;
        double horizontalVerticalBias = 0.5;
        int lineMinimumLength = 50;
        int lineMaximumLength = (int) (lineMinimumLength * 1.5);
        double complexity = 0.9;

        int resizeFactor = 1;

        String imageFormat = "png";
        String filename = "C:\\Maze-" + width + "x" + heigth + "-" + linearFactor + "-" + horizontalVerticalBias + "-" + lineMinimumLength + "-" + lineMaximumLength + "-" + complexity + "-" + resizeFactor;
        String filenameAStar = filename + "-AStar." + imageFormat;
        String filenameBuBu = filename + "-BuBu." + imageFormat;

        Map map = mg.generateMaze(width, heigth, linearFactor, horizontalVerticalBias, lineMinimumLength, lineMaximumLength, complexity, -1, -2);

        StopWatch.stopTimer();

        width = pathFinder.getMapWidth(map.getGrid());
        heigth = pathFinder.getMapHeigth(map.getGrid());

        System.out.println("Generated maze of " + width + " x " + heigth + " in " + StopWatch.getDuration() + " milliseconds");

        boolean loadImageBuBu = false;
        boolean loadImageAStar = false;

        ArrayList<Integer[]> paletteRoute = PaletteTools.generateRandomPaletteRoute();

        try {

            List<Coordinate> path = new ArrayList<Coordinate>();

            if (true) {

                System.out.println("Finding path using BuBu Algorithm....");
                StopWatch.startTimer();
                path = pathFinder.findPath(map, false);
                StopWatch.stopTimer();
                System.out.println("Found path in " + StopWatch.getDuration() + " milliseconds, " + path.size() + " steps");

                if (true) {
                    System.out.println("Saving image...." + filenameBuBu);
                    pathFinder.saveMapImage(map,
                            path,
                            filenameBuBu,
                            resizeFactor,
                            true,
                            imageFormat,
                            paletteRoute);
                    loadImageBuBu = true;
                }
            }


            if (true) {

                System.out.println("Finding path using A* Algorithm....");
                AStarPathFinder aStarPathFinder = new AStarPathFinder();
                StopWatch.startTimer();
                AStarResponse aStarResponse = new AStarResponse();
                aStarResponse = aStarPathFinder.findPath(map);
                StopWatch.stopTimer();
                System.out.println("Found path in " + StopWatch.getDuration() + " milliseconds, " + aStarResponse.getPath().size() + " steps");

                if (true) {
                    System.out.println("Saving image...." + filenameAStar);
                    aStarPathFinder.saveMapImage(map,
                            aStarResponse.getPath(),
                            filenameAStar,
                            resizeFactor,
                            true,
                            imageFormat,
                            aStarResponse.getMaxCost(),
                            paletteRoute);
                    loadImageAStar = true;
                }
            }

            if (loadImageBuBu) {
                Runtime.getRuntime().exec("rundll32.exe C:\\WINDOWS\\System32\\shimgvw.dll,ImageView_Fullscreen " + filenameBuBu);
            }
            if (loadImageAStar) {
                Runtime.getRuntime().exec("rundll32.exe C:\\WINDOWS\\System32\\shimgvw.dll,ImageView_Fullscreen " + filenameAStar);
            }


        } catch (CannotFindPathException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}

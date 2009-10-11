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

        int width = 6000 / 2;
        int heigth = 6000 / 2;
        int linearFactor = Integer.MAX_VALUE;
        double horizontalVerticalBias = 0.5;
        int lineMinimumLength = 50;
        int lineMaximumLength = (int) (lineMinimumLength * 1.5);
        double complexity = 0.5;
        boolean saveImages = false;

        int resizeFactor = 1;

        String imageFormat = "bmp";
        String filename = "C:\\Maze-" + width + "x" + heigth + "-" + linearFactor + "-" + horizontalVerticalBias + "-" + lineMinimumLength + "-" + lineMaximumLength + "-" + complexity + "-" + resizeFactor;
        String filenameAStar = filename + "-AStar." + imageFormat;
        String filenameBuBu = filename + "-BuBu." + imageFormat;

        Map bubuMap = mg.generateMaze(width, heigth, linearFactor, horizontalVerticalBias, lineMinimumLength, lineMaximumLength, complexity, -1, -2);
        Map astarMap = bubuMap.clone();

        StopWatch.stopTimer();

        width = pathFinder.getMapWidth(bubuMap.getGrid());
        heigth = pathFinder.getMapHeigth(bubuMap.getGrid());

        System.out.println("Generated maze of " + width + " x " + heigth + " in " + StopWatch.getDuration() + " milliseconds");

        boolean loadImageBuBu = false;
        boolean loadImageAStar = false;

        ArrayList<Integer[]> deadEndPaletteRoute = PaletteTools.generateRandomPaletteRoute(3, true);

        ArrayList<Integer[]> pathPaletteRoute = new ArrayList<Integer[]>();
        pathPaletteRoute.add(new Integer[]{0, 0, 255});
        pathPaletteRoute.add(new Integer[]{128, 128, 128});
        pathPaletteRoute.add(new Integer[]{0, 0, 255});


        try {

            List<Coordinate> path = new ArrayList<Coordinate>();

            if (true) {

                System.out.println("Finding path using BuBu Algorithm....");
                StopWatch.startTimer();
                path = pathFinder.findPath(bubuMap, false);
                StopWatch.stopTimer();
                System.out.println("Found path in " + StopWatch.getDuration() + " milliseconds, " + path.size() + " steps");

                if (true && saveImages) {
                    System.out.println("Saving image...." + filenameBuBu);
                    pathFinder.saveMapImage(bubuMap,
                            path,
                            filenameBuBu,
                            resizeFactor,
                            true,
                            imageFormat,
                            deadEndPaletteRoute,
                            pathPaletteRoute);

                    loadImageBuBu = true;
                }
            }

            bubuMap = null;

            if (true) {

                System.out.println("Finding path using A* Algorithm....");
                AStarPathFinder aStarPathFinder = new AStarPathFinder();
                StopWatch.startTimer();
                AStarResponse aStarResponse = new AStarResponse();
                aStarResponse = aStarPathFinder.findPath(astarMap);
                StopWatch.stopTimer();
                System.out.println("Found path in " + StopWatch.getDuration() + " milliseconds, " + aStarResponse.getPath().size() + " steps");

                if (true && saveImages) {
                    System.out.println("Saving image...." + filenameAStar);
                    aStarPathFinder.saveMapImage(astarMap,
                            aStarResponse.getPath(),
                            filenameAStar,
                            resizeFactor,
                            true,
                            imageFormat,
                            aStarResponse.getMaxCost(),
                            deadEndPaletteRoute,
                            pathPaletteRoute);

                    loadImageAStar = true;
                }
            }

            if (false) {

                if (loadImageBuBu) {
                    Runtime.getRuntime().exec("rundll32.exe C:\\WINDOWS\\System32\\shimgvw.dll,ImageView_Fullscreen " + filenameBuBu);
                }
                if (loadImageAStar) {
                    Runtime.getRuntime().exec("rundll32.exe C:\\WINDOWS\\System32\\shimgvw.dll,ImageView_Fullscreen " + filenameAStar);
                }

            }


        } catch (CannotFindPathException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}

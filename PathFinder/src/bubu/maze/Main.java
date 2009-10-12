package bubu.maze;

import bubu.astar.AStarPathFinder;
import bubu.astar.AStarResponse;
import bubu.palette.PaletteTools;
import bubu.pathfinder.BuBuPathFinder;
import bubu.pathfinder.BuBuPathFinderResponse;
import bubu.pathfinder.beans.Map;
import bubu.pathfinder.exception.CannotFindPathException;
import bubu.tools.StopWatch;
import java.util.*;

public class Main {

    public static void main(String args[]) {

        BuBuPathFinder pathFinder = new BuBuPathFinder();
        MazeGenerator mg = new MazeGenerator();

        System.out.println("Generating maze....");

        StopWatch.startTimer();

        int width = 90 / 2;
        int heigth = 90 / 2;
        int linearFactor = Integer.MAX_VALUE;
        double horizontalVerticalBias = 0.5;
        int lineMinimumLength = 50;
        int lineMaximumLength = (int) (lineMinimumLength * 1.5);
        double complexity = 0.5;
        boolean saveImages = true;
        boolean savePalettes = false;

        int resizeFactor = 1;

        String imageFormat = "bmp";
        String filename = "C:\\Maze-" + width + "x" + heigth + "-" + linearFactor + "-" + horizontalVerticalBias + "-" + lineMinimumLength + "-" + lineMaximumLength + "-" + complexity + "-" + resizeFactor;
        String filenameAStar = filename + "-AStar." + imageFormat;
        String filenameBuBu = filename + "-BuBu." + imageFormat;

        Map bubuMap = new Map();

        bubuMap = mg.generateMaze(width, heigth, linearFactor, horizontalVerticalBias, lineMinimumLength, lineMaximumLength, complexity, -1, -2);
        //bubuMap = MazeTools.loadMapFromFile("maze1");
        bubuMap = MazeTools.enlargeMap(bubuMap, 10);
        //bubuMap = TextMaze.getHugeEmptyMap(2000, 2000);
        Map astarMap = bubuMap.clone();

        StopWatch.stopTimer();

        int mazeWidth = pathFinder.getMapWidth(bubuMap.getGrid());
        int mazeHeigth = pathFinder.getMapHeigth(bubuMap.getGrid());

        System.out.println("Generated maze of " + mazeWidth + " x " + mazeHeigth + " in " + StopWatch.getDuration() + " milliseconds");

        boolean loadImageBuBu = false;
        boolean loadImageAStar = false;

        ArrayList<Integer[]> deadEndPaletteRoute = PaletteTools.generateRandomPaletteRoute(1, true);

        ArrayList<Integer[]> pathPaletteRoute = new ArrayList<Integer[]>();
//        pathPaletteRoute.add(new Integer[]{0, 0, 255});
//        pathPaletteRoute.add(new Integer[]{128, 128, 128});
//        pathPaletteRoute.add(new Integer[]{0, 0, 255});

        Integer[] firstFromDeadEnd = deadEndPaletteRoute.get(0);

        pathPaletteRoute.add(new Integer[]{255- firstFromDeadEnd[0].intValue(), 255- firstFromDeadEnd[1].intValue(), 255- firstFromDeadEnd[2].intValue()});
        pathPaletteRoute.add(new Integer[]{255, 255, 255});


        try {

            if (true) {

                System.out.println("Finding path using BuBu Algorithm....");
                StopWatch.startTimer();
                BuBuPathFinderResponse buBuPathFinderResponse = pathFinder.findPath(bubuMap, false);
                StopWatch.stopTimer();
                System.out.println("Found path in " + StopWatch.getDuration() + " milliseconds, " + buBuPathFinderResponse.getPath().size() + " steps");

                if (true && saveImages) {
                    System.out.println("Saving image...." + filenameBuBu);
                    pathFinder.saveMapImage(bubuMap,
                            buBuPathFinderResponse.getPath(),
                            filenameBuBu,
                            resizeFactor,
                            true,
                            imageFormat,
                            deadEndPaletteRoute,
                            pathPaletteRoute,
                            savePalettes);

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
                            pathPaletteRoute,
                            savePalettes);

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

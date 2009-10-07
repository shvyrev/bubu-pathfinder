package bubu.maze;

import bubu.astar.AStarPathFinder;
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


        int width = 2000;
        int heigth = width;
        int linearFactor = 20000;
        double horizontalVerticalBias = 0.5;
        int lineMinimumLength = 50;
        int lineMaximumLength = (int)(lineMinimumLength * 1.5);
        double complexity = 0.45;

        int resizeFactor = 10;

        String imageFormat = "png";

        Map map = mg.generateMaze(width, heigth, linearFactor, horizontalVerticalBias, lineMinimumLength, lineMaximumLength, complexity, -1, -2);

        endTime = System.currentTimeMillis();

        width = pathFinder.getMapWidth(map.getGrid());
        heigth = pathFinder.getMapHeigth(map.getGrid());

        System.out.println("Generated maze of " + width + " x " + heigth + " in "  + (endTime - startTime) + " milliseconds");
        
        try {

            System.out.println("Finding path....");
            startTime = System.currentTimeMillis();
            List<Coordinate> path = pathFinder.findPath(map, true);
            endTime = System.currentTimeMillis();
            System.out.println("Found path in " + (endTime - startTime) + " milliseconds, " + path.size() + " steps");
            //pathFinder.drawMap(map, (ArrayList) path);
            
            System.out.println("Finding path....");
            AStarPathFinder aStarPathFinder = new AStarPathFinder();
            startTime = System.currentTimeMillis();
            path = aStarPathFinder.findPath(map);
            endTime = System.currentTimeMillis();
            System.out.println("Found path in " + (endTime - startTime) + " milliseconds");
            //pathFinder.drawMap(map, (ArrayList) path);

            /*
            System.out.println("Saving image....");

            String filename = "C:\\Maze-"
                        + width + "x" + heigth + "-"
                        + linearFactor + "-"
                        + horizontalVerticalBias + "-"
                        + lineMinimumLength + "-"
                        + lineMaximumLength + "-"
                        + complexity + "-"
                        + resizeFactor;

            

             pathFinder.saveMapImage(map,
                    path,
                    filename,
                    resizeFactor,
                    true,
                    imageFormat);

            Runtime.getRuntime().exec("rundll32.exe C:\\WINDOWS\\System32\\shimgvw.dll,ImageView_Fullscreen " + filename + "." + imageFormat);
            */
            
        } catch (CannotFindPathException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     
    }

}

package bubu.maze;

import bubu.astar.AStarPathFinder;
import bubu.astar.AStarResponse;
import bubu.pathfinder.BuBuPathFinder;
import bubu.pathfinder.beans.Coordinate;
import bubu.pathfinder.beans.Map;
import bubu.pathfinder.exception.CannotFindPathException;
import bubu.tools.StopWatch;
import java.util.*;

public class Benchmark {

    public static void main(String[] args) {

        BuBuPathFinder pathFinder = new BuBuPathFinder();
        MazeGenerator mg = new MazeGenerator();

        int startSize = 500;
        int incrementSize = 250;
        int increments = 4;
        int iterations = 4;


        int linearFactor = Integer.MAX_VALUE;
        double horizontalVerticalBias = 0.5;
        int lineMinimumLength = 50;
        int lineMaximumLength = (int) (lineMinimumLength * 1.5);
        double complexity = 0.5;


        for (int algoCounter = 1; algoCounter <= 2; algoCounter++) {

            for (int q = startSize; q <= startSize + (incrementSize * increments); q = q + incrementSize) {

                int width = q / 2;
                int heigth = q / 2;

                String algoName = "";

                if (algoCounter == 1) {
                    algoName = "BuBu";
                } else if (algoCounter == 2) {
                    algoName = "A*";
                }

                System.out.println("Benchmarking " + algoName + " Algorithm " + ((width * 2) + 1) + "x" + ((heigth * 2) + 1));

                long totalPathFindingTime = 0;

                for (int i = 1; i <= iterations; i++) {

                    Map map = mg.generateMaze(width, heigth, linearFactor, horizontalVerticalBias, lineMinimumLength, lineMaximumLength, complexity, -1, -2);

                    try {

                        StopWatch.startTimer();

                        if (algoCounter == 1) {

                            List<Coordinate> path = new ArrayList<Coordinate>();
                            path = pathFinder.findPath(map, false);
                            StopWatch.stopTimer();
                            System.out.println(i + " : Found path in " + StopWatch.getDuration() + " milliseconds, " + path.size() + " steps");

                        } else if (algoCounter == 2) {

                            AStarPathFinder aStarPathFinder = new AStarPathFinder();
                            AStarResponse aStarResponse = new AStarResponse();
                            aStarResponse = aStarPathFinder.findPath(map);
                            StopWatch.stopTimer();
                            System.out.println(i + " : Found path in " + StopWatch.getDuration() + " milliseconds, " + aStarResponse.getPath().size() + " steps");

                        }

                        totalPathFindingTime = totalPathFindingTime + StopWatch.getDuration();

                    } catch (CannotFindPathException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished Benchmarking " + algoName + " Algorithm.... Total Time : " + totalPathFindingTime + "ms, Average Time : " + totalPathFindingTime / iterations + "ms\n");

            }
        }



    }
}

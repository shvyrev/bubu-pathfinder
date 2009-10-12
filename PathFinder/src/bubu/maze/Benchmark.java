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
        try {
            Thread.sleep(0);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        BuBuPathFinder pathFinder = new BuBuPathFinder();
        MazeGenerator mg = new MazeGenerator();

        int startSize = 100;
        int incrementSize = 250;
        int increments = 20;
        int iterations = 10;


        int linearFactor = Integer.MAX_VALUE;
        double horizontalVerticalBias = 0.5;
        int lineMinimumLength = 50;
        int lineMaximumLength = (int) (lineMinimumLength * 1.5);
        double complexity = 0.5;

        boolean showAllInfo = false;

        ArrayList<BenchmarkResult> results = new ArrayList<BenchmarkResult>();


        for (int q = startSize; q <= startSize + (incrementSize * increments); q = q + incrementSize) {

            int width = q / 2;
            int heigth = q / 2;
            BenchmarkResult result = new BenchmarkResult(((width * 2) + 1) + "x" + ((heigth * 2) + 1));

            System.out.println("Benchmarking " + ((width * 2) + 1) + "x" + ((heigth * 2) + 1));

            Map map = mg.generateMaze(width, heigth, linearFactor, horizontalVerticalBias, lineMinimumLength, lineMaximumLength, complexity, -1, -2);

            for (int algoCounter = 1; algoCounter <= 2; algoCounter++) {

                String algoName = "";

                if (algoCounter == 1) {
                    algoName = "BuBu";
                } else if (algoCounter == 2) {
                    algoName = "A*  ";
                }

                long totalPathFindingTime = 0;

                for (int i = 1; i <= iterations; i++) {

                    try {

                        Map bubumap = map.clone();
                        Map astarmap = map.clone();

                        StopWatch.startTimer();

                        if (algoCounter == 1) {

                            List<Coordinate> path = new ArrayList<Coordinate>();
                            path = pathFinder.findPath(bubumap, false);
                            StopWatch.stopTimer();
                            if (showAllInfo) {
                                System.out.println(i + " : Found path in " + StopWatch.getDuration() + " milliseconds, " + path.size() + " steps");
                            } else {
                                System.out.print(".");
                            }


                        } else if (algoCounter == 2) {

                            AStarPathFinder aStarPathFinder = new AStarPathFinder();
                            AStarResponse aStarResponse = new AStarResponse();
                            aStarResponse = aStarPathFinder.findPath(astarmap);
                            StopWatch.stopTimer();
                            if (showAllInfo) {
                                System.out.println(i + " : Found path in " + StopWatch.getDuration() + " milliseconds, " + aStarResponse.getPath().size() + " steps");
                            } else {
                                System.out.print(".");
                            }

                        }

                        totalPathFindingTime = totalPathFindingTime + StopWatch.getDuration();

                    } catch (CannotFindPathException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                System.out.println(algoName + " Algorithm.... Total Time : " + totalPathFindingTime + "ms, Average Time : " + totalPathFindingTime / iterations + "ms");

                if (algoCounter == 1) {
                    result.setBubuScore(totalPathFindingTime / iterations);
                } else if (algoCounter == 2) {
                    result.setaStarScore(totalPathFindingTime / iterations);
                }

            }
            results.add(result);
            System.out.println();
        }

        for (BenchmarkResult current : results) {

            System.out.println(current.getTitle() + "," + current.getBubuScore() + "," + current.getaStarScore());

        }


    }
}

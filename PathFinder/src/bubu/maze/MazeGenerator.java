package bubu.maze;

import bubu.pathfinder.beans.Coordinate;
import bubu.pathfinder.beans.Map;
import java.util.ArrayList;
import java.util.LinkedList;

public class MazeGenerator {

    private Map map;
    private int gridWidth;
    private int gridHeigth;
    LinkedList<Coordinate> stack = new LinkedList<Coordinate>();
    
    public MazeGenerator() {
    }

    public Map generateMaze(int width, 
            int heigth,
            int linearFactor,
            double horizontalVerticalBias,
            int lineMinimumLength,
            int lineMaximumLength,
            double complexity,
            int emptySpaceValue,
            int wallValue) {

        map = new Map();

        gridWidth = (width * 2) + 1;
        gridHeigth = (heigth * 2) + 1;

        map.setGrid(new int[gridWidth][gridHeigth]);

        for (int y = 0; y < gridHeigth; y++) {
            for (int x = 0; x < gridWidth; x++) {

                if (y == 0 || y == gridHeigth - 1 || x == 0 || x == gridWidth - 1) {
                    map.getGrid()[x][y] = wallValue; // wall
                } else if (x % 2 == 0 || y % 2 == 0) {
                    map.getGrid()[x][y] = wallValue; // wall
                } else {
                    map.getGrid()[x][y] = emptySpaceValue; // empty
                }

            }

        }

        fillMaze(new Coordinate(0, heigth - 1),
                linearFactor,
                horizontalVerticalBias,
                lineMinimumLength,
                lineMaximumLength,
                complexity,
                emptySpaceValue);

        for (int y = 0; y < gridHeigth; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (map.getGrid()[x][y] == -4) {
                    map.getGrid()[x][y] = emptySpaceValue;
                }
            }
        }

        map.getGrid()[0][heigth*2] = emptySpaceValue;
        map.getGrid()[1][heigth*2] = emptySpaceValue;
        map.getGrid()[0][(heigth*2)-1] = emptySpaceValue;

        map.getGrid()[0][0] = emptySpaceValue;
        map.getGrid()[0][1] = emptySpaceValue;
        map.getGrid()[1][0] = emptySpaceValue;

        map.getGrid()[width*2][0] = emptySpaceValue;
        map.getGrid()[(width*2)-1][0] = emptySpaceValue;
        map.getGrid()[width*2][1] = emptySpaceValue;
        
        map.getGrid()[width*2][heigth*2] = emptySpaceValue;
        map.getGrid()[(width*2)-1][heigth*2] = emptySpaceValue;
        map.getGrid()[width*2][(heigth*2)-1] = emptySpaceValue;

        map.getGrid()[0][heigth*2] = -100;
        map.getGrid()[width*2][0] = -200;

        map.setStartLocation(new Coordinate(0, (heigth * 2)));
        map.setEndLocation(new Coordinate((width * 2), 0));

        return map;

    }

    private void fillMaze(Coordinate startCoordinate,
            int linearFactor,
            double horizontalVerticalBias,
            int lineMinimumLength,
            int lineMaximumLength,
            double complexity,
            int emptySpaceValue) {

        boolean firstIteration = true;

        Coordinate current = calculateCoordinate(startCoordinate);

        LinkedList<Coordinate> visited = new LinkedList<Coordinate>();

        int counter = 0;
        int nextStop = 0;

        int horiz = 0;
        int vert = 0;

        int straightLineDirection = 0;

        while (firstIteration || !visited.isEmpty()) {

            counter++;

            firstIteration = false;

            map.getGrid()[current.getX()][current.getY()] = -4; //visited

            ArrayList<Coordinate> notVisitedNeighbours = getUnvisitedNeighbours(current, emptySpaceValue);

            if (!notVisitedNeighbours.isEmpty()) {

                visited.add(current);
                
                int chosenCell = 0;

                if ((int) (Math.random() * linearFactor) == 0 && nextStop < counter) {
                    nextStop = counter + (int) (Math.random() * ((Math.random() * (lineMaximumLength - lineMinimumLength)) + lineMinimumLength));

                    if (Math.random() > horizontalVerticalBias) {
                        straightLineDirection = 0; // horizontal
                    } else {
                        straightLineDirection = 1; // vertical
                    }
                }

                if (nextStop > counter) {
                    if (straightLineDirection == 0) {
                        chosenCell = chooseHorizontalCoordinate(current, notVisitedNeighbours);
                        horiz++;
                    } else {
                        chosenCell = chooseVerticalCoordinate(current, notVisitedNeighbours);
                        vert++;
                    }
                } else {
                    chosenCell = (int) (Math.random() * notVisitedNeighbours.size());
                }

                Coordinate next = notVisitedNeighbours.get(chosenCell);
                removeWall(current, next, emptySpaceValue);
                current = next;

            } else {

                if (Math.random() > complexity) { // the smaller the value the quicker it is to reach the destination
                    if (!visited.isEmpty()) {
                        visited.removeFirst();
                    }

                    if (!visited.isEmpty()) {
                        current = visited.getFirst();
                    }
                } else {
                    if (!visited.isEmpty()) {
                        visited.removeLast();
                    }
                    if (!visited.isEmpty()) {
                        current = visited.getLast();

                    }
                }

            }

        }

    }

    private int chooseHorizontalCoordinate(Coordinate param, ArrayList<Coordinate> list) {

        int counter = 0;

        for (Coordinate current : list) {
            if (param.getY() == current.getY()) {
                return counter;
            }
            counter++;
        }


        return 0;

    }

    private int chooseVerticalCoordinate(Coordinate param, ArrayList<Coordinate> list) {

        int counter = 0;

        for (Coordinate current : list) {
            if (param.getX() == current.getX()) {
                return counter;
            }
            counter++;
        }

        return 0;

    }

    private void removeWall(Coordinate pointA, Coordinate pointB, int emptySpaceValue) {
        Coordinate coord = new Coordinate((pointA.getX() + pointB.getX()) / 2, (pointA.getY() + pointB.getY()) / 2);
        map.getGrid()[coord.getX()][coord.getY()] = emptySpaceValue;
    }

    private ArrayList<Coordinate> getUnvisitedNeighbours(Coordinate param, int emptySpaceValue) {

        ArrayList<Coordinate> unvisitedNeighboursTemp = new ArrayList<Coordinate>();

        try {
            if (map.getGrid()[param.getX() + 2][param.getY()] == emptySpaceValue) {
                unvisitedNeighboursTemp.add(new Coordinate(param.getX() + 2, param.getY()));
            }
        } catch (Exception e) {
        }

        try {
            if (map.getGrid()[param.getX()][param.getY() + 2] == emptySpaceValue) {
                unvisitedNeighboursTemp.add(new Coordinate(param.getX(), param.getY() + 2));
            }
        } catch (Exception e) {
        }

        try {
            if (map.getGrid()[param.getX() - 2][param.getY()] == emptySpaceValue) {
                unvisitedNeighboursTemp.add(new Coordinate(param.getX() - 2, param.getY()));
            }
        } catch (Exception e) {
        }

        try {
            if (map.getGrid()[param.getX()][param.getY() - 2] == emptySpaceValue) {
                unvisitedNeighboursTemp.add(new Coordinate(param.getX(), param.getY() - 2));
            }
        } catch (Exception e) {
        }

        return unvisitedNeighboursTemp;

    }

    public Coordinate calculateCoordinate(Coordinate param) {

        return new Coordinate(((param.getX() + 1) * 2) - 1, ((param.getY() + 1) * 2) - 1);

    }

    public Coordinate reverseCalculateCoordinate(Coordinate param) {

        return new Coordinate((param.getX() - 1) / 2, (param.getY() - 1) / 2);

    }
}




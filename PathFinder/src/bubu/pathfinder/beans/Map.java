package bubu.pathfinder.beans;

public class Map implements Cloneable {

    private int[][] grid;
    private Coordinate startLocation;
    private Coordinate endLocation;

    public Map() {
    }

    public Coordinate getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Coordinate endLocation) {
        this.endLocation = endLocation;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public Coordinate getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Coordinate startLocation) {
        this.startLocation = startLocation;
    }

    public Map clone() {

        Map newmap = new Map();
        newmap.grid = new int[grid.length][grid[0].length];

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                newmap.grid[x][y] = grid[x][y];
            }
        }

        newmap.startLocation = new Coordinate(startLocation.getX(), startLocation.getY());
        newmap.endLocation = new Coordinate(endLocation.getX(), endLocation.getY());

        return newmap;

    }
}

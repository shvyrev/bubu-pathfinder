package bubu.pathfinder.beans;

public class Map implements Cloneable {

    int[][] grid;
    Coordinate startLocation;
    Coordinate endLocation;

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
        try {
            return (Map) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bubu.pathfinder.beans;

/**
 *
 * @author Reuben
 */
public class Map {

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
}

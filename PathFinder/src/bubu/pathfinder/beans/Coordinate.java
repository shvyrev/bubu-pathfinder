package bubu.pathfinder.beans;

public class Coordinate {

    private int x;
    private int y;

    public Coordinate() {
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return x + "," + y;
    }

    public boolean equals(Object obj) {
        
        Coordinate c = (Coordinate) obj;


        if (c.getX() == this.x && c.getY() == this.y) {
            return true;
        }

        return false;

    }

}

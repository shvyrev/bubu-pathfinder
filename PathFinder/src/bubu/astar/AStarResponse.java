package bubu.astar;

import bubu.pathfinder.beans.Coordinate;
import java.util.ArrayList;

public class AStarResponse {

    ArrayList<Coordinate> path = new ArrayList<Coordinate>();
    int maxCost;

    public AStarResponse() {
    }

    public int getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(int maxCost) {
        this.maxCost = maxCost;
    }

    public ArrayList<Coordinate> getPath() {
        return path;
    }

    public void setPath(ArrayList<Coordinate> path) {
        this.path = path;
    }

    


}

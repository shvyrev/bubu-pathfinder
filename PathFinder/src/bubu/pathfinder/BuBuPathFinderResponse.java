package bubu.pathfinder;

import bubu.pathfinder.beans.Coordinate;
import java.util.List;

public class BuBuPathFinderResponse {

    private List<Coordinate> path;

    public BuBuPathFinderResponse() {
    }

    public List<Coordinate> getPath() {
        return path;
    }

    public void setPath(List<Coordinate> path) {
        this.path = path;
    }

    

}

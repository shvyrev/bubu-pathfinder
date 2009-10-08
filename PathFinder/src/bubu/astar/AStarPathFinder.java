/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bubu.astar;

import bubu.pathfinder.beans.Coordinate;
import bubu.pathfinder.beans.Map;
import bubu.pathfinder.exception.CannotFindPathException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Reuben
 */
public class AStarPathFinder {

    public ArrayList<Coordinate> findPath(Map map) throws CannotFindPathException, Exception {

        ArrayList<Coordinate> path = new ArrayList<Coordinate>();

        int width = getMapWidth(map.getGrid());
        int heigth = getMapHeigth(map.getGrid());

        int[] openList = new int[width * width + 2];
        int[][] whichList = new int[width + 1][heigth + 1];
        int[] openX = new int[width * width + 2];
        int[] openY = new int[width * width + 2];
        int[][] parentX = new int[width + 1][heigth + 1];
        int[][] parentY = new int[width + 1][heigth + 1];
        int[] fCost = new int[width * width + 2];
        int[][] gCost = new int[width + 1][heigth + 1];
        int[] hCost = new int[width * width + 2];

        int onClosedList = 10;
        int onOpenList = 9;
        int notfinished = 0;
        int notstarted = 0;
        int found = 1;
        int nonexistent = 2;
        int walkable = -1;
        int unwakable = -2;

        int v = 0, u = 0, temp = 0, m = 0;

        int numberOfOpenListItems = 1;

        int newOpenListItemID = 0;

        openList[1] = 1;
        openX[1] = map.getStartLocation().getX();
        openY[1] = map.getStartLocation().getY();

        int parentXVal = 0;
        int parentYVal = 0;

        gCost[map.getStartLocation().getX()][map.getStartLocation().getY()] = 0;

        boolean keepLooping = true;

        while (keepLooping) {
            System.out.println(numberOfOpenListItems + " " + parentXVal +"," + parentYVal);

            if (numberOfOpenListItems != 0) {
                parentXVal = openX[openList[1]];
                parentYVal = openY[openList[1]];

                whichList[parentXVal][parentYVal] = onClosedList;

                numberOfOpenListItems--;
                openList[1] = openList[numberOfOpenListItems + 1];

                v = 1;

                for (;;) { // START LOOP
                    u = v;
                    if (2 * u + 1 <= numberOfOpenListItems) {
                        if (fCost[openList[u]] >= fCost[openList[2 * u]]) {
                            v = 2 * u;
                        }
                        if (fCost[openList[v]] >= fCost[openList[2 * u + 1]]) {
                            v = 2 * u + 1;
                        }
                    } else {
                        if (2 * u <= numberOfOpenListItems) {
                            if (fCost[openList[u]] >= fCost[openList[2 * u]]) {
                                v = 2 * u;
                            }
                        }
                    }
                    if (u != v) {
                        temp = openList[u];
                        openList[u] = openList[v];
                        openList[v] = temp;
                    } else {
                        break;
                    }
                } // END LOOP

                for (int b = parentYVal - 1; b <= parentYVal + 1; b++) {
                    for (int a = parentXVal - 1; a <= parentXVal + 1; a++) {
                        if (a != -1 && b != -1 && a != width && b != heigth && (a == parentXVal || b == parentYVal)) {
                            if (whichList[a][b] != onClosedList) {
                                if (map.getGrid()[a][b] != unwakable) {
                                    int corner = walkable;
                                    if (corner == walkable) {
                                        if (whichList[a][b] != onOpenList) {
                                            newOpenListItemID++;
                                            m = numberOfOpenListItems + 1;
                                            openList[m] = newOpenListItemID;
                                            openX[newOpenListItemID] = a;
                                            openY[newOpenListItemID] = b;

                                            int addedGcost;

                                            if (Math.abs(a - parentXVal) == 1 && Math.abs(b - parentYVal) == 1) {
                                                addedGcost = 14;
                                            } else {
                                                addedGcost = 10;
                                            }
                                            gCost[a][b] = gCost[parentXVal][parentYVal] + addedGcost;
                                            hCost[openList[m]] = 10 * (Math.abs(a - map.getEndLocation().getX()) + Math.abs(b - map.getEndLocation().getY()));
                                            fCost[openList[m]] = gCost[a][b] + hCost[openList[m]];
                                            parentX[a][b] = parentXVal;
                                            parentY[a][b] = parentYVal;

                                            while (m != 1) {
                                                if (fCost[openList[m]] < fCost[openList[m / 2]]) {
                                                    temp = openList[m / 2];
                                                    openList[m / 2] = openList[m];
                                                    openList[m] = temp;
                                                } else {
                                                    break;
                                                }
                                            }
                                            numberOfOpenListItems++;
                                            whichList[a][b] = onOpenList;
                                        } else {
                                            int addedGcost;
                                            if (Math.abs(a - parentXVal) == 1 && Math.abs(b - parentYVal) == 1) {
                                                addedGcost = 14;
                                            } else {
                                                addedGcost = 10;
                                            }

                                            int tempGcost = gCost[parentXVal][parentYVal] + addedGcost;

                                            if (tempGcost < gCost[a][b]) {
                                                parentX[a][b] = parentXVal;
                                                parentY[a][b] = parentYVal;
                                                gCost[a][b] = tempGcost;

                                                for (int x = 1; x <= numberOfOpenListItems; x++) {
                                                    if (openX[openList[x]] == a && openY[openList[x]] == b) {
                                                        fCost[openList[x]] = gCost[a][b] + hCost[openList[x]];
                                                        m = x;
                                                        while (m != 1) {
                                                            if (fCost[openList[m]] < fCost[openList[m / 2]]) {
                                                                temp = openList[m / 2];
                                                                openList[m / 2] = openList[m];
                                                                openList[m] = temp;
                                                                m = m / 2;
                                                            } else {
                                                                break;
                                                            }
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("No path");
                return path;
            }

            if (whichList[map.getEndLocation().getX()][map.getEndLocation().getY()] == onOpenList) {

                int pathLength = 0;
                System.out.println("Saving path");

                int pathX = map.getEndLocation().getX();
                int pathY = map.getEndLocation().getY();
                for (;;) {
                    int tempx = parentX[pathX][pathY];
                    pathY = parentY[pathX][pathY];
                    pathX = tempx;
                    pathLength++;

                    path.add(new Coordinate(pathX, pathY));
                    //System.out.println(pathX + "," + pathY);
                    if (pathX == map.getStartLocation().getX() && pathY == map.getStartLocation().getY()) {
                        break;
                    }
                }

                System.out.println("Path found. " + pathLength + " steps.");
                keepLooping = false;


            }

        }

        return path;

    }

    public int getMapWidth(int[][] map) {

        int counter = 0;

        while (true) {

            try {
                int location = map[counter][0];
                counter++;
            } catch (Exception e) {
                return counter;
            }

        }

    }

    public int getMapHeigth(int[][] map) {

        int counter = 0;

        while (true) {

            try {

                int location = map[0][counter];
                counter++;

            } catch (Exception e) {
                return counter;
            }

        }

    }
}

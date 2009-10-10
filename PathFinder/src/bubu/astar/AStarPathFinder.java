package bubu.astar;

import bubu.palette.PaletteTools;
import bubu.pathfinder.beans.Coordinate;
import bubu.pathfinder.beans.Map;
import bubu.pathfinder.exception.CannotFindPathException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class AStarPathFinder {

    public AStarResponse findPath(Map map) throws CannotFindPathException, Exception {

        AStarResponse response = new AStarResponse();

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



                                    map.getGrid()[a][b] = map.getGrid()[parentXVal][parentYVal] + 1;

                                    if (map.getGrid()[a][b] > response.getMaxCost()) {
                                        response.setMaxCost(map.getGrid()[a][b]);
                                    }


                                }
                            }
                        }
                    }
                }
            } else {

                System.out.println("No path");
                return response;

            }

            if (whichList[map.getEndLocation().getX()][map.getEndLocation().getY()] == onOpenList) {

                int pathLength = 0;

                int pathX = map.getEndLocation().getX();
                int pathY = map.getEndLocation().getY();
                for (;;) {
                    int tempx = parentX[pathX][pathY];
                    pathY = parentY[pathX][pathY];
                    pathX = tempx;
                    pathLength++;

                    response.getPath().add(new Coordinate(pathX, pathY));

                    if (pathX == map.getStartLocation().getX() && pathY == map.getStartLocation().getY()) {
                        break;
                    }
                }

                System.out.println("Path found. " + pathLength + " steps.");
                keepLooping = false;

            }

        }

        return response;
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

    public void saveMapImage(Map map, List<Coordinate> path, String filename, int resizeFactor, boolean drawDeadEnds, String fileFormat, int maxCost, ArrayList<Integer[]> paletteRoute) throws IOException {

        int[] palette;

        palette = PaletteTools.generatePalette(paletteRoute);

        int mapWidth = getMapWidth(map.getGrid());
        int mapHeigth = getMapHeigth(map.getGrid());

        int imageWidth = mapWidth * resizeFactor;
        int imageHeigth = mapHeigth * resizeFactor;

        BufferedImage image = new BufferedImage(imageWidth, imageHeigth, BufferedImage.TYPE_INT_RGB);

        WritableRaster raster = image.getRaster();

        int[] endPointMarkerColour = new int[]{255, 0, 0};
        int[] startPontMarkerColour = new int[]{0, 255, 0};
        int[] wallColour = new int[]{0, 0, 0};
        int[] unvisitedSpaceColour = new int[]{255, 255, 255};

        for (int y = 0; y < mapHeigth; y++) {

            for (int x = 0; x < mapWidth; x++) {

                // draw walls
                if (map.getGrid()[x][y] == -2) {

                    for (int rasterY = 0; rasterY < resizeFactor; rasterY++) {
                        for (int rasterX = 0; rasterX < resizeFactor; rasterX++) {
                            int rasterCoordX = (x * resizeFactor) + rasterX;
                            int rasterCoordY = imageHeigth - 1 - ((y * resizeFactor) + rasterY);
                            raster.setPixel(rasterCoordX, rasterCoordY, wallColour);
                        }
                    }

                } else {

                    double distanceProgressionPercentage = (double) map.getGrid()[x][y] / (double) maxCost;

                    int palettePosition = (int) ((double) distanceProgressionPercentage * (double) palette.length) - 1;

                    palettePosition = palettePosition < 0 ? 0 : palettePosition;
                    palettePosition = palettePosition >=  palette.length ? palettePosition = palette.length - 1 : palettePosition;

                    int[] paletteColour = PaletteTools.intToRgb(palette[palettePosition]);

                    for (int rasterY = 0; rasterY < resizeFactor; rasterY++) {
                        for (int rasterX = 0; rasterX < resizeFactor; rasterX++) {
                            if (!drawDeadEnds || map.getGrid()[x][y] <= 0) {
                                // unvisited
                                int rasterCoordX = (x * resizeFactor) + rasterX;
                                int rasterCoordY = imageHeigth - 1 - ((y * resizeFactor) + rasterY);
                                raster.setPixel(rasterCoordX, rasterCoordY, unvisitedSpaceColour);

                            } else {
                                // visited
                                if (map.getGrid()[x][y] > 0) {
                                    int rasterCoordX = (x * resizeFactor) + rasterX;
                                    int rasterCoordY = imageHeigth - 1 - ((y * resizeFactor) + rasterY);
                                    raster.setPixel(rasterCoordX, rasterCoordY, paletteColour);
                                }
                            }
                        }
                    }

                }

            }

        }

        // start point marker
        for (int rasterY = 0; rasterY < resizeFactor; rasterY++) {

            for (int rasterX = 0; rasterX < resizeFactor; rasterX++) {
                raster.setPixel(
                        (map.getStartLocation().getX() * resizeFactor) + rasterX,
                        imageHeigth - 1 - ((map.getStartLocation().getY() * resizeFactor) + rasterY),
                        startPontMarkerColour);
            }

        }

        // end point marker
        for (int rasterY = 0; rasterY < resizeFactor; rasterY++) {

            for (int rasterX = 0; rasterX < resizeFactor; rasterX++) {
                raster.setPixel(
                        (map.getEndLocation().getX() * resizeFactor) + rasterX,
                        imageHeigth - 1 - ((map.getEndLocation().getY() * resizeFactor) + rasterY),
                        endPointMarkerColour);
            }

        }

        int counter = 0;

        for (Coordinate currentCoordinate : path) {

            counter++;

            int[] pathColour = new int[]{
                255 - Math.abs((int) (((counter / 5) % 510) - 255)),
                0,
                Math.abs((int) (((counter / 5) % 510) - 255))
            };

            if (counter % 40 > 38) {
                pathColour = new int[]{255, 255, 255};
            } else {
                pathColour = new int[]{255, 0, 0};
            }


            for (int rasterY = 0; rasterY < resizeFactor; rasterY++) {

                for (int rasterX = 0; rasterX < resizeFactor; rasterX++) {

                    raster.setPixel(
                            (currentCoordinate.getX() * resizeFactor) + rasterX,
                            imageHeigth - 1 - ((currentCoordinate.getY() * resizeFactor) + rasterY),
                            pathColour);

                }

            }

        }

        ImageIO.write(image, fileFormat.toUpperCase(), new File(filename));

        savePaletteImage(palette, 30, filename, fileFormat);

    }

    private void savePaletteImage(int palette[], int paletteImageHeigth, String filename, String imageFormat) {

        int paletteLength = palette.length;

        BufferedImage image = new BufferedImage(paletteLength, paletteImageHeigth, BufferedImage.TYPE_INT_RGB);

        WritableRaster raster = image.getRaster();

        for (int y = 0; y < paletteImageHeigth; y++) {

            for (int x = 0; x < paletteLength; x++) {

                raster.setPixel(x, y, PaletteTools.intToRgb(palette[x]));

            }

        }
        try {
            ImageIO.write(image, imageFormat.toUpperCase(), new File(filename.replace("." + imageFormat, "-palette." + imageFormat)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    

    
}

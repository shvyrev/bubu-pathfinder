package bubu.pathfinder;

import bubu.palette.PaletteTools;
import bubu.pathfinder.beans.Map;
import bubu.pathfinder.beans.Coordinate;
import bubu.pathfinder.exception.CannotFindPathException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;

public class BuBuPathFinder {

    private final String START_MARKER = "A";
    private final String END_MARKER = "B";
    private final String PATH_MARKER = "O";
    private final String EMPTY_MARKER = " ";
    private final String MAP_BORDER = "";
    private int[][] mapArray;

    public void drawVisitedMap() {

        // -1 = empty space
        // -2 = blocked space
        // -100 = start location
        // -200 = end location

        int mapWidth = getMapWidth(mapArray);
        int mapHeigth = getMapHeigth(mapArray);

        String[][] drawing = new String[mapWidth][mapHeigth];

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeigth; y++) {

                if (mapArray[x][y] == -2) {

                    try {

                        if (mapArray[x][y + 1] == -2 && mapArray[x + 1][y] != -2 && mapArray[x][y - 1] == -2 && mapArray[x - 1][y] != -2) {
                            drawing[x][y] = "|";
                        } else if (mapArray[x][y + 1] != -2 && mapArray[x + 1][y] == -2 && mapArray[x][y - 1] != -2 && mapArray[x - 1][y] == -2) {
                            drawing[x][y] = "-";
                        } else {
                            drawing[x][y] = "+";
                        }

                    } catch (Exception e) {
                        drawing[x][y] = "+";
                    }

                } else if (mapArray[x][y] == -100) {
                    drawing[x][y] = START_MARKER;
                } else if (mapArray[x][y] == -200) {
                    drawing[x][y] = END_MARKER;
                } else if (mapArray[x][y] > 0) {
                    drawing[x][y] = PATH_MARKER;
                } else {
                    drawing[x][y] = " ";
                }

            }
        }

        for (int x = 0; x < mapWidth + 2; x++) {
            System.out.print(MAP_BORDER);
        }

        System.out.println();

        for (int y = mapHeigth - 1; y >= 0; y--) {
            System.out.print(MAP_BORDER);
            for (int x = 0; x < mapWidth; x++) {
                System.out.print(drawing[x][y]);
            }
            System.out.println(MAP_BORDER);
        }

        for (int x = 0; x < mapWidth + 2; x++) {
            System.out.print(MAP_BORDER);
        }

        System.out.println();

    }

    public void saveMapImage(Map map, List<Coordinate> path, String filename, int resizeFactor, boolean drawDeadEnds, String fileFormat, ArrayList<Integer[]> deadEndPaletteRoute, ArrayList<Integer[]> pathPaletteRoute) throws IOException, Exception {

        int[] deadEndPalette;

        deadEndPalette = PaletteTools.generatePalette(deadEndPaletteRoute);

        int mapWidth = getMapWidth(map.getGrid());
        int mapHeigth = getMapHeigth(map.getGrid());

        int imageWidth = mapWidth * resizeFactor;
        int imageHeigth = mapHeigth * resizeFactor;

        System.out.println("Image Dimensions : " + imageWidth + "x" + imageHeigth);

        BufferedImage image = new BufferedImage(imageWidth, imageHeigth, BufferedImage.TYPE_INT_RGB);

        WritableRaster raster = image.getRaster();

        int[] endPointMarkerColour = PaletteTools.expandPixelColour(new int[]{255, 0, 0}, resizeFactor);
        int[] startPontMarkerColour = PaletteTools.expandPixelColour(new int[]{0, 255, 0}, resizeFactor);
        int[] wallColour = PaletteTools.expandPixelColour(new int[]{0, 0, 0}, resizeFactor);
        int[] unvisitedSpaceColour = PaletteTools.expandPixelColour(new int[]{255, 255, 255}, resizeFactor);

        int pathSize = path.size();

        for (int y = 0; y < mapHeigth; y++) {

            for (int x = 0; x < mapWidth; x++) {

                if (map.getGrid()[x][y] == -2) {
                    // wall
                    raster.setPixels(
                            x * resizeFactor,
                            imageHeigth - resizeFactor -  (y * resizeFactor),
                            resizeFactor,
                            resizeFactor,
                            wallColour);

                } else {

                    double distanceProgressionPercentage = (double) map.getGrid()[x][y] / (double) pathSize;

                    int palettePosition = (int) ((double) distanceProgressionPercentage * (double) deadEndPalette.length) - 1;

                    palettePosition = palettePosition < 0 ? 0 : palettePosition;
                    palettePosition = palettePosition >= deadEndPalette.length ? palettePosition = deadEndPalette.length - 1 : palettePosition;

                    int[] paletteColour = PaletteTools.expandPixelColour(PaletteTools.intToRgb(deadEndPalette[palettePosition]), resizeFactor);

                    if (!drawDeadEnds || map.getGrid()[x][y] <= 0) {
                        // unvisited
                        try {
                        raster.setPixels(
                                x * resizeFactor,
                                imageHeigth - resizeFactor - (y * resizeFactor),
                                resizeFactor,
                                resizeFactor,
                                unvisitedSpaceColour);
                        } catch (Exception e) {
                            System.out.println((x * resizeFactor) + " " + (imageHeigth - 1 - resizeFactor - (y * resizeFactor)));
                            throw e;
                        }
                    } else {
                        // visited
                        raster.setPixels(
                                x * resizeFactor,
                                imageHeigth - resizeFactor -  (y * resizeFactor),
                                resizeFactor,
                                resizeFactor,
                                paletteColour);
                    }

                }

            }

        }

        // start point marker
        raster.setPixels(
                map.getStartLocation().getX() * resizeFactor,
                imageHeigth - resizeFactor - (map.getStartLocation().getY() * resizeFactor),
                resizeFactor,
                resizeFactor,
                startPontMarkerColour);

        // end point marker
        raster.setPixels(
                map.getStartLocation().getX() * resizeFactor,
                imageHeigth - resizeFactor - (map.getStartLocation().getY() * resizeFactor),
                resizeFactor,
                resizeFactor,
                endPointMarkerColour);

        int counter = 0;

        int[] pathPalette = PaletteTools.generatePalette(pathPaletteRoute);

        for (Coordinate currentCoordinate : path) {

            counter++;

            int[] pathColour = PaletteTools.expandPixelColour(PaletteTools.intToRgb(pathPalette[counter % pathPalette.length]), resizeFactor);

            raster.setPixels(
                    currentCoordinate.getX() * resizeFactor,
                    imageHeigth - resizeFactor - (currentCoordinate.getY() * resizeFactor),
                    resizeFactor,
                    resizeFactor,
                    pathColour);

        }

        ImageIO.write(image, fileFormat.toUpperCase(), new File(filename));
        savePaletteImage(deadEndPalette, 30, filename, fileFormat);

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
            ImageIO.write(image, imageFormat.toUpperCase(), new File(filename.replace("." + imageFormat, "-Palette." + imageFormat)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    public void drawMap(Map map, ArrayList<Coordinate> path) {

        int mapWidth = getMapWidth(map.getGrid());
        int mapHeigth = getMapHeigth(map.getGrid());

        char[][] drawing = new char[mapWidth][mapHeigth];

        for (int y = mapHeigth - 1; y >= 0; y--) {

            for (int x = 0; x < mapWidth; x++) {

                if (map.getGrid()[x][y] == -2) {

                    try {

                        if (map.getGrid()[x][y + 1] == -2 && map.getGrid()[x + 1][y] != -2 && map.getGrid()[x][y - 1] == -2 && map.getGrid()[x - 1][y] != -2) {
                            drawing[x][y] = '|';
                        } else if (map.getGrid()[x][y + 1] != -2 && map.getGrid()[x + 1][y] == -2 && map.getGrid()[x][y - 1] != -2 && map.getGrid()[x - 1][y] == -2) {
                            drawing[x][y] = '-';
                        } else {
                            drawing[x][y] = '+';
                        }

                    } catch (Exception e) {
                        drawing[x][y] = '+';
                    }

                } else {
                    drawing[x][y] = EMPTY_MARKER.charAt(0);
                }

            }

        }

        drawing[map.getStartLocation().getX()][map.getStartLocation().getY()] = START_MARKER.charAt(0);
        drawing[map.getEndLocation().getX()][map.getEndLocation().getY()] = END_MARKER.charAt(0);

        for (Coordinate currentCoordinate : path) {
            drawing[currentCoordinate.getX()][currentCoordinate.getY()] = PATH_MARKER.charAt(0);
        }


        for (int x = 0; x < mapWidth + 2; x++) {
            System.out.print(MAP_BORDER);
        }

        System.out.println();

        for (int y = mapHeigth - 1; y >= 0; y--) {
            System.out.print(MAP_BORDER);
            for (int x = 0; x < mapWidth; x++) {
                System.out.print(drawing[x][y]);
            }
            System.out.println(MAP_BORDER);
        }

        for (int x = 0; x < mapWidth + 2; x++) {
            System.out.print(MAP_BORDER);
        }

        System.out.println();



    }

    public List<Coordinate> findPath(Map map, boolean allowDiagonal) throws CannotFindPathException, Exception {

        int mapWidth = getMapWidth(map.getGrid());
        int mapHeigth = getMapHeigth(map.getGrid());

        ArrayList<Coordinate> path = new ArrayList<Coordinate>();

        // -1 = empty space
        // -2 = blocked space
        // -100 = start location
        // -200 = end location

        mapArray = map.getGrid();

        mapArray[map.getStartLocation().getX()][map.getStartLocation().getY()] = -100;
        mapArray[map.getEndLocation().getX()][map.getEndLocation().getY()] = -200;

        ArrayList<Coordinate> currentCoordinates = new ArrayList<Coordinate>();
        currentCoordinates.add(map.getStartLocation());

        ArrayList<Coordinate> adjacentBlocks = new ArrayList<Coordinate>();

        boolean keepLooping = true;

        int currentDistance = 1;

        while (keepLooping) {

            adjacentBlocks = findAdjacentBlocks(mapArray, currentCoordinates, allowDiagonal, mapWidth, mapHeigth, currentDistance);

            if (adjacentBlocks.isEmpty()) {
                throw new CannotFindPathException();
            }

            if (isLocationInList(adjacentBlocks, map.getEndLocation())) {
                keepLooping = false;
            }

            //System.out.println(currentDistance + " : " + adjacentBlocks.toString());

            currentCoordinates.clear();
            currentCoordinates.addAll(adjacentBlocks);
            adjacentBlocks.clear();

            currentDistance++;

            //if (currentDistance % 10 == 0) { drawArray(); }
        }

        Coordinate currentCoordinate = map.getEndLocation();

        adjacentBlocks.clear();
        adjacentBlocks = new ArrayList<Coordinate>();

        for (int distance = currentDistance; distance > 0; distance--) {

            adjacentBlocks = findAdjacentBlocksForPathBack(mapArray, currentCoordinate, adjacentBlocks, allowDiagonal, mapWidth, mapHeigth);

            for (Coordinate currentAdjacentBlock : adjacentBlocks) {

                int value = mapArray[currentAdjacentBlock.getX()][currentAdjacentBlock.getY()];

                if (value == distance) {
                    currentCoordinate = currentAdjacentBlock;
                    path.add(currentAdjacentBlock);
                    break;
                }

            }

        }


        return path;

    }

    private ArrayList<Coordinate> findAdjacentBlocks(int[][] map, List<Coordinate> locations, boolean allowDiagonal, int width, int height, int currentDistance) throws Exception {

        ArrayList<Coordinate> coords = new ArrayList<Coordinate>();

        for (Coordinate location : locations) {

            if (map[location.getX()][location.getY()] == -2) {
                throw new Exception("Cannot find adjacent locations to a blocked location");
            }

            ArrayList<Coordinate> coordinateArray = new ArrayList<Coordinate>();

            coordinateArray.add(new Coordinate(location.getX() - 1, location.getY())); // left
            coordinateArray.add(new Coordinate(location.getX(), location.getY() - 1)); // down
            coordinateArray.add(new Coordinate(location.getX() + 1, location.getY())); // right
            coordinateArray.add(new Coordinate(location.getX(), location.getY() + 1)); // up

            if (allowDiagonal) {

                int diagonalCheckA;
                int diagonalCheckB;

                if (location.getX() > 0 && location.getY() > 0) {
                    diagonalCheckA = map[location.getX() - 1][location.getY()];
                    diagonalCheckB = map[location.getX()][location.getY() - 1];
                    if (diagonalCheckA == -1 || diagonalCheckB == -1) {
                        coordinateArray.add(new Coordinate(location.getX() - 1, location.getY() - 1)); //down left
                    }
                }

                if (location.getX() > 0 && location.getY() < height - 1) {
                    diagonalCheckA = map[location.getX() - 1][location.getY()];
                    diagonalCheckB = map[location.getX()][location.getY() + 1];
                    if (diagonalCheckA == -1 || diagonalCheckB == -1) {
                        coordinateArray.add(new Coordinate(location.getX() - 1, location.getY() + 1)); // up left
                    }
                }

                if (location.getX() < width - 1 && location.getY() > 0) {
                    diagonalCheckA = map[location.getX() + 1][location.getY()];
                    diagonalCheckB = map[location.getX()][location.getY() - 1];
                    if (diagonalCheckA == -1 || diagonalCheckB == -1) {
                        coordinateArray.add(new Coordinate(location.getX() + 1, location.getY() - 1)); //down right
                    }
                }


                if (location.getX() < width - 1 && location.getY() < height - 1) {
                    diagonalCheckA = map[location.getX() + 1][location.getY()];
                    diagonalCheckB = map[location.getX()][location.getY() + 1];
                    if (diagonalCheckA == -1 || diagonalCheckB == -1) {
                        coordinateArray.add(new Coordinate(location.getX() + 1, location.getY() + 1)); // up right
                    }
                }
            }


            for (Coordinate current : coordinateArray) {
                if (current.getX() >= 0 && current.getX() < width && current.getY() >= 0 && current.getY() < height) { // range check
                    int adjacentLocationValue = map[current.getX()][current.getY()];
                    if (adjacentLocationValue == -1) { // if adjacent location is empty
                        if (!coords.contains(current)) {
                            coords.add(current);
                        }
                        map[current.getX()][current.getY()] = currentDistance;
                    } else if (adjacentLocationValue == -200) { // if adjacent location is end point
                        if (!coords.contains(current)) {
                            coords.add(current);
                        }
                    }
                }
            }

        }

        return coords;

    }

    private ArrayList<Coordinate> findAdjacentBlocksForPathBack(int[][] map, Coordinate location, ArrayList currentList, boolean allowDiagonal, int width, int height) throws Exception {

        ArrayList<Coordinate> coords = new ArrayList<Coordinate>();

        if (map[location.getX()][location.getY()] == -2) {
            throw new Exception("Cannot find adjacent locations to a blocked location");
        }

        ArrayList<Coordinate> coordinateArray = new ArrayList<Coordinate>();

        coordinateArray.add(new Coordinate(location.getX() - 1, location.getY()));
        coordinateArray.add(new Coordinate(location.getX(), location.getY() - 1));
        coordinateArray.add(new Coordinate(location.getX() + 1, location.getY()));
        coordinateArray.add(new Coordinate(location.getX(), location.getY() + 1));

        if (allowDiagonal) {

            coordinateArray.add(new Coordinate(location.getX() - 1, location.getY() - 1));
            coordinateArray.add(new Coordinate(location.getX() - 1, location.getY() + 1));
            coordinateArray.add(new Coordinate(location.getX() + 1, location.getY() - 1));
            coordinateArray.add(new Coordinate(location.getX() + 1, location.getY() + 1));
        }

        for (Coordinate current : coordinateArray) {
            if (current.getX() >= 0 && current.getX() < width && current.getY() >= 0 && current.getY() < height) {
                int locationValue = map[current.getX()][current.getY()];

                if (locationValue != -2) {
                    if (!isLocationInList(currentList, current)) {
                        coords.add(current);
                    }
                }
            }

        }

        return coords;

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

    private boolean isLocationInList(ArrayList<Coordinate> coordinateList, Coordinate coordinate) {

        return coordinateList.contains(coordinate);

    }
}

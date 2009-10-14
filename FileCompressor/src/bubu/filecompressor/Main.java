package bubu.filecompressor;

import java.io.*;
import java.util.*;
import java.util.ArrayList;

public class Main {

    private static String filePath = "F:\\Reuben\\My Documents\\NetBeansProjects\\FileCompressor\\data\\data.txt";

    public static void main(String[] args) {

        BufferedReader reader = null;
        try {
            File file = new File(filePath);
            byte[] data = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);



            fis.read(data);

            int code = 256;

            ArrayList<DataNode> dictionary = new ArrayList<DataNode>();

            for (short i = 0; i < 256; i++) {
                //System.out.println(i + " " + String.valueOf((char) i));

                DataNode node = new DataNode();
                node.setCharacter(i);
                dictionary.add(node);
            }


//            for (DataNode x : dictionary) {
//                displayDataNode(x, 1);
//            }

            String p = "";

            for (int i = 0; i < data.length; i++) {
                char c = (char) (data[i]);

                System.out.println((int) c + " - " + c);

                String temp = p + String.valueOf(c);

                if (isInDictionary(dictionary, temp)) {
                    p = p + String.valueOf(c);
                } else {
                    System.out.println("Dictionary: " + code + " " + p);
                    addToDictionary(dictionary, p + String.valueOf(c));
                    p = String.valueOf(c);
                    code++;
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void displayDataNode(DataNode node, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("->");
        }
        System.out.println(node.getCharacter());
        for (DataNode fdn : node.getFollowingCharacters()) {
            displayDataNode(fdn, depth + 1);
        }

    }

    private static boolean isInDictionary(ArrayList<DataNode> dictionary, String data) {
        boolean ret = true;

        DataNode node = null;

        for (char current : data.toCharArray()) {

            DataNode temp = new DataNode((short) current);

            int index;

            if (node == null) {
                index = dictionary.indexOf(temp);

            } else {
                index = node.getFollowingCharacters().indexOf(temp);

            }

            if (index == -1) {
                return false;
            } else {
                if (node == null) {
                    node = dictionary.get(index);
                } else {
                    node = node.getFollowingCharacters().get(index);
                }
            }

        }

        return ret;
    }

    private static void addToDictionary(ArrayList<DataNode> dictionary, String data) {

        DataNode node = null;

        for (char current : data.toCharArray()) {
            
            DataNode temp = new DataNode((short) current);

            int index;

            if (node == null) {
                index = dictionary.indexOf(temp);
            } else {
                index = node.getFollowingCharacters().indexOf(temp);
            }

            if (index > -1) {
                if (node == null) {
                    node = dictionary.get(index);
                } else {
                    node = node.getFollowingCharacters().get(index);
                }
            } else {
                DataNode newNode = new DataNode((short) current);
                node.getFollowingCharacters().add(newNode);
                node = newNode;
            }

        }

    }

    private static char[] getCharData(int start, int end, char[] source) {

        char[] data = new char[end - start + 1];

        int counter = 0;
        for (int i = start; i <= end; i++) {
            data[counter] = source[i];
            counter++;
        }

        return data;


    }

    private static LinkedList<Integer> findCharPatterns(char[] pattern, char[] source) {
        LinkedList<Integer> positions = new LinkedList<Integer>();

        for (int i = 0; i < source.length - pattern.length; i++) {

            char[] currentComparisionBytes = new char[pattern.length];
            for (int x = 0; x < pattern.length; x++) {
                currentComparisionBytes[x] = source[i + x];
            }

            if (compareCharPatterns(pattern, currentComparisionBytes)) {
                positions.add(new Integer(i));
            }
        }



        return positions;
    }

    private static boolean compareCharPatterns(char[] patternA, char[] patternB) {
        boolean ret = false;

        if (patternA.length == patternB.length) {
            boolean charComparision = true;
            for (int i = 0; i < patternA.length && charComparision; i++) {
                if (patternA[i] != patternB[i]) {
                    charComparision = false;
                }
            }
            ret = charComparision;
        }

        return ret;
    }
}

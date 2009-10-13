package bubu.filecompressor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    private static String filePath = "F:\\Reuben\\My Documents\\NetBeansProjects\\FileCompressor\\data\\data.txt";

    public static void main(String[] args) {

        BufferedReader reader = null;
        try {
            File file = new File(filePath);
            reader = new BufferedReader(new FileReader(file));
            char[] data = new char[(int) file.length()];
            reader.read(data);

            ArrayList<DataNode> nodes = new ArrayList<DataNode>();

            System.out.println(data.length);

            for (int i = 1; i < data.length; i++) {

                DataNode node = new DataNode();
                node.setCharacter(data[i - 1]);
                node.setFollowingCharacter(data[i]);


                int indexOf = nodes.indexOf(node);
                if (indexOf > -1) {
                    nodes.get(indexOf).incrementQuantity();
                } else {
                    node.incrementQuantity();
                    nodes.add(node);
                }

            }

            int totalQuantity = 0;

            for (DataNode current : nodes) {

                if (current.getQuantity() > 100) {

                    System.out.println(current.getCharacter() + "\t->\t" + current.getFollowingCharacter() + "\t:\t" + current.getQuantity());
                }
                totalQuantity += current.getQuantity();

            }

            System.out.println(totalQuantity);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
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

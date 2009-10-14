package bubu.filecompressor;

import java.io.*;
import java.util.*;
import java.util.ArrayList;

public class Main {

    private static String filePath = ".\\data\\test.txt";

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
                DataNode node = new DataNode();
                node.setCharacter(i);
                dictionary.add(node);
            }

            String p = "";

            for (int i = 0; i < data.length; i++) {
                char c = (char) (data[i] > 0 ? data[i] : data[i] + 256);

                String temp = p + String.valueOf(c);

                if (isInDictionary(dictionary, temp)) {
                    p = p + String.valueOf(c);
                } else {
                    addToDictionary(dictionary, p + String.valueOf(c));
                    p = String.valueOf(c);
                    code++;
                }

            }

            for (DataNode x : dictionary) {
                displayDataNode(x, 0);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void displayDataNode(DataNode node, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("> ");
        }
        System.out.println((char)node.getCharacter() + " (" + node.getQuantity()+")");
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

        node.incrementQuantity();

        return ret;
    }

    private static void addToDictionary(ArrayList<DataNode> dictionary, String data) {
        System.out.println("+ " + data);
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
                newNode.incrementQuantity();
                node.getFollowingCharacters().add(newNode);
                node = newNode;
            }
        }
    }


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mt.com.go.rule.engine;

import java.util.StringTokenizer;

/**
 *
 * @author Reuben
 */
public class Main {

    public static void main(String[] args) {


        String temp = "@date(peter,reuben)";
        temp = temp.substring(6, temp.length()).replace(")", "");
        System.out.println(temp);
        
        System.out.println(temp.substring(0, temp.indexOf(",")));
        System.out.println(temp.substring(temp.indexOf(",")+1, temp.length()));

        
    }
}

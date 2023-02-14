/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TEST;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ani
 */
public class CallFileParse {

    public static void main(String args[]) {
        File dir = new File("D:\\others\\RTST\\Java Code\\CallGraphParse.txt");
        Scanner fileToRead = null;
        StringBuffer callGraph = new StringBuffer();
        String regex;

        try {
            fileToRead = new Scanner(dir); //point the scanner method to a file
            //check if there is a next line and it is not null and then read it in
            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                callGraph.append(line).append("\r\n");//this small line here is to appened all text read in from the file to a string buffer which will be used to edit the contents of the file
            }
            fileToRead.close();//this is used to release the scanner from file
        } catch (FileNotFoundException ex) {
        }

        //BibleWS objBible = new BibleWS();
        String parameterOp[] = {"bgChapter2", "bgChapter3"};
        List<String> methodsAffected  = new ArrayList<String>();
        methodsAffected = recursion(callGraph.toString(), parameterOp);
        methodsAffected.add(parameterOp[1]);
        methodsAffected.add(parameterOp[2]);
        System.out.println(methodsAffected);
    }

    public static List<String> recursion(String callGraph, String[] methodAffected) {
        String regex;
        List<String> classCall = new ArrayList<String>();
        List<String> operationAffected  = new ArrayList<String>();

        int k  = 0;
        for (int i = 0; i < methodAffected.length; i++) {
            //System.out.println("CallGraph  " + callGraph);
            regex = ".*:" + methodAffected[i];
            classCall = regexList(regex, callGraph.toString());
            System.out.println("call level 1 " + classCall);

            for (String s : classCall) {
                regex = ":.* ";
                //System.out.println("\n\nVariables Added");
                String classObject = regex(regex, s);
                System.out.println("call level 2 " + classObject);

                regex = "[^:].*";
                String classOb = regex(regex, classObject);
                System.out.println("call level 2 " + classOb);

                operationAffected.add(classOb);
                k++;

                regex = ".*" + classObject;
                String classCall1 = regex(regex, callGraph.toString());
                System.out.println("call level 3 " + classCall1);

                regex = ":.* ";
                String classObject1 = regex(regex, classCall1);
                System.out.println("call level 3 " + classObject1);

                regex = "[^:].*";
                String classObject2 = regex(regex, classObject1);
                System.out.println("call level 3 " + classObject2);

                operationAffected.add(classObject2);
                k++;
            }
        }

        operationAffected = uniqueVar(operationAffected);
        return operationAffected;
    }

    public static List<String> uniqueVar(List<String> operation) {
        List<String> uniqueVariable = new ArrayList<String>();

        // List<String> list = Arrays.asList(variable);
        Set<String> set = new HashSet<String>(operation);

        // System.out.print("Remove duplicate result: ");

        String[] result = new String[set.size()];
        set.toArray(result);

        for (String s : result) {
            uniqueVariable.add(s);
            // System.out.println(s + ", ");
        }
        return uniqueVariable;
    }

    public static List<String> regexList(String regex, String code) {
        Pattern checker = Pattern.compile(regex);
        Matcher Matcher = checker.matcher(code);
        List<String> trimInfo = new ArrayList<String>();
        int Counter = 0, i = 0;
        while (Matcher.find()) {

            if (Matcher.group().length() != 0) {
                trimInfo.add(Matcher.group().trim());
                i++;
                Counter++;
            }
        }
        return trimInfo;
    }

    public static String regex(String regex, String code) {
        Pattern checker = Pattern.compile(regex);
        Matcher Matcher = checker.matcher(code);
        String trimInfo = "";
        int Counter = 0, i = 0;
        while (Matcher.find()) {

            if (Matcher.group().length() != 0) {
                trimInfo = Matcher.group().trim();
                i++;
                Counter++;
            }
        }
        return trimInfo;
    }
}

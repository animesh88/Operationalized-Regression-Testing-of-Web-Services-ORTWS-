/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CodeBreak;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ani
 */
public class SeperateOperations {

    String lastOperation = "";

    public static void main(String args[]) {
        String sourceFileFolderName = "C:\\Users\\Animesh2\\Documents\\ICSME Tool Demo\\ORTWS\\Eucalyptus\\Eucalyptus Code";
        String sourceFileName = "C:\\Users\\Animesh2\\Documents\\ICSME Tool Demo\\ORTWS\\Eucalyptus\\Eucalyptus Code\\NewClusterHandler.c";
        Scanner fileToRead = null;
        StringBuffer stringBufferOfData = new StringBuffer();
        File oldFolder = new File(sourceFileFolderName + "\\CodeBreak\\New" + "102");
        oldFolder.mkdirs();

        try {
            fileToRead = new Scanner(new File(sourceFileName));

            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                stringBufferOfData.append(line).append("\r\n");
            }
            fileToRead.close();
        } catch (FileNotFoundException ex) {
        }

        String regex = "[a-z]+ [A-Za-z_]+((\\s)+)?\\([a-zA-Z.*_, ]+\\)((\\s)+)?((\r)+)?(\n)?\\{";//+[{]//w[}]//w[A-Za-z ]+[ ][(][A-Za-z,* ][)]+[{]";
        String str = stringBufferOfData.toString();
        SeperateOperations obj = new SeperateOperations();
        obj.regexSeperateOperation(regex, str, oldFolder.getAbsolutePath());
    }

    public void CodeBreakOperation(File sourceFileName, String arg) {
        Scanner fileToRead = null;
        StringBuffer stringBufferOfData = new StringBuffer();

        try {
            fileToRead = new Scanner(sourceFileName);

            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                stringBufferOfData.append(line).append("\r\n");
            }
            fileToRead.close();
        } catch (FileNotFoundException ex) {
        }

        String regex = "[a-z]+ [A-Za-z_]+((\\s)+)?\\([a-zA-Z.*_, ]+\\)((\\s)+)?((\r)+)?(\n)?\\{";//+[{]//w[}]//w[A-Za-z ]+[ ][(][A-Za-z,* ][)]+[{]";
        String str = stringBufferOfData.toString();

        regexSeperateOperation(regex, str, arg);
    }

    private String regexSeperateOperation(String Regex, String str, String sourceFolderPath) {
        Pattern checker = Pattern.compile(Regex);
        Matcher Matcher = checker.matcher(str);
        String TrimInfo = "";
        String operationName = "";
        int Counter = 0, i = 0;
        int index = 0;
        Matcher.find(0);
        TrimInfo = (Matcher.group().trim());
        //System.out.println(" str " + TrimInfo);
        index = Matcher.start();
        CharSequence seperation = "";
        while (Matcher.find()) {
            if (Matcher.group().length() != 0) {
                TrimInfo = (Matcher.group().trim());
                i++;
                Counter++;
            }
            //System.out.println(" str " + TrimInfo);
            //System.out.println("Start Indexing of str " + Matcher.start());
            //System.out.println("End Indexing of str" + Matcher.end());
            //System.out.println("Length of str " + Matcher.group().length());
            //String s = regexOperation(regex, str);
            seperation = str.subSequence(index, Matcher.start());
            int returnIndex = seperation.toString().lastIndexOf("}");
            CharSequence operation = seperation.subSequence(0, returnIndex + 1);
            index = Matcher.start();
            //System.out.println(" Seperate \n " + operation);
            System.out.println(" Operation Content \n " + operation);

            operationName = operationName(operation);
//            System.out.println(" Operation Name: " + operationName);
            if (operationName == "") {
                try {
                    System.out.println(" Last Operation " + lastOperation);
                    String fileName1 = sourceFolderPath.toString() + "\\" + lastOperation + ".c";
                    BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1, true));
                    bufwriter.write(seperation.toString());//writes the edited string buffer to the new file
                    bufwriter.close();//closes the file
                } catch (Exception e) {//if an exception occurs
                }
            } else {
                try {
                    if (operationName.startsWith("do")) {
                        operationName = operationName.replaceFirst("do", "");
                        System.out.println("after " + operationName);
                    }

                    lastOperation = operationName;
                    System.out.println(" Writing: " + operationName);
                    String fileName1 = sourceFolderPath.toString() + "\\" + operationName + ".c";
                    BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
                    bufwriter.write(operation.toString());//writes the edited string buffer to the new file
                    bufwriter.close();//closes the file
                } catch (Exception e) {//if an exception occurs
                }
            }
        }
        seperation = str.subSequence(index, str.length());
        operationName = operationName(seperation);
        System.out.println(" Operation Name: " + operationName);
        if (operationName == "") {
            try {
                System.out.println(" Last Operation " + lastOperation);
                String fileName1 = sourceFolderPath.toString() + "\\" + lastOperation + ".c";
                BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1, true));
                bufwriter.write(seperation.toString());//writes the edited string buffer to the new file
                bufwriter.close();//closes the file
            } catch (Exception e) {//if an exception occurs
            }
        } else {
            try {
                if (operationName.startsWith("do")) {
                    operationName = operationName.replaceFirst("do", "");
                    System.out.println("after " + operationName);
                }

                String fileName1 = sourceFolderPath.toString() + "\\" + operationName + ".c";
                BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
                bufwriter.write(seperation.toString());//writes the edited string buffer to the new file
                bufwriter.close();//closes the file
            } catch (Exception e) {//if an exception occurs
            }
        }
        //System.out.println(" Seperate Ending " + operation);
        //System.out.println(" str " + TrimInfo);
        //System.out.println("\nStr:  " + TrimInfo);
        //System.out.println("Counter  " + Counter);
        return TrimInfo;
    }

    private static String regexOperation(String regex, String str) {
        Pattern checker = Pattern.compile(regex);
        Matcher Matcher = checker.matcher(str);
        String TrimInfo = "";
        int Counter = 0, i = 0;
        while (Matcher.find()) {
            if (Matcher.group().length() != 0) {
                TrimInfo = TrimInfo.concat(Matcher.group().trim());
                i++;
                Counter++;
            }

            //System.out.println("Start Indexing of str " + Matcher.start());
            //System.out.println("End Indexing of str" + Matcher.end());
            //System.out.println("Length of str " + Matcher.group().length());
        }

        //System.out.println("\nStr:  " + TrimInfo);
        //System.out.println("Counter  " + Counter);
        return TrimInfo;
    }

    private static String operationName(CharSequence operation) {
        String name = null;
        String regex = "[a-z]+ [A-Za-z_]+((\\s)+)?\\([a-zA-Z.*_, ]+\\)\r\n\\{";
        String s = regexOperation(regex, operation.toString());
//        System.out.println(" \nStr:  " + s);

        regex = "[A-Za-z_]+((\\s)+)?\\(";
        s = regexOperation(regex, s);
//        System.out.println(" \nStr:  " + s);

        regex = "[A-Za-z_]+";
        name = regexOperation(regex, s);
//        System.out.println(" \nName:  " + name);

        return name;
    }
}
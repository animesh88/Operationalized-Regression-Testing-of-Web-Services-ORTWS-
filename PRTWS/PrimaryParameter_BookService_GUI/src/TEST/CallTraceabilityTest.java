/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TEST;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ani
 */
public class CallTraceabilityTest {

    public static void main(String args[]) {
        File dir = new File("D:\\others\\RTST\\Java Code\\Operations\\" + "GetAllVerseByBookAndChapterNumber.java");
        Scanner fileToRead = null;
        StringBuffer code = new StringBuffer();
        String regex;

        try {
            fileToRead = new Scanner(dir); //point the scanner method to a file
            //check if there is a next line and it is not null and then read it in
            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                code.append(line).append("\r\n");//this small line here is to appened all text read in from the file to a string buffer which will be used to edit the contents of the file
            }
            fileToRead.close();//this is used to release the scanner from file
        } catch (FileNotFoundException ex) {
        }


        //BibleWS objBible = new BibleWS();
        System.out.println("Code  " + code);
        regex = "([ *_A-Za-z0-9-]+)([ ]+)?=([ ]+)?new([ *_A-Za-z0-9-]+\\(\\))([ ]+)?;";
//      System.out.println("\n\nVariables Added");
        String classCall = regex(regex, code.toString());
        System.out.println("call " + classCall);



        System.out.println("Code  " + code);
        regex = "([ *_A-Za-z0-9-]+)([ ]+)?=";
//      System.out.println("\n\nVariables Added");
        String classObject = regex(regex, classCall);
        System.out.println("call " + classObject);



//      verse = objBible.bibleAllVerse(chapterNumber);
        String methodCall = "";
        regex = classCall + ".([ *_A-Za-z0-9-]+\\(\\))([ ]+)?;";
        System.out.println("Class method  " + methodCall);
        
//      System.out.println("\n\nVariables Added");
        String call = regex(regex, methodCall);
        System.out.println("call " + classCall);

    }

    private static String regex(String regex, String code) {
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

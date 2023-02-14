/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reducedtestcasedata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

/**
 *
 * @author ani
 */
public class End {

    public static void main(String[] args) throws FileNotFoundException {
        String fileName = "C:\\Users\\ani\\Desktop\\AfterGS1\\dirPortBinding-TestSuite.xml";

        End obj = new End();
        CharSequence x = obj.buildEnd(fileName);
        try {
            String fileName1 = "C:\\Users\\ani\\Desktop\\AfterGS1\\Reduced testSuite.xml";
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
            bufwriter.write(x.toString());//writes the edited string buffer to the new file
            bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }
    }
    CharSequence buildEnd(String fileName) throws FileNotFoundException {

        StringBuffer stringBufferOfData = new StringBuffer();
        String start = "<?xml ";
        String end = "</con:runType>";
        Scanner fileToRead = null;
        fileToRead = new Scanner(new File(fileName)); //point the scanner method to a file
        //check if there is a next line and it is not null and then read it in
        for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
            stringBufferOfData.append(line).append("\r\n");
        }
        int startIndex = stringBufferOfData.indexOf(start);//now we get the starting point of the text we want to edit
        System.out.println(startIndex);
        int endIndex = stringBufferOfData.indexOf(end);//now we get the starting point of the text we want to edit
        System.out.println(endIndex);
        //int endIndex = startIndex + lineToEdit.length();//now we add the staring index of the text with text length to get the end index
        CharSequence x = stringBufferOfData.subSequence(startIndex, endIndex);
        return x.toString().concat(end);
    }
}

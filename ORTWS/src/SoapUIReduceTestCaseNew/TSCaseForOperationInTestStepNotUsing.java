/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SoapUIReduceTestCaseNew;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

/**
 *
 * @author ani
 */
public class TSCaseForOperationInTestStepNotUsing {

    public static void main(String[] args) throws FileNotFoundException {

        String fileName = "C:\\Users\\ani\\Desktop\\AfterGS1\\dirPortBinding-TestSuite.xml";
        File file = new File(fileName);
        String s[] = {"Index", "editFile"};
        TestCases obj = new TestCases();

        CharSequence x = obj.buildTestCase(file, s);
        try {
            String fileName1 = "C:\\Users\\ani\\Desktop\\AfterGS1\\Reduced testSuite.xml";
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
            bufwriter.write(x.toString());//writes the edited string buffer to the new file
            bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }

    }

    CharSequence buildTestCase(File fileName, String s[]) throws FileNotFoundException {
        CharSequence x = "";

        StringBuffer stringBufferOfData = new StringBuffer();
        String start = "<con:testCase";
        String end = "</con:testCase>";
        Scanner fileToRead = null;
        fileToRead = new Scanner(fileName); //point the scanner method to a file
        //check if there is a next line and it is not null and then read it in
        for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
            stringBufferOfData.append(line).append("\r\n");
        }

        int lastTC = stringBufferOfData.toString().lastIndexOf(end);
        for (int i = 0; i < s.length && s[i] != null; i++) {
            int startIndex = 0, endIndex;
            //do {
                startIndex = stringBufferOfData.indexOf(start, startIndex);//now we get the starting point of the text we want to edit
                endIndex = stringBufferOfData.indexOf(end, startIndex);//now we get the starting point of the text we want to edit
                System.out.println("end " + endIndex);
                //int endIndex = startIndex + lineToEdit.length();//now we add the staring index of the text with text length to get the end index
                CharSequence testcase = stringBufferOfData.subSequence(startIndex, endIndex).toString();
                testcase = testcase.toString().concat(end);
                System.out.println("testCase " + testcase.toString());

                //if (x.toString().contains("<con:operation>" + s[i] + "</con:operation>")) {
                    x = x.toString().concat(testcase.toString());
                    System.out.println("x " + x.toString());
                //}
            //} while (endIndex == lastTC);
        }

        return x;
    }
}

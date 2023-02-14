/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SoapUIReduceTestCaseNew;

/**
 *
 * @author ani
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class TestCases {

    public static void main(String[] args) throws FileNotFoundException {

        String fileName = "D:\\others 1\\Demo files\\ICSME Tool Demo\\ORTWS\\SaaS\\SaaS Test Suite\\dirPortBinding-TestSuite.xml";
        File file = new File(fileName);
        String s[] = {"edit", "editFile"};
        TestCases obj = new TestCases();

        CharSequence x = obj.buildTestCase(file, s);
        System.out.println(x);
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
        for (int i = 0; i < s.length && s[i] != null; i++) {
            StringBuffer stringBufferOfData = new StringBuffer();
            String start = "<con:testCase failOnError=\"true\" failTestCaseOnErrors=\"true\" keepSession=\"false\" maxResults=\"0\" name=\"" + s[i] + "\" searchProperties=\"true\">";
            String end = "</con:testCase>";
            Scanner fileToRead = null;
            fileToRead = new Scanner(fileName); //point the scanner method to a file
            //check if there is a next line and it is not null and then read it in
            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                stringBufferOfData.append(line).append("\r\n");
            }
            int startIndex = stringBufferOfData.indexOf(start);//now we get the starting point of the text we want to edit
            if (startIndex == -1) {
                System.out.println("Operation Name " + s[i]);
                start = "<con:testCase failOnError=\"true\" failTestCaseOnErrors=\"true\" keepSession=\"false\" maxResults=\"0\" name=\"" + s[i] + " TestCase\" searchProperties=\"true\">";
                startIndex = stringBufferOfData.indexOf(start);
            }
            if (startIndex != -1) {
                System.out.println(s[i] + " second if ");
                System.out.println("start" + startIndex);
                int endIndex = stringBufferOfData.indexOf(end, startIndex);//now we get the starting point of the text we want to edit
                System.out.println("end " + endIndex);
                //int endIndex = startIndex + lineToEdit.length();//now we add the staring index of the text with text length to get the end index
                CharSequence testcase = stringBufferOfData.subSequence(startIndex, endIndex).toString();
                testcase = testcase.toString().concat(end);
                x = x.toString().concat(testcase.toString());
            }
            if (startIndex == -1) {
                System.out.println(" Third if ");
                CharSequence testcase = "<con:testCase failOnError=\"true\" failTestCaseOnErrors=\"true\" keepSession=\"false\" maxResults=\"0\" name=\"" + s[i] + " TestCase\" searchProperties=\"true\">";
                testcase = testcase.toString().concat(end);
                x = x.toString().concat(testcase.toString());
            }
        }
        return x;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ConstructRRTS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

/**
 *
 * @author ani
 */
public class TestSuite {

    public static void main(String[] args) throws FileNotFoundException {

        String fileName = "C:\\Users\\ani\\Desktop\\others\\AfterGS1\\Test Suite Export Import\\CurrencyConvertorSoap-TestSuite.xml";
        File file = new File(fileName);
        String s[] = {"India", "Japan", "Australian Dollar"};
        TestCases obj = new TestCases();

        String x = obj.buildTestCase(file, s);
        System.out.println(x);
        try {
            String fileName1 = "C:\\Users\\ani\\Desktop\\others\\AfterGS1\\Reduced testSuite.xml";
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
            bufwriter.write(x);//writes the edited string buffer to the new file
            bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }

    }

    public String buildTestCase(File fileName, String s[]) throws FileNotFoundException {
        String x = "";
        String TestSuite = "";
        String end = "<con:properties/></con:testSuite>";
      
            StringBuffer stringBufferOfData = new StringBuffer();
            //String startTestStep = "<con:testStep type=\"request\" name=\""+ s[i] +"\">";
            String endTestSuite = "<con:testCase ";
            Scanner fileToRead = null;
            fileToRead = new Scanner(fileName); //point the scanner method to a file
            //check if there is a next line and it is not null and then read it in
            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                stringBufferOfData.append(line).append("\r\n");
            }
            int startTSIndex = 0; //stringBufferOfData.indexOf(startTestStep);//now we get the starting point of the text we want to edit
            System.out.println("start" + startTSIndex);
            int endTSIndex = stringBufferOfData.indexOf(endTestSuite, startTSIndex);//now we get the starting point of the text we want to edit
            System.out.println("end " + endTSIndex);
            //int endIndex = startIndex + lineToEdit.length();//now we add the staring index of the text with text length to get the end index
            String testsuite = stringBufferOfData.subSequence(startTSIndex, endTSIndex).toString();

             testsuite = testsuite.toString().concat(endTestSuite);

            
             String testcase = "";
             //x = x.concat(testsuite.toString());

            /* Test Step Call
            for (int i = 0; i < s.length && s[i]!=null; i++) {
            StringBuffer stringBufferOfData = new StringBuffer();
            String start = "<con:testCase failOnError=\"true\" failTestCaseOnErrors=\"true\" keepSession=\"false\" maxResults=\"0\" name=\""+ s[i]+ "\" searchProperties=\"true\"><con:settings/>";
            String end = "<con:properties/><con:reportParameters/></con:testCase>";
            Scanner fileToRead = null;
            TestSteps obj = new TestSteps();
            CharSequence teststep = obj.buildTestSteps(fileName, StepName);
            CharSequence testcase = start.concat(teststep.toString().concat(end));
            x = x.toString().concat(testcase.toString());
            }
             */

        TestSuite = TestSuite.concat(testsuite).concat(testcase).concat(end);

        return TestSuite;
    }

}

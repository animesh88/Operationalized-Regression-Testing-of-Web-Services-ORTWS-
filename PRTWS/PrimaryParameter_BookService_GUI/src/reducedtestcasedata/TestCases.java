/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reducedtestcasedata;

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

        String fileName = "D:\\others 1\\Demo files\\ICSME Tool Demo\\PRTWS\\BookService\\BookService Test Suite\\BooKServicePortBinding-TestSuite.xml";
        File file = new File(fileName);
        String s[] = {"GetAbstractOfChapter", "bgWS", "bgAllVerse", "GetAllVerseByBookAndChapterNumber", "GetVerseByBooKAndChapterAndVerseNumber", "bgChapter3", "bgChapter2"};
        TestCases obj = new TestCases();

        String x = obj.buildTestCase(file, s);
        System.out.println(x);
        try {
            String fileName1 = "D:\\others\\RTST\\RRTS.xml";
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
            bufwriter.write(x);//writes the edited string buffer to the new file
            bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }
    }

    public String buildTestCase(File fileName, String s[]) throws FileNotFoundException {
        Scanner fileToRead = null;
        fileToRead = new Scanner(fileName); //point the scanner method to a file
        //check if there is a next line and it is not null and then read it in
        StringBuffer stringBufferOfData = new StringBuffer();
        for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
            stringBufferOfData.append(line).append("\r\n");
        }

        String x = "";
        String TestCase = "";
        System.out.println("start " + s[0] + "  ");
        String start = "<con:testCase failOnError=\"true\" failTestCaseOnErrors=\"true\" keepSession=\"false\" maxResults=\"0\" name=\"" + s[0] + "\" searchProperties=\"true\"><con:settings/>";
        String end = "<con:properties/></con:testCase>";

        //<con:testCase failOnError="true" failTestCaseOnErrors="true" keepSession="false" maxResults="0" name="FindBookNumber TestCase" searchProperties="true">

        for (int i = 1; i < s.length && s[i] != null; i++) {

            String startTestStep = "<con:testStep type=\"request\" name=\"" + s[i] + "\">";
            String endTestStep = "</con:testStep>";

            int startTSIndex = stringBufferOfData.indexOf(startTestStep);//now we get the starting point of the text we want to edit
            System.out.println("start " + s[i] + "  " + startTSIndex);
            int endTSIndex = stringBufferOfData.indexOf(endTestStep, startTSIndex);//now we get the starting point of the text we want to edit
            System.out.println("end " + endTSIndex);
            //int endIndex = startIndex + lineToEdit.length();//now we add the staring index of the text with text length to get the end index

            if (startTSIndex != -1 && endTSIndex != -1) {
                CharSequence teststep = stringBufferOfData.subSequence(startTSIndex, endTSIndex).toString();
                teststep = teststep.toString().concat(endTestStep);
                x = x.concat(teststep.toString());
            }
        }
        int startIndex = stringBufferOfData.indexOf(start);//now we get the starting point of the text we want to edit
        System.out.println("start" + startIndex);

        if (startIndex == -1) {
            start = "<con:testCase failOnError=\"true\" failTestCaseOnErrors=\"true\" keepSession=\"false\" maxResults=\"0\" name=\"" + s[0] + " TestCase\" searchProperties=\"true\"><con:settings/>";
            startIndex = stringBufferOfData.indexOf(start);//now we get the starting point of the text we want to edit
            System.out.println("start" + startIndex);
        }

        if (startIndex != -1) {
            TestCase = TestCase.concat(start).concat(x).concat(end);
        }

        System.out.println("  Test Case " + TestCase);

        return TestCase;
    }
}

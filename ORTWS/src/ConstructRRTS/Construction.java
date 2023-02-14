/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConstructRRTS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author ani
 */
public class Construction {

    public static void main(String[] args) throws IOException {

        String testSuiteFile = "C:\\Users\\ani\\Desktop\\AfterGS1\\dirPortBinding-TestSuite.xml";
        File testSuiteFileName = new File(testSuiteFile);
        String fileName1 = "C:\\Users\\ani\\Desktop\\AfterGS1\\Reduced testSuite.xml";
        File ReduceTS = new File(fileName1);
        //String s[] = {"Index", "editFile"};

        ConstructRRTS.Construction TC = new ConstructRRTS.Construction();

        //TC.ConstructReduceTC(testSuiteFileName, s, ReduceTS);

        /* Start start = new Start();
        CharSequence x = start.buildStart(testSuiteFileName);

        TestCases TC = new TestCases();
        x = x.toString().concat(TC.buildTestCase(testSuiteFileName, s).toString());

        x = x.toString().concat("<con:properties/><con:reportParameters/></con:testSuite>");
        
        try {
        String fileName1 = "C:\\Users\\ani\\Desktop\\AfterGS1\\Reduced testSuite.xml";
        BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
        bufwriter.write(x.toString());//writes the edited string buffer to the new file
        bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }*/
    }

    public void ConstructReduceTC(File testSuiteFile, String TestCase, File ReduceTS) throws IOException {
        //String newWSDLFileName = "C:\\Users\\ani\\Desktop\\AfterGS1\\Eucalyptus 1 month ago.xml";
        //String patchFileName = "C:\\Users\\ani\\Desktop\\AfterGS1\\EucalyptusPatch.patch";
        //String[] s = {"DescribeSensors", "BundleRestartInstance"};
        //for (int i = 0; s.length>i ; i++) {
        // System.out.println("ConsRedWSDL"+ s[i]);
        //}

        Start start = new Start();
        CharSequence x = start.buildStart(testSuiteFile);

        TestCases TC = new TestCases();
        System.out.println("Test Cases " + TestCase);
        x = x.toString().concat(TestCase);

        StringBuffer stringBufferOfData = new StringBuffer();
        Scanner fileToRead = null;
        fileToRead = new Scanner(testSuiteFile); //point the scanner method to a file
        for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
            stringBufferOfData.append(line).append("\r\n");
        }

        if (stringBufferOfData.toString().contains("<con:properties/><con:reportParameters/></con:testSuite>")) {
            x = x.toString().concat("<con:properties/><con:reportParameters/></con:testSuite>");
        }
        if (stringBufferOfData.toString().contains("<con:properties/></con:testSuite>")) {
            x = x.toString().concat("<con:properties/></con:testSuite>");
        }

        try {
            //String fileName1 = "C:\\Users\\ani\\Desktop\\AfterGS1\\Reduced testSuite.xml";
            String fileName1 = ReduceTS.getAbsolutePath();
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
            bufwriter.write(x.toString());//writes the edited string buffer to the new file
            bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }
    }
}

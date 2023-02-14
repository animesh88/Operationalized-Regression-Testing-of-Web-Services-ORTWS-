/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reducedtestcasedata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        
        reducedtestcasedata.Construction TC = new reducedtestcasedata.Construction();

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

    public void ConstructReduceTC(File testSuiteFile, String TestCase, File ReduceTS) throws IOException
    {
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

        x = x.toString().concat("<con:properties/></con:testSuite>");

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
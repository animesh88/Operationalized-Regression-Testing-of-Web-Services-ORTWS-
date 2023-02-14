/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TEST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import reducedtestcasedata.TestCases;

/**
 *
 * @author ani
 */
public class Test {

    public static void main(String args[]) throws IOException {

        String parameterOp[] = new String[100];
        parameterOp[0] = "BGWS";
        parameterOp[1] = "BGVerse2";
        parameterOp[2] = "BGVerse3";
        File testSuiteFile4 = new File("D:\\others\\RTST\\Test Suite Export Import\\GetAbstractOfChapter.xml");
        File ParameterTS = new File("D:\\others\\RTST\\RRTS.xml");

        String buildTestCase[] = new String[100];
        String testCase = "";

        reducedtestcasedata.Construction TC = new reducedtestcasedata.Construction();
//        WSDLOp[0] = "GetAllVerseByBookAndChapterNumber";
//        WSDLOp[1] = "FindBookNumber";
//        WSDLOp[2] = "GetVerseByBooKAndChapterAndVerseNumber";
//        WSDLOp[3] = "GetAbstractOfChapter";

        for (int k = 0; parameterOp[k] != null && parameterOp.length > k; k++) {
            buildTestCase[0] = parameterOp[k];
            System.out.println("buildtszCase0 " + buildTestCase[0]);

            for (int i = 0; parameterOp[i] != null && parameterOp.length > i; i++) {
                buildTestCase[i + 1] = parameterOp[i];
                System.out.println("buildTestCase[i + 1] " + buildTestCase[i + 1]);
            }

            try {
                TestCases obj = new TestCases();
                String z = obj.buildTestCase(testSuiteFile4, buildTestCase);
                testCase = testCase.concat(z);
                System.out.println("testCase" + testCase);
            } catch (Exception e) {
            }//(testSuiteFile4, buildTestCase);
        }
        TC.ConstructReduceTC(testSuiteFile4, testCase, ParameterTS);
    }
}



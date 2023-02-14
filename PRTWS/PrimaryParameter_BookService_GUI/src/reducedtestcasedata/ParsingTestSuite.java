/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reducedtestcasedata;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ani
 */
public class ParsingTestSuite {

    public static void main(String[] args) {
        ParsingTestSuite obj = new ParsingTestSuite();
        File fileName = new File("C:\\Users\\ani\\Desktop\\others\\AfterGS1\\Test Suite Export Import\\CurrencyConvertorSoap-TestSuite.xml");
        String testCase[][] = new String[100][100];
        testCase = obj.testCases(fileName);
        int i = 0;
         for (; testCase[i][0] != null ; i++) {
            for (int j = 1; testCase[i][j] != null; j++) {
                System.out.println("Test Case: " + testCase[i][0]);
                System.out.println("    Test Step: " + testCase[i][j]);
            }
        }
    }

    public String[][] testCases(File fileName) {
        String TC[][] = new String[100][100];

        Document dom = null;
        ParsingTestSuite obj = new ParsingTestSuite();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            dom = db.parse(fileName);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        NodeList nodeList = null, innernodeList = null;
        //Element docEle = dom.getDocumentElement();

        NodeList nl = dom.getElementsByTagName("con:testSuite");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                Element e1 = (Element) nl.item(i);
                if (e1.getNodeType() == Node.ELEMENT_NODE) {

                    nodeList = e1.getElementsByTagName("con:testCase");

                    if (nodeList != null && nodeList.getLength() > 0) {
                        for (int j = 0; j < nodeList.getLength(); j++) {
                            Element e2 = (Element) nodeList.item(j);
                            String testCaseName = obj.getNodeAttr("name", e2);
                            System.out.println("Test case name    " + testCaseName);
                            TC[j][0] = testCaseName;
                            //System.out.println("T C    " + TC[j]);
                            //important code for Reading test  Step Name

                            innernodeList = e2.getElementsByTagName("con:testStep");

                            if (innernodeList != null && innernodeList.getLength() > 0) {
                                for (int k = 0; k < innernodeList.getLength(); k++) {
                                    Element e3 = (Element) innernodeList.item(k);
                                    //Node exec = obj.getNode("name", e3.getChildNodes());
                                    String testStepName = obj.getNodeAttr("name", e3);
                                    TC[j][k + 1] = testStepName;
                                    System.out.println("Test Step name    " + testStepName);
                                }
                            }
                        }

                    }
                }
            }
        }

        return TC;

    }

    protected Node getNode(String tagName, NodeList nodes) {
        for (int x = 0; x
                < nodes.getLength(); x++) {
            Node node = nodes.item(x);


            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                return node;


            }
        }

        return null;


    }

    protected String getNodeValue(Node node) {
        NodeList childNodes = node.getChildNodes();


        for (int x = 0; x
                < childNodes.getLength(); x++) {
            Node data = childNodes.item(x);


            if (data.getNodeType() == Node.TEXT_NODE) {
                return data.getNodeValue();


            }
        }
        return "";


    }

    protected String getNodeValue(String tagName, NodeList nodes) {
        for (int x = 0; x
                < nodes.getLength(); x++) {
            Node node = nodes.item(x);


            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();


                for (int y = 0; y
                        < childNodes.getLength(); y++) {
                    Node data = childNodes.item(y);


                    if (data.getNodeType() == Node.TEXT_NODE) {
                        return data.getNodeValue();


                    }
                }
            }
        }
        return "";


    }

    protected String getNodeAttr(String attrName, Node node) {
        NamedNodeMap attrs = node.getAttributes();


        for (int y = 0; y
                < attrs.getLength(); y++) {
            Node attr = attrs.item(y);


            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                return attr.getNodeValue();


            }
        }
        return "";


    }

    protected String getNodeAttr(String tagName, String attrName, NodeList nodes) {
        for (int x = 0; x
                < nodes.getLength(); x++) {
            Node node = nodes.item(x);


            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();


                for (int y = 0; y
                        < childNodes.getLength(); y++) {
                    Node data = childNodes.item(y);


                    if (data.getNodeType() == Node.ATTRIBUTE_NODE) {
                        if (data.getNodeName().equalsIgnoreCase(attrName)) {
                            return data.getNodeValue();


                        }
                    }
                }
            }
        }

        return "";

    }
}

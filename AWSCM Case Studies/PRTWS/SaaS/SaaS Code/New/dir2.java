/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.Scanner;

/**
 *
 * @author ani
 */
@WebService()
public class dir {

    @WebMethod(operationName = "Index")
    public String Index(@WebParam(name = "Indexing") String Indexing) {


        File X = new File(Indexing);
        new visitAllDirsAndFiles(X);

        return "end";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "Searching")
    public String Searching(@WebParam(name = "QueryString") String QueryString) {
        String outputString = null;
        try {
            //new Search(QueryString);
            CommonsHttpSolrServer server = null;

            try {
                server = new CommonsHttpSolrServer("http://localhost:8983/solr");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // escape special characters
            SolrQuery query = new SolrQuery();
            query.setQuery(QueryString);

            //try {
            QueryResponse rsp = server.query(query);
            rsp.getResults();
            SolrDocumentList listOfdoc = rsp.getResults();
            String Output = ("\n\n***** The total results returned is = " + listOfdoc.size() + " *****\n");
            String output = "\n***********************************************************\n";

            for (Iterator<SolrDocument> it = listOfdoc.iterator(); it.hasNext();) {
                SolrDocument solrDocument = it.next();
                String id = (String) solrDocument.getFieldValue("id");
                //Output.concat(" "+ id +" ");
                //System.out.println(id);
                String name = (String) solrDocument.getFieldValue("name");

                output = output.concat("\n ID: " + id.concat("           Path: " + name + " \n\n"));
                //System.out.println(output);
                //X.concat(output);
            }
            outputString = Output.concat(output);

        } catch (SolrServerException ex) {
        }

        return outputString;

    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "readingFile")
    public String readingFile(@WebParam(name = "File") String File) {
        String str = "";
        String output = "";
        try {
            FileReader reader = null;
            try {
                //String dir = "File";
                reader = new FileReader(new File(File));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader bufferReader = new BufferedReader(reader);

            while ((str = bufferReader.readLine()) != null) {
                output = output.concat(str);
            }
            output = output.concat("");
        } catch (IOException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*String msg = output;
        String pubKey = "";

        BigInteger message = new BigInteger(msg.getBytes());
        BigInteger publicKey = new BigInteger(pubKey);
        BigInteger modulus = new BigInteger(mod);
        BigInteger encrypt = message.modPow(publicKey, modulus);
        //System.out.println("message   = " + message);
        //System.out.println("encrpyted = " + encrypt);
        String s = "out \n ";
        s = s.concat("Message   \n ").concat(message.toString()).concat("\nEncrpt message").concat(encrypt.toString());
        return encrypt.toString();
        */
        return output;
    }

    /**
     * Web service operation author Animesh Chaturvedi
     */
    @WebMethod(operationName = "editFile")
    public String editFile(@WebParam(name = "fileName") String fileName, @WebParam(name = "lineToEdit") String lineToEdit, @WebParam(name = "replacementText") String replacementText) {
        try {
            Scanner fileToRead = null;
            StringBuffer stringBufferOfData = new StringBuffer();

            try {
                fileToRead = new Scanner(new File(fileName)); //point the scanner method to a file
                //check if there is a next line and it is not null and then read it in
                for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
                    stringBufferOfData.append(line).append("\r\n");//this small line here is to appened all text read in from the file to a string buffer which will be used to edit the contents of the file
                }
                fileToRead.close();//this is used to release the scanner from file
            } catch (FileNotFoundException ex) {
            }

            int startIndex = stringBufferOfData.indexOf(lineToEdit);//now we get the starting point of the text we want to edit
            int endIndex = startIndex + lineToEdit.length();//now we add the staring index of the text with text length to get the end index
            stringBufferOfData.replace(startIndex, endIndex, replacementText);

            try {
                BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName));
                bufwriter.write(stringBufferOfData.toString());//writes the edited string buffer to the new file
                bufwriter.close();//closes the file
            } catch (Exception e) {//if an exception occurs
            }

        } catch (Exception e) {
        }

        String str = "";
        String output = "";
        try {
            FileReader reader = null;
            try {
                //String dir = "File";
                reader = new FileReader(new File(fileName));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader bufferReader = new BufferedReader(reader);

            while ((str = bufferReader.readLine()) != null) {
                output = output.concat(str);
            }
            output = output.concat("");
        } catch (IOException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;

    }

    /**
     * Web service operation
     
    @WebMethod(operationName = "EditFile")
    public String EditFile(@WebParam(name = "fileName") String fileName, @WebParam(name = "text") String text) {
        String output = null;
       /* String output = null;
        String str;
        try {
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName));
            bufwriter.write(text);//writes the edited string buffer to the new file
            bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }

        try {
            FileReader reader = null;
            try {
                //String dir = "File";
                reader = new FileReader(new File(fileName));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader bufferReader = new BufferedReader(reader);

            while ((str = bufferReader.readLine()) != null) {
                output = output.concat(str);
            }
        } catch (IOException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    
     * Web service operation
     */
    @WebMethod(operationName = "edit")
    public String edit(@WebParam(name = "filename")
    String filename, @WebParam(name = "text")
    String text) {
       
        BufferedWriter bufwriter = null;
        try {
            
            String str;
            bufwriter = new BufferedWriter(new FileWriter(filename));
            bufwriter.write(text); //writes the edited string buffer to the new file
            bufwriter.close(); //closes the file
            
        } catch (IOException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufwriter.close();
            } catch (IOException ex) {
                Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String str = "";
        String output = "";
        try {
            FileReader reader = null;
            try {
                //String dir = "File";
                reader = new FileReader(new File(filename));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader bufferReader = new BufferedReader(reader);

            while ((str = bufferReader.readLine()) != null) {
                output = output.concat(str);
            }
            output = output.concat("");
        } catch (IOException ex) {
            Logger.getLogger(dir.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }
}

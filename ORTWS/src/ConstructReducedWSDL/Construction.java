/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConstructReducedWSDL;

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

        String newWSDLFileName = "C:\\Users\\Animesh2\\Documents\\ICSME Tool Demo\\ORTWS\\Eucalyptus\\Eucalyptus WSDL\\Eucalyptus new WSDL.xml";
        File WSDL2 = new File(newWSDLFileName);
        //String patchFileName = "C:\\Users\\ani\\Desktop\\AfterGS1\\EucalyptusPatch.patch";
        String[] s = {"doModifyNode", "ccInstance_to_ncInstance", "ncInstance_to_ccInstance", "doMigrateInstances", "refresh_instances", "init_config", "init_thread", "print_ccInstance", "doDetachVolume", "doDescribeInstances", "print_abbreviated_instances", "doAttachVolume", "find_instanceCacheId", "ncClientCall"};

        for (int i = 0; i < s.length && s[i] != null; i++) {
            System.out.println("before " + s[i]);
            if (s[i].startsWith("do")) {
                s[i] = s[i].replaceFirst("do", "");
                System.out.println("after " + s[i]);
            }
        }
        //Clean clean = new Clean();
        //clean.cleanPatch(patchFileName);

        StartDefinition obj = new StartDefinition();
        String x = obj.buildDefinition(WSDL2);

        BuildMessage objmsg = new BuildMessage();
        String message = objmsg.buildMessage(WSDL2, s);
        x = x.concat(message);

        BuildPortType objoperation = new BuildPortType();
        String strPort = objoperation.buildportType(WSDL2, s);
        x = x.concat("\n" + strPort);

        BuildBinding objBinding = new BuildBinding();
        String strBinding = objBinding.buildBinding(WSDL2, s);
        x = x.concat("\n" + strBinding);

        EndServiceDefinition objEnd = new EndServiceDefinition();
        String strEnd = objEnd.buildDefinition(WSDL2);
        x = x.concat("\n" + strEnd);

        System.out.println(x);
        try {
            String fileName1 = "C:\\Users\\ani\\Desktop\\AfterGS1\\ReduceWSDLEucalyptus.xml";
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
            bufwriter.write(x);//writes the edited string buffer to the new file
            bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }
    }

    public void ConstructReduceWSDL(File WSDL1, String s[], File ReduceWSDL) throws IOException {
        //String newWSDLFileName = "C:\\Users\\ani\\Desktop\\AfterGS1\\Eucalyptus 1 month ago.xml";
        //String patchFileName = "C:\\Users\\ani\\Desktop\\AfterGS1\\EucalyptusPatch.patch";
        //String[] s = {"DescribeSensors", "BundleRestartInstance"};      

        //Clean clean = new Clean();
        //clean.cleanPatch(patchFileName);

        StartDefinition obj = new StartDefinition();
        String x = obj.buildDefinition(WSDL1);

        BuildMessage objmsg = new BuildMessage();

        String message = objmsg.buildMessage(WSDL1, s);
        x = x.concat(message);

        BuildPortType objoperation = new BuildPortType();
        String strPort = objoperation.buildportType(WSDL1, s);
        x = x.concat("\n" + strPort);

        BuildBinding objBinding = new BuildBinding();
        String strBinding = objBinding.buildBinding(WSDL1, s);
        x = x.concat("\n" + strBinding);

        EndServiceDefinition objEnd = new EndServiceDefinition();
        String strEnd = objEnd.buildDefinition(WSDL1);
        x = x.concat("\n" + strEnd);

        try {
            String fileName1 = null;
            fileName1 = ReduceWSDL.getPath();
            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName1));
            bufwriter.write(x);//writes the edited string buffer to the new file
            bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
        }
    }
}
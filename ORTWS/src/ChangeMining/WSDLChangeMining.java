/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ChangeMining;

import java.util.List;
import com.predic8.wsdl.*;
import com.predic8.wsdl.diff.WsdlDiffGenerator;
import com.predic8.soamodel.Difference;
import java.io.File;

/**
 *
 * @author ani
 */
public class WSDLChangeMining {

    public static void main(String[] args) {
        File firstDir = new File("C:\\Users\\AnimeshChaturvedi\\Documents\\NetBeansProjects\\AWSCM\\AWSCM Case Studies\\ORTWS\\SaaS\\SaaS WSDL\\");
        File[] observationFile = firstDir.listFiles();

        for (int i = 0; i < firstDir.list().length - 1; i++) {
            System.out.println("\n" + observationFile[i].getName() +  " and " + observationFile[i+1].getName());
            File WSDL1 = new File(observationFile[i].getAbsolutePath());
            File WSDL2 = new File(observationFile[i+1].getAbsolutePath());
            WSDLChangeMining obj = new WSDLChangeMining();
            String[] Difference = obj.Diff(WSDL1, WSDL2);

            System.out.println();
            System.out.println(" All Change ");
            for (int z = 0; Difference[z] != null; z++) {
                System.out.println(z + " " + Difference[z]);
            }
            String[] DiffOp = obj.ChangeOperation(Difference);
            String[] Diffschema = obj.ChangeSchema(Difference);

            System.out.println();
            System.out.println(" Operation Change ");
            for (int z = 0; DiffOp[z] != null; z++) {
                System.out.println(z + " " + DiffOp[z]);
            }

            System.out.println();
            System.out.println(" Schema Change ");
            for (int z = 0; Diffschema[z] != null; z++) {
                System.out.println(z + " " + Diffschema[z]);
            }
        }
    }
    

    public String[] Diff(File WSDL1, File WSDL2) {

        String[] Difference = null;

        WSDLParser parser = new WSDLParser();

        Definitions wsdl1 = parser.parse(WSDL1.getPath());

        Definitions wsdl2 = parser.parse(WSDL2.getPath());

        WsdlDiffGenerator diffGen = new WsdlDiffGenerator(wsdl1, wsdl2);

        List<Difference> lst = diffGen.compare();
        WSDLChangeMining obj = new WSDLChangeMining();
        for (Difference diff : lst) {
            Difference = obj.dumpChanges(diff, "");
        }
        for (int z = 0; Difference[z] != null; z++) {
            for (int x = z + 1; Difference[x] != null; x++) {
                if ((Difference[x]).equalsIgnoreCase((Difference[z]))) {
                    Difference[x] = null;
                }
            }
        }
        return Difference;
    }

    int i = 0;
    String[] Diff = new String[1000];

    private String[] dumpChanges(Difference diff, String level) {

        System.out.println(level + diff.getDescription());
        WSDLChangeMining obj = new WSDLChangeMining();
        String s = obj.RetrievChange(diff.getDescription());
        if (s != null) {
            Diff[i] = s;
            i++;
        }
        for (Difference localDiff : diff.getDiffs()) {
            dumpChanges(localDiff, level + "  ");
        }

        return Diff;
    }

    private String RetrievChange(String description) {
        String s = null;
        int beginIndex, endIndex;
        String schema = "Schema";
        if (description.startsWith("Operation") && description.endsWith("added.")) {
            String opStrart = "Operation ";
            String opEnd = " added";
            beginIndex = description.indexOf(opStrart) + opStrart.length();
            endIndex = description.indexOf(opEnd);
//            System.out.println(" beginIndex " + beginIndex + " EndIndex " + endIndex);
            s = description.substring(beginIndex, endIndex);
//            System.out.println(s);
//            System.out.println();
        }
        if (description.startsWith("Schema")) {
            s = "YYYSchemaYYY";
            System.out.println(s);
        }
        if (description.startsWith("ComplexType") && description.endsWith("ResponseType added.")) {
            String opStrart = "ComplexType ";
            String opEnd = "ResponseType added";
            beginIndex = description.indexOf(opStrart) + opStrart.length();
            endIndex = description.indexOf(opEnd);
//            System.out.println(" beginIndex " + beginIndex + " EndIndex " + endIndex);
            s = description.substring(beginIndex, endIndex);
//            System.out.println(s);
        } else if (description.startsWith("ComplexType") && description.endsWith("Type added.")) {
            String opStrart = "ComplexType ";
            String opEnd = "Type added";
            beginIndex = description.indexOf(opStrart) + opStrart.length();
            endIndex = description.indexOf(opEnd);
//            System.out.println(" beginIndex " + beginIndex + " EndIndex " + endIndex);
            s = description.substring(beginIndex, endIndex);
//            System.out.println(s);
        }
        if (description.startsWith("Element") && description.endsWith("Response added.")) {
            String opStrart = "Element ";
            String opEnd = "Response added";
            beginIndex = description.indexOf(opStrart) + opStrart.length();
            endIndex = description.indexOf(opEnd);
//            System.out.println(" beginIndex " + beginIndex + " EndIndex " + endIndex);
            s = description.substring(beginIndex, endIndex);
//            System.out.println(s);
        } else if (description.startsWith("Element") && description.endsWith(" added.")) {
            String opStrart = "Element ";
            String opEnd = " added";
            beginIndex = description.indexOf(opStrart) + opStrart.length();
            endIndex = description.indexOf(opEnd);
//            System.out.println(" beginIndex " + beginIndex + " EndIndex " + endIndex);
            s = description.substring(beginIndex, endIndex);
//            System.out.println(s);
//            System.out.println();
        }
        return s;
    }

    public String[] ChangeOperation(String[] changeOperation) {
        String[] DiffOp = new String[100];
        int z = 0;
        for (int y = 0; changeOperation[z] == null && changeOperation[z] != ("YYYSchemaYYY"); z++, y++) {
            DiffOp[y] = changeOperation[z];
//            System.out.println(z + " " + DiffOp[y]);
        }

        return DiffOp;
    }

    public String[] ChangeSchema(String[] changeOperation) {
        String[] ChangeSchema = new String[1000];
        int z = 0;

        for (; changeOperation[z] == null && changeOperation[z] != ("YYYSchemaYYY"); z++) {
            //System.out.println(z + " " + Difference[z]);
        }
        z++;
        //System.out.println(z + " " + ChangeSchema[z]);
        for (int y = 0; changeOperation[z] != null; z++, y++) {
            ChangeSchema[y] = changeOperation[z];
//            System.out.println(y + " " + ChangeSchema[y]);
        }
        return ChangeSchema;
    }
}

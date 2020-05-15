package juppaal;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.List;

public class UppaalUtil {

    public static final String PATH = ".\\src\\main\\resources\\verification\\";

    public static void writeXml(NTA nta) throws IOException {
        Document document = DocumentHelper.createDocument();
        document.addDocType("nta","-//Uppaal Team//DTD Flat System 1.1//EN","http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd");

        Element root = document.addElement("nta");

        Element declaration = root.addElement("declaration");
        declaration.addText(nta.getDeclaration());

        for(Template template: nta.getTemplateList()){
            Element templateElement = root.addElement("template");

            Element name = templateElement.addElement("name");
            name.addText(template.getName());

            Element templateDeclaration = templateElement.addElement("declaration");
            templateDeclaration.addText(template.getDeclaration());

            for(UppaalLocation uppaalLocation: template.getLocationList()){
                Element location = templateElement.addElement("location");
                location.addAttribute("id",template.getName()+uppaalLocation.getId());
                Element locationName = location.addElement("name");
                locationName.addText("s"+uppaalLocation.getName());
            }

            Element init = templateElement.addElement("init");
            init.addAttribute("ref",template.getName()+template.getInitLocation().getId());

            List<UppaalTransition> uppaalTransitionList = template.getTransitionList();
            for(UppaalTransition uppaalTransition: uppaalTransitionList){
                Element transition = templateElement.addElement("transition");
                Element source = transition.addElement("source");
                source.addAttribute("ref",template.getName()+uppaalTransition.getSource().getId());
                Element target = transition.addElement("target");
                target.addAttribute("ref",template.getName()+uppaalTransition.getTarget().getId());
                Element sycnLabel = transition.addElement("label");
                sycnLabel.addAttribute("kind", "synchronisation");
                sycnLabel.addText(uppaalTransition.getSynchronisation().getText());
                Element guardLabel = transition.addElement("label");
                guardLabel.addAttribute("kind","guard");
                guardLabel.addText(uppaalTransition.getGuard().getText());
                Element assignmentLabel = transition.addElement("label");
                assignmentLabel.addAttribute("kind","assignment");
                assignmentLabel.addText(uppaalTransition.getAssignment().getText());
            }
        }

        Element system = root.addElement("system");
        system.addText(nta.getSystem());

        XMLWriter writer = new XMLWriter( new FileOutputStream(PATH+"nta.xml"),
                OutputFormat.createPrettyPrint());
        writer.write(document);
        writer.close();
    }

    public static void writeProperty(String property){
        OutputStreamWriter writer = null;
        try{
            writer = new OutputStreamWriter(new FileOutputStream(PATH+"nta.q"));
            writer.write(property);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isSatisfied(NTA nta, String property){
        StringBuilder sb = new StringBuilder();
        try{
            writeXml(nta);
            writeProperty(property);
            String cmd = "verifyta -S2 -t1 -f  .\\src\\main\\resources\\verification\\trace .\\src\\main\\resources\\verification\\nta.xml .\\src\\main\\resources\\verification\\nta.q";
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            InputStreamReader isr=new InputStreamReader(in);
            BufferedReader br=new BufferedReader(isr);
            String line=null;
            while((line=br.readLine())!=null)
            {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(sb.indexOf("NOT")==-1){
            return true;
        }
        return false;
    }

}

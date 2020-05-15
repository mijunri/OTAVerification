package juppaal;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import ota.Location;
import ota.OTA;
import ota.Transition;

import java.io.*;
import java.util.*;

public class UppaalBuilder {

    public static final String PATH = ".\\src\\main\\resources\\verification\\";

    public static Template buildAnTemplate(OTA ota){
        String name = ota.getName();
        String declaration = "clock x;";
        List<UppaalLocation> uppaalLocationList = new ArrayList<>();
        List<UppaalTransition> uppaalTransitionList = new ArrayList<>();
        UppaalLocation initLocation = null;

        Map<Location,UppaalLocation> uppaalLocationMap = new HashMap<>();
        for(Location location: ota.getLocationList()){
            UppaalLocation uppaalLocation = new UppaalLocation(String.valueOf(location.getId()),location.getName());
            uppaalLocationList.add(uppaalLocation);
            uppaalLocationMap.put(location,uppaalLocation);
            if(location == ota.getInitLocation()){
                initLocation = uppaalLocation;
            }
        }

        for(Transition transition: ota.getTransitionList()){
            UppaalLocation source = uppaalLocationMap.get(transition.getSourceLocation());
            UppaalLocation target = uppaalLocationMap.get(transition.getTargetLocation());
            Label synchronisation = new Label("synchronisation",transition.getAction());
            Label guard = new Label("kind",transition.getTimeGuard().toExpression());
            Label assignment = null;
            if(transition.isReset()){
                assignment = new Label("assignment","x:=0");
            }else {
                assignment = new Label("assignment","");
            }
            UppaalTransition uppaalTransition = new UppaalTransition(source,target,synchronisation,guard,assignment);
            uppaalTransitionList.add(uppaalTransition);
        }

        return new Template(name,declaration,initLocation,uppaalLocationList,uppaalTransitionList);
    }

    public static NTA buildAnNTA(OTA... otas){

        String declaration = null;
        List<Template> templateList = new ArrayList<>();
        String system = null;

        Set<String> broadcaseChanSet = new HashSet<>();
        Set<String> chanSet = new HashSet<>();
        Map<String, OTA> stringRTAMap = new HashMap<>();
        for(int i = 0; i <otas.length; i++){
            Set<String> sigma = otas[i].getSigma();
            for(String action:sigma){
                if(broadcaseChanSet.contains(action)){
                    chanSet.add(action);
                }else {
                    broadcaseChanSet.add(action);
                    stringRTAMap.put(action,otas[i]);
                }
            }
        }
        broadcaseChanSet.removeAll(chanSet);

        StringBuilder sb = new StringBuilder();
        for(String action: broadcaseChanSet){
            sb.append("broadcast chan ").append(action).append(";").append("\n");
        }
        for(String action: chanSet){
            sb.append("chan ").append(action).append(";").append("\n");
        }
        declaration = sb.toString();

        for(int i = 0; i < otas.length; i++){
            Template template = UppaalBuilder.buildAnTemplate(otas[i]);
            templateList.add(template);
            List<UppaalTransition> uppaalTransitionList = template.getTransitionList();
            for(UppaalTransition uppaalTransition: uppaalTransitionList){
                Label synchronisation = uppaalTransition.getSynchronisation();
                String action = synchronisation.getText();
                if(stringRTAMap.get(action) == otas[i]){
                    action+="!";
                }else {
                    action+="?";
                }
                synchronisation.setText(action);
            }
        }

        StringBuilder sb1 = new StringBuilder();
        sb1.append("system ");
        for(Template template: templateList){
            sb1.append(template.getName()).append(", ");
        }
        int index = sb1.lastIndexOf(",");
        sb1.replace(index,index+1,";");
        system = sb1.toString();

        return new NTA(declaration,templateList,system);
    }

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

    public static boolean isSatisfied(NTA nta, String property) throws IOException {
        writeXml(nta);
        writeProperty(property);
        String cmd = "verifyta -S2 -t1 -f  .\\src\\main\\resources\\verification\\trace.xtr .\\src\\main\\resources\\verification\\nta.xml .\\src\\main\\resources\\verification\\nta.q";
        Process process = Runtime.getRuntime().exec(cmd);
        InputStream in = process.getInputStream();
        InputStreamReader isr=new InputStreamReader(in);
        BufferedReader br=new BufferedReader(isr);
        String line=null;
        StringBuilder sb = new StringBuilder();
        while((line=br.readLine())!=null)
        {
            sb.append(line).append("\n");
        }
        if(sb.indexOf("NOT")==-1){
            return true;
        }
        return false;
    }


}

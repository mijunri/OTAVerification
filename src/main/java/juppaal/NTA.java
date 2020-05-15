package juppaal;

import java.util.List;

public class NTA {
    private  String declaration;
    private List<Template> templateList;
    private String system;

    public NTA(String declaration, List<Template> templateList, String system) {
        this.declaration = declaration;
        this.templateList = templateList;
        this.system = system;
    }

    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    public List<Template> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<Template> templateList) {
        this.templateList = templateList;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Override
    public String toString() {
        return "NTA{" +
                "declaration='" + declaration + '\'' +
                ", templateList=" + templateList +
                ", system='" + system + '\'' +
                '}';
    }
}

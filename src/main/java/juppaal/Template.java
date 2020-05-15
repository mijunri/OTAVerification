package juppaal;

import java.util.ArrayList;
import java.util.List;

public class Template {
    private String name;
    private String declaration;
    private UppaalLocation initLocation;
    private List<UppaalLocation> uppaalLocationList = new ArrayList<>();
    private List<UppaalTransition> uppaalTransitionList = new ArrayList<>();

    public Template(String name, String declaration, UppaalLocation initLocation,
                    List<UppaalLocation> uppaalLocationList, List<UppaalTransition> uppaalTransitionList) {
        this.name = name;
        this.declaration = declaration;
        this.initLocation = initLocation;
        this.uppaalLocationList = uppaalLocationList;
        this.uppaalTransitionList = uppaalTransitionList;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    public UppaalLocation getInitLocation() {
        return initLocation;
    }

    public void setInitLocation(UppaalLocation initLocation) {
        this.initLocation = initLocation;
    }

    public List<UppaalLocation> getLocationList() {
        return uppaalLocationList;
    }

    public void setLocationList(List<UppaalLocation> locationList) {
        this.uppaalLocationList = locationList;
    }

    public List<UppaalTransition> getTransitionList() {
        return uppaalTransitionList;
    }

    public void setTransitionList(List<UppaalTransition> transitionList) {
        this.uppaalTransitionList = transitionList;
    }

}

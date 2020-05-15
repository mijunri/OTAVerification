package lstar;

import ota.OTA;
import ota.TimeWords;


import java.util.Map;
import java.util.Set;

public abstract class AbstractLearningMethod implements LearningMethod {
    private String name;
    private Set<String> sigma;
    private Membership membership;
    private OTA hypothesis;
    private Map<String,String> resetMap;

    public AbstractLearningMethod(String name, Set<String> sigma, Membership membership) {
        this.name = name;
        this.sigma = sigma;
        this.membership = membership;
        this.resetMap = membership.getResetMap();
    }



    public boolean answer(TimeWords timeWords){
        return membership.answer(timeWords);
    }

    public Set<String> getSigma(){
        return sigma;
    }

    public void setHypothesis(OTA hypothesis){
        this.hypothesis = hypothesis;
    }

    @Override
    public OTA getHypothesis(){
        return hypothesis;
    }

    public String getName(){
        return name;
    }

    public Map<String, String> getResetMap() {
        return resetMap;
    }
}

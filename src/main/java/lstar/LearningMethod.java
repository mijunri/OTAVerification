package lstar;


import ota.OTA;
import ota.TimeWords;

public interface LearningMethod {


    void refine(TimeWords timeWords);

    void buildHypothesis();

    OTA getHypothesis();

}

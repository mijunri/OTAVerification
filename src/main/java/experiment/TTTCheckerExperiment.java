package experiment;

import checking.Checker;
import juppaal.UppaalEquivalence;
import juppaal.UppaalMembership;
import lstar.EquivalenceQuery;
import lstar.LearningMethod;
import lstar.Membership;
import ota.OTA;
import ota.OTABuilder;
import ttt.DTree;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TTTCheckerExperiment {

    public static void main(String[] args) throws IOException {
        OTA input = OTABuilder.getOTAFromJsonFile(".\\src\\main\\resources\\json\\b.json");
        OTA output = OTABuilder.getOTAFromJsonFile(".\\src\\main\\resources\\json\\a.json");
        OTA property = OTABuilder.getOTAFromJsonFile(".\\src\\main\\resources\\json\\p.json");
        Map<String,String> resetMap = new HashMap<>();
        resetMap.put("a","r");
        resetMap.put("b","n");
        resetMap.put("c","r");
        Set<String> sigma = new HashSet<>();
        sigma.add("a");
        sigma.add("b");
        sigma.add("c");
        OTA[] m1 = new OTA[]{input};
        OTA[] m2 = new OTA[]{output};
        Membership membership = new UppaalMembership(m1,m2,property,resetMap);
        EquivalenceQuery equivalenceQuery = new UppaalEquivalence(m1,m2,property);
        LearningMethod learningMethod = new DTree("assume",sigma,membership);
        Checker checker = new Checker(m1,m2,property,membership,equivalenceQuery,learningMethod);
        boolean answer = checker.check();
        System.out.println(answer);
    }
}

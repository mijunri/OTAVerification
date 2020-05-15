package juppaal;

import lstar.Membership;

import ota.*;

import java.io.IOException;
import java.util.*;

public class UppaalMembership implements Membership{
    private OTA[] m1;
    private OTA[] m2;
    private OTA property;
    private Map<String,String> resetMap;
    private int count = 0;
    public UppaalMembership(OTA[] m1, OTA[] m2, OTA property, Map<String,String> resetMap) {
        this.m1 = m1;
        this.m2 = m2;
        this.property = property;
        this.resetMap = resetMap;
    }

    @Override
    public boolean answer(TimeWords timeWords){
        count++;
        try{
            Set<String> sigma = new HashSet<>();
            for(OTA o1:m1){
                sigma.addAll(o1.getSigma());
            }
            sigma.addAll(property.getSigma());
            for(OTA o2:m2){
                sigma.retainAll(o2.getSigma());
            }
            OTA trace = traceRTA(sigma,timeWords);
            OTA[] otas = new OTA[m1.length+2];
            for(int i = 0; i < m1.length; i++){
                otas[i] = m1[i];
            }
            otas[m1.length] = trace;
            otas[m1.length+1] = property;
            NTA nta = UppaalBuilder.buildAnNTA(otas);
            StringBuilder sb = new StringBuilder();
            Template propertyTemplate = UppaalBuilder.buildAnTemplate(property);
            List<UppaalLocation> uppaalLocationList = propertyTemplate.getLocationList();
            int size = uppaalLocationList.size();
            UppaalLocation last = uppaalLocationList.get(size-1);
            sb.append("A[] not ").append(propertyTemplate.getName()).append(".s").append(last.getName());
            boolean accepted = UppaalUtil.isSatisfied(nta,sb.toString());
            return accepted;
        }catch (Exception e){
            System.out.println("membership error");
        }
        return false;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Map<String, String> getResetMap() {
        return resetMap;
    }

    private OTA traceRTA(Set<String> sigma, TimeWords timeWords) {
        setReset(timeWords);
        List<Location> locationList = new ArrayList<>();
        List<Transition> transitionList = new ArrayList<>();
        List<TimeWord> wordList = timeWords.getWordList();
        for(int i = 0; i <= wordList.size(); i ++){
            Location location = new Location(i+1,String.valueOf(i+1),false,false);
            if(i==0){
                location.setInit(true);
            }
            locationList.add(location);
        }

        for(int i = 0; i < wordList.size(); i ++){
            TimeWord word = wordList.get(i);
            TimeGuard timeGuard = new TimeGuard(word);
            String action = word.getAction();
            Transition transition = new Transition(locationList.get(i),locationList.get(i+1),timeGuard,word.getReset(),action);
            transitionList.add(transition);
        }

        return new OTA("trace",sigma,locationList,transitionList);
    }

    private void setReset(TimeWords timeWords){
        for(TimeWord w:timeWords.getWordList()){
            w.setReset(resetMap.get(w.getAction()));
        }
    }
}

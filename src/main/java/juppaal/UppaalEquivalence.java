package juppaal;

import lstar.EquivalenceQuery;
import ota.OTA;
import ota.OTABuilder;
import ota.TimeWord;
import ota.TimeWords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UppaalEquivalence implements EquivalenceQuery {
    private OTA[] m1;
    private OTA[] m2;
    private OTA property;

    private long cost;

    public UppaalEquivalence(OTA[] m1, OTA[] m2, OTA property) {
        this.m1 = m1;
        this.m2 = m2;
        this.property = property;
    }

    public long getCost() {
        return cost;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public TimeWords findCounterExample1(OTA assume){
        OTA assumeption = OTABuilder.removeSink(assume);
        System.out.println(assumeption);
        OTA[] otas = new OTA[m1.length+2];
        otas[m1.length] = assumeption;
        otas[m1.length+1] = property;
        for(int i = 0; i < m1.length; i++){
            otas[i] = m1[i];
        }
        NTA nta = UppaalBuilder.buildAnNTA(otas);
        Template propertyTemplate = UppaalBuilder.buildAnTemplate(property);
        StringBuilder sb = new StringBuilder();
        List<UppaalLocation> uppaalLocationList = propertyTemplate.getLocationList();
        int size = uppaalLocationList.size();
        UppaalLocation last = uppaalLocationList.get(size-1);
        sb.append("A[] not ").append(propertyTemplate.getName()).append(".s").append(last.getName());

        boolean isAccepted = UppaalUtil.isSatisfied(nta,sb.toString());
        if(isAccepted){
            return null;
        }else {
            return inputAnTimeWords();
        }
    }

    @Override
    public TimeWords findCounterExample2(OTA hypothesis){
        OTA ota = hypothesis;
        String name = ota.getSink().getName();

        System.out.println(hypothesis);
        OTA[] otas = new OTA[m2.length+1];
        otas[m2.length] = ota;
        for(int i = 0; i < m2.length; i++){
            otas[i] = m2[i];
        }
        NTA nta = UppaalBuilder.buildAnNTA(otas);
        Template hypothesisTemplate = UppaalBuilder.buildAnTemplate(ota);
        StringBuilder sb = new StringBuilder();
        sb.append("A[] not ").append(hypothesisTemplate.getName()).append(".s").append(name);
        boolean isAccepted = UppaalUtil.isSatisfied(nta,sb.toString());
        if(isAccepted){
            return null;
        }else {
            return inputAnTimeWords();
        }
    }

    @Override
    public Set<String> getSigma() {
        return null;
    }

    private TimeWords inputAnTimeWords(){
        System.out.println("请给出一个反例：先给出反例长度n，再给出n行输入：\n");
        BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        TimeWords timeWords = null;
        try{
            int n = Integer.parseInt(rd.readLine());
            List<TimeWord> words = new ArrayList<>();
            for(int i = 0; i < n ;i++){
                String[] str = rd.readLine().split(",");
                TimeWord word = new TimeWord(str[0],Double.parseDouble(str[1]));
                words.add(word);
            }
            timeWords = new TimeWords(words);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return timeWords;
    }
}

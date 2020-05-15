package ota;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class OTABuilder {

    public static OTA getOTAFromJsonFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String str = null;
        StringBuilder json = new StringBuilder();
        while ((str = reader.readLine()) != null){
            json.append(str);
        }
        return getOTAFromJson(json.toString());
    }

    public static OTA getOTAFromJson(String json){
        JSONObject jsonObject = JSON.parseObject(json);
        String name = jsonObject.getString("name");

        JSONArray jsonArray = jsonObject.getJSONArray("sigma");
        Set<String> sigma = new HashSet<>();
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()){
            sigma.add((String)iterator.next());
        }

        List<Location> locationList = new ArrayList<>();
        JSONArray locationArray = jsonObject.getJSONArray("l");
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < locationArray.size(); i ++){
            list.add(locationArray.getIntValue(i));
        }
        JSONArray acceptArray = jsonObject.getJSONArray("accept");
        Set<Integer> set = new HashSet<>();
        for(int i = 0; i < acceptArray.size(); i ++){
            set.add(acceptArray.getIntValue(i));
        }
        int initId = jsonObject.getInteger("init");
        for(int id:list){
            Location location = new Location(id);
            location.setName(""+id);
            if(set.contains(id)){
                location.setAccept(true);
            }else {
                location.setAccept(false);
            }
            if(id == initId){
                location.setInit(true);
            }else {
                location.setInit(false);
            }
            locationList.add(location);
        }

        Map<Integer, Location> map = new HashMap<>();
        for(Location l: locationList){
            map.put(l.getId(),l);
        }

        JSONObject tranJsonObject = jsonObject.getJSONObject("tran");

        int size = tranJsonObject.size();
        List<Transition> transitionList = new ArrayList<>();
        for(int i = 0; i < size; i++){
            JSONArray array = tranJsonObject.getJSONArray(String.valueOf(i));
            int sourceId = array.getInteger(0);
            Location sourceLocation =  map.get(sourceId);
            String action = array.getString(1);
            TimeGuard timeGuard = new TimeGuard(array.getString(2));
            String reset = array.getString(3);
            int targetId = array.getInteger(4);
            Location targetLocation = map.get(targetId);
            Transition transition = new Transition(sourceLocation,targetLocation,timeGuard,reset,action);
            transitionList.add(transition);
        }

        OTA ota =  new OTA(name,sigma,locationList,transitionList);
        return ota;
    }

    public static void sortTran(List<Transition> transitionList){
        transitionList.sort(new Comparator<Transition>() {
            @Override
            public int compare(Transition o1, Transition o2) {
                if(o1.getSourceId() != o2.getSourceId()){
                    return o1.getSourceId() - o2.getSourceId();
                }
                if(o1.getAction().compareTo(o2.getAction())!= 0 ){
                    return o1.getAction().compareTo(o2.getAction());
                }
                if(o1.getTimeGuard().getLeft() != o2.getTimeGuard().getLeft()){
                    return o1.getTimeGuard().getLeft() - o2.getTimeGuard().getLeft();
                }

                if(o1.getTimeGuard().isLeftOpen() != o2.getTimeGuard().isLeftOpen()){
                    return o1.getTimeGuard().isLeftOpen()?1:-1;
                }
                return 1;
            }
        });
    }

    public static OTA completeOTA(OTA ota,Map<String,String> resetMap){

        OTA copy = ota.copy();
        Location sink = new Location(copy.size()+1,"sink",false,false);

        List<Transition> transitionList0 = new ArrayList<>();
        for(Location l: copy.getLocationList()){
            for(String action: copy.getSigma()){
                List<Transition> transitionList = ota.getTransitions(l,action,null);
                if(transitionList.isEmpty()){
                    continue;
                }
                sortTran(transitionList);
                Transition t0 = transitionList.get(0);
                TimeGuard g0 = t0.getTimeGuard();
                if(g0.getLeft()!=0 || g0.isLeftOpen()){
                    TimeGuard guard = new TimeGuard(false,!g0.isLeftOpen(),0,g0.getLeft());
                    Transition t = new Transition(l,sink,guard,resetMap.get(action),action);
                    transitionList0.add(t);
                }
                for(int i = 1; i < transitionList.size(); i++){
                    Transition t1 = transitionList.get(i);
                    TimeGuard g1 = t1.getTimeGuard();
                    if(g0.getRight()!= g1.getLeft() || (g0.isRightOpen() && g1.isLeftOpen())){
                        TimeGuard guard = new TimeGuard(!g0.isRightOpen(),!g1.isLeftOpen(),g0.getRight(),g1.getLeft());
                        Transition t = new Transition(l,sink,guard,resetMap.get(action),action);
                        transitionList0.add(t);
                    }
                    t0 = t1;
                    g0 = t0.getTimeGuard();
                }
                g0 = t0.getTimeGuard();
                if(g0.getRight()!=TimeGuard.MAX_TIME ){
                    TimeGuard guard = new TimeGuard(!g0.isRightOpen(),false,g0.getRight(),TimeGuard.MAX_TIME);
                    Transition t = new Transition(l,sink,guard,resetMap.get(action),action);
                    transitionList0.add(t);
                }
            }
        }
        if(transitionList0.isEmpty()){
            return copy;
        }else {
            for(String action:copy.getSigma()){
                TimeGuard timeGuard = new TimeGuard(false,false,0,TimeGuard.MAX_TIME);
                Transition transition = new Transition(sink,sink,timeGuard,resetMap.get(action),action);
                transitionList0.add(transition);
            }
            copy.getLocationList().add(sink);
            copy.getTransitionList().addAll(transitionList0);
            return copy;
        }

    }

    public static OTA removeSink(OTA ota){
        List<Transition> newTransitionList = new ArrayList<>(ota.getTransitionList());
        List<Location> locationList = ota.getLocationList();
        Set<String> sigma = ota.getSigma();

        List<Location> newLocationList = new ArrayList<>();

        for(int i = 0; i < locationList.size(); i++){
            Location location = locationList.get(i);
            if(location.isAccept()){
                newLocationList.add(location);
            }else {
                List<Transition> list1 = ota.getTransitions(location,null,null);
                List<Transition> list2 = ota.getTransitions(null,null,location);
                newTransitionList.removeAll(list1);
                newTransitionList.removeAll(list2);
            }
        }


        return new OTA(ota.getName(),sigma,newLocationList,newTransitionList);
    }

    public static OTA evidToOTA(OTA evidenceOTA){
        for(Location l:evidenceOTA.getLocationList()){
            for(String action: evidenceOTA.getSigma()){
                List<Transition> transitionList1 = evidenceOTA.getTransitions(l,action,null);
                transitionList1.sort(new Comparator<Transition>() {
                    @Override
                    public int compare(Transition o1, Transition o2) {
                        if(o1.getTimeGuard().getLeft() < o2.getTimeGuard().getLeft()){
                            return -1;
                        }
                        if(o1.getTimeGuard().getLeft() == o2.getTimeGuard().getLeft()
                                && !o1.getTimeGuard().isLeftOpen()){
                            return -1;
                        }
                        return 1;
                    }
                });
                for(int i = 0; i < transitionList1.size(); i++){
                    if(i < transitionList1.size()-1){
                        TimeGuard timeGuard1 = transitionList1.get(i).getTimeGuard();
                        TimeGuard timeGuard2 = transitionList1.get(i+1).getTimeGuard();
                        timeGuard1.setRight(timeGuard2.getLeft());
                        timeGuard1.setRightOpen(!timeGuard2.isLeftOpen());
                    }else {
                        TimeGuard timeGuard1 = transitionList1.get(i).getTimeGuard();
                        timeGuard1.setRight(TimeGuard.MAX_TIME);
                        timeGuard1.setRightOpen(false);
                    }
                }
            }
        }
        return evidenceOTA;
    }

}

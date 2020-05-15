package ota;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Bool;


import java.util.*;

public class OTA {
    private String name;
    private Set<String> sigma;
    private List<Location> locationList;
    private List<Transition> transitionList;

    public OTA(String name, Set<String> sigma, List<Location> locationList, List<Transition> transitionList) {
        this.name = name;
        this.sigma = sigma;
        this.locationList = locationList;
        this.transitionList = transitionList;
    }

    public String getName() {
        return name;
    }

    public Set<String> getSigma() {
        return sigma;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public List<Transition> getTransitionList() {
        return transitionList;
    }

    public int indexof(Location location){
        for(int i = 0; i < locationList.size(); i ++){
            if(locationList.get(i) == location){
                return i;
            }
        }
        return -1;
    }

    public Location getLocation(int id){
        for(Location location:locationList){
            if(location.getId()==id){
                return location;
            }
        }
        return null;
    }


    public List<Location> getAcceptedLocations(){
        List<Location> list = new ArrayList<>();
        for(Location l:locationList){
            if(l.isAccept()){
                list.add(l);
            }
        }
        return list;
    }

    public Location getInitLocation(){
        for(Location l:locationList){
            if(l.isInit()){
                return l;
            }
        }
        return null;
    }


    public List<Transition> getTransitions(Location fromLocation, String action, Location toLocation){
        List<Transition> list = new ArrayList<>(transitionList);
        if(fromLocation != null){
            Iterator<Transition> iterator = list.iterator();
            while(iterator.hasNext()){
                Transition t = iterator.next();
                int tSourceId  = t.getSourceId();
                int fromId = fromLocation.getId();
                if(tSourceId != fromId){
                    iterator.remove();
                }
            }
        }

        if(action != null){
            Iterator<Transition> iterator = list.iterator();
            while(iterator.hasNext()){
                Transition t = iterator.next();
                String tAction  = t.getAction();
                if(!tAction.equals(action)){
                    iterator.remove();
                }
            }
        }

        if(toLocation != null){
            Iterator<Transition> iterator = list.iterator();
            while(iterator.hasNext()){
                Transition t = iterator.next();
                int tTargetId  = t.getTargetId();
                int toId = toLocation.getId();
                if(tTargetId != toId){
                    iterator.remove();
                }
            }
        }
        return list;
    }

    public Location getLocation(TimeWords timeWords){
        Location location = getInitLocation();
        for(int k = 0; k < timeWords.size(); k++){
            TimeWord w = timeWords.get(k);
            boolean flag = false;
            List<Transition> transitionList = getTransitions(location,null,null);
            for(Transition t:transitionList){
                if(t.isPass(w)){
                    location = t.getTargetLocation();
                    flag = true;
                    break;
                }
            }
            if(flag == false){
                return null;
            }
        }
        return location;
    }

    public boolean isAccepted(TimeWords timeWords){
        Location location = getLocation(timeWords);
        if(location == null || !location.isAccept()){
            return false;
        }
        return true;
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\t").append("\"sigma\":[");
        for(String action: getSigma()){
            sb.append("\""+action+"\",");
        }
        sb.deleteCharAt(sb.length()-1).append("],\n\t").append("\"init\":");
        int init = getInitLocation().getId();
        sb.append(init).append(",\n\t").append("\"name\":\"").append(getName()).append("\"\n\t");
        sb.append("\"l\":[");
        for(Location l:getLocationList()){
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length()-1).append("]\n\t\"tran\":{\n");

        OTABuilder.sortTran(getTransitionList());
        for(int i = 0; i < getTransitionList().size();i++){
            Transition t = getTransitionList().get(i);
            sb.append("\t\t\"").append(i).append("\":[")
                    .append(t.getSourceId()).append(",")
                    .append("\"").append(t.getAction()).append("\",")
                    .append("\"").append(t.getTimeGuard()).append("\",")
                    .append(t.getTargetId()).append("]").append(",\n");
        }
        sb.deleteCharAt(sb.length()-2);
        sb.append("\t},\n\t").append("\"accpted\":[");
        for(Location l:getAcceptedLocations()){
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length()-1).append("]\n}");
        return sb.toString();
    }

    public OTA copy(){
        String name1 = name;
        Set<String> sigma1 = new HashSet<>(sigma);
        List<Location> locationList1 = new ArrayList<>();
        Map<Location,Location> locationMap = new HashMap<>();
        for(Location l:locationList){
            Location l1 = new Location(l.getId(),l.getName(),l.isInit(),l.isAccept());
            locationMap.put(l,l1);
            locationList1.add(l1);
        }
        List<Transition> transitionList1 = new ArrayList<>();
        for(Transition t:transitionList){
            Location source = locationMap.get(t.getSourceLocation());
            Location target = locationMap.get(t.getTargetLocation());
            TimeGuard guard = t.getTimeGuard().copy();
            Transition t1 = new Transition(source,target,guard,t.getReset(),t.getAction());
            transitionList1.add(t1);
        }
        return new OTA(name1,sigma1,locationList1,transitionList1);
    }

    public int size(){
        return locationList.size();
    }


    public String getSinkName(){
        for(Location l: locationList){
            if(!l.isAccept()){
                return l.getName();
            }
        }
        return null;
    }

    public Location getSink(){
        for(Location l:locationList){
            if (!l.isAccept()){
                return l;
            }
        }
        return null;
    }

}

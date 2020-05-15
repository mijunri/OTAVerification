package ota;


import java.util.Comparator;

public class Transition {

    public static final double THETA = 0.2;

    private Location sourceLocation;
    private Location targetLocation;
    private String action;
    private TimeGuard timeGuard;
    private String reset;

    public Transition(Location sourceLocation, Location targetLocation, TimeGuard timeGuard, String reset, String action) {
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
        this.timeGuard = timeGuard;
        this.reset = reset;
        this.action = action;
    }

    public int getSourceId() {
        return sourceLocation.getId();
    }

    public int getTargetId() {
        return targetLocation.getId();
    }

    public String getSourceName() {
        return sourceLocation.getName();
    }


    public String getTargetName() {
        return targetLocation.getName();
    }

    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public boolean isReset(){
        return reset.equals("r");
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public TimeGuard getTimeGuard() {
        return timeGuard;
    }

    public void setTimeGuard(TimeGuard timeGuard) {
        this.timeGuard = timeGuard;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isPass(TimeWord word){
        String action = word.getAction();
        double value = word.getValue();
        if(this.action.equals(action)){
            return timeGuard.isPass(value);
        }
        return false;
    }

    public static Comparator<Transition> getComparator(){
        return new TranComparator();
    }

    @Override
    public int hashCode(){
        return sourceLocation.hashCode()+targetLocation.hashCode()*2+action.hashCode()*3+timeGuard.hashCode()*4;
    }

    @Override
    public boolean equals(Object o){
        Transition transition = (Transition) o;
        if(sourceLocation == transition.sourceLocation && targetLocation == transition.targetLocation
            && action.equals(transition.action) && timeGuard.equals(transition.timeGuard)){
            return true;
        }
        return false;
    }


    //是否需要
    public TimeWord toWord(){
        boolean leftOpen = timeGuard.isLeftOpen();
        int left = timeGuard.getLeft();
        if(!leftOpen){
            return new TimeWord(action,left);
        }
        else {
            return new TimeWord(action,left+THETA);
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(sourceLocation.getId()).append(", ").append(action).append(",").append(timeGuard).append(", ")
                .append(targetLocation.getId()).append(")");
        return sb.toString();
    }



    static class TranComparator implements Comparator<Transition>{

        @Override
        public int compare(Transition o1, Transition o2) {
            int var1 = o1.sourceLocation.getId() - o2.sourceLocation.getId();
            if(var1 != 0){
                return var1;
            }
            int var2 = o1.action.compareTo(o2.action);
            if(var2 != 0){
                return var2;
            }
            int var3 = o1.timeGuard.getLeft() - o1.timeGuard.getLeft();
            if(var3 !=0){
                return var3;
            }
            int var4 = o1.timeGuard.getRight() - o2.timeGuard.getRight();
            if(var4 != 0){
                return var4;
            }
            return -1;
        }
    }
}


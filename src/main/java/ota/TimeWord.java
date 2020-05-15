package ota;

public class TimeWord {
    private String action;
    private double value;
    private String reset;

    public TimeWord(String action, double value) {
        this.action = action;
        this.value = value;
    }

    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(getAction());
        stringBuilder.append(",");
        stringBuilder.append(getValue());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o){
        TimeWord word = (TimeWord)o;
        if(word.getAction().equals(getAction()) && word.getValue() == getValue()){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return getAction().hashCode()+(int)getValue();
    }
}


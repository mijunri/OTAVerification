package ttt;


import ota.TimeWord;
import ota.TimeWords;

public class Track {
    private TimeWords source;
    private TimeWords target;
    private TimeWord word;

    public Track(TimeWords source, TimeWords target, TimeWord word) {
        this.source = source;
        this.target = target;
        this.word = word;
    }

    public TimeWords getSource() {
        return source;
    }

    public void setSource(TimeWords source) {
        this.source = source;
    }

    public TimeWords getTarget() {
        return target;
    }

    public void setTarget(TimeWords target) {
        this.target = target;
    }

    public TimeWord getWord() {
        return word;
    }

    public void setWord(TimeWord word) {
        this.word = word;
    }

    @Override
    public int hashCode(){
        return source.hashCode()+target.hashCode()+word.hashCode();
    }

    @Override
    public boolean equals(Object o){
        Track guard = (Track)o;
        boolean var1 = source.equals(guard.source);
        boolean var2 = target.equals(guard.target);
        boolean var3 = word.equals(guard.word);
        return var1 && var2 && var3;
    }

    @Override
    public String toString() {
        return "Track{" +
                "source=" + source +
                ", target=" + target +
                ", word=" + word +
                '}';
    }
}

package ota;

import java.util.ArrayList;
import java.util.List;

public class TimeWords {

    private List<TimeWord> wordList;

    public static final TimeWords EMPTY_WORDS = new TimeWords(new ArrayList<TimeWord>());

    public TimeWords(List<TimeWord> wordList) {
        this.wordList = wordList;
    }

    public TimeWords(TimeWord... timeWords){
        List<TimeWord> list = new ArrayList<>();
        for(TimeWord timeWord: timeWords){
            list.add(timeWord);
        }
        this.wordList = list;
    }
    public List<TimeWord> getWordList(){
        return new ArrayList(wordList);
    }

    public int size(){
        return wordList.size();
    }

    public TimeWord get(int i){
        return wordList.get(i);
    }


    //获取从fromIndex到toIndex之间的序列，其中包含fromIndex，不包含toindex，如果index等于toindex，则为空。
    public TimeWords subWords(int fromIndex, int toIndex){
        List<TimeWord> subList = wordList.subList(fromIndex,toIndex);
        return new TimeWords(subList);
    }

    public boolean isEmpty(){
        return wordList.isEmpty();
    }

    @Override
    public String toString(){
        if(isEmpty()){
            return "empty";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < size(); i ++){
            stringBuilder.append(get(i));
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o){
        TimeWords words = (TimeWords)o;
        if(words.size() != size()){
            return false;
        }
        for(int i = 0; i < size(); i ++){
            if(!words.get(i).equals(get(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 0;
        for(int i = 0; i < size() ; i++){
            hash+=i*get(i).hashCode();
        }
        return hash;
    }

}


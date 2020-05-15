package ota;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeWordsUtil {
    public static Set<TimeWords> getAllPrefixes(TimeWords words){
        int len = words.size();
        Set<TimeWords> prefixes = new HashSet<>();
        List<TimeWord> words1 = new ArrayList<>();
        for(int i = 0; i < len; i ++){
            words1.add(words.get(i));
            List<TimeWord> words2 = new ArrayList<>(words1);
            TimeWords timeWords = new TimeWords(words2);
            prefixes.add(timeWords);
        }
        return prefixes;
    }

    public static TimeWords concat(TimeWords prefix, TimeWords suffix){
        List<TimeWord> prefixList = prefix.getWordList();
        List<TimeWord> suffixList = suffix.getWordList();
        List<TimeWord> list = new ArrayList<>();
        list.addAll(prefixList);
        list.addAll(suffixList);
        return new TimeWords(list);
    }

    public static TimeWords concat(TimeWords prefix, TimeWord suffixWord){
        List<TimeWord> suffixList = new ArrayList<>();
        suffixList.add(suffixWord);
        TimeWords suffix = new TimeWords(suffixList);
        return concat(prefix,suffix);
    }

    public static TimeWords concat(TimeWord prefixWord, TimeWords suffix){
        List<TimeWord> prefixList = new ArrayList<>();
        prefixList.add(prefixWord);
        TimeWords prefix = new TimeWords(prefixList);
        return concat(prefix,suffix);
    }

    public static TimeWords concat(TimeWord prefixWord, TimeWord suffixWord){
        List<TimeWord> prefixList = new ArrayList<>();
        prefixList.add(prefixWord);
        TimeWords prefix = new TimeWords(prefixList);
        return concat(prefix,suffixWord);
    }


    public static TimeWords actionWords(String action){
        TimeWord timeWord = new TimeWord(action,0);
        TimeWords timeWords = new TimeWords(timeWord);
        return timeWords;
    }

}

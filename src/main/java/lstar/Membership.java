package lstar;


import ota.TimeWords;

import java.util.Map;

public interface Membership {
    boolean answer(TimeWords timeWords);
    int getCount();
    Map<String,String> getResetMap();
}

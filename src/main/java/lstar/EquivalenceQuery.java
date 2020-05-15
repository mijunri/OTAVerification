package lstar;



import ota.OTA;
import ota.TimeWords;

import java.util.Set;

public interface EquivalenceQuery {
    int getCount();
    TimeWords findCounterExample1(OTA ota);
    TimeWords findCounterExample2(OTA ota);
    Set<String> getSigma();
}

package checking;

import lstar.EquivalenceQuery;
import lstar.LearningMethod;
import lstar.Membership;
import ota.OTA;
import ota.TimeWords;

public class Checker {

    private OTA[] m1;
    private OTA[] m2;
    private OTA property;
    private Membership membership;
    private EquivalenceQuery equivalenceQuery;
    private LearningMethod learnMethod;
    private long costTime;
    private TimeWords counterExample;

    public Checker(OTA[] m1, OTA[] m2, OTA property, Membership membership, EquivalenceQuery equivalenceQuery, LearningMethod learnMethod) {
        this.m1 = m1;
        this.m2 = m2;
        this.property = property;
        this.membership = membership;
        this.equivalenceQuery = equivalenceQuery;
        this.learnMethod = learnMethod;
    }

    public boolean check() {
        long start = System.currentTimeMillis();
        learnMethod.buildHypothesis();
        OTA assume = learnMethod.getHypothesis();
        TimeWords ce = null;
//        System.out.println(assume);
        for(;;){
            if((ce = equivalenceQuery.findCounterExample1(assume))!=null){
                if(membership.answer(ce)){
                    System.out.println("this is not a counterExample");
                    continue;
                }
                learnMethod.refine(ce);
                learnMethod.buildHypothesis();
                assume = learnMethod.getHypothesis();
                continue;
            }

            if ((ce = equivalenceQuery.findCounterExample2(assume)) != null) {
                if(membership.answer(ce) == false){
                    counterExample = ce;
                    costTime = System.currentTimeMillis()-start;
                    return false;
                }
                learnMethod.refine(ce);
                learnMethod.buildHypothesis();
                assume = learnMethod.getHypothesis();
                continue;
            }
            break;
        }
        costTime = System.currentTimeMillis()-start;
        return true;
    }


    public long getCostTime() {
        return costTime;
    }

    public TimeWords getCounterExample() {
        return counterExample;
    }
}

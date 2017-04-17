package liir.nlp.srl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;

/**
 * @author  quynh on 09.04.17.
 */
public class WEPredicate extends  WEFeatureArgument{

    public WEPredicate(String name, WEDict dict, int len) {
        super(name, dict, len);
    }

    public  float[] getWE(Predicate pred, Word arg) {

        return  getWE(pred);

    }
}

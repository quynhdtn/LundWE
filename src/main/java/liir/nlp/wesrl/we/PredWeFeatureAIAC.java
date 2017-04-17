package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;

/**
 * Created by quynh on 08.04.17.
 */
public class PredWeFeatureAIAC extends WEFeatureArgument {
    public PredWeFeatureAIAC(String name, WEDict dict, int len) {
        super(name, dict, len);
    }


    public  float[] getWE(Predicate pred, Word arg) {

        return  getWE(pred);

    }
}

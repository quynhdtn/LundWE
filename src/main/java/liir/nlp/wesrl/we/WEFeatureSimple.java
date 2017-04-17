package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

/**
 * Created by quynhdo on 05/11/15.
 * A simple word embedding, just return the origin WE for each word
 */
public class WEFeatureSimple extends  WEFeature {
    public WEFeatureSimple(String name, WEDict dict, int len){
        super(name, dict, len);
    }

    @Override
    public float[] getWE(Sentence s, int i) {
        return super.getWE(s.get(i).getForm().toLowerCase());
    }

    public float[] getWE(Word w) {
        return super.getWE(w.getForm().toLowerCase());
    }

    public  float[] getWE(Predicate pred, Word arg) {
        return getWE (arg);
    }
}

package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

/**
 * Created by quynhdo on 13/04/16.
 */
public class WEFeatureSimpleOldNew  extends WEFeature {
    WEDict dict2;
    public WEFeatureSimpleOldNew(String name, WEDict dict, WEDict dict2, int len){
        super(name, dict, len);
        this.dict2=dict2;
    }

    @Override
    public float[] getWE(Sentence s, int i) {
        return super.getWE(s.get(i).getForm().toLowerCase());
    }

    public float[] getWE(Word w) {

        float[] rs = new float[2*len];

        float[] v1 = getWE(w.getForm().toLowerCase());
        float[] v2 = getWE(w.getForm().toLowerCase(), dict2);


        for (int i=0;i<len;i++)
            rs[i]=v1[i];
        for (int i=0;i<len;i++)
            rs[i+len]=v2[i];

        return rs;
    }

    public  float[] getWE(Predicate pred, Word arg) {
        return getWE (arg);
    }
}

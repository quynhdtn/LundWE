package liir.nlp.srl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;


/**
 * Created by quynhdo on 12/12/15.
 */
public class WEFeatureArgument extends WEFeatureSimple {
    public WEFeatureArgument(String name, WEDict dict, int len) {
        super(name, dict, len);
    }

    public  float[] getWE(Predicate pred, Word arg) {
        float[] rs = new float[2*len];
        float[] v1= getWE(pred);
        float[] v2=getWE(arg);
        for (int i=0;i<len;i++)
            rs[i]=v1[i];
        for (int i=0;i<len;i++)
            rs[i+len]=v2[i];
        return rs;
    }
}

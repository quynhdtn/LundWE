package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;

/**
 * Created by quynhdo on 12/12/15.
 */
public class WEFeatureAI_V extends WEFeatureArgument {
    public WEFeatureAI_V(String name, WEDict dict, int len) {
        super(name, dict, len);
    }


    public  float[] getWE(Predicate pred, Word arg) {
        float[] rs = new float[3*len];
     //   float[] v1= getWE(pred);
        float[] v1=getWE(arg);

        Word rightsibling = arg.getRightSibling();
        Word predparent = pred.getHead();
        float[] v2 = new float[len];
        float[] v3 = new float[len];
     //   float[] v5 = new float[len];

        if (rightsibling == null)
            v2 = getWE("");
        else
            v2 = getWE(rightsibling);

        if (predparent == null)
            v3 = getWE("");
        else
            v3 = getWE(predparent);



        for (int i=0;i<len;i++)
            rs[i]=v1[i];
        for (int i=0;i<len;i++)
            rs[i+len]=v2[i];

        for (int i=0;i<len;i++)
            rs[i+2*len]=v3[i];


        return rs;
    }
}

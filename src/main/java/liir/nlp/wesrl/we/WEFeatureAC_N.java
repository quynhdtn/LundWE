package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;

/**
 * Created by quynhdo on 12/12/15.
 */
public class WEFeatureAC_N extends WEFeatureArgument {
    public WEFeatureAC_N(String name, WEDict dict, int len) {
        super(name, dict, len);
    }


    public  float[] getWE(Predicate pred, Word arg) {
        float[] rs = new float[5*len];
        float[] v1= getWE(pred);
        float[] v2=getWE(arg);

        Word rightword = arg.getRightmostDep();
        Word leftword = arg.getLeftmostDep();
        Word leftsibling = arg.getLeftSibling();
        float[] v3 = new float[len];
        float[] v4 = new float[len];
        float[] v5 = new float[len];

        if (rightword == null)
            v3 = getWE("");
        else
            v3 = getWE(rightword);

        if (leftword == null)
            v4 = getWE("");
        else
            v4 = getWE(leftword);

        if (leftsibling == null)
            v5 = getWE("");
        else
            v5 = getWE(leftsibling);

        for (int i=0;i<len;i++)
            rs[i]=v1[i];
        for (int i=0;i<len;i++)
            rs[i+len]=v2[i];

        for (int i=0;i<len;i++)
            rs[i+2*len]=v3[i];

        for (int i=0;i<len;i++)
            rs[i+3*len]=v4[i];

        for (int i=0;i<len;i++)
            rs[i+4*len]=v5[i];
        return rs;
    }
}

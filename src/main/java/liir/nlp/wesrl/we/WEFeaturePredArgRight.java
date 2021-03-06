package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 14/12/15.
 */
public class WEFeaturePredArgRight extends  WEFeatureArgument {



    public WEFeaturePredArgRight(String name, WEDict dict, int len){
        super(name, dict, len);
    }



    public  float[] getWE(Predicate pred, Word arg) {
        float[] rs = new float[3*len];
        float[] v1= getWE(pred);
        float[] v2=getWE(arg);

        Word rightword = arg.getRightmostDep();


        float[] v3 = new float[len];
        if (rightword == null)
            v3 = getWE("");
        else
            v3 = getWE(rightword);



        for (int i=0;i<len;i++)
            rs[i]=v1[i];
        for (int i=0;i<len;i++)
            rs[i+len]=v2[i];

        for (int i=0;i<len;i++)
            rs[i+2*len]=v3[i];


        return rs;
    }
}

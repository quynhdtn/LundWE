package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;

/**
 * Created by quynhdo on 13/12/15.
 */
public class WEFeatureAC_Right extends WEFeatureArgument {
    public WEFeatureAC_Right(String name, WEDict dict, int len) {
        super(name, dict, len);
    }


    public  float[] getWE(Predicate pred, Word arg) {
        float[] rs = new float[len];

        Word rightword = arg.getRightmostDep();


        if (rightword == null)
            rs = getWE("");
        else{
            if (arg.getPOS().startsWith("IN"))
            rs = getWE(rightword);
            else

                rs = getWE("");

        }


        return rs;
    }
}

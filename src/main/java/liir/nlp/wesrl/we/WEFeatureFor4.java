package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;

/**
 * Created by quynhdo on 14/12/15.
 */
public class WEFeatureFor4  extends WEFeatureSimple {

    WEDict dict2;
    WEDict dict3;
    WEDict dict4;
    public WEFeatureFor4(String name, WEDict dict1, WEDict dict2,WEDict dict3,WEDict dict4,int len) {
        super(name, dict1, len);
        this.dict2=dict2;
        this.dict3=dict3;
        this.dict4=dict4;

    }

    public  float[] getWE(Predicate pred, Word arg) {
        float[] rs = new float[4*len];
        Word rightword = arg.getRightmostDep();

        float[] v1 = new float[len];
        float[] v2 = new float[len];
        float[] v3 = new float[len];
        float[] v4 = new float[len];
        if (rightword == null)
        {
            v1 = getWE("");
            v2= getWE ("", dict2);
            v3= getWE ("", dict3);
            v4= getWE ("", dict4);
        }
        else
        {
            v1 = getWE(rightword.getForm().toLowerCase());
            v2 = getWE(rightword.getForm().toLowerCase(), dict2);
            v3 = getWE(rightword.getForm().toLowerCase(), dict3);
            v4 = getWE(rightword.getForm().toLowerCase(), dict4);

        }

        for (int i=0;i<len;i++)
            rs[i]=v1[i];
        for (int i=0;i<len;i++)
            rs[i+len]=v2[i];

        for (int i=0;i<len;i++)
            rs[i+2*len]=v3[i];

        for (int i=0;i<len;i++)
            rs[i+3*len]=v4[i];

        return rs;
    }
}
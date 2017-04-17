package liir.nlp.srl.we;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

/**
 * Created by quynhdo on 04/11/15.
 */
public abstract class WEFeature {

    String name = "we";
    WEDict dict;
    int len;
    public WEFeature(String name, WEDict dict, int len){
        this.name = name;
        this.dict = dict;
        this.len = len;
    }

    public abstract float[] getWE(Sentence s, int i);
    public abstract float[] getWE(Predicate pred, Word arg) ;
    /*{
        float[] rs = new float[2*len];
        float[] v1= getWE(pred);
        float[] v2=getWE(arg);
        for (int i=0;i<len;i++)
            rs[i]=v1[i];
        for (int i=0;i<len;i++)
            rs[i+len]=v2[i];
        return rs;
    }*/

    public float[] getWE(Word w) {
        Sentence s = w.getMySentence();
        int i = s.indexOf(w);
        return getWE(s, i);

    }



    public float[] getWE(String target){
            if (target == "")
            {
                float[]  rs =new float[len];
                for (int j=0;j<rs.length;j++)
                    rs[j]=-1;
                return rs;

            }

            if (dict.containsKey(target))
                return dict.get(target);
            else {
            //    if (dict.containsKey("!!!null!!!"))
             //       return dict.get("!!!null!!!");
              //  else
                return new float[len];  //if the word is not in dict, return zero vector
            }

    }

    public float[] getWE(String target, WEDict d){
        if (target == "")
        {
            float[]  rs =new float[len];
            for (int j=0;j<rs.length;j++)
                rs[j]=-1;
            return rs;

        }

        if (d.containsKey(target))
            return d.get(target);
        else {
            //    if (dict.containsKey("!!!null!!!"))
            //       return dict.get("!!!null!!!");
            //  else
            return new float[len];  //if the word is not in dict, return zero vector
        }

    }

    public float[] getWE(String[] context){
        float[] rs =new  float[len];
        for (int i=0;i<context.length;i++)
        {
            float[] we = getWE(context[i]);
            if (we == null) return null;
            for (int j=0;j<we.length;j++)
                rs[j]+=we[j];
        }
        if (context.length>0)
        for(int j=0;j<rs.length;j++)
            rs[j]=rs[j]/(float)context.length;

        return rs;
    }

    public float[] getWE (String target, String[] context){
        float[] wet = getWE(target);
        float[] wcontext = getWE(context);

        if (wet == null || wcontext ==null) return null;

        float[] rs = new float[wet.length + wcontext.length];

        for (int i=0;i<wet.length; i++)
            rs[i]=wet[i];

        int l = wet.length;
        for (int i=0;i<wcontext.length; i++)
            rs[l + i]= wcontext[i];

        return rs;
    }

}

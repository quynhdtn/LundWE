package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 22/11/15.
 */
public class WEFeatureParent extends  WEFeature {

    public WEFeatureParent(String name, WEDict dict, int len){
        super(name, dict, len);
    }

    @Override
    public float[] getWE(Sentence s, int i) {
        String target = s.get(i).getForm().toLowerCase();
        Word  p = s.get(i).getHead();
        String[] ctx = new String[1];
        if (p == null)
            ctx[0]="!!!null!!!";
        else
        ctx[0] = p.getForm().toLowerCase();

        return super.getWE(target,ctx);

    }

    @Override
    public float[] getWE(Word w) {
        Sentence s = w.getMySentence();
        int i = s.indexOf(w);
        return getWE(s, i);

    }
}

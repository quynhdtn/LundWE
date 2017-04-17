package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 06/11/15.
 * WE is the WE for the target word + average of the context words
 */
public class WEFeatureContext extends  WEFeature {


    int context_windows=5;
    public WEFeatureContext(String name, WEDict dict, int len){
        super(name, dict, len);
    }

    public WEFeatureContext(String name, WEDict dict, int len, int context_windows){
        super(name, dict, len); this.context_windows = context_windows;
    }

    @Override
    public float[] getWE(Sentence s, int i) {
        String target = s.get(i).getForm().toLowerCase();
        List<String> ctxt=new ArrayList<>();
        for (int j=-context_windows;j<=context_windows;j++){
            if(i+j>=0 && i+j<s.size())
                ctxt.add(s.get(i+j).getForm().toLowerCase());


        }
        String[] contextarr = new String[ctxt.size()];
        ctxt.toArray(contextarr );

        return super.getWE(target,contextarr);

    }

    @Override
    public float[] getWE(Word w) {
        Sentence s = w.getMySentence();
        int i = s.indexOf(w);
        return getWE(s, i);

    }
}

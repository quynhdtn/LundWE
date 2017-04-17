package liir.nlp.wesrl.we;

import se.lth.cs.srl.corpus.Sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynhdo on 06/11/15.
 */
public class WEFeatureChildren extends  WEFeature {



    public WEFeatureChildren(String name, WEDict dict, int len){
        super(name, dict, len);
    }



    @Override
    public float[] getWE(Sentence s, int i) {
        String target = s.get(i).getForm().toLowerCase();
        List<String> ctxt=new ArrayList<>();
        s.get(i).getChildren().forEach(w -> ctxt.add(w.getForm().toLowerCase()));
        String[] contextarr = new String[ctxt.size()];
        ctxt.toArray(contextarr );

        return super.getWE(target,contextarr);

    }
}

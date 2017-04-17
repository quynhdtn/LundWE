package se.lth.cs.srl.io;

import se.lth.cs.srl.corpus.Sentence;

import java.io.File;
import java.io.IOException;

/**
 * Created by quynhdo on 17/02/16.
 */
public class SRLOnlyCoNLL09ReaderGold extends AbstractCoNLL09Reader {

    public SRLOnlyCoNLL09ReaderGold(File file) {
        super(file);
    }

    @Override
    protected void readNextSentence() throws IOException {
        String str;
        Sentence sen=null;
        StringBuilder senBuffer=new StringBuilder();
        while ((str = in.readLine()) != null) {
            if(!str.trim().equals("")) {
                senBuffer.append(str).append("\n");
            } else {
                sen=Sentence.newSRLOnlySentence((NEWLINE_PATTERN.split(senBuffer.toString())), true);
                break;
            }
        }
        if(sen==null){
            nextSen=null;
            in.close();
        } else {
            nextSen=sen;
        }


    }

}

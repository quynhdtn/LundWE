package se.lth.cs.srl.io;

import se.lth.cs.srl.corpus.Sentence;

import java.io.File;
import java.io.IOException;

/**
 * Created by quynhdo on 17/02/16.
 */
public class AllCoNLL09ReaderGold extends AbstractCoNLL09Reader {

    public AllCoNLL09ReaderGold(File file) {
        super(file);
    }

    protected void readNextSentence() throws IOException {
        String str;
        Sentence sen=null;
        StringBuilder senBuffer=new StringBuilder();
        while ((str = in.readLine()) != null) {
            if(!str.trim().equals("")) {
                senBuffer.append(str).append("\n");
            } else {
                sen=Sentence.newSentence((NEWLINE_PATTERN.split(senBuffer.toString())), true);
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

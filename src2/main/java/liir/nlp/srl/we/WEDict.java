package liir.nlp.srl.we;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quynhdo on 04/11/15.
 * the dictionary of the word embeddings
 */



public class WEDict extends HashMap<String,float[]> {
    int len=0;

    public WEDict(String path){
        try {
            List<String> lines= Files.readAllLines(Paths.get(path));
            for (String l : lines){
                l=l.trim();
                if (l!="")
                    put(l);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void put(String key, String val_str){

        String[] tmp = val_str.split("\\s+");
        this.len = tmp.length;
        float[] vals= new float[len];
        for (int i =0;i<tmp.length; i++)
            vals[i]= 10* Float.parseFloat(tmp[i]);

        this.put(key, vals);

    }

    public void put (String key_val_str){
        String[] tmp = key_val_str.split("\\s+");

        this.len = tmp.length-1;
        float[] vals= new float[len];
        for (int i =1;i<tmp.length; i++)
            vals[i-1]= Float.parseFloat(tmp[i]);

        this.put(tmp[0], vals);
    }


    public int getWELength(){
        return this.len;
    }

}



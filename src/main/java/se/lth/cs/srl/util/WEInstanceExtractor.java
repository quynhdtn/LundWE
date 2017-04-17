package se.lth.cs.srl.util;

import edu.stanford.nlp.util.Sets;
import is2.io.CONLLWriter09;
import liir.nlp.wesrl.we.WEDict;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.io.AllCoNLL09Reader;
import se.lth.cs.srl.io.CoNLL09Writer;
import se.lth.cs.srl.io.SentenceWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by quynhdo on 16/11/15.
 */
public class WEInstanceExtractor {


    public static void compareData(String train, String test){
        List<Sentence> train_sens = new AllCoNLL09Reader(new File (train)).readAll();
        List<Sentence> test_sens = new AllCoNLL09Reader(new File (test)).readAll();

        ArrayList<String> preds =new ArrayList<>();
    int total=0;
        int count = 0;
        for (Sentence s : train_sens){
            for (Predicate p : s.getPredicates())
            if (! preds.contains(p.getForm().toLowerCase()))
                preds.add(p.getForm().toLowerCase());}

            for (Sentence s : test_sens){
                for (Predicate p : s.getPredicates()) {
                    total++;
                    if (preds.contains(p.getForm().toLowerCase()))
                        count++;
                }
        }

        System.out.println("Total number of predicates: " + String.valueOf(total));
        System.out.println("Total number of sentences: " + String.valueOf( count));
    }
    public static void filterData(String dict, String path, String output){
        WEDict traindict = new WEDict(dict);
        List<Sentence> sens = new AllCoNLL09Reader(new File (path)).readAll();

        System.out.println("Total number of sentences: " + String.valueOf( sens.size()));
        int count =0;
        int total =0 ;
        for (Sentence s : sens){
       //     boolean indict = true;
            for (Predicate p : s.getPredicates()) {
                if (!p.getPOS().startsWith("V"))
                    continue;
                if (traindict.containsKey(p.getForm().toLowerCase()))
                {
                    for (Word w : p.getArgMap().keySet()){
              //      for (Word w : s){
                        boolean indict = true;
                         /*
                        Word c = w.getLeftmostDep();
                        if (c!=null)
                        if (!traindict.containsKey(c.getForm().toLowerCase()))
                            indict=false;
                            */


                        if (!traindict.containsKey(w.getForm().toLowerCase()))
                            indict=false;

                        Word c = w.getRightmostDep();
                        if (c!=null)
                        if (!traindict.containsKey(c.getForm().toLowerCase()))
                            indict=false;

                        Word head = p.getHead();
                        if (head!=null)

                            if (!traindict.containsKey(w.getForm().toLowerCase()))
                                indict=false;

                        /*
                        c = w.getLeftSibling();
                        if (c!=null)
                        if (!traindict.containsKey(c.getForm().toLowerCase()))
                            indict=false;

                        c = w.getRightSibling();
                        if (c!=null)
                        if (!traindict.containsKey(c.getForm().toLowerCase()))
                            indict=false;
*/

                        total++;
                        if (indict)
                            count++;
                    }
                }
                    //indict=false;
            }

        //    if (indict)
          //      count++;
        }
        System.out.println("Total number of predicates: " + String.valueOf(total));
        System.out.println("Total number of sentences: " + String.valueOf( count));
    }
    public static void clusterPredicates(String path, String output){
        List<Sentence> sens = new AllCoNLL09Reader(new File(path)).readAll();
        HashMap<String, Set<String>> store = new HashMap<>();

        ArrayList<ArrayList<String>> clusters = new ArrayList<>();
        HashMap<Integer, Integer> predicateToCluster = new HashMap<>();
        for (Sentence s : sens){
            for (Predicate p : s.getPredicates()){
                if (! store.containsKey(p.getForm().toLowerCase())){
                    Set<String> args = new HashSet<>();
                    store.put(p.getForm().toLowerCase(), args);
                }

                Set<String> args = store.get(p.getForm().toLowerCase());

                for (Word w : p.getArgMap().keySet()){
                    String lbl=p.getArgumentTag(w);
                    if (! args.contains(lbl))
                        args.add(lbl);
                }
            }
        }

        String[] preds = new String[store.keySet().size()];
        store.keySet().toArray(preds);

        for (int i=0;i <preds.length; i++){
            for (int j = i-1;j >=0; j--){
                if ( Sets.symmetricDiff(store.get(preds[i]), store.get(preds[j])).isEmpty()){
                    clusters.get(predicateToCluster.get(j)).add(preds[i]);
                    predicateToCluster.put(i, predicateToCluster.get(j));
                    break;
                }
            }
            if (!predicateToCluster.containsKey(i)) {
                ArrayList<String> cl = new ArrayList<>();
                cl.add(preds[i]);
                predicateToCluster.put(i, clusters.size());
                clusters.add(cl);
            }
        }
        List<String> lines= new ArrayList<>();
        for (int i=0;i <preds.length; i++){


            lines.add(preds[i] + "\t" + String.valueOf( predicateToCluster.get(i)));
        }
        try {
            Files.write(Paths.get(output), lines);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*

        for (ArrayList<String> cl : clusters){
            for (String p : cl)
            System.out.print(p + " ");
            System.out.println ();
        }
        System.out.println ();
        */

    }

    public static void createSubset(String path, int newsize, String output){
        List<Sentence> sens = new AllCoNLL09Reader(new File(path)).readAll();
        int size = sens.size();

        newsize = size /100 * newsize;

        Random random = new Random();

        List<Sentence> newsens = new ArrayList<>();
        for (int i = 0;i<newsize; i ++)
        {
            int p = random.nextInt(size);

            newsens.add(sens.get(p));

        }

        SentenceWriter writer=new CoNLL09Writer( new File (output));

        for (Sentence s: newsens)
            writer.write(s);

        writer.close();




    }

    public static void getRightWordOfPP(String path, String out){
        AllCoNLL09Reader reader =  new AllCoNLL09Reader(new File(path));
        List<String> lines= new ArrayList<>();
        for (Sentence s : reader.readAll()) {

            for (Predicate p : s.getPredicates()) {
                if (p.getPOS().startsWith("N")) {

                    for (Word w : s) {

                                if (!w.getPOS().startsWith("IN")) continue;

                        if (p.getArgMap().containsKey(w)) {
                            Word rightword = w.getRightmostDep();
                            if (rightword != null)
                                lines.add(rightword.getForm().toLowerCase() + "\t" + w.getForm().toLowerCase() + "\t" + p.getForm().toLowerCase() + "\t" + p.getArgumentTag(w));

                        }


                    }

                }

            }

        }

        try {
            Files.write(Paths.get(out), lines);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void GetWEsForVob(String path, String outN, String outV){
        AllCoNLL09Reader reader =  new AllCoNLL09Reader(new File(path));
        List<String> linesV= new ArrayList<>();
        List<String> linesN= new ArrayList<>();
        for (Sentence s : reader.readAll()){

            for (Predicate p : s.getPredicates()){
                if (p.getPOS().startsWith( "V")){

                    for (Word w  : p.getArgMap().keySet()) {
                        String tmp = "";
                        linesV.add(tmp);
                    }
                }

                if (p.getPOS().startsWith( "N")){
                    String tmp = "";
                    tmp+=p.getForm().toLowerCase();
                    Word pp  = p.getHead();
                    if(!pp.isBOS())
                        tmp+= "\t" + pp.getForm().toLowerCase();
                    else
                        tmp+="\t" +"_";

                    if (p instanceof Predicate)
                        tmp += "\t 1";
                    else
                        tmp += "\t 0";


                    linesN.add(tmp);
                }
            }




        }
        try {
            Files.write(Paths.get(outN), linesN);

            Files.write(Paths.get(outV), linesV);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void GetWEs(String path, String outN, String outV){
        AllCoNLL09Reader reader =  new AllCoNLL09Reader(new File(path));
        List<String> linesV= new ArrayList<>();
        List<String> linesN= new ArrayList<>();
        for (Sentence s : reader.readAll()){

            for (Predicate p : s.getPredicates()){
                if (p.getPOS().startsWith( "V")){

                    for (Word w  : p.getArgMap().keySet()) {
                        String tmp = "";
                        linesV.add(tmp);
                    }
                }

                if (p.getPOS().startsWith( "N")){
                    String tmp = "";
                    tmp+=p.getForm().toLowerCase();
                    Word pp  = p.getHead();
                    if(!pp.isBOS())
                        tmp+= "\t" + pp.getForm().toLowerCase();
                    else
                        tmp+="\t" +"_";

                    if (p instanceof Predicate)
                        tmp += "\t 1";
                    else
                        tmp += "\t 0";


                    linesN.add(tmp);
                }
            }




        }
        try {
            Files.write(Paths.get(outN), linesN);

            Files.write(Paths.get(outV), linesV);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    // for pi, we take the predicate candidate, its parent
    public static void PIInstances(String path, String outN, String outV){

        AllCoNLL09Reader reader =  new AllCoNLL09Reader(new File(path));
        List<String> linesV= new ArrayList<>();
        List<String> linesN= new ArrayList<>();
        for (Sentence s : reader.readAll()){

            for (Word p : s){
                if (p.getPOS().startsWith( "V")){
                    String tmp = "";
                    tmp+=p.getForm().toLowerCase();
                    Word pp  = p.getHead();
                    if(!pp.isBOS())
                        tmp+= "\t" + pp.getForm().toLowerCase();
                    else
                        tmp+="\t" +"_";

                    if (p instanceof Predicate)
                        tmp += "\t 1";
                    else
                        tmp += "\t 0";


                    linesV.add(tmp);
                }

                if (p.getPOS().startsWith( "N")){
                    String tmp = "";
                    tmp+=p.getForm().toLowerCase();
                    Word pp  = p.getHead();
                    if(!pp.isBOS())
                        tmp+= "\t" + pp.getForm().toLowerCase();
                    else
                        tmp+="\t" +"_";

                    if (p instanceof Predicate)
                        tmp += "\t 1";
                    else
                        tmp += "\t 0";


                    linesN.add(tmp);
                }
            }




        }
        try {
        Files.write(Paths.get(outN), linesN);

            Files.write(Paths.get(outV), linesV);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    // for ai, we take the predicate candidate, argument, and its argument most right child

    public static void AIInstances(String path, String outN, String outV){

        AllCoNLL09Reader reader =  new AllCoNLL09Reader(new File(path));
        List<String> linesV= new ArrayList<>();
        List<String> linesN= new ArrayList<>();
        for (Sentence s : reader.readAll()){

           for (Predicate p : s.getPredicates()){
               if (p.getPOS().startsWith("V")){

                   for (Word w  : s){
                       if (!w.isBOS()){

                           String l = "0";
                           if (p.getArgMap().containsKey(w))
                               l="1";

                           String rc = "!null!";
                           String lc = "!null!";


                           if (w.getRightmostDep() !=null)
                               rc = w.getRightmostDep().getForm().toLowerCase();
                           if (w.getLeftmostDep() != null)
                               lc = w.getLeftmostDep().getForm().toLowerCase();


                           linesV.add(w.getForm().toLowerCase()+"\t"+p.getForm().toLowerCase()+"\t"+lc + "\t"+rc +"\t" + l);




                       }


                   }

               }


               if (p.getPOS().startsWith("N")){

                   for (Word w  : s){
                       if (!w.isBOS()){

                            String l = "0";
                           if (p.getArgMap().containsKey(w))
                                   l="1";

                                   String rc = "!null!";
                                   String lc = "!null!";



                                   if (w.getRightmostDep() !=null)
                                       rc = w.getRightmostDep().getForm().toLowerCase();
                                   if (w.getLeftmostDep() != null)
                                       lc = w.getLeftmostDep().getForm().toLowerCase();


                                   linesN.add(w.getForm().toLowerCase()+"\t"+p.getForm().toLowerCase()+"\t"+lc + "\t"+rc +"\t" + l);




                       }


                   }

               }


           }



        }
        try {
            Files.write(Paths.get(outN), linesN);

            Files.write(Paths.get(outV), linesV);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    // for ai, we take the predicate candidate, argument, and its argument most right child

    public static void ACInstances(String path, String outN, String outV){

        AllCoNLL09Reader reader =  new AllCoNLL09Reader(new File(path));
        List<String> linesV= new ArrayList<>();
        List<String> linesN= new ArrayList<>();

        List<String> labels=new ArrayList<>();

        for (Sentence s : reader.readAll()){

            for (Predicate p : s.getPredicates()){
                if (p.getPOS().startsWith("V")){

                    for (Word w  : p.getArgMap().keySet()){
                        if (!w.isBOS()){

                            String l = p.getArgMap().get(w);
                    //        if (!labels.contains(l))
                   //             labels.add(l);

                            String rc = "!null!";
                            String lc = "!null!";


                            if (w.getRightmostDep() !=null)
                                rc = w.getRightmostDep().getForm().toLowerCase();
                            if (w.getLeftmostDep() != null)
                                lc = w.getLeftmostDep().getForm().toLowerCase();


                            linesV.add(w.getForm().toLowerCase()+"\t"+p.getForm().toLowerCase()+"\t"+lc + "\t"+rc +"\t" + l);




                        }


                    }

                }


                if (p.getPOS().startsWith("N")){

                    for (Word w  : p.getArgMap().keySet()){
                        if (!w.isBOS()){

                            String l = p.getArgMap().get(w);

                            if (!labels.contains(l))
                                labels.add(l);

                            String rc = "!null!";
                            String lc = "!null!";



                            if (w.getRightmostDep() !=null)
                                rc = w.getRightmostDep().getForm().toLowerCase();
                            if (w.getLeftmostDep() != null)
                                lc = w.getLeftmostDep().getForm().toLowerCase();


                            linesN.add(w.getForm().toLowerCase()+"\t"+p.getForm().toLowerCase()+"\t"+lc + "\t"+rc +"\t" + l);




                        }


                    }

                }


            }



        }

        for (String l : labels) {



            System.out.print("\"" + l  +"\""+ ":" + String.valueOf(labels.indexOf(l)) + ",");
        }

            try {
                Files.write(Paths.get(outN), linesN);

                Files.write(Paths.get(outV), linesV);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




    public static void main(String[] args) {
     //   WEInstanceExtractor.ACInstances("/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-train.txt",
    //           "ac_train_N", "ac_train_V");

       WEInstanceExtractor.getRightWordOfPP("/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-train.txt",
        "right.pp.train.N.txt");


    //    WEInstanceExtractor.createSubset("/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-train.txt",50,

      //          "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-train.50percent.txt");


      //  WEInstanceExtractor.clusterPredicates("/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-evaluation-ood.txt","pred.ood.txt");




   //     WEInstanceExtractor.filterData("/Users/quynhdo/Documents/WORKING/PhD/NNSRL/vob_we_reuters_300.txt",
    //            "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-evaluation-ood.txt",
     //           "/Users/quynhdo/Desktop"
       //         );
    //    WEInstanceExtractor.compareData("/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-train.txt",
    //            "/Users/quynhdo/Documents/WORKING/MYWORK/EACL/CoNLL2009-ST-English2/CoNLL2009-ST-English-evaluation.txt");
    }
}

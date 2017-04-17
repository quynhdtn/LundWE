package se.lth.cs.srl.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import liir.nlp.wesrl.we.WEFeature;
import se.lth.cs.srl.corpus.ArgMap;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.features.Feature;
import se.lth.cs.srl.features.FeatureSet;
import se.lth.cs.srl.ml.Model;
import se.lth.cs.srl.ml.liblinear.Label;
import se.lth.cs.srl.ml.liblinear.LibLinearLearningProblem;

public class ArgumentClassifier extends ArgumentStep{

	private static final String FILEPREFIX="ac_";
	
	private List<String> argLabels;


	//public LibLinearLearningProblem lp_test_v=new LibLinearLearningProblem(new File("ac_eval_GOLD_goldAI_V"),false);

	//public LibLinearLearningProblem lp_test_n=new LibLinearLearningProblem(new File("ac_eval_GOLD_goldAI_N"),false);
	
	public ArgumentClassifier(FeatureSet fs,List<String> argLabels) {
		super(fs);
		this.argLabels=argLabels;
/*
		try {
			Files.write(Paths.get("/Users/quynhdo/Desktop/features/ac/Labels"+".txt"), argLabels);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	public ArgumentClassifier(FeatureSet fs,List<String> argLabels, HashMap<String,WEFeature> wf) {
		super(fs, wf);
		this.argLabels=argLabels;
		/*
		try {
			Files.write(Paths.get("/Users/quynhdo/Desktop/features/ac/Labels"+".txt"), argLabels);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	@Override
	public void extractInstances(Sentence s) {

		for(Predicate pred:s.getPredicates()){

		//	for (Word arg : s){

			//	if (pred.getArgMap().containsKey(arg)) {
					for(Word arg:pred.getArgMap().keySet()){

			//		System.out.println(pred.getForm() + ":" + arg.getForm());
					super.addInstance(pred, arg);
				}

		}
	}

	@Override
	public void parse(Sentence s) {
		for(Predicate pred:s.getPredicates()){
			Map<Word,String> argMap=pred.getArgMap();
	//		for(Word arg:argMap.keySet()){

			for(Word arg:s){
				if (argMap.containsKey(arg)){
				Integer label=classifyInstance(pred, arg);
				//System.out.println(pred.getForm() + "," + arg.getForm()+ ":" + String.valueOf(label) + ":"+argLabels.get(label));
				argMap.put(arg, argLabels.get(label));}
			}
		}
	}

	@Override
	protected Integer getLabel(Predicate pred, Word arg) {
		return argLabels.indexOf(pred.getArgMap().get(arg));
	}

	@Override
	public void prepareLearning() {
		super.prepareLearning(FILEPREFIX);
	}

	@Override
	protected String getModelFileName() {
		return FILEPREFIX+".models";
	}

//	List<ArgMap> beamSearch(Predicate pred,List<ArgMap> candidates,int beamSize){
//		String POSPrefix=super.getPOSPrefix(pred.getPOS());
//		if(POSPrefix==null)
//			POSPrefix=super.featureSet.POSPrefixes[0]; //TODO fix me. or discard examples with wrong POS-tags
//		Model model=models.get(POSPrefix);
//		Map<Word,List<Label>> wordLabelMapping=new HashMap<Word,List<Label>>();
//		int minSize=999;
//		int maxSize=-1;
//		for(ArgMap argMap:candidates){ //Start by computing the probabilities for the labels for all arguments involved so we dont do this more than once for the same argument
//			for(Word arg:argMap.keySet()){
//				if(!wordLabelMapping.containsKey(arg)){ //Compute and add the probabilities for this
//					Collection<Integer> indices=super.collectIndices(pred, arg, POSPrefix, new TreeSet<Integer>());
//					List<Label> probs=model.classifyProb(indices);
//					wordLabelMapping.put(arg,probs);
//				}
//			}
//		}
//		ArrayList<ArgMap> ret=new ArrayList<ArgMap>();
//		for(ArgMap argMap:candidates){ //Then iterate over each candidate and generate the beamSize best labelings of this candidate.
//			
//		}
//		return ret;
//	}
	
	List<ArgMap> beamSearch(Predicate pred,List<ArgMap> candidates,int beamSize){
		ArrayList<ArgMap> ret=new ArrayList<ArgMap>();
		String POSPrefix=super.getPOSPrefix(pred.getPOS());
		if(POSPrefix==null)
			POSPrefix=super.featureSet.POSPrefixes[0]; //TODO fix me. or discard examples with wrong POS-tags
		Model model=models.get(POSPrefix);
		for(ArgMap argMap:candidates){ //Candidates from AI module
			ArrayList<ArgMap> branches=new ArrayList<ArgMap>();
			branches.add(argMap);
			SortedSet<ArgMap> newBranches=new TreeSet<ArgMap>(ArgMap.REVERSE_PROB_COMPARATOR);
			for(Word arg:argMap.keySet()){ //TODO we can optimize this severely by not computing the labels for the same arg more than once.

				Collection<Integer> indices= new TreeSet<Integer>();
				List<Label> probs = null;
				if(POSPrefix==null)
					return null;
				Integer offset=0;
				for(Feature f:featureSet.get(POSPrefix)){
					f.addFeatures(indices, pred, arg,offset,false);
					offset+=f.size(false);
				}

				if (weFeatures.get(POSPrefix) != null) {
					WEFeature weFeature= weFeatures.get(POSPrefix);

					float[] we = weFeature.getWE(pred,arg);
					if (we != null) {


						probs = model.classifyProb(indices, we, offset);

					}
					else {
						System.out.print(pred.getForm().toLowerCase().concat(" is not in the WE dict!!! Skip adding to the model."));
					}
				}

				else
					probs=model.classifyProb(indices);

				for(ArgMap branch:branches){ //Study this branch
					for(int i=0;i<beamSize;++i){ //and create k new branches with current arg
						Label label=probs.get(i);
						ArgMap newBranch=new ArgMap(branch);
						newBranch.put(arg, argLabels.get(label.getLabel()),label.getProb());
						newBranches.add(newBranch);
					}					
				}
				branches.clear();
				Iterator<ArgMap> it=newBranches.iterator();
				for(int i=0;i<beamSize && it.hasNext();++i){
					ArgMap cur=it.next();
					branches.add(cur);
				}
				newBranches.clear();
			}
			//When this loop finishes, we have the best 4 in branches
			for(int i=0,size=branches.size();i<beamSize && i<size;++i){
				ArgMap cur=branches.get(i);
				cur.setLblProb(cur.getProb());
				cur.resetProb();
				ret.add(cur);
			}
		}
		return ret;
	}

	public void train(){

		super.train();
/*
		try {
			Files.createDirectory(Paths.get("/Users/quynhdo/Desktop/features/ac"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> linesV=new ArrayList<>();
		for (Feature f : featureSet.get("V")) {
			linesV.add(f.getName());

		}

		try {
			Files.write(Paths.get("/Users/quynhdo/Desktop/features/ac/V" + ".txt"), linesV);
		} catch (IOException e) {
			e.printStackTrace();
		}



		List<String> linesN=new ArrayList<>();
		for (Feature f : featureSet.get("N")) {
			linesN.add(f.getName());

		}

		try {
			Files.write(Paths.get("/Users/quynhdo/Desktop/features/ac/N"+".txt"), linesN);
		} catch (IOException e) {
			e.printStackTrace();
		}


		for (Feature f : featureSet.get("V")){
			List<String> lines=new ArrayList<>();
			for (String k : f.getMap().keySet()){
				lines.add(k +" " + String.valueOf(f.getMap().get(k)));

			}
			try {
				Files.write(Paths.get("/Users/quynhdo/Desktop/features/ac/V"+"_"+f.getName()+".txt"), lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (Feature f : featureSet.get("N")){
			List<String> lines=new ArrayList<>();
			for (String k : f.getMap().keySet()){
				lines.add(k + " " + String.valueOf(f.getMap().get(k)));

			}
			try {
				Files.write(Paths.get("/Users/quynhdo/Desktop/features/ac/N"+"_"+f.getName()+".txt"), lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
*/
	}

	public Integer classifyInstance(Predicate pred, Word arg) {
		String POSPrefix=getPOSPrefix(pred.getPOS());
		if(POSPrefix==null){
			POSPrefix=featureSet.POSPrefixes[0];
			System.out.println("Unknown POS-tag for predicate '"+pred.getForm()+"', falling back to "+POSPrefix);
		}
		Model m=models.get(POSPrefix);
		//	Collection<Integer> indices=collectIndices(pred, arg, POSPrefix, new TreeSet<Integer>());


		Collection<Integer> indices = new TreeSet<Integer>();


		Integer offset=0;
		for(Feature f:featureSet.get(POSPrefix)){
			f.addFeatures(indices, pred, arg,offset,false);
			offset+=f.size(false);
		}

		if (weFeatures.get(POSPrefix) != null) {
			WEFeature weFeature= weFeatures.get(POSPrefix);

			float[] we = weFeature.getWE(pred,arg);
			if (we != null) {


				return m.classify(indices, we, offset);

			}
			else {
				System.out.print(pred.getForm().toLowerCase().concat(" is not in the WE dict!!! Skip adding to the model."));
			}
		}


		return m.classify(indices);

	}
}

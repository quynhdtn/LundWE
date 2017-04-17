package se.lth.cs.srl.pipeline;

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
import se.lth.cs.srl.ml.LearningProblem;
import se.lth.cs.srl.ml.Model;
import se.lth.cs.srl.ml.liblinear.Label;

public class ArgumentIdentifier extends ArgumentStep {

	private static final String FILEPREFIX="ai_";

	public ArgumentIdentifier(FeatureSet fs) {
		super(fs);
	}
	public ArgumentIdentifier(FeatureSet fs, HashMap<String,WEFeature> wf) {
		super(fs, wf);
	}

	@Override
	public void extractInstances(Sentence s) {
		for(Predicate pred:s.getPredicates()){
			for(int i=1,size=s.size();i<size;++i){
				super.addInstance(pred,s.get(i));
			}
		}
	}

	@Override
	public void parse(Sentence s) {
		for(Predicate pred:s.getPredicates()){
			for(int i=1,size=s.size();i<size;++i){
				Word arg=s.get(i);
				Integer label=super.classifyInstance(pred,arg);
				if(label.equals(POSITIVE))
					pred.addArgMap(arg,"ARG");
			}
		}
		
	}

	@Override
	protected Integer getLabel(Predicate pred, Word arg) {
		return pred.getArgMap().containsKey(arg) ? POSITIVE : NEGATIVE;
	}

	@Override
	public void prepareLearning() {
		super.prepareLearning(FILEPREFIX);
	}

	@Override
	protected String getModelFileName() {
		return FILEPREFIX+".models";
	}
	
	List<ArgMap> beamSearch(Predicate pred,int beamSize){
		List<ArgMap> candidates=new ArrayList<ArgMap>();
		candidates.add(new ArgMap());
		Sentence s=pred.getMySentence();
		SortedSet<ArgMap> newCandidates=new TreeSet<ArgMap>(ArgMap.REVERSE_PROB_COMPARATOR);
		String POSPrefix=super.getPOSPrefix(pred.getPOS());
		if(POSPrefix==null)
			POSPrefix=super.featureSet.POSPrefixes[0]; //TODO fix me. or discard examples with wrong POS-tags
		Model model=models.get(POSPrefix);
		
			
		for(int i=1,size=s.size();i<size;++i){
			newCandidates.clear();
			Word arg=s.get(i);
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
			for(ArgMap argmap:candidates){
				for(Label label:probs){
					ArgMap branch=new ArgMap(argmap);
					if(label.getLabel().equals(POSITIVE)){
						branch.put(arg, "ARG",label.getProb());
					} else {
						branch.multiplyProb(label.getProb());
					}
					newCandidates.add(branch);
				}
			}
			candidates.clear();
			Iterator<ArgMap> it=newCandidates.iterator();
			for(int j=0;j<beamSize && it.hasNext();j++)
				candidates.add(it.next());
		}
		for(ArgMap argmap:candidates){
			argmap.setIdProb(argmap.getProb());
			argmap.resetProb();
		}
		return candidates;
	}

	public void train(){

		super.train();
/*
		try {
			Files.createDirectory(Paths.get("/Users/quynhdo/Desktop/features/ai"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> linesV=new ArrayList<>();
		for (Feature f : featureSet.get("V")) {
			linesV.add(f.getName());

		}

		try {
			Files.write(Paths.get("/Users/quynhdo/Desktop/features/ai/V" + ".txt"), linesV);
		} catch (IOException e) {
			e.printStackTrace();
		}



		List<String> linesN=new ArrayList<>();
		for (Feature f : featureSet.get("N")) {
			linesV.add(f.getName());

		}

		try {
			Files.write(Paths.get("/Users/quynhdo/Desktop/features/ai/N"+".txt"), linesV);
		} catch (IOException e) {
			e.printStackTrace();
		}


		for (Feature f : featureSet.get("V")){
			List<String> lines=new ArrayList<>();
			for (String k : f.getMap().keySet()){
				lines.add(k +" " + String.valueOf(f.getMap().get(k)));

			}
			try {
				Files.write(Paths.get("/Users/quynhdo/Desktop/features/ai/V"+"_"+f.getName()+".txt"), lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (Feature f : featureSet.get("N")){
			List<String> lines=new ArrayList<>();
			for (String k : f.getMap().keySet()){
				lines.add(k +" " + String.valueOf(f.getMap().get(k)));

			}
			try {
				Files.write(Paths.get("/Users/quynhdo/Desktop/features/ai/N"+"_"+f.getName()+".txt"), lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
*/
	}
}

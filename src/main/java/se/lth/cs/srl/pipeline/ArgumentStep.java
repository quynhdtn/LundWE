package se.lth.cs.srl.pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import liir.nlp.wesrl.we.WEFeature;
import se.lth.cs.srl.Learn;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.features.Feature;
import se.lth.cs.srl.features.FeatureSet;
import se.lth.cs.srl.ml.LearningProblem;
import se.lth.cs.srl.ml.Model;

public abstract class ArgumentStep extends AbstractStep {

	public ArgumentStep(FeatureSet fs) {
		super(fs);
	}

	public ArgumentStep(FeatureSet fs, HashMap<String,WEFeature> wf) {
		super(fs);
		this.weFeatures = wf;
	}
	
	protected abstract Integer getLabel(Predicate pred,Word arg);
	protected HashMap<String,WEFeature> weFeatures = null;

	protected String getPOSPrefix(String pos) {
		for(String prefix:featureSet.POSPrefixes){
			if(pos.startsWith(prefix))
				return prefix;
		}
		return null;
	}
	
	//TODO this way we lookup the POSPrefix several times for the same predicate. Maybe the second parameter should be Iterable<Word> ?
	protected void addInstance(Predicate pred, Word arg) {

	///	if (pred.isIgnore) return;
		String POSPrefix=getPOSPrefix(pred.getPOS());
		if(POSPrefix==null){
			if(Learn.learnOptions.skipNonMatchingPredicates){
				return;
			} else {
				POSPrefix=featureSet.POSPrefixes[0];
			}
		}
		LearningProblem lp=learningProblems.get(POSPrefix);
	//	Collection<Integer> indices=collectIndices(pred, arg, POSPrefix, new TreeSet<Integer>());

		Collection<Integer> indices = new TreeSet<Integer>();


		Integer offset=0;
		for(Feature f:featureSet.get(POSPrefix)){
			f.addFeatures(indices, pred, arg,offset,false);
			offset+=f.size(false);
		}

		int label = getLabel(pred,arg);

		if (weFeatures.get(POSPrefix) != null) {
			WEFeature weFeature= weFeatures.get(POSPrefix);

			float[] we = weFeature.getWE(pred,arg);
			if (we != null)
				lp.addInstance(label, indices, we, offset);
			else {
				System.out.print(arg.getForm().toLowerCase().concat(" is not in the WE dict!!! Skip adding to the model."));
			}
		} else
			/////////////////
			lp.addInstance(label, indices);





	}

	protected Collection<Integer> collectIndices(Predicate pred, Word arg, String POSPrefix, Collection<Integer> indices) {
		if(POSPrefix==null)
			return null;
		Integer offset=0;
		for(Feature f:featureSet.get(POSPrefix)){
			f.addFeatures(indices, pred, arg,offset,false);
			offset+=f.size(false);
		}
		return indices;
	}

	//TODO same thing as above.
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

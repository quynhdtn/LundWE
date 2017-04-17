package se.lth.cs.srl.pipeline;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Handler;

import liir.nlp.wesrl.we.WEFeature;
import se.lth.cs.srl.Learn;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.features.Feature;
import se.lth.cs.srl.features.FeatureSet;
import se.lth.cs.srl.ml.LearningProblem;
import se.lth.cs.srl.ml.Model;
import se.lth.cs.srl.ml.liblinear.LibLinearLearningProblem;

public class PredicateIdentifier extends AbstractStep {

	private static final String FILEPREFIX = "pi_";

	/***
	 * We add new WE feature management
	 */
	private HashMap<String,WEFeature> weFeatures = null;



	public PredicateIdentifier(FeatureSet fs) {
		super(fs);
	}

	/***
	 * we add new constructor to deal with WE
	 *
	 * @param fs
	 * @param wf
	 */
	public PredicateIdentifier(FeatureSet fs, HashMap<String,WEFeature> wf) {
		super(fs);
		this.weFeatures = wf;
	}

	/***
	 *
	 *
	 */

	public void extractInstances(Sentence s) {
		/*
		 * We add an instance if it
		 * 1) Is a predicate. Then either to its specific classifier, or the fallback one. (if fallback behavior is specified, i.e. skipNonMatchingPredicates=false
		 * 2) Is not a predicate, but matches the POS-tag
		 */
		for (int i = 1, size = s.size(); i < size; ++i) {
			Word potentialPredicate = s.get(i);
			String POS = potentialPredicate.getPOS();
			String POSPrefix = null;
			for (String prefix : featureSet.POSPrefixes) {
				if (POS.startsWith(prefix)) {
					POSPrefix = prefix;
					break;
				}
			}
			if (POSPrefix == null) { //It matches a prefix, we will use it for sure.
				if (!Learn.learnOptions.skipNonMatchingPredicates && potentialPredicate instanceof Predicate) {
					POSPrefix = featureSet.POSPrefixes[0];
				} else {
					continue; //Its just some word we dont care about
				}
			}
			Integer label = potentialPredicate instanceof Predicate ? POSITIVE : NEGATIVE;
			addInstance(s, i, POSPrefix, label);
		}
	}

	private void addInstance(Sentence s, int i, String POSPrefix, Integer label) {
		LearningProblem lp = learningProblems.get(POSPrefix);
		Collection<Integer> indices = new TreeSet<Integer>();
		Integer offset = 0;
		for (Feature f : featureSet.get(POSPrefix)) {
			f.addFeatures(s, indices, i, -1, offset, true);
			offset += f.size(true);

		}
		/***
		 * We add we lines here
		 */
		if (weFeatures.get(POSPrefix) != null) {
			WEFeature weFeature= weFeatures.get(POSPrefix);
			float[] we = weFeature.getWE(s, i);
			if (we != null)
				lp.addInstance(label, indices, we, offset);
			else {
				System.out.print(s.get(i).getForm().toLowerCase().concat(" is not in the WE dict!!! Skip adding to the model."));
			}
		} else
			/////////////////
			lp.addInstance(label, indices);
	}

	public void parse(Sentence s) {
		for (int i = 1, size = s.size(); i < size; ++i) {
			Integer label = classifyInstance(s, i);
			if (label.equals(POSITIVE))
				s.makePredicate(i);
		}
	}

	private Integer classifyInstance(Sentence s, int i) {
		String POSPrefix = null;
		String POS = s.get(i).getPOS();
		for (String prefix : featureSet.POSPrefixes) {
			if (POS.startsWith(prefix)) {
				POSPrefix = prefix;
				break;
			}
		}
		if (POSPrefix == null)
			return NEGATIVE;
		Model m = models.get(POSPrefix);
		Collection<Integer> indices = new TreeSet<Integer>();
		Integer offset = 0;
		for (Feature f : featureSet.get(POSPrefix)) {
			f.addFeatures(s, indices, i, -1, offset, true);
			offset += f.size(true);
		}

		/***
		 * We add we lines here
		 */
		if (weFeatures.get(POSPrefix) != null) {
			WEFeature weFeature= weFeatures.get(POSPrefix);

			float[] we = weFeature.getWE(s, i);
			if (we != null) {


				return m.classify(indices, we, offset);
			}
			else {
				System.out.print(s.get(i).getForm().toLowerCase().concat(" is not in the WE dict!!! Skip adding to the model."));
			}
		} else
			/////////////////
		{





			return m.classify(indices);
		}
		return null;
	}

	@Override
	public void prepareLearning() {
		super.prepareLearning(FILEPREFIX);
	}

	@Override
	protected String getModelFileName() {
		return FILEPREFIX + ".models";
	}


	public void train() {
		int count = 0;
		for (Feature f : featureSet.get("V")) {
			//System.out.println(f.getName() + ":" +f.size(true));
			count += f.size(true);
		}
		System.out.println("Total:%d " + count);


		for (Feature f : featureSet.get("N")) {
			//System.out.println(f.getName() + ":"+f.size(true));
			count += f.size(true);
		}
		System.out.println("Total:%d " + count);
		super.train();
	}
}
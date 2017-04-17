package se.lth.cs.srl.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import liir.nlp.wesrl.we.WEFeature;
import se.lth.cs.srl.Learn;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.PredicateReference;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.features.Feature;
import se.lth.cs.srl.features.FeatureSet;
import se.lth.cs.srl.ml.LearningProblem;
import se.lth.cs.srl.ml.Model;
import se.lth.cs.srl.ml.liblinear.LibLinearLearningProblem;

public class PredicateDisambiguator implements PipelineStep {

	public static final String FILE_PREFIX="pd_";
	private HashMap<String,WEFeature> weFeatures = null;

	private FeatureSet featureSet;
	private PredicateReference predicateReference;
	
	//This is a map filename -> model
	protected Map<String,Model> models;

	
	private Map<String,List<Predicate>> instances;


	public PredicateDisambiguator(FeatureSet featureSet,PredicateReference predicateReference) {
		this.featureSet=featureSet;
		this.predicateReference=predicateReference;
	}

	public PredicateDisambiguator(FeatureSet featureSet,PredicateReference predicateReference, HashMap<String,WEFeature> wf) {
		this.featureSet=featureSet;
		this.predicateReference =predicateReference;
		this.weFeatures = wf;
	}

	public void parse(Sentence s) {
		for(Predicate pred:s.getPredicates()){
			String POSPrefix=getPOSPrefix(pred);
			String lemma=pred.getLemma();
			String sense="";
			if(POSPrefix==null){
				sense=predicateReference.getSimpleSense(pred, null);
			} else {

				String filename=predicateReference.getFileName(lemma,POSPrefix);
				if(filename==null){
					sense=predicateReference.getSimpleSense(pred,POSPrefix);
				} else {
					Model m=getModel(filename);
					Collection<Integer> indices=new TreeSet<Integer>();
					Integer offset=0;
					for(Feature f:featureSet.get(POSPrefix)){
						f.addFeatures(indices,pred,null,offset,false);
						offset+=f.size(false);
					}


					if (weFeatures.get(POSPrefix) != null) {
						WEFeature weFeature= weFeatures.get(POSPrefix);

						float[] we = weFeature.getWE(pred);
						if (we != null) {


							Integer label= m.classify(indices, we, offset);
							sense=predicateReference.getSense(lemma,POSPrefix,label);
						}
						else {
							System.out.print(pred.getForm().toLowerCase().concat(" is not in the WE dict!!! Skip adding to the model."));
						}
					} else
					/////////////////
					{

						try {
							Integer label = m.classify(indices);
							sense=predicateReference.getSense(lemma,POSPrefix,label);
						}
						catch (Exception e){
							sense=predicateReference.getSimpleSense(pred,POSPrefix);
					}

					}


				}
			}

			pred.setSense(sense);
		}
	}
	
	private Model getModel(String filename){
		return models.get(filename);
	}

	public void extractInstances(Sentence s) {
		for(Predicate pred:s.getPredicates()){
			//Note we would prefer to get the gold standard POS. Cause this always makes sense.
		//	if (pred.isIgnore) continue;
			String POSPrefix=getPOSPrefix(pred);
			if(POSPrefix==null){
				if(Learn.learnOptions.skipNonMatchingPredicates){
					continue;
				} else {
					POSPrefix=featureSet.POSPrefixes[0];
				}
			}
			String filename=predicateReference.getFileName(pred.getLemma(),POSPrefix);
			if(filename==null)
				continue;
			if(!instances.containsKey(filename))
				instances.put(filename,new ArrayList<Predicate>());
			instances.get(filename).add(pred);
		}
	}

	private String getPOSPrefix(Predicate pred) {
		for(String prefix:featureSet.POSPrefixes){
			if(pred.getPOS().startsWith(prefix))
				return prefix;
		}
		return null;
	}
	

	public void prepareLearning() {
		instances=new HashMap<String,List<Predicate>>();
	}
	
	private void addInstance(Predicate pred,LearningProblem lp){

	//	if (pred.isIgnore ) return;

		String POSPrefix=getPOSPrefix(pred);
		if(POSPrefix==null){
			POSPrefix=featureSet.POSPrefixes[0];
		}
		Collection<Integer> indices=new TreeSet<Integer>();
		Integer offset=0;
		for(Feature f:featureSet.get(POSPrefix)){
			f.addFeatures(indices,pred,null,offset,false);
			offset+=f.size(false);
		}
		Integer label=predicateReference.getLabel(pred.getLemma(),POSPrefix,pred.getSense());

		if (weFeatures.get(POSPrefix) != null) {
			WEFeature weFeature= weFeatures.get(POSPrefix);

			float[] we = weFeature.getWE(pred);
			if (we != null)
				lp.addInstance(label, indices, we, offset);
			else {
				System.out.print(pred.getForm().toLowerCase().concat(" is not in the WE dict!!! Skip adding to the model."));
			}
		}
		else
		lp.addInstance(label,indices);
	}

	@Override
	public void done() {
		// we do nothing here since we have no filehandles open. All writing of training data goes on in train()
	}

	@Override
	public void train() {
		models=new HashMap<String,Model>();
		//Here we need to do them one at a time, thats the whole reason why we collected the Map<String,List<Predicate>> instances map. Otherwise we would easily run out of filehandles.
		Iterator<String> it=instances.keySet().iterator();
		while(it.hasNext()){
			String key=it.next();
			File dataFile=new File(Learn.learnOptions.tempDir,FILE_PREFIX+key);
			LibLinearLearningProblem lp=new LibLinearLearningProblem(dataFile,false);
			for(Predicate pred:instances.get(key)){
//				if(pred.getLemma().equals("import"))
//					System.out.println("here");
				addInstance(pred,lp);
			}
			lp.done();
			Model m=lp.train(true);
			models.put(key,m);
			it.remove(); //This way we should lose references to the words and sentences we no longer need, and the gc should be able to clean this up for us.
		}
	}

	@Override
	public void writeModels(ZipOutputStream zos) throws IOException {
		AbstractStep.writeModels(zos, models, getModelFileName());
	}
	
	@Override
	public void readModels(ZipFile zipFile) throws IOException, ClassNotFoundException {
		models=new HashMap<String,Model>();
		AbstractStep.readModels(zipFile, models, getModelFileName());
	}
	
	private String getModelFileName(){
		return FILE_PREFIX+".models";
	}
	
}

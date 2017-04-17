package se.lth.cs.srl.options;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import liir.nlp.wesrl.we.*;
import se.lth.cs.srl.Learn;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.pipeline.Step;

public class LearnOptions extends Options {
	public static final Map<Step,String> featureFileNames;
	static {
		Map<Step,String> map=new HashMap<Step,String>();
		map.put(Step.pi,"pi.feats");
		map.put(Step.pd,"pd.feats");
		map.put(Step.ai,"ai.feats");
		map.put(Step.ac,"ac.feats");
		featureFileNames=Collections.unmodifiableMap(map);
	}
	
	public File liblinearBinary;
	public File tempDir;
	private File featureFileDir;
	
	public boolean skipNonMatchingPredicates=false;
	public boolean trainReranker=false;
	public boolean deleteTrainFiles=true;
	
	public boolean deterministicPipeline=true;
	public boolean deterministicReranker=true;
	
	public boolean global_insertGoldMapForTrain=true;
	public int global_numberOfCrossTrain=5;
	
	private Map<Step,File> featureFiles;

	public File brownClusterFile;

	/***
	 * we add new feature
	 */
	public final static  Map<Step,WEFeature> weFeaturesMapforV;
	static {
		Map<Step,WEFeature> map=new HashMap<Step,WEFeature>();
		map.put(Step.pi,null);
		map.put(Step.pd,null);
		map.put(Step.ai,null);
		map.put(Step.ac,null);
		weFeaturesMapforV=map;
	}

	public final static  Map<Step,WEFeature> weFeaturesMapforN;
	static {
		Map<Step,WEFeature> map=new HashMap<Step,WEFeature>();
		map.put(Step.pi,null);
		map.put(Step.pd,null);
		map.put(Step.ai,null);
		map.put(Step.ac,null);
		weFeaturesMapforN=map;

	}

	LearnOptions(){}
	
	public LearnOptions(String[] args) {
		superParseCmdLine(args);
	}
	@Override
	int parseCmdLine(String[] args, int ai) {
		if(args[ai].equals("-fdir")){
			ai++;
			featureFileDir=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-llbinary")){
			ai++;
			liblinearBinary=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-reranker")){
			ai++;
			trainReranker=true;
		} else if(args[ai].equals("-partitions")){
			ai++;
			global_numberOfCrossTrain=Integer.parseInt(args[ai]);
			ai++;
		} else if(args[ai].equals("-dontInsertGold")){
			ai++;
			global_insertGoldMapForTrain=false;
		} else if(args[ai].equals("-skipUnknownPredicates")){
			ai++;
			skipNonMatchingPredicates=true;
		} else if(args[ai].equals("-dontDeleteTrainData")){
			ai++;
			deleteTrainFiles=false;
		} else if(args[ai].equals("-ndPipeline")){
			ai++;
			deterministicPipeline=false;
		} else if(args[ai].equals("-ndReranker")){
			ai++;
			deterministicPipeline=false;
			deterministicReranker=false;
			trainReranker=true;
		} else if(args[ai].equals("-cluster")){
			ai++;
			brownClusterFile=new File(args[ai++]);
		}

		else if(args[ai].equals("-tempDir")){
			ai++;
			tempDir=new File(args[ai++]);
		}
		/***
		 * we add new lines
		 */
		else if (args[ai].equals("-we")){
			ai++;
			try {
				List<String> welines = Files.readAllLines(Paths.get(args[ai++]));
				for (String l : welines){
					String[] tmps =l.trim().split(" ");
					switch (tmps[0]){
						case "piV": {
							switch (tmps[1]) {
								case "simple":
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureSimple("weSimple", dict, dict.getWELength());
									this.weFeaturesMapforV.put(Step.pi, wf);
									break;
								case "context":
									dict = new WEDict(tmps[2]);
									wf = new WEFeatureContext("weContext", dict, dict.getWELength(), Integer.parseInt(tmps[3]));
									this.weFeaturesMapforV.put(Step.pi, wf);
									break;

								case "parent":
									dict = new WEDict(tmps[2]);
									wf = new WEFeatureParent("weParent", dict, dict.getWELength());
									this.weFeaturesMapforV.put(Step.pi, wf);
									break;


							}
							break;

						}

						case "piN": {
							switch (tmps[1]) {
								case "simple":
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureSimple("weSimple", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.pi, wf);
									break;
								case "context":
									dict = new WEDict(tmps[2]);
									wf = new WEFeatureContext("weContext", dict, dict.getWELength(), Integer.parseInt(tmps[3]));
									this.weFeaturesMapforN.put(Step.pi, wf);
									break;

								case "parent":
									dict = new WEDict(tmps[2]);
									wf = new WEFeatureParent("weParent", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.pi, wf);
									break;
							}
							break;

						}

						case "pdV": {
							switch (tmps[1]) {
								case "simple":{
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureSimple("weSimple", dict, dict.getWELength());
									this.weFeaturesMapforV.put(Step.pd, wf);}
									break;

								case "children": {
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureChildren("weChildren", dict, dict.getWELength());
									this.weFeaturesMapforV.put(Step.pd, wf);}
								break;

							}

							break;
						}

						case "pdN": {
							switch (tmps[1]) {
								case "simple": {
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureSimple("weSimple", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.pd, wf);}
									break;

								case "children": {
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureChildren("weChildren", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.pd, wf);}
								break;

							}

							break;
						}

						case "aiV": {
							switch (tmps[1]) {
								case "simple": {
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureSimple("weSimple", dict, dict.getWELength());
									this.weFeaturesMapforV.put(Step.ai, wf);}
									break;


								case "standard": {
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureAI_V("aiv", dict, dict.getWELength());
									this.weFeaturesMapforV.put(Step.ai, wf);}
									break;

								case "PredArgRight" : {

									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeaturePredArgRight("predargright", dict, dict.getWELength());
									this.weFeaturesMapforV.put(Step.ai, wf);}
								break;

								case "pred" : {

									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new PredWeFeatureAIAC("pred", dict, dict.getWELength());
									this.weFeaturesMapforV.put(Step.ai, wf);}
								break;




							}
							break;
						}

						case "aiN": {
							switch (tmps[1]) {
								case "simple": {
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureSimple("weSimple", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ai, wf);}
									break;

								case "standard": {
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureAI_N("ain", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ai, wf);}
									break;


								case "PredArgRight" : {

									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeaturePredArgRight("predargright", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ai, wf);}
								break;

								case "pred" : {

									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new PredWeFeatureAIAC("pred", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ai, wf);}
								break;


							}
							break;
						}
							case "acV":{
								switch (tmps[1]) {
									case "simple":{
										WEDict dict = new WEDict(tmps[2]);
										WEFeature wf = new WEFeatureSimple("weSimple", dict, dict.getWELength());
										this.weFeaturesMapforV.put(Step.ac, wf);}
										break;

									case "standard":{
										WEDict dict = new WEDict(tmps[2]);
										WEFeature wf = new WEFeatureAC_V("acv", dict, dict.getWELength());
										this.weFeaturesMapforV.put(Step.ac, wf);}
										break;

									case "right":{
										WEDict dict = new WEDict(tmps[2]);
										WEFeature wf = new WEFeatureAC_Right("acr", dict, dict.getWELength());
										this.weFeaturesMapforV.put(Step.ac, wf);}
										break;


									case "PredArgRight" : {

										WEDict dict = new WEDict(tmps[2]);
										WEFeature wf = new WEFeaturePredArgRight("predargright", dict, dict.getWELength());
										this.weFeaturesMapforV.put(Step.ac, wf);}
									break;

									case "for4" : {

										WEDict dict = new WEDict(tmps[2]);
										WEDict dict2 = new WEDict(tmps[3]);
										WEDict dict3 = new WEDict(tmps[4]);
										WEDict dict4 = new WEDict(tmps[5]);
										WEFeature wf = new WEFeatureFor4("4", dict, dict2, dict3, dict4, dict.getWELength());
										this.weFeaturesMapforV.put(Step.ac, wf);}
									break;


									case "oldnew" : {

										WEDict dict = new WEDict(tmps[2]);
										WEDict dict1 = new WEDict(tmps[3]);

										WEFeature wf = new WEFeatureSimpleOldNew("on", dict, dict1, dict.getWELength());
										this.weFeaturesMapforV.put(Step.ac, wf);}
									break;

									case "pred" : {

										WEDict dict = new WEDict(tmps[2]);
										WEFeature wf = new PredWeFeatureAIAC("pred", dict, dict.getWELength());
										this.weFeaturesMapforV.put(Step.ac, wf);}
									break;




								}
								break;
						}

						case "acN":{
							switch (tmps[1]) {
								case "simple":{
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureSimple("weSimple", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ac, wf);}
									break;

								case "standard":{
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureAC_N("acn", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ac, wf);}
									break;

								case "right":{
									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeatureAC_Right("acr", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ac, wf);}
									break;


								case "PredArgRight" : {

									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new WEFeaturePredArgRight("predargright", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ac, wf);}
								break;

								case "for4" : {

									WEDict dict = new WEDict(tmps[2]);
									WEDict dict2 = new WEDict(tmps[3]);
									WEDict dict3 = new WEDict(tmps[4]);
									WEDict dict4 = new WEDict(tmps[5]);
									WEFeature wf = new WEFeatureFor4("4", dict, dict2, dict3, dict4, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ac, wf);}
								break;

								case "oldnew" : {

									WEDict dict = new WEDict(tmps[2]);
									WEDict dict1 = new WEDict(tmps[3]);

									WEFeature wf = new WEFeatureSimpleOldNew("on", dict, dict1,  dict.getWELength());
									this.weFeaturesMapforN.put(Step.ac, wf);}
								break;

								case "pred" : {

									WEDict dict = new WEDict(tmps[2]);
									WEFeature wf = new PredWeFeatureAIAC("pred", dict, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ac, wf);}
								break;

							}
							break;
						}
					}
				}


			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return ai;
	}
	@Override
	void usage() {
		System.err.println("Usage:");
		System.err.println(" java -cp <classpath> "+Learn.class.getName()+" <lang> <input-corpus> <model-file> [options]");
		System.err.println();
		System.err.println("Example:");
		System.err.println(" java -cp srl.jar:lib/liblinear-1.51-with-deps.jar "+Learn.class.getName()+" eng ~/corpora/eng/CoNLL2009-ST-English-train.txt eng-srl.mdl -reranker -fdir ~/features/eng -llbinary ~/liblinear-1.6/train");
		System.err.println();
		System.err.println(" trains a complete pipeline and reranker based on the corpus and saves it to eng-srl.mdl");
		System.err.println();
		super.printUsageLanguages(System.err);
		System.err.println();
		super.printUsageOptions(System.err);
		System.err.println();
		System.err.println("Learning-specific options:");
		System.err.println(" -fdir <dir>             the directory with feature files (see below)");
		System.err.println(" -reranker               trains a reranker also (not done by default)");
		System.err.println(" -llbinary <file>        a reference to a precompiled version of liblinear,");
		System.err.println("                         makes training much faster than the java version.");
		System.err.println(" -partitions <int>       number of partitions used for the reranker");
		System.err.println(" -dontInsertGold         don't insert the gold standard proposition during");
		System.err.println("                         training of the reranker.");
		System.err.println(" -skipUnknownPredicates  skips predicates not matching any POS-tags from");
		System.err.println("                         the feature files.");
		System.err.println(" -dontDeleteTrainData    doesn't delete the temporary files from training");
		System.err.println("                         on exit. (For debug purposes)");
		System.err.println(" -ndPipeline             Causes the training data and feature mappings to be");
		System.err.println("                         derived in a non-deterministic way. I.e. training the pipeline");
		System.err.println("                         on the same corpus twice does not yield the exact same models.");
		System.err.println("                         This is however slightly faster.");
		//TODO There is some something undeterministic about the deterministic reranker. Needs to be looked into.
//		System.err.println(" -dReranker              Same as above, but with the reranker. This option implies");
//		System.err.println("                         a deterministic pipeline as well. It also implies the");
//		System.err.println("                         -reranker option (obviously)");
		System.err.println();
		System.err.println("The feature file dir needs to contain four files with feature sets. See");
		System.err.println("the website for further documentation. The files are called");
		System.err.println("pi.feats, pd.feats, ai.feats, and ac.feats");
		System.err.println("All need to be in the feature file dir, otherwise you will get an error.");
	}
	@Override
	boolean verifyArguments() {
		verifyFeatureFiles();
		if(liblinearBinary!=null && (!liblinearBinary.exists() || !liblinearBinary.canExecute())){
			System.err.println("The provided liblinear binary does not exists or can not be executed. Aborting.");
			System.exit(1);
		}
		if (tempDir==null) {
			setupTempDir();
		}
		else{
			tempDir=setupTempDir(tempDir.getAbsolutePath());
		}
		return true;
	}
	public static File setupTempDir(String path) {

		File tempDir=new File(path);
		if(tempDir.exists()){
			throw new Error("Temporary dir "+tempDir+" already exists. Look into this.");
		} else { 
			if(!tempDir.mkdir()){
				throw new Error("Failed to create temporary dir "+tempDir);
			}
			System.out.println("Using temporary directory "+tempDir);
		}
		tempDir.deleteOnExit();
		return tempDir;
	}

	public static File setupTempDir() {

		String curDateTime=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String tempDirPath = System.getProperty("java.io.tmpdir") + File.separator + "srl_" + curDateTime;

		File tempDir=new File(tempDirPath);
		if(tempDir.exists()){
			throw new Error("Temporary dir "+tempDir+" already exists. Look into this.");
		} else {
			if(!tempDir.mkdir()){
				throw new Error("Failed to create temporary dir "+tempDir);
			}
			System.out.println("Using temporary directory "+tempDir);
		}
		tempDir.deleteOnExit();
		return tempDir;
	}
	private void verifyFeatureFiles() {
		if(featureFileDir==null){
			featureFileDir=new File("featuresets"+File.separator+Language.getLanguage().getL());
		}
		if(!featureFileDir.exists() || !featureFileDir.canRead()){
			System.err.println("Feature file dir "+featureFileDir+" does not exist or can not be read. Aborting.");
			System.exit(1);
		}
		featureFiles=new HashMap<Step,File>();
		for(Step s:Step.values()){
			File f=new File(featureFileDir,featureFileNames.get(s));
			if(!f.exists() || !f.canRead()){
				System.out.println("Feature file "+f+" does not exist or can not be read, aborting.");
				System.exit(1);
			}
			featureFiles.put(s, f);
		}
	}

	public static Map<Step, WEFeature> getWeFeaturesMapforV() {
		return weFeaturesMapforV;
	}
	public static Map<Step, WEFeature> getWeFeaturesMapforN() {
		return weFeaturesMapforN;
	}



	public Map<Step, File> getFeatureFiles() {
		return featureFiles;
	}
}

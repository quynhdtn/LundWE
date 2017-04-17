package se.lth.cs.srl.options;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import liir.nlp.wesrl.we.*;
import se.lth.cs.srl.Parse;
import se.lth.cs.srl.pipeline.Step;

public class ParseOptions extends Options {

	ParseOptions(){}

	public ParseOptions(String[] args) {
		superParseCmdLine(args);
	}
	
	public boolean skipPI=false; //Whether to skip predicate identification.
	public boolean useReranker=false;
	
	public File output;
	
	public double global_alfa=1.0;

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



	@Override
	int parseCmdLine(String[] args, int ai) {
		if(ai==args.length-1){
			output=new File(args[ai]);
			return ai+1;
		}
		if(args[ai].equals("-nopi")){
			ai++;
			skipPI=true;
		} else if(args[ai].equals("-reranker")){
			ai++;
			useReranker=true;
		} else if(args[ai].equals("-alfa")){
			ai++;
			global_alfa=Double.parseDouble(args[ai]);
			ai++;
		}
		/***
		 * we add new lines
		 */
		else if (args[ai].equals("-we")){
			System.out.println("is here");
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
								case "simple":
								{  WEDict dict = new WEDict(tmps[2]);
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

								case "for4" : {

									WEDict dict = new WEDict(tmps[2]);
									WEDict dict2 = new WEDict(tmps[3]);
									WEDict dict3 = new WEDict(tmps[4]);
									WEDict dict4 = new WEDict(tmps[5]);
									WEFeature wf = new WEFeatureFor4("predargright", dict, dict2, dict3, dict4, dict.getWELength());
									this.weFeaturesMapforV.put(Step.ac, wf);}
								break;

								case "oldnew" : {

									WEDict dict = new WEDict(tmps[2]);
									WEDict dict1 = new WEDict(tmps[3]);

									WEFeature wf = new WEFeatureSimpleOldNew("on", dict, dict1,  dict.getWELength());
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

								case "for4" : {

									WEDict dict = new WEDict(tmps[2]);
									WEDict dict2 = new WEDict(tmps[3]);
									WEDict dict3 = new WEDict(tmps[4]);
									WEDict dict4 = new WEDict(tmps[5]);
									WEFeature wf = new WEFeatureFor4("predargright", dict, dict2, dict3, dict4, dict.getWELength());
									this.weFeaturesMapforN.put(Step.ac, wf);}
								break;


								case "oldnew" : {

									WEDict dict = new WEDict(tmps[2]);
									WEDict dict1 = new WEDict(tmps[3]);

									WEFeature wf = new WEFeatureSimpleOldNew("on", dict, dict1, dict.getWELength());
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

			}
					catch (IOException e) {
							e.printStackTrace();
						}

					}

				return ai;
	}

	@Override
	void usage() {
		System.err.println("Usage:");
		System.err.println(" java -cp <classpath> "+Parse.class.getName()+" <lang> <input-corpus> <model-file> [options] <output>");
		System.err.println();
		System.err.println("Example:");
		System.err.println(" java -cp srl.jar:lib/liblinear-1.51-with-deps.jar"+Parse.class.getName()+" eng ~/corpora/eng/CoNLL2009-ST-English-evaluation-SRLonly.txt eng-srl.mdl -reranker -nopi -alfa 1.0 eng-eval.out");
		System.err.println();
		System.err.println(" parses in the input corpus using the model eng-srl.mdl and saves it to eng-eval.out, using a reranker and skipping the predicate identification step");
		System.err.println();
		super.printUsageLanguages(System.err);
		System.err.println();
		super.printUsageOptions(System.err);
		System.err.println();
		System.err.println("Parsing-specific options:");
		System.err.println(" -nopi           skips the predicate identification. This is equivalent to the");
		System.err.println("                 setting in the CoNLL 2009 ST.");
		System.err.println(" -reranker       uses a reranker (assumed to be included in the model)");
		System.err.println(" -alfa <double>  the alfa used by the reranker. (default "+global_alfa+")");
	}

	@Override
	boolean verifyArguments() {
		if(!modelFile.exists() || !modelFile.canRead()){
			System.err.println("Model file "+modelFile+" does not exist or can not be read. Aborting.");
			System.exit(1);
		}
		return true;
	}

	public static Map<Step, WEFeature> getWeFeaturesMapforV() {
		return weFeaturesMapforV;
	}
	public static Map<Step, WEFeature> getWeFeaturesMapforN() {
		return weFeaturesMapforN;
	}



}

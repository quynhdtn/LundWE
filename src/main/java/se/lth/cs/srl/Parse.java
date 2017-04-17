package se.lth.cs.srl;

import java.util.zip.ZipFile;

import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.io.*;
import se.lth.cs.srl.options.ParseOptions;
import se.lth.cs.srl.pipeline.*;
import se.lth.cs.srl.util.Util;

public class Parse {
	public static ParseOptions parseOptions;
	
	public static void main(String[] args) throws Exception{
		long startTime=System.currentTimeMillis();
		parseOptions=new ParseOptions(args);
		
		SemanticRoleLabeler srl;
		
		if(parseOptions.useReranker){
			srl = new Reranker(parseOptions);
			//srl = Reranker.fromZipFile(zipFile,parseOptions.skipPI,parseOptions.global_alfa,parseOptions.global_aiBeam,parseOptions.global_acBeam);
		} else {
			ZipFile zipFile=new ZipFile(parseOptions.modelFile);
			srl = parseOptions.skipPI ? Pipeline.fromZipFile(zipFile,new Step[]{Step.pd,Step.ai,Step.ac}) : Pipeline.fromZipFile(zipFile);

		//	srl = parseOptions.skipPI ? Pipeline.fromZipFile(zipFile,new Step[]{Step.pd,Step.ai}) : Pipeline.fromZipFile(zipFile);

			zipFile.close();
		}
		
		SentenceWriter writer=new CoNLL09Writer(parseOptions.output);
		SentenceReader reader=parseOptions.skipPI ? new SRLOnlyCoNLL09Reader(parseOptions.inputCorpus) : new DepsOnlyCoNLL09Reader(parseOptions.inputCorpus);
	//	SentenceReader reader=parseOptions.skipPI ? new AllCoNLL09ReaderGold(parseOptions.inputCorpus) : new SRLOnlyCoNLL09Reader(parseOptions.inputCorpus);

		//
		int senCount=0;
		for(Sentence s:reader){
			senCount++;
			if(senCount%100==0)
				System.out.println("Parsing sentence "+senCount);
			srl.parseSentence(s);
			writer.write(s);
		}
 //        		((PredicateIdentifier)((Pipeline)srl).getPi()).lp_test_v.done();
//		((PredicateIdentifier)((Pipeline)srl).getPi()).lp_test_n.done();

	//	((ArgumentClassifier)((Pipeline)srl).getAc()).lp_test_n.done();
	//	((ArgumentClassifier)((Pipeline)srl).getAc()).lp_test_v.done();


		writer.close();
		reader.close();
		long totalTime=System.currentTimeMillis()-startTime;
		System.out.println("Done.");
		System.out.println(srl.getStatus());
		System.out.println();
		System.out.println("Total execution time: "+Util.insertCommas(totalTime)+"ms");
	}
}

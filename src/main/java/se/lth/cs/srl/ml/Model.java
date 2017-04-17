package se.lth.cs.srl.ml;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import se.lth.cs.srl.ml.liblinear.Label;

public interface Model extends Serializable {

	public List<Label> classifyProb(Collection<Integer> indices);
	public List<Label> classifyProb(Collection<Integer> indices,float[] we, int offset);

	public Integer classify(Collection<Integer> indices);
	public Integer classify(Collection<Integer> indices, float[] we, int offset);
	//public void sparsify();
	
}

package se.lth.cs.srl.ml;

import java.util.Collection;

public interface LearningProblem {

	public void addInstance(int label, Collection<Integer> indices);

	/***
	 * we add new method here to add WE to feature vector
	 * @param label
	 * @param indices
	 * @param we
	 * @param offset
	 */
	public void addInstance(int label, Collection<Integer> indices, float[] we, int offset);

	public void done();
	public Model train();
}
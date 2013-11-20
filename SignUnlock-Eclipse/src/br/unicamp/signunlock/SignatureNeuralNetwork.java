package br.unicamp.signunlock;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import java.util.List;

public class SignatureNeuralNetwork {
    private NeuralNetwork mNeuralNetwork;

    public void initialize(int... layersSize) {
    	mNeuralNetwork = new MultiLayerPerceptron(layersSize);
    }
    
    public void reset() {
    	mNeuralNetwork.reset();
    }
    
    private TrainingSet<TrainingElement> convert(List<double[]> featureVector) {
    	TrainingSet<TrainingElement> set = new TrainingSet<TrainingElement>(featureVector.get(0).length);
    	
    	for (double[] vec : featureVector) {
    		set.addElement(new SupervisedTrainingElement(vec, new double[] { 1 }));
    	}
    	
    	return set;
    }

    public void learn(List<double[]> fvs) {
    	mNeuralNetwork.learn(convert(fvs), new BackPropagation());
    }

    public double test(double[] testV) {
    	mNeuralNetwork.setInput(testV);
    	mNeuralNetwork.calculate();
    	
    	return mNeuralNetwork.getOutput()[0];
    }
}

package br.unicamp.signunlock;

import android.os.Environment;
import android.util.Log;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;

import java.util.List;

public class SignatureNeuralNetwork {
    private NeuralNetwork mNeuralNetwork;
    private TrainingSet<TrainingElement> tSet;

    SignatureNeuralNetwork(List<double[]> features, List<double[]> classes) {
        int numInput = features.get(0).length;
        int numOutput = classes.get(0).length;
        initialize(numInput, 10, numOutput);
        constructTraining(features, classes);
        //use:   tSet.save(FILE) then tSet.load(FILE)
    }

    public void initialize(int ...layersSize) {
        mNeuralNetwork = new MultiLayerPerceptron(layersSize);
    }

    public void reset() {
        mNeuralNetwork.reset();
    }

    private void constructTraining(List<double[]> featureVector, List<double[]> classes) {
        if(tSet == null)
            tSet = new TrainingSet<TrainingElement>();

        for (int i=0; i<featureVector.size(); i++) {
            tSet.addElement(new SupervisedTrainingElement(featureVector.get(i), classes.get(i)));
        }

        return;
    }

    public void learn() {
        mNeuralNetwork.randomizeWeights();
        mNeuralNetwork.learn(tSet);
        mNeuralNetwork.setLearningRule(new BackPropagation());
        String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myNN.nnet";
        //mNeuralNetwork.save(file);
        Log.d("NNET", "saved model at " + file);
    }

    public double[] test(double[] testV) {
        mNeuralNetwork.setInput(testV);
        mNeuralNetwork.calculate();

        return mNeuralNetwork.getOutput();
    }
}

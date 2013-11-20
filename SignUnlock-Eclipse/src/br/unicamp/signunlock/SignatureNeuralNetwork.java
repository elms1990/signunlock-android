package br.unicamp.signunlock;

import android.util.Log;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;

import java.util.List;

public class SignatureNeuralNetwork {
    private NeuralNetwork mNeuralNetwork;

    private static SignatureNeuralNetwork sInstance = null;
    private DataSet trainingSet;

    SignatureNeuralNetwork(){

    }

    SignatureNeuralNetwork(List<double[]> features, List<double[]> classes) {
        int numInput = features.get(0).length;
        int numOutput = classes.get(0).length;
        mNeuralNetwork = new MultiLayerPerceptron(numInput, numOutput);
        trainingSet = new DataSet(numInput, numOutput);
        learn(features, classes);
    }

    public static void createInstance() {
        if (sInstance == null) {
            sInstance = new SignatureNeuralNetwork();
        }
    }

    public static SignatureNeuralNetwork getInstance() {
        return sInstance;
    }

    public void learn(List<double[]> features, List<double[]> classes) {

        for(int i=0; i<features.size(); i++){
            trainingSet.addRow(
                    features.get(i), //inputs
                    classes.get(i)); //outputs
        }

         // learn the training set
        mNeuralNetwork.learn(trainingSet);
        // save the trained network into file
        mNeuralNetwork.save("myperceptron.nnet");
        Log.d("NNET", "saved model");

    }

    public double[] test(double[] testV) {
        // load the saved network
        //NeuralNetwork neuralNetwork =
        //        NeuralNetwork.createFromFile("myperceptron.nnet");

        // set network input
        mNeuralNetwork.setInput(testV);
        // calculate network
        mNeuralNetwork.calculate();
        // get network output
        double[] networkOutput = mNeuralNetwork.getOutput();
        return networkOutput;
    }
}

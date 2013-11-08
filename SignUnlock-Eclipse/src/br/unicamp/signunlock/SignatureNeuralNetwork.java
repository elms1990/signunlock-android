package br.unicamp.signunlock;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;

public class SignatureNeuralNetwork {
    private NeuralNetwork mNeuralNetwork;

    private static SignatureNeuralNetwork sInstance = null;
    private DataSet trainingSet;

    private SignatureNeuralNetwork() {
        int numInput = 2;
        int numOutput = 1;
        mNeuralNetwork = new Perceptron(numInput, numOutput);
        trainingSet = new DataSet(numInput, numOutput);
    }

    public static void createInstance() {
        if (sInstance == null) {
            sInstance = new SignatureNeuralNetwork();
        }
    }

    public static SignatureNeuralNetwork getInstance() {
        return sInstance;
    }

    public void learn() {
        //example, do that for drawPoints
        trainingSet.addRow(
                new DataSetRow(new double[]{0, 0}, //inputs
                        new double[]{0}) //outputs
        );
        // learn the training set
        mNeuralNetwork.learn(trainingSet);
        // save the trained network into file
        mNeuralNetwork.save("myperceptron.nnet");

    }

    public void test() {
        // load the saved network
        NeuralNetwork neuralNetwork =
                NeuralNetwork.createFromFile("myperceptron.nnet");

        // set network input
        neuralNetwork.setInput(1, 1);
        // calculate network
        neuralNetwork.calculate();
        // get network output
        double[] networkOutput = neuralNetwork.getOutput();
    }
}

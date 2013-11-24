package br.unicamp.signunlock;

import android.content.Context;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SignatureNeuralNetwork {
    private NeuralNetwork mNeuralNetwork;
    private TrainingSet<TrainingElement> tSet;
    Context context;

    SignatureNeuralNetwork(List<double[]> features, List<double[]> classes, Context c) {
        context = c;
    	int numInput = features.get(0).length;
        int numOutput = classes.get(0).length;
        
        initialize(numInput, numInput/4, numOutput);

        Log.d("NEURAL NETWORK", "Input:"+numInput + " Output:"+numOutput);

        loadTrainingSet("myFakeTrain.csv");
        Log.d("LOADED", "FAKETRAIN");
        
        constructTraining(features, classes);
        
        String file = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/myFakeTrain.csv";
        tSet.saveAsTxt(file, ",");
                
       
        Log.d("SAVEDSIGS", file + " NumTrain:" + tSet.size());

    }
    
    private void loadTrainingSet(String fileName){
    	tSet = new TrainingSet<TrainingElement>();
    	
    	try {
    	    BufferedReader reader = new BufferedReader(
    	        new InputStreamReader(context.getAssets().open(fileName)));

    	    // do reading, usually loop until end of file reading  
    	    String mLine = reader.readLine();
    	    while (mLine != null) {
    	       //process line
    	    	String[]feats = mLine.split(",");
    	    	double[] fv = new double[feats.length - 2];
    	    	double[] classI = new double[2];
    	    	classI[0] = Double.parseDouble(feats[feats.length-2]);
    	    	classI[1] = Double.parseDouble(feats[feats.length-1]);

    	    	for(int i=0; i<feats.length-2;i++){
    	    		fv[i] = Double.parseDouble(feats[i]);
    	    	}
    	    	
    	    	tSet.addElement(new SupervisedTrainingElement(fv, classI));
    	    	
    	       mLine = reader.readLine(); 
    	    }

    	    reader.close();
    	} catch (IOException e) {
    	    Log.e("Exception", e.toString());
    	}
    	
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
        //tSet.normalize();
        //mNeuralNetwork.randomizeWeights();
        mNeuralNetwork.learn(tSet, new BackPropagation());
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

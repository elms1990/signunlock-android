package br.unicamp.signunlock;

import org.neuroph.core.NeuralNetwork;

public class SignatureNeuralNetwork {
	private NeuralNetwork mNeuralNetwork;
	
	private static SignatureNeuralNetwork sInstance = null;
	
	private SignatureNeuralNetwork() {
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
		
	}
}

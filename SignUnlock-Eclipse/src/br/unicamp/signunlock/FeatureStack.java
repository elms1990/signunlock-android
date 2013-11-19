package br.unicamp.signunlock;

import java.util.ArrayList;

// Stores Training examples
public class FeatureStack {
	private static ArrayList<double[]> sFeatures = new ArrayList<double[]>();
	
	public static void addVector(double[] vector) {
		sFeatures.add(vector);
	}
	
	public static ArrayList<double[]> getFeatures() {
		return sFeatures;
	}
	
	public static void clearStack() {
		sFeatures.clear();
	}
}

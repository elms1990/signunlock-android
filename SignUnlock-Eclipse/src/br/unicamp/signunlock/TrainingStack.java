package br.unicamp.signunlock;

import java.util.ArrayList;

// Stores Training examples
public class TrainingStack {
	private static ArrayList<double[]> sFeatures = new ArrayList<double[]>();
	private static ArrayList<double[]> sClasses = new ArrayList<double[]>();

	public static void addFeature(double[] vector) {
		sFeatures.add(vector);
	}

	public static ArrayList<double[]> getFeatures() {
		return sFeatures;
	}

	public static void addClass(double[] vector) {
		sClasses.add(vector);
	}

	public static ArrayList<double[]> getClasses() {
		return sClasses;
	}

	public static int getStackSize() {
		return sFeatures.size();
	}

	public static void clearStack() {
		sFeatures.clear();
		sClasses.clear();
	}

}

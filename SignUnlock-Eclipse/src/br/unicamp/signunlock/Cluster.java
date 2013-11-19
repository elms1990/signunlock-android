package br.unicamp.signunlock;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private List<double[]> mFeatureVectors = new ArrayList<double[]>();
	private double[] mCentroid = null;
	
	public void addFeature(double[] feat) {
		mFeatureVectors.add(feat);
	}
	
	public void addFeature(List<double[]> features) {
		mFeatureVectors.addAll(features);
	}
	
	public void calculateCentroid() {
		double[] centroid = new double[mFeatureVectors.get(0).length];
		
		for (double[] vec : mFeatureVectors) {
			for (int i = 0; i < centroid.length; i++) {
				centroid[i] += vec[i];
			}
		}
		
		for (int i = 0; i < centroid.length; i++) {
			centroid[i] /= mFeatureVectors.size();
		}
		
		mCentroid = centroid;
	}
	
	public double[] getCentroid() {
		return mCentroid;
	}
	
	public double euclideanDistance(double[] vec) {
		double distance = 0;
		
		for (int i = 0; i < mCentroid.length; i++) {
			distance += Math.pow((vec[i] - mCentroid[i]), 2);
		}
		
		return Math.sqrt(distance);
	}
}

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
		return euclideanDistance(vec, mCentroid);
	}

	private double euclideanDistance(double[] vec, double[] vec2) {
		double distance = 0;

		for (int i = 0; i < vec2.length; i++) {
			distance += Math.pow((vec[i] - vec2[i]), 2);
		}

		return Math.sqrt(distance);
	}

	/**
	 * Calculates the euclidean distance between the test vector and all
	 * training vectors.
	 * @param vec Test example
	 * @param threshold Threshold
	 * @return False, if the vec could not be validated. True, otherwise.
	 */
	public boolean oneVsAllEuclideanDistance(double[] vec, double threshold) {
		for (double[] training : mFeatureVectors) {
			if (euclideanDistance(vec, training) > threshold) {
				return false;
			}
		}
		
		return true;
	}
}

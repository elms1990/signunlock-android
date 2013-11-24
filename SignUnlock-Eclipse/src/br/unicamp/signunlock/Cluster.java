package br.unicamp.signunlock;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private List<double[]> mFeatureVectors = new ArrayList<double[]>();
	private double[] mCentroid = null;
	private boolean mRenormalize = false;
	private double[] mRenormalizedCentroid;

	public void addFeature(double[] feat) {
		mFeatureVectors.add(feat);
	}

	public void addFeature(List<double[]> features) {
		mFeatureVectors.addAll(features);
	}

	public void renormalize(boolean b) {
		mRenormalize = b;
	}

	private void relativeNormalization(double[] base, double[] target) {
		for (int i = 0; i < base.length; i++) {
			if (base[i] > 1) {
				target[i] /= base[i];
				mRenormalizedCentroid[i] = 1;
			} else {
				mRenormalizedCentroid[i] = base[i];
			}
		}
	}

	public void calculateCentroid() {
		double[] centroid = new double[mFeatureVectors.get(0).length];
		mRenormalizedCentroid = new double[centroid.length];

		for (double[] vec : mFeatureVectors) {
			for (int i = 0; i < centroid.length; i++) {
				centroid[i] += vec[i];
			}
		}

		for (int i = 0; i < centroid.length; i++) {
			centroid[i] /= mFeatureVectors.size();
			mRenormalizedCentroid[i] = centroid[i];
		}

		mCentroid = centroid;
	}

	public double[] getCentroid() {
		return mCentroid;
	}

	public double[] getRenormalizedCentroid() {
		return mRenormalizedCentroid;
	}

	public double euclideanDistance(double[] vec) {
		return euclideanDistance(vec, mCentroid);
	}

	public double estimateThreshold() {
		double threshold = 0;

		if (mFeatureVectors.size() <= 1) {
			return -1;
		}

		boolean old = mRenormalize;
		mRenormalize = false;
		for (int j = 0; j < mFeatureVectors.size(); j++) {
			for (int i = 0; i < mFeatureVectors.size(); i++) {
				if (i == j) {
					continue;
				}

				threshold += euclideanDistance(mFeatureVectors.get(i),
						mFeatureVectors.get(j));
			}
		}
		mRenormalize = old;

		return threshold
				/ ((mFeatureVectors.size() - 1) * mFeatureVectors.size());
	}

	private double euclideanDistance(double[] vec, double[] vec2) {
		double distance = 0;

		if (mRenormalize) {
			relativeNormalization(vec, vec2);
			vec = mRenormalizedCentroid;
		}

		for (int i = 0; i < vec2.length; i++) {
			distance += Math.pow((vec[i] - vec2[i]), 2);
		}

		return Math.sqrt(distance);
	}

	/**
	 * Calculates the euclidean distance between the test vector and all
	 * training vectors.
	 * 
	 * @param vec
	 *            Test example
	 * @param threshold
	 *            Threshold
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

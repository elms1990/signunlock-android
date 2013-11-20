package br.unicamp.signunlock;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ValidateSignatureActivity extends Activity {
	private DrawView drawView;
	private Cluster mCluster;
	
	private TextView mBestScoreText;
	private TextView mLastScoreText;
	private EditText mThresholdText;
	
	private double mBestScore = Double.MAX_VALUE;
	private double mLastScore;
	private double mThreshold = 50.f;
	
	private SignatureNeuralNetwork mNNetwork;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate_signature);
		
		if (FeatureStack.getFeatures().size() == 0) {
			finish();
			return;
		}
		
		drawView = (DrawView) findViewById(R.id.home_signature_view);
        drawView.requestFocus();
		mCluster = new Cluster();
		mCluster.renormalize(true);
		mCluster.addFeature(FeatureStack.getFeatures());
		mCluster.calculateCentroid();
		
		mBestScoreText = (TextView)findViewById(R.id.validate_best_score);
		mLastScoreText = (TextView)findViewById(R.id.validate_last_score);
		mThresholdText = (EditText)findViewById(R.id.validate_threshold);
		
		mNNetwork = new SignatureNeuralNetwork();
		mNNetwork.initialize(FeatureStack.getFeatures().get(0).length, 1);
		mNNetwork.learn(FeatureStack.getFeatures());
	}
	
	public void thresholdButton(View v) {
		mThreshold = Double.parseDouble(mThresholdText.getText().toString());
	}
	
	public void restartButton(View v) {
        drawView.path.reset();
        drawView.invalidate();
    }

    public void okButton(View v) {
        drawView.path.reset();
        drawView.invalidate();
        
        if (drawView.points.size() == 0) {
        	return;
        }

        double[] featureVector = (new SignPreProcess(drawView.points)).getFeatureVector();
        
        double nnScore = mNNetwork.test(featureVector);  
        
        double edist = mCluster.euclideanDistance(featureVector);
        edist *= 100;
        String ss = "";
        for (int i = 0; i < mCluster.getRenormalizedCentroid().length; i++) {
        	 ss += mCluster.getRenormalizedCentroid()[i] + " ";
        }
        Log.e("SS", ss);
//        boolean ova = mCluster.oneVsAllEuclideanDistance(featureVector, mThreshold);
        
//        mThreshold = 100 * mCluster.estimateThreshold();
        if (mBestScore > edist) {
        	mBestScore = edist;
        }
        mLastScore = edist;
        
        mBestScoreText.setText("Best: " + String.valueOf(mBestScore).substring(0,  5));
        mLastScoreText.setText("Last: " + String.valueOf(mLastScore).substring(0, 5));
        
        if (edist < mThreshold) {
        	Toast.makeText(this, mCluster.estimateThreshold() + " Unlocked Device. " + nnScore, Toast.LENGTH_SHORT).show();
        } else {
        	Toast.makeText(this, mCluster.estimateThreshold() + " YOU SHALL NOT PASS. " + nnScore, Toast.LENGTH_SHORT).show();
        }
        
        drawView.points.clear();
    }
}
package br.unicamp.signunlock;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
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
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate_signature);
		
		if (TrainingStack.getFeatures().size() == 0) {
			finish();
			return;
		}
		
		drawView = (DrawView) findViewById(R.id.home_signature_view);
        drawView.requestFocus();
		mCluster = new Cluster();
		mCluster.addFeature(TrainingStack.getFeatures());
		mCluster.calculateCentroid();
		
		mBestScoreText = (TextView)findViewById(R.id.validate_best_score);
		mLastScoreText = (TextView)findViewById(R.id.validate_last_score);
		mThresholdText = (EditText)findViewById(R.id.validate_threshold);
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
        String ss = "";
        for (int i = 0; i < featureVector.length; i++) {
        	 ss += featureVector[i] + " ";
        }
        Log.e("SS", ss);
        
        double edist = mCluster.euclideanDistance(featureVector);
        boolean ova = mCluster.oneVsAllEuclideanDistance(featureVector, mThreshold);
        
        if (mBestScore > edist) {
        	mBestScore = edist;
        }
        mLastScore = edist;
        
        mBestScoreText.setText("Best: " + mBestScore);
        mLastScoreText.setText("Last: " + mLastScore);
        
        if (edist < mThreshold) {
        	Toast.makeText(this, "Unlocked Device.", Toast.LENGTH_SHORT).show();
        } else {
        	Toast.makeText(this, "YOU SHALL NOT PASS.", Toast.LENGTH_SHORT).show();
        }
        
        drawView.points.clear();
    }
}
package br.unicamp.signunlock;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
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

	private double mThreshold = 0.6f;

	private static SignatureNeuralNetwork mNNetwork;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate_signature);

		if (TrainingStack.getFeatures().size() == 0) {
			finish();
			Toast.makeText(getApplicationContext(),
					"Please add some signatures", Toast.LENGTH_SHORT).show();
			return;
		}

		drawView = (DrawView) findViewById(R.id.home_signature_view);
		drawView.requestFocus();
		mCluster = new Cluster();
		mCluster.addFeature(TrainingStack.getFeatures());
		mCluster.calculateCentroid();

		mBestScoreText = (TextView) findViewById(R.id.validate_best_score);
		mLastScoreText = (TextView) findViewById(R.id.validate_last_score);
		mThresholdText = (EditText) findViewById(R.id.validate_threshold);

		if (TrainingStack.updated) {
			mNNetwork = new SignatureNeuralNetwork(TrainingStack.getFeatures(),
					TrainingStack.getClasses(), this);

			final Context context = ValidateSignatureActivity.this;
			final ProgressDialog progress = new ProgressDialog(this);
			progress.setTitle("Please Wait!!");
			progress.setMessage("Training Signature Model!");
			progress.setCancelable(false);
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			progress.show();
			new Thread() {
				public void run() {
					mNNetwork.learn();
					Log.d("TRAINED", "DONE");
					progress.dismiss();

				}

			}.start();

			TrainingStack.updated = false;
		}

	}

	public void thresholdButton(View v) {
		mThreshold = Double.parseDouble(mThresholdText.getText().toString());
	}

	public void restartButton(View v) {
		drawView.path.reset();
		drawView.invalidate();
		drawView.points.clear();
	}

	public void okButton(View v) {

		if (drawView.points.size() < 5) {
			Toast.makeText(getApplicationContext(), "Please draw more",
					Toast.LENGTH_SHORT).show();
			return;
		}

		double[] featureVector = (new SignPreProcess(drawView.points))
				.getFeatureVector();
		double[] nnScore = mNNetwork.test(featureVector);

		mBestScoreText.setText(String.format("Positive: %.4f", nnScore[0]));
		mLastScoreText.setText(String.format("Negative: %.4f", nnScore[1]));

		if (nnScore[0] > mThreshold && nnScore[0] > nnScore[1]) {
			Toast.makeText(this, " Unlocked Device. ", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, " YOU SHALL NOT PASS. ", Toast.LENGTH_SHORT)
					.show();
		}

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				drawView.points.clear();
				drawView.path.reset();
				drawView.invalidate();
			}
		}, 1000);
	}
}
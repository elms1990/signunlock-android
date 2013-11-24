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

    private double mBestScore = Double.MAX_VALUE;
    private double mLastScore;
    private double mThreshold = 0.6f;

    private SignatureNeuralNetwork mNNetwork;

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

        mNNetwork = new SignatureNeuralNetwork(TrainingStack.getFeatures(), TrainingStack.getClasses());

        final Context context = ValidateSignatureActivity.this;
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Please Wait!!");
        progress.setMessage("Training Signature Model!");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        
        progress.show();
        new Thread()
        {
            public void run()
            {
                mNNetwork.learn();
                Log.d("TRAINED","DONE");
                progress.dismiss();

            }

        }.start();

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
        drawView.path.reset();
        drawView.invalidate();

        if (drawView.points.size() == 0) {
            return;
        }

        double[] featureVector = (new SignPreProcess(drawView.points)).getFeatureVector();
        double[] nnScore = mNNetwork.test(featureVector);
        for(int i=0; i<nnScore.length; i++)
            Log.e("SCORE:"+i, ""+nnScore[i]);

        double edist = nnScore[0];
        /*
        double edist = mCluster.euclideanDistance(featureVector);
        edist *= 100;
        String ss = "";
        for (int i = 0; i < mCluster.getRenormalizedCentroid().length; i++) {
            ss += mCluster.getRenormalizedCentroid()[i] + " ";
        }
        Log.d("SS", ss);
//        boolean ova = mCluster.oneVsAllEuclideanDistance(featureVector, mThreshold);

//        mThreshold = 100 * mCluster.estimateThreshold();
*/
        if (mBestScore < edist) {
            mBestScore = edist;
        }
        mLastScore = edist;

       mBestScoreText.setText("Best: " +
               String.valueOf(mBestScore).substring(0, Math.min(5,String.valueOf(mBestScore).length()))
       );
       mLastScoreText.setText("Last: " +
               String.valueOf(mLastScore).substring(0, Math.min(5,String.valueOf(mLastScore).length()))
       );

        if (edist > mThreshold) {
            Toast.makeText(this, mCluster.estimateThreshold() + " Unlocked Device. ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, mCluster.estimateThreshold() + " YOU SHALL NOT PASS. ", Toast.LENGTH_SHORT).show();
        }

        drawView.points.clear();
    }
}
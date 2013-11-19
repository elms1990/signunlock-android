package br.unicamp.signunlock;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class ModePickerActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode_picker);
	}
	
	public void learnSignature(View v) {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}
	
	public void validateSignature(View v) {
		Intent i = new Intent(this, ValidateSignatureActivity.class);
		startActivity(i);
	}
}

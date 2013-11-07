package br.unicamp.signunlock;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private DrawView drawView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        drawView = (DrawView)findViewById(R.id.home_signature_view);     
        drawView.requestFocus();
    }
    
    public void restartButton(View v) {
    	 drawView.path.reset();
         drawView.invalidate();
    }

    public void okButton(View v) {
    	
    }
}

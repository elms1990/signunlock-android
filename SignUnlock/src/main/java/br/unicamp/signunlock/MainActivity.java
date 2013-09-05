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

    DrawView drawView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //Set full screen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout lLayout = new LinearLayout(this);
        lLayout.setOrientation(LinearLayout.VERTICAL);


        drawView = new DrawView(this);
        Button bt = new Button(this);
        bt.setText("restart");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.path.reset();
                drawView.invalidate();
            }
        });
        lLayout.addView(bt);
        lLayout.addView(drawView);

        setContentView(lLayout);







        drawView.requestFocus();
    }

}

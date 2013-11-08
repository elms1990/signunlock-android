package br.unicamp.signunlock;

import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private DrawView drawView;
    int numsigs = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DrawView) findViewById(R.id.home_signature_view);
        drawView.requestFocus();
    }

    public void restartButton(View v) {
        drawView.path.reset();
        drawView.invalidate();
    }

    public void okButton(View v) {

        numsigs++;
        List<DrawPoint> myPoints = drawView.points;
        Log.d("GOT", "" + myPoints.size());
        saveSignature(myPoints, "/mysig" + numsigs + ".ser");
        List<DrawPoint> myPoints2 = loadSignature("/mysig" + numsigs + ".ser");
        Log.d("RESTORED", myPoints2.get(1).toString());
    }


    public void saveSignature(List<DrawPoint> obj, String fileName) {
        try {
            // file is stored in /storage/emulated/0/mysigX.ser
            File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + fileName);
            Log.d("FILENAME", myFile.getAbsolutePath());
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            FileOutputStream fileOut =
                    new FileOutputStream(myFile, false);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            Log.d("ERROR", i.toString());
            i.printStackTrace();
        }
    }

    List<DrawPoint> loadSignature(String fileName) {
        List<DrawPoint> dp = null;
        try {
            File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + fileName);
            FileInputStream fileIn = new FileInputStream(myFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            dp = (List<DrawPoint>) in.readObject();
            in.close();
            fileIn.close();

        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dp;

    }
}

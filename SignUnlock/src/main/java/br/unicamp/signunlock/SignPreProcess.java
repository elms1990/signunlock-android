package br.unicamp.signunlock;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by default on 10/09/13.
 */
public class SignPreProcess {

    List<DrawPoint> points = new ArrayList<DrawPoint>();

    public SignPreProcess(List<DrawPoint> lp){
        points = lp;

        process();

    }

    private void process(){
        for(DrawPoint p : points){
            Log.d("POINT",p.toString());
        }
    }

}

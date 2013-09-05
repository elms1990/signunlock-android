package br.unicamp.signunlock;

import android.util.Log;
import android.view.MotionEvent;


public class DrawPoint {

        float x, y;
        float pressure;
        float time;
        int action;

        public DrawPoint(MotionEvent event){

            x = event.getX();
            y = event.getY();
            time = event.getEventTime();
            action = event.getAction();
            Log.d("EV", event.toString());
            Log.d("NEW POINT", this.toString());


        }

        @Override
        public String toString() {
            return String.format("A:%s -- (%s,%s) P:%s T:%s",action, x,y,pressure,time) ;
        }
}

package br.unicamp.signunlock;

import android.view.MotionEvent;

import java.io.Serializable;


public class DrawPoint implements Serializable {

    double x, y;
    double pressure;
    double time;
    int action;

    public DrawPoint(MotionEvent event) {

        x = event.getX();
        y = event.getY();
        time = event.getEventTime();
        action = event.getAction();
        pressure = event.getPressure();

        //Log.d("EV", event.toString());
        // Log.d("NEW POINT", this.toString());


    }

    @Override
    public String toString() {
        return String.format("A:%s -- (%s,%s) P:%s T:%s", action, x, y, pressure, time);
    }

    public double distanceTo(DrawPoint P2) {
        return Math.sqrt(Math.pow((x - P2.x), 2) + Math.pow((y - P2.y), 2));
    }
}

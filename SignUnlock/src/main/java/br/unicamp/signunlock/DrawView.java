package br.unicamp.signunlock;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import static android.graphics.Color.*;

public class DrawView extends View implements OnTouchListener {
    private static final String TAG = "DrawView";
    List<DrawPoint> points = new ArrayList<DrawPoint>();
    Paint paint;
    Path path;
    Canvas canvas;


    public DrawView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        Button bt = new Button(context);
        bt.setText("NEW BT");
        ArrayList<View> al = new ArrayList<View>();
        al.add(bt);
        this.addTouchables(al);

        this.setOnTouchListener(this);
        setupDrawing();
    }

    private void setupDrawing(){

        //prepare for drawing and setup paint stroke properties
        path = new Path();
        paint = new Paint();
        paint.setColor(BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBitmap);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events

        points.add(new DrawPoint((event)));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(touchX, touchY);
                canvas.drawPath(path, paint);
                //path.reset();
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;
    }
}

package br.unicamp.signunlock;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by default on 10/09/13.
 */
public class SignPreProcess {

    private static final String TAG = "PROCESS";

    public static int AMSIZE = 10;
    public static int NUMGRID = 10;

    //determined experimentally
    final double MAXVELPOSSIBLE = 1000.0f;

    List<DrawPoint> drawPoints;
    List<Point> points;
    List<Double> velocity;
    double[] amPoints;
    double[] amVelocity;
    int numLifts;
    double totalDuration;
    double maxVel = 0, avgVel;
    double maxPress = 0, avgPress;
    int numXchanges, numYChanges;
    double widthHeightRatio;

    double[][] density;
    double maxX = 0, minX = 99999999, maxY = 0, minY = 99999999;

    double[] numVectorsGeoCoords = new double[8];
    double[] sizeVectorsGeoCoords = new double[8];
    
    private double[] mFeatureVector = null;

    public SignPreProcess(List<DrawPoint> lp) {
        drawPoints = lp;
        getNumLifts();
        calcMinMaxvals();
        normalizePoints();
        totalDuration = drawPoints.get(drawPoints.size() - 1).time - drawPoints.get(0).time;
        calcVelocity();
        normalizeVelocity();
        calcPress();
        calcDirectionChanges();
        calcDensity();
        amostratePoints();
        Log.d(TAG, this.toString());

    }

    @Override
    public String toString() {
        return String.format("NP:%s NL:%s TD:%s MXV:%.2f MDV:%.2f XC:%s YC:%s MXP:%.2f MDP:%.2f",
                drawPoints.size(), numLifts, totalDuration, maxVel, avgVel, numXchanges, numYChanges, maxPress, avgPress);
    }

    private void getNumLifts() {
        int n = 0;
        for (DrawPoint p : drawPoints) {
            if (p.action == MotionEvent.ACTION_UP)
                n++;
        }
        numLifts = n;
    }

    private void normalizePoints() {
        points = new ArrayList<Point>();

        for (DrawPoint dp : drawPoints) {
            if(dp.action == MotionEvent.ACTION_MOVE){
                Point p = new Point((dp.x - minX) / (maxX - minX),
                        (dp.y - minY) / (maxY - minY));
                points.add(p);
            }
        }

    }

    private void normalizeVelocity(){
    	List<Double> dummy = new ArrayList<Double>();
    	
        for(double vel :velocity){
        	dummy.add(vel/maxVel);
        }
        
        velocity = dummy;
    }

    private void calcMinMaxvals() {
        for (DrawPoint p : drawPoints) {
            if (p.x > maxX) maxX = p.x;
            if (p.x < minX) minX = p.x;
            if (p.y > maxY) maxY = p.y;
            if (p.y < minY) minY = p.y;
        }

        widthHeightRatio = (maxX - minX)/(maxY - minY);
    }


    private void calcVelocity() {
        double v;
        double sumV = 0;
        List<Double> vel = new ArrayList<Double>();
        for (int i = 0; i < drawPoints.size() - 1; i++) {
            if(drawPoints.get(i).action == MotionEvent.ACTION_MOVE){
                v = drawPoints.get(i).distanceTo(drawPoints.get(i + 1));
                v /= MAXVELPOSSIBLE;
                if (v > maxVel)
                    maxVel = v;
                sumV += v;
                vel.add(v);
            }
        }
        avgVel = sumV / drawPoints.size();
        Log.d("MAXVEL", ""+maxVel);
        velocity = vel;
    }

    private void calcPress() {
        double totalP = 0;
        for (DrawPoint p : drawPoints) {
            totalP += p.pressure;
            if (p.pressure > maxPress) maxPress = p.pressure;
        }
        avgPress = totalP / drawPoints.size();
    }

    private void calcDirectionChanges() {
        int directionX = 0, directionY = 0; // UP=0, DOWN=1
        int dcX = 0, dcY = 0;
        DrawPoint lastChangedPoint = drawPoints.get(0);
        for(int i=0; i<numVectorsGeoCoords.length; i++){
            numVectorsGeoCoords[i]=0;
            sizeVectorsGeoCoords[i]=0;
        }


        for (int i = 0; i < points.size() - 1; i++) {
            DrawPoint p1 = drawPoints.get(i);
            DrawPoint p2 = drawPoints.get(i + 1);

            double deltaX = p2.x - p1.x;
            double deltaY = p2.y - p1.y;
            int thresh = 10;
            Boolean changed = false;
            if ((deltaX > thresh && directionX == 1) ||
                    (deltaX < -thresh && directionX == 0)){
                dcX++;
                changed = true;
            }
            if ((deltaY > thresh && directionY == 1) ||
                    (deltaY < -thresh && directionY == 0)){
                dcY++;
                changed = true;
            }

            if(i==points.size()-2)
                changed=true;

            if(changed){
                double angle = Math.atan2(lastChangedPoint.y - p1.y, lastChangedPoint.x - p1.x);
                int gradeAngle = (int)(angle * 180/Math.PI);
                long geoCoord = Math.round(gradeAngle/45.0) + 3;
                if(geoCoord == -1)
                    geoCoord = 7;

                numVectorsGeoCoords[(int)geoCoord]++;
                sizeVectorsGeoCoords[(int)geoCoord]+= lastChangedPoint.distanceTo(p1)/Math.max(maxX,maxY);

                Log.d("ANGLES", gradeAngle +" "+geoCoord);


            }

            if (deltaX > 0) directionX = 0;
            else directionX = 1;
            if (deltaY > 0) directionY = 0;
            else directionY = 1;

        }
        numXchanges = dcX;
        numYChanges = dcY;

    }

    private void calcDensity() {
        int[][] countdensity = new int[NUMGRID+1][NUMGRID+1];
        density = new double[NUMGRID+1][NUMGRID+1];
        Log.d("SIZE", density.length+ " " + density[0].length);
        double xSize = maxX / NUMGRID;
        double ySize = maxY / NUMGRID;

        for (DrawPoint p : drawPoints) {
            countdensity[(int) (p.x / xSize)][(int) (p.y / ySize)]++;
        }

        //normalize density
        double maxD = 0, minD = 99999999;
        for (int i = 0; i < NUMGRID; i++) {
            for (int j = 0; j < NUMGRID; j++) {
                if (countdensity[i][j] > maxD) maxD = countdensity[i][j];
                if (countdensity[i][j] < minD) minD = countdensity[i][j];
            }
        }
        for (int i = 0; i < NUMGRID; i++) {
            for (int j = 0; j < NUMGRID; j++) {
                density[i][j] = (countdensity[i][j] - minD) / (maxD - minD);
            }
        }

        //print density
//        for (int i = 0; i < NUMGRID; i++) {
//            String line = "";
//            for (int j = 0; j < NUMGRID; j++) {
//                line += " " + String.format("%.4f", density[j][i]);
//            }
//            Log.d(TAG, line);
//        }

    }

    private void amostratePoints(){
        int deltaAm = points.size()/AMSIZE;
        amPoints = new double[2*AMSIZE];
        amVelocity = new double[AMSIZE];

        for(int i=0; i<AMSIZE; i++){
            amPoints[2*i] = points.get(i*deltaAm).x;
            amPoints[2*i+1] = points.get(i*deltaAm).x;
            amVelocity[i] = velocity.get(i*deltaAm).doubleValue();
        }

    }

    private void process() {
        for (Point p : points) {
            Log.d("POINT", "" + p.x + ", " + p.y);
        }
    }
    
    private void purgeVector() {
    	mFeatureVector = new double[0];
    }
    
    private void addFeature(double feat) {
    	addFeature(new double[] { feat });
    }
    
    private void addFeature(double[] feat) {
    	double[] concatenated = new double[feat.length + mFeatureVector.length];
    	
    	System.arraycopy(mFeatureVector, 0, concatenated, 0, mFeatureVector.length);
    	System.arraycopy(feat, 0, concatenated, mFeatureVector.length, feat.length);
    	
    	mFeatureVector = concatenated;
    }
    
    private void addFeature(double[][] feat) {
    	int size = 0;

        size = feat.length * feat[0].length;

    	double[] linearized = new double[size];  	
    	for (int j = 0; j < feat.length; j++) {
    		for (int i = 0; i < feat[j].length; i++) {
    			linearized[i + j*feat.length] = feat[j][i];
    		}
    	}
    	
    	addFeature(linearized);
    }

    public double[] getFeatureVector(){
    	purgeVector();
    	
    	addFeature(numLifts);
        Log.d("FEATURELENGHT","numLifts "+mFeatureVector.length);
        //addFeature(totalDuration);
        //Log.d("FEATURELENGHT","totalDuration "+mFeatureVector.length);

        addFeature(widthHeightRatio);
        Log.d("FEATURELENGHT","widthHeightRatio "+mFeatureVector.length);

        addFeature(maxVel);
        Log.d("FEATURELENGHT","maxVel "+mFeatureVector.length);

        addFeature(avgVel);
        Log.d("FEATURELENGHT","avgVel "+mFeatureVector.length);

        addFeature(maxPress);
        Log.d("FEATURELENGHT","maxPress "+mFeatureVector.length);

        addFeature(avgPress);
        Log.d("FEATURELENGHT","avgPress "+mFeatureVector.length);

        addFeature(numXchanges);
        Log.d("FEATURELENGHT","numXchanges "+mFeatureVector.length);

        addFeature(numYChanges);
        Log.d("FEATURELENGHT","numYchanges"+mFeatureVector.length);

        addFeature(amPoints);
        Log.d("FEATURELENGHT","amPoints "+mFeatureVector.length);

        addFeature(amVelocity);
        Log.d("FEATURELENGHT","amVelocity"+mFeatureVector.length);

        addFeature(density);
        Log.d("FEATURELENGHT","density "+mFeatureVector.length);

        addFeature(numVectorsGeoCoords);
        Log.d("FEATURELENGHT","numvectorgeoocords "+mFeatureVector.length);

        addFeature(sizeVectorsGeoCoords);
        Log.d("FEATURELENGHT","sizevectorgeoocords "+mFeatureVector.length);


        return mFeatureVector;
    }


}

package se.jwallinder.airborne;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private TextView mTextViewAirborneTime;
    private TextView mTextViewAccX, mTextViewAccY, mTextViewAccZ, mTextViewAccG, mTextViewMinMaxG;
    private TextView mTextViewGravX, mTextViewGravY, mTextViewGravZ, mTextViewGravG;
    private SensorManager mSensorManager;
    private Sensor mSensorAcc, mSensorGrav;

    private boolean inFreefall = false;
    private long start, stop, freeFallTime;

    private static final double THRESHOLD_FREEFALL = 3.0;
    private static final double THRESHOLD_BACK_TO_EARTH = 9.0;

    double maxG = 0;
    double minG = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextViewAirborneTime = (TextView) stub.findViewById(R.id.airborne_time);
                mTextViewMinMaxG = (TextView) stub.findViewById(R.id.minMaxG);

                mTextViewAccX = (TextView) stub.findViewById(R.id.accX);
                mTextViewAccY = (TextView) stub.findViewById(R.id.accY);
                mTextViewAccZ = (TextView) stub.findViewById(R.id.accZ);
                mTextViewAccG = (TextView) stub.findViewById(R.id.accG);

                mTextViewGravX = (TextView) stub.findViewById(R.id.gravX);
                mTextViewGravY = (TextView) stub.findViewById(R.id.gravY);
                mTextViewGravZ = (TextView) stub.findViewById(R.id.gravZ);
                mTextViewGravG = (TextView) stub.findViewById(R.id.gravG);



                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                mSensorGrav = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                mSensorManager.registerListener(MainActivity.this, mSensorGrav, SensorManager.SENSOR_DELAY_GAME);

                mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(MainActivity.this, mSensorAcc, SensorManager.SENSOR_DELAY_GAME);

            }
        });
    }

    private void onFreefall(){
        if (!inFreefall){
            start = System.currentTimeMillis();
            inFreefall = true;
            freeFallTime = 0;
        }

    }

    private void onBackToEarth(){
        if (inFreefall){
            stop = System.currentTimeMillis();
            inFreefall = false;
            freeFallTime = stop-start;
        }
    }

    private long getFreeFallTime(){
        if (inFreefall) {
            freeFallTime = System.currentTimeMillis()-stop;
        }

        return freeFallTime;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            onGravitySensorChanged(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            onAccSensorChanged(event);
        }

    }


    public void onAccSensorChanged(SensorEvent event) {

        float axisX = event.values[0];
        float axisY = event.values[1];
        float axisZ = event.values[2];

        double g = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);

        if (g < THRESHOLD_FREEFALL){
            onFreefall();
        }
        if (g > THRESHOLD_BACK_TO_EARTH){
            onBackToEarth();
        }

        minG = Math.min(minG, g);
        maxG = Math.max(maxG,g);

        mTextViewMinMaxG.setText(nf.format(minG) + ":" + nf.format(maxG));


        mTextViewAirborneTime.setText(getFreeFallTime() + "ms");
        mTextView.setText(nf.format(g));
        mTextViewAccX.setText(nf.format(axisX));
        mTextViewAccY.setText(nf.format(axisY));
        mTextViewAccZ.setText(nf.format(axisZ));
        mTextViewAccG.setText(nf.format(g));

    }

    public void onGravitySensorChanged(SensorEvent event) {

        float axisX = event.values[0];
        float axisY = event.values[1];
        float axisZ = event.values[2];

        double g = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        mTextView.setText(nf.format(g));
        mTextViewGravX.setText(nf.format(axisX));
        mTextViewGravY.setText(nf.format(axisY));
        mTextViewGravZ.setText(nf.format(axisZ));
        mTextViewGravG.setText(nf.format(g));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

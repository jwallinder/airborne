package se.jwallinder.airborne;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;

import java.text.NumberFormat;

import se.heinrisch.talkclient.TalkClient;
import se.heinrisch.talkclient.adapters.TalkCallbackAdapter;

public class MainActivityWear extends Activity implements SensorEventListener, AirborneListener {

    private TextView mTextViewAirborneTime;
    private AirborneManager airborneManager = new AirborneManager();
    private SensorManager mSensorManager;
    private Sensor mSensorAcc, mSensorGrav;
    private TalkClient mTalkClient;


    private long start, stop, freeFallTime;

    private static final double THRESHOLD_FREEFALL = 3.0;
    private static final double THRESHOLD_BACK_TO_EARTH = 9.0;

    double maxG = 0;
    double minG = 1000;

    public static final NumberFormat NF = NumberFormat.getNumberInstance();

    {
        NF.setMaximumFractionDigits(2);
    }

    public MainActivityWear() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTalkClient = new TalkClient(this);
        mTalkClient.setTalkCallbackAdapter(new TalkCallbackAdapter() {
            @Override
            public void onConnected(Bundle bundle) {
                sendMessage("/airborne", "airborneTime", "device is ready");
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextViewAirborneTime = (TextView) stub.findViewById(R.id.airborne_time);


                mSensorManager.registerListener(MainActivityWear.this, getmSensorAcc(), SensorManager.SENSOR_DELAY_GAME);
                airborneManager.setAirborneListener(MainActivityWear.this);

            }
        });
    }

    public Sensor getmSensorAcc() {
        if (mSensorAcc == null) {
            mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        return mSensorAcc;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(MainActivityWear.this, getmSensorAcc(), SensorManager.SENSOR_DELAY_GAME);

        mTalkClient.connectClient();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTalkClient.disconnectClient();

    }

    @Override
    public void onBeginFreeFall() {

    }

    @Override
    public void onEndFreeFall() {


        sendMessage("/airborne", "airborneTime", getFreeFallString());
    }


    private void sendMessage(String path, String key, String value) {
        DataMap dataMap = new DataMap();
        dataMap.putString(key, value);

        mTalkClient.sendMessage(path, dataMap);
    }

    private String getFreeFallString() {
        //free fall time equation
        //T = sqrt ( 2 * height / 9.8 )
        //T^2 = 2*height/9.9
        //H = 9.8*T^2/2
        //since we start at level 0, reach height H and return to level
        //we
        freeFallTime = getFreeFallTime();
        double height = 9.8 * (freeFallTime * freeFallTime / 1000000.0) / 8;
        String s = NF.format(freeFallTime) + "ms, " + NF.format(height * 100.0) + "cm";
        Log.e("dv", s);
        return s;

    }

    private long getFreeFallTime() {

        return airborneManager.getFreeFallTime();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float axisX = event.values[0];
        float axisY = event.values[1];
        float axisZ = event.values[2];

        airborneManager.setSensorData(axisX, axisY, axisZ);


        setTextToView();

    }

    private void setTextToView() {
        if (mTextViewAirborneTime != null ) {
            mTextViewAirborneTime.post(new Runnable() {
                @Override
                public void run() {
                    mTextViewAirborneTime.setText(getFreeFallTime() + "ms");
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}

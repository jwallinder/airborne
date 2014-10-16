package se.jwallinder.airborne;

import android.hardware.SensorEventListener;

public class AirborneManager {
    private static final double THRESHOLD_FREEFALL = 3.0;
    private static final double THRESHOLD_BACK_TO_EARTH = 9.0;

    private float axisX = 0, axisY = 0, axisZ = 0;
    private boolean inFreefall = false;
    private long start, stop, freeFallTime;
    private AirborneListener airborneListener;

    public void setSensorData(float axisX, float axisY, float axisZ) {
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;

        double g = getAcceleration();
        if (g < THRESHOLD_FREEFALL) {
            onFreefall();
        }
        if (g > THRESHOLD_BACK_TO_EARTH) {
            onBackToEarth();
        }
    }

    private void onBackToEarth() {
        if (inFreefall) {
            stop = System.currentTimeMillis();
            inFreefall = false;
            freeFallTime = stop - start;

            if (airborneListener != null) {
                airborneListener.onEndFreeFall();
            }
        }

    }


    private void onFreefall() {
        if (!inFreefall) {
            start = System.currentTimeMillis();
            inFreefall = true;
            freeFallTime = 0;

            if (airborneListener != null) {
                airborneListener.onBeginFreeFall();
            }
        }

    }

    public long getFreeFallTime() {
        if (inFreefall) {
            freeFallTime = System.currentTimeMillis() - stop;
        }
        return freeFallTime;
    }

    public void setAirborneListener(AirborneListener airborneListener) {
        this.airborneListener = airborneListener;
    }

    public double getAcceleration(){
        return  Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
    }

}

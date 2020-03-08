package com.example.aayushguptaa1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView  textView2, textView4, textView5, textView6;
    private EditText editText1;
    private SensorManager sensorManager;
    private String threshold = null;
    Sensor accelerometer, barometer;
    private Date lastTime = null;
    private Date curTime = null;
    private List<String>  dataList = null;
    private boolean isShake = false, isStartButtonClicked = false;
    private static String TAG = "ShakeDetectorActivityMain";
    private int numShakes;
    Double lastShakeVal;
    boolean isPeak, isTrough;
    double lastDifference = 0.0;
    boolean isBarometerClicked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Activity start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSensor();
        bindView();
        textView2.setVisibility(View.INVISIBLE);
        editText1.setText("12");
        textView6.setText("Number of shakes:"+numShakes);
    }
    private void createSensor()
    {
        Log.d(TAG, "Inside createSensor()");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }
    private void bindView(){

        Log.d(TAG,"Inside bindView()");
        textView2 =findViewById(R.id.textView2);
        textView4 =findViewById(R.id.textView4);
        editText1 = findViewById(R.id.editText1);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);

    }
    public void onStartClick(View view)
    {
        Log.d(TAG, "Start button pressed");
        threshold = String.valueOf(editText1.getText());
        numShakes = 0;
        lastShakeVal = null;
        isPeak = true;
        isTrough = true;
        lastDifference = 0.0;
        isStartButtonClicked = true;
        textView6.setText("Number of shakes:"+numShakes);
        if(threshold == null || threshold.length() == 0)
        {
            Log.e(TAG, "Threshold value not specified");
            Toast.makeText(getBaseContext(), "Please enter threshold value", Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.d(TAG, "Threshold value specified");
            sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            textView2.setVisibility(View.VISIBLE);
            dataList = new ArrayList<>();
        }
    }

    public void onStopClick(View view) throws IOException
    {
        Log.d(TAG, "Stop button pressed");
        Log.d(TAG, "Num Shakes: "+numShakes);
        sensorManager.unregisterListener(this);
        textView2.setVisibility(View.INVISIBLE);
        editText1.setText("");
        writeFile(threshold);
        threshold = "";
        isStartButtonClicked = false;
        if(isShake)
        {
            textView4.setText(numShakes + " Shakes detected");
        }
        else
        {
            textView4.setText("No Shake");
        }
        isShake = false;
        isPeak = false;
        isTrough = false;
        numShakes = 0;
        textView6.setText("Number of shakes:"+numShakes);
    }
    public void showBarometerData(View view)
    {

        boolean hasBarometer = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
        if(!hasBarometer)
        {
            Log.e(TAG, "Device does not have a barometer");
            Toast.makeText(getBaseContext(), "Device doesn't have a barometer", Toast.LENGTH_LONG).show();
        }
        else
        {
            if(!isBarometerClicked)
            {
                Log.d(TAG, "Barometer found");
                Log.d(TAG, "Registering barometer");
                isBarometerClicked = true;
                sensorManager.registerListener(MainActivity.this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else
            {
                Log.d(TAG, "Unregistering barometer");
                isBarometerClicked = false;
                sensorManager.unregisterListener(this);
                textView5.setText("");
            }

        }

    }
    public void writeFile(String threshold) throws IOException
    {
        Log.d(TAG, "Inside writeFile()");
        String fileName = threshold + "_output.csv";
        FileOutputStream output = openFileOutput(fileName, Context.MODE_PRIVATE);
        output.write((threshold+","+numShakes+"\n").getBytes());
        float time = 0;
        Log.d(TAG, ""+dataList.size());
        for(String s : dataList)
        {
            Log.d(TAG, s + ","+time);
            output.write((s + "," + time + "\n").getBytes());
            time += 0.5;
        }
        output.close();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            Log.d(TAG, "The sensor is accelerometer");
            String ax = String.format("%.3f",event.values[0]);
            String ay = String.format("%.3f",event.values[1]);
            String az = String.format("%.3f",event.values[2]);
            Double val = Math.sqrt(Math.pow(Double.parseDouble(ax),2)+Math.pow(Double.parseDouble(ay),2)+Math.pow(Double.parseDouble(az),2));
            textView2.setText("{"+ax+","+ay+","+az+"}");
            if(lastTime == null && isStartButtonClicked)
            {
                lastTime = new Date();
                dataList.add(""+val);
                System.out.println("Inside first if");
            }
            else if(lastTime != null && isStartButtonClicked)
            {
                curTime = new Date();
                System.out.println("Inside else"+Long.toString(curTime.getTime()-lastTime.getTime()));
                if(curTime.getTime() - lastTime.getTime() >= 500)
                {
                    System.out.println("Inside else if");
                    if(val > Double.parseDouble(threshold))
                    {
                        textView4.setText("Shake detected");
                        textView4.setBackgroundColor(Color.GREEN);
                        //isShake = true;
                    }
                    else
                    {
                        textView4.setText("No Shake");
                        textView4.setBackgroundColor(Color.RED);
                    }
                    if(lastShakeVal == null)
                    {
                        Log.d(TAG, "lastShake is null");
                        lastShakeVal = val;
                    }
                    else if(val < lastShakeVal && isPeak)
                    {
                        Log.d(TAG, "Trough start: "+lastShakeVal+","+val);
                        Log.d(TAG, "Threshold: "+threshold);
                        Log.d(TAG, "Last Difference:"+lastDifference);
                        if(lastShakeVal >= Double.parseDouble(threshold) && lastDifference >= 0.5)
                        {
                            Log.d(TAG, "Increase numShakes");
                            numShakes++;

                        }
                        isTrough = true;
                        isPeak = false;

                    }
                    else if(val < lastShakeVal && isTrough)
                    {
                        Log.d(TAG, "Trough: "+lastShakeVal+","+val);
                        isTrough = true;
                    }
                    else if(val >= lastShakeVal)
                    {
                        Log.d(TAG, "Peak: "+lastShakeVal+","+val);
                        isPeak = true;
                    }
                    else if(val >= lastShakeVal && isTrough)
                    {
                        Log.d(TAG, "Peak start: "+lastShakeVal+","+val);
                        isPeak = true;
                        isTrough = false;
                    }
                    lastDifference = val - lastShakeVal;
                    lastShakeVal = val;
                    dataList.add(""+val);
                    lastTime = curTime;
                    textView6.setText("Number of shakes:"+numShakes);
                }
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_PRESSURE)
        {
            Log.d(TAG, "The sensor is barometer");
            textView5.setText(String.format("%.3f mbar", event.values[0]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onResume() {
        super.onResume();
        if(sensorManager != null && accelerometer != null)
        {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


}


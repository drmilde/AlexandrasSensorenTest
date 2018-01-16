package com.example.milde.sensorentest;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private LinearLayout ll_basic;
    private Sensor gyroSensor;
    private TextView tvSensorXVal, tvSensorYVal, tvSensorZVal;
    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    private Sensor compass;
    private ImageView image;
    private TextView compassAngle;
    private float currentDegree = 0f;

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, gyroSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_NORMAL);
    }


    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];


        tvSensorXVal.setText("X: " + x);
        tvSensorYVal.setText("Y: " + y);
        tvSensorZVal.setText("Z: " + z);


        ll_basic.setBackgroundColor(Color.GREEN);
        //Der Vektor ist die Beschleunigung. √(x2 + y2 + z2) ist der Betrag der Beschleunigung.
        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(this, "es ist aber schüttelig...", Toast.LENGTH_SHORT)
                    .show();
            if (color) {
                ll_basic.setBackgroundColor(Color.GREEN);
            } else {
                ll_basic.setBackgroundColor(Color.RED);
            }
            color = !color;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ll_basic = (LinearLayout) findViewById(R.id.ll_basic);

        tvSensorXVal = (TextView) findViewById(R.id.tvSensorXVal);
        tvSensorYVal = (TextView) findViewById(R.id.tvSensorYVal);
        tvSensorZVal = (TextView) findViewById(R.id.tvSensorZVal);


/*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

*/
        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.GREEN);

        lastUpdate = System.currentTimeMillis();

        image = (ImageView) findViewById(R.id.imageViewCompass);
        compassAngle = (TextView) findViewById(R.id.angle);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (compass != null) {
            sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    private void rausDamit(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_compass, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        //     if (id == R.id.action_settings) {
        //        return true;
        //   }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            getCompass(event);
        }

    }

    private void getCompass(SensorEvent event) {
        //float degree = Math.round(event);

        float degree = Math.round(event.values[0]);
        compassAngle.setText("Heading: " + Float.toString(degree) + " degrees");
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;
    }
}
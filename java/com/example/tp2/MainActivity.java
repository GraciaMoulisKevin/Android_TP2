package com.example.tp2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Button button;
    private TextView text;
    private Sensor accelero;
    private Sensor prox;

    private String cameraID;
    private boolean on_off;
    private CameraManager camManager;

    private ImageView img;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.activity_main_txt);
        button = (Button) findViewById(R.id.activity_main_button);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Exercice 1
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor t : sensorList){
            text.setText(text.getText() + "\n" + t.getType());
        }

        // Exercice 2
        boolean ch = false;
        for(Sensor t : sensorList){
            if(t.getType() == Sensor.TYPE_ACCELEROMETER){
                ch = true;
            }
        }
        if(!ch){
            text.setText("L'accélerometre n'est pas présent sur l'appareil.");
        }

        // Exercice 3
        accelero = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Exercice 4

        // Exercice 5
        camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraID = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        on_off = false;

        // Exercice 6
        img = (ImageView) findViewById(R.id.activity_imageView);
        img.setImageResource(R.drawable.unknow);
        prox = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    }

    @Override
    protected void onPause(){
        sensorManager.unregisterListener(this, accelero);
        sensorManager.unregisterListener(this, prox);
        super.onPause();
    }

    @Override
    protected void onResume(){
        sensorManager.registerListener(this, accelero, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, prox, SensorManager.SENSOR_DELAY_FASTEST);
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public final void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ){
            float x, y, z;
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            float vit = Math.abs(x); // + Math.abs(y) + Math.abs(z);
            if(vit > 5){
                button.setBackgroundColor(Color.RED);
            }else if(vit > 3){
                button.setBackgroundColor(Color.BLACK);
            }else{
                button.setBackgroundColor(Color.GREEN);
            }

            if( x > 0){ // GAUCHE
                text.setText("GAUCHE _ ");
            }else if(x < 0){ // DROITE
                text.setText("DROITE _ ");
            }else{ // Pas de mouvement sur l'axe X
                text.setText("NEUTRE _ ");
            }

            if( y < 9.80){ // HAUT
                text.setText(text.getText()+"HAUT");
            }else if(y > 9.81){ // BAS
                text.setText(text.getText()+"BAS");
            }else{ // Pas de mouvement sur l'axe Y
                text.setText(text.getText()+"NEUTRE");
            }

            // Shake
            if (vit > 4) {
                if(on_off){
                    try {
                        camManager.setTorchMode(cameraID, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        camManager.setTorchMode(cameraID, true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY ){
            float x = event.values[0];

            if( x < 3 ){
                img.setImageResource(R.drawable.proche);
            }else{
                img.setImageResource(R.drawable.loin);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
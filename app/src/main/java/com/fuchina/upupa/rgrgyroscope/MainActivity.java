package com.fuchina.upupa.rgrgyroscope;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.Math;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private SensorManager manager;
    private Sensor orient;
    private float y_angle;
    private double y_angleDEG;
    private String angleString;

    private TextView angle;
    private TextView savedAngle;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        orient = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        angle = (TextView) findViewById(R.id.angle);
        savedAngle = (TextView) findViewById(R.id.savedAngle);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        savedAngle.setText("Нажмите кнопку");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                y_angle = event.values[1];
                y_angleDEG = Math.abs(y_angle);
                y_angleDEG *= 180;
                DecimalFormat df = new DecimalFormat("#");
                angleString = "Y:" + df.format(y_angleDEG);
                angle.setText(angleString);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile("data.txt");
                openFile("data.txt");
            }
        });
        orient = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        manager.registerListener(listener, orient, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void openFile(String fileName) {
        try {
            InputStream inputStream = openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                inputStream.close();
                savedAngle.setText(builder.toString());
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveFile(String fileName) {
        try {
            OutputStream outputStream = openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(angleString);
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }

    }

}

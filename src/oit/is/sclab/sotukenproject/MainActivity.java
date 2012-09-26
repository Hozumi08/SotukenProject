package oit.is.sclab.sotukenproject;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
//import jp.marunomaruno.android.orientationsensor.R;

/**
 * 方位を表示する。
 * @author marunomaruno
 * @version 1.0, 2011-12-01
 */
public class MainActivity extends Activity {

    // TextView
    private TextView orientationTextView; // 方位センサーの表示域

    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
                    // onPause()が呼ばれたときに、すべてのリスナーを解除するため

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orientationTextView = (TextView) findViewById(R.id.sensor_value);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorEventListener = new OrientationSensorListener(orientationTextView); // (1)

        // 加速度センサーを取得して登録する
        for (Sensor sensor : sensorManager
                .getSensorList(Sensor.TYPE_ACCELEROMETER)) {
            sensorManager.registerListener(sensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        // 磁気センサーを取得して登録する
        for (Sensor sensor : sensorManager
                .getSensorList(Sensor.TYPE_MAGNETIC_FIELD)) {
            sensorManager.registerListener(sensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // すべてのリスナーを解除する
        sensorManager.unregisterListener(sensorEventListener);
    }
}
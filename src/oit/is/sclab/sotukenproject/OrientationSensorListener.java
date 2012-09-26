package oit.is.sclab.sotukenproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
//import jp.marunomaruno.android.orientationsensor.R;

/**
 * 方位センサーのリスナー
 * @author maruno
 * @version 1.0, 2011-12-01
 * @since 1.0
 */
public class OrientationSensorListener implements SensorEventListener {

    private TextView textView; // 表示域
    private float[] accelerometerValues; // 加速度センサーの値    // (1)
    private float[] magneticFieldValues; // 磁気センサーの値    // (2)

    public OrientationSensorListener(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] orientationValues = new float[3]; // 方位の値    // (3)

        switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER: // 加速度センサーが変化したとき
            accelerometerValues = clone(event.values);    // (4)
            break;

        case Sensor.TYPE_MAGNETIC_FIELD: // 磁気センサーが変化したとき
            magneticFieldValues = clone(event.values);    // (5)
            break;
        }

        if (accelerometerValues != null && magneticFieldValues != null) {

            float[] inRotationMatrix = new float[16]; // 入力用の回転行列  // (6)
            float[] outRotationMatrix = new float[16]; // 出力用の回転行列 // (7)

            SensorManager.getRotationMatrix(inRotationMatrix, null,
                    accelerometerValues, magneticFieldValues);           // (8)
            SensorManager.remapCoordinateSystem(inRotationMatrix,
                    SensorManager.AXIS_X, SensorManager.AXIS_Z,
                    outRotationMatrix);                                  // (9)
            SensorManager.getOrientation(outRotationMatrix, orientationValues); // (10)

            // TextViewに表示する
            textView.setText("");

            // ラジアンを表示
            textView.append(String
                    .format(textView.getResources().getString(
                            R.string.sensor_value_format), textView
                            .getResources().getString(R.string.orientation),
                            orientationValues[0], orientationValues[1],
                            orientationValues[2], textView.getResources()
                                    .getString(R.string.OrientationUnit)));
            textView.append("¥n");

            // 度を表示
            textView.append(String.format(textView.getResources().getString(
                    R.string.sensor_value_format), textView.getResources()
                    .getString(R.string.orientation),
                    toOrientationDegrees(orientationValues[0]),
                    toOrientationDegrees(orientationValues[1]),
                    toOrientationDegrees(orientationValues[2]), textView
                            .getResources()
                            .getString(R.string.OrientationUnit2)));
            textView.append("¥n");

            // 方位を表示
            textView.append(String.format(textView.getResources().getString(
                    R.string.orientation_value_format),
                    toOrientationString(orientationValues[0]),
                    toOrientationDegrees(orientationValues[0]), textView
                            .getResources()
                            .getString(R.string.OrientationUnit2)));
            textView.append("¥n");
        }
    }

    /**
     * 配列の深いコピーを行う。
     * ※Android2.2 の Arrays クラスには copyOf() メソッドがない。
     * @param source ソースの配列
     * @return 深いコピーを行った結果
     */
    private float[] clone(float[] source) {                        // (11)
        float[] target = new float[source.length];
        for (int i = 0; i < target.length; i++) {
            target[i] = source[i];
        }
        return target;
//        return Arrays.copyOf(sourse, sourse.length);    // 本来はこれでOK
    }

    /**
     * 方位の角度にする。方位の角度は、0以上360未満。
     * @param rad ラジアン。ただし、-π以上π未満。
     * @return 方位の角度
     */
    private float toOrientationDegrees(float rad) {                // (12)

        return (float) (rad >= 0 ? Math.toDegrees(rad) : 360 + Math
                .toDegrees(rad));    // (13)
    }

    /**
     * 方位をあらわす文字列を取得する。
     *         北 0 rad
     *         東 π/2 rad
     *         南 π rad
     *         西 -π/2 rad
     * @param azimuth 方位
     * @return 方位をあらわす文字列
     */
    private String toOrientationString(float azimuth) {            // (14)
        double[] ORIENTATION_RANGE = {
                -(Math.PI * 3 / 4), // 南
                -(Math.PI * 1 / 4), // 西
                +(Math.PI * 1 / 4), // 北
                +(Math.PI * 3 / 4), // 東
                // Math.PI, // 南
        };    // (15)

        for (int i = 0; i < ORIENTATION_RANGE.length; i++) {
            if (azimuth < ORIENTATION_RANGE[i]) {
                return textView.getResources().getStringArray(
                        R.array.orientationNames)[i];    // (16)
            }
        }

        return textView.getResources().getStringArray(R.array.orientationNames)[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        assert true; // 何もしない
    }
}
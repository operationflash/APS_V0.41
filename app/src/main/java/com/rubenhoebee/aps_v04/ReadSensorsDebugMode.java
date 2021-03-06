package com.rubenhoebee.aps_v04;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.Math;
import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

public class ReadSensorsDebugMode extends AppCompatActivity implements
        SensorEventListener,
        OnTouchListener{
    private boolean accelerometer = false, ConnectionAccess = false, gravity = false;
    private float GValueX = 0, GValueY = 0, GValueZ = 0;
    private TextView GaxisX, GaxisY, GaxisZ, XAngle, YAngle;
    private WebSocketClient mWebSocketClient;
    private byte z = 0;
    private final byte zStep = 10;
    private GestureDetectorCompat mDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_sensors_debug_mode);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        android.hardware.SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){ // Check if there is a gravity sensor
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "Gravity sensor detected!", duration);
            toast.show();
            Sensor gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            sensorManager.registerListener(this, gSensor, android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
            gravity = true;
            accelerometer = false; //Because we don't need it
        }
        else {
            gravity = false;
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){ // Check if there is a accelerometer
                accelerometer = true;
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Accelerometer detected!", duration);
                toast.show();
                Sensor aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, aSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
            }
            else{
                accelerometer = false;
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "This device doesn't support robot control", duration);
                toast.show();
            }
        }
        Button Dead = (Button) findViewById(R.id.DeadBtn);
        Dead.setOnTouchListener(this);
        GaxisX =(TextView)findViewById(R.id.GXOutput);
        GaxisY=(TextView)findViewById(R.id.GYOutput);
        GaxisZ =(TextView)findViewById(R.id.GZOutput);
        XAngle=(TextView)findViewById(R.id.XAngle);
        YAngle =(TextView)findViewById(R.id.YAngle);

        URI uri;
        try {
            uri = new URI("ws://149.201.4.25:1280/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                if (mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) { //Ready to send data
                    mWebSocketClient.send("Login::Android::App"); //Login to server with the android login credentials
                }
                //mWebSocketClient.send("ContinuousMovement::On");
            }


            @Override
            public void onError (Exception e){
                Log.i("Websocket", "Error " + e.getMessage());
            }

            @Override
            public void onClose (int i, String s, boolean b){
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView logView = (TextView)findViewById(R.id.Log);
                        logView.setMaxLines(25);
                        TextView PingTime = (TextView)findViewById(R.id.Ping);
                        logView.setText(logView.getText() + "\n" + message);
                        String[] Split = message.split("::");
                        switch (Split[0]){
                            case "Ping":
                                PingTime.setText(Split[1]);
                                return;
                            case "PosXYZ":
                                TextView xCoor = (TextView)findViewById(R.id.xPos);
                                TextView yCoor = (TextView)findViewById(R.id.yPos);
                                TextView zCoor = (TextView)findViewById(R.id.zPos);
                                TextView xAngle = (TextView)findViewById(R.id.xAngle);
                                TextView yAngle = (TextView)findViewById(R.id.yAngle);
                                TextView zAngle = (TextView)findViewById(R.id.zAngle);
                                xCoor.setText(Split[1]);
                                yCoor.setText(Split[2]);
                                zCoor.setText(Split[3]);
                                xAngle.setText(Split[4]);
                                yAngle.setText(Split[5]);
                                zAngle.setText(Split[6]);
                                return;
                            case "RobotSpeed":
                                TextView rSpeed = (TextView)findViewById(R.id.rSpeed);
                                rSpeed.setText(Split[1]);
                                return;
                            case "CollisionDetection":
                                switch (Split[1]){
                                    case "Enabled": // Not tested in real life, didn't want to force robot into a collision
                                        Intent opencollisionDetected = new Intent(ReadSensorsDebugMode.this, CollisionDetected.class);
                                        startActivity(opencollisionDetected);
                                        return;
                                    case "Disabled":
                                        return;
                                }
                                return;
                            case "NOTICE":
                                Toast toast = Toast.makeText(getApplicationContext(), Split[1], Toast.LENGTH_SHORT);
                                toast.show();
                                switch (Split[1]) {
                                    case "No Connection to Robot Control Unit.":
                                        // turn robot text color to red
                                        return;
                                    case "You were kicked from the server.":
                                        // turn robot text color to red
                                        break;
                                    case "Admin took control over the robot.":
                                        break;
                                }
                                break;
                            case "WARNING":
                                toast = Toast.makeText(getApplicationContext(), Split[1], Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                        }
                    }
                });
            }
        };
        mWebSocketClient.connect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (gravity) {
            GValueX = event.values[0];
            GValueY = event.values[1];
            GValueZ = event.values[2];
        }
        else if (accelerometer) {
            final float alpha = 0.8f;
            // Isolate the force of gravity with the low-pass filter.
            GValueX = alpha * GValueX  + (1 - alpha) * event.values[0];
            GValueY = alpha * GValueY  + (1 - alpha) * event.values[1];
            GValueZ = alpha * GValueZ  + (1 - alpha) * event.values[2];
        }
        //For debugging and because engineers like raw data
        GaxisX.setText(String.valueOf(GValueX));
        GaxisY.setText(String.valueOf(GValueY));
        GaxisZ.setText(String.valueOf(GValueZ));
        float AValueX = (float) ((Math.atan2(GValueX, GValueZ) * 180) / Math.PI);
        float AValueY = (float) ((Math.atan2(GValueY, GValueZ) * 180) / Math.PI);
        XAngle.setText(String.valueOf(AValueX));
        YAngle.setText(String.valueOf(AValueY));
        if (ConnectionAccess) {
            if (AValueX >= -5 && AValueX <= 5){
                AValueX = 0;
            }
            if (AValueY >= -5 && AValueY <= 5){
                AValueY = 0;
            }
            String relative = "MoveL::" + String.valueOf(-AValueY) + "::" + String.valueOf(AValueX) + "::"  + String.valueOf(z)+ "::0::0::0::Rel";
            if (mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) { //Ready to send data
                mWebSocketClient.send(relative);
            }
            //mWebSocketClient.send("ContinuousMovement::On");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onTouch (View view, MotionEvent me){
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            ConnectionAccess = true;
        }
        else if (me.getAction() == MotionEvent.ACTION_UP) {
            ConnectionAccess = false;
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        mWebSocketClient.close();
        ConnectionAccess = false;
        super.onBackPressed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keycode = event.getKeyCode();
        switch (keycode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    z = -zStep;
                }
                else if (action == KeyEvent.ACTION_UP) {
                    z = 0;
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    z = zStep;
                }
                else if (action == KeyEvent.ACTION_UP) {
                    z = 0;
                }
                return true;
            default:
                z = 0;
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            if (velocityX > 3500) {
                Intent openAxisInterface = new Intent(ReadSensorsDebugMode.this, DrawJoystick.class);
                startActivity(openAxisInterface);

            }
            else if (velocityX < -3500) {
                Intent joystickInterface = new Intent(ReadSensorsDebugMode.this, DrawJoystick.class);
                startActivity(joystickInterface);
            }
            return true;
        }
    }
}

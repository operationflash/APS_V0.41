package com.rubenhoebee.aps_v04;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public final static String CAM_MESSAGE = "0";
    public final static String EXTRA_MESSAGE = "http://149.201.4.25/html/stream1.html";
    public final static String TOAST_MESSAGE = "toast";
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openCamera1(View view) {
        Intent openCameraView = new Intent(this, CameraViewActivity.class);
        String url = "http://149.201.4.25/html/stream1.html";
        String toast = "Workspace view";
        openCameraView.putExtra(EXTRA_MESSAGE, url);
        openCameraView.putExtra(TOAST_MESSAGE, toast);
        openCameraView.putExtra(CAM_MESSAGE, String.valueOf(1));
        startActivity(openCameraView);
    }

    public void openCamera2(View view) {
        Intent openCameraView = new Intent(this, CameraViewActivity.class);
        String url = "http://149.201.4.25/html/stream2.html";
        String toast = "Front View";
        openCameraView.putExtra(EXTRA_MESSAGE, url);
        openCameraView.putExtra(TOAST_MESSAGE, toast);
        openCameraView.putExtra(CAM_MESSAGE, String.valueOf(2));
        startActivity(openCameraView);
    }

    public void openCamera3(View view) {
        Intent openCameraView = new Intent(this, CameraViewActivity.class);
        String url = "http://149.201.4.25/html/stream3.html";
        String toast = "Side View";
        openCameraView.putExtra(EXTRA_MESSAGE, url);
        openCameraView.putExtra(TOAST_MESSAGE, toast);
        openCameraView.putExtra(CAM_MESSAGE, String.valueOf(3));
        startActivity(openCameraView);
    }

    public void openCamera4(View view) {
        Intent openCameraView = new Intent(this, CameraViewActivity.class);
        String url = "http://149.201.4.25/html/stream4.html";
        String toast = "Tool View, may be slow to load";
        openCameraView.putExtra(EXTRA_MESSAGE, url);
        openCameraView.putExtra(TOAST_MESSAGE, toast);
        openCameraView.putExtra(CAM_MESSAGE, String.valueOf(4));
        startActivity(openCameraView);
    }


    public void Login(View view) {

        EditText username = (EditText) findViewById(R.id.UsernameInpt);
        EditText password = (EditText) findViewById(R.id.PasswordInpt);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        if (username.getText().toString().equals("Android") && password.getText().toString().equals("App")) {
            String toastText = "Login successful";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
            Intent openReadSensors = new Intent(this, ReadSensors.class);
            startActivity(openReadSensors);
        }
        else if(username.getText().toString().equals("Android") && password.getText().toString().equals("Admin")){
            String toastText = "Debug Mode";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
            Intent openReadSensorsDebugMode = new Intent(this, ReadSensorsDebugMode.class);
            startActivity(openReadSensorsDebugMode);

        }
        else{
            String toastText = "Login failed";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
            username.setText("");
            password.setText("");
        }

    }
    public void test (View view) {
        Intent openAxisInterface = new Intent(this, DrawJoystick.class);
        startActivity(openAxisInterface);
    }
}


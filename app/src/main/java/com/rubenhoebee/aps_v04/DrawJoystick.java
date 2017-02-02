package com.rubenhoebee.aps_v04;

//http://stackoverflow.com/questions/4979212/how-to-create-a-relativelayout-programmatically-with-two-buttons-one-on-top-of-t

import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

public class DrawJoystick extends AppCompatActivity{
    Activity_DrawJoystick DrawJoystickView;
    private int width, height;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        DrawJoystickView = new Activity_DrawJoystick(this, width, height);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(DrawJoystickView);
    }

    @Override
    protected void onPause(){
        super.onPause();
        DrawJoystickView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        DrawJoystickView.resume();
    }
}


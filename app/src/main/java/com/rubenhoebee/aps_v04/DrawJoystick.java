package com.rubenhoebee.aps_v04;

//http://stackoverflow.com/questions/4979212/how-to-create-a-relativelayout-programmatically-with-two-buttons-one-on-top-of-t

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DrawJoystick extends AppCompatActivity {
    Activity_DrawJoystick DrawJoystickView;
    private int width, height, camera = 3;
    private static final String TAG = "MyActivity";
    private float scale1, scale2;
    private final int duration = Toast.LENGTH_SHORT;
    private GestureDetectorCompat mDetector;
    private WebView webView;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = new RelativeLayout(this); // test/
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT); // test/
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
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
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://149.201.4.25/html/stream3.html");
        webView.setBackgroundColor(Color.TRANSPARENT);
        scale1 = (float) 100*((float)width / (float)640);
        scale2 = (float) 100*((float)height / (float)480);
        final int screenOrientation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (screenOrientation){
            case 0:
                webView.setInitialScale((int)scale1);
                break;
            case 1:
                webView.setInitialScale((int)scale2);
                break;
            case 2:
                webView.setInitialScale((int)scale1);
                break;
            case 3:
                webView.setInitialScale((int)scale2);
                break;
        }
        Log.i("WebView", "Scale1: " + scale1 + ", Scale2: " + scale2);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        webView.draw(canvas);
        webView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
        webView.setLayoutParams(lp);
        //setContentView(relativeLayout, lp);
        //setContentView(webView);
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
private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        if (velocityX > 3500) {
            switch (camera){
                case 1:
                    webView.loadUrl("http://149.201.4.25/html/stream4.html");
                    toast = Toast.makeText(getApplicationContext(), "Tool View, may be slow to load", duration);
                    toast.show();
                    camera = 4;
                    break;
                case 2:
                    webView.loadUrl("http://149.201.4.25/html/stream1.html");
                    toast = Toast.makeText(getApplicationContext(), "Workspace view", duration);
                    toast.show();
                    camera = 1;
                    break;
                case 3:
                    webView.loadUrl("http://149.201.4.25/html/stream2.html");
                    toast = Toast.makeText(getApplicationContext(), "Front View", duration);
                    toast.show();
                    camera = 2;
                    break;
                case 4:
                    webView.loadUrl("http://149.201.4.25/html/stream3.html");
                    toast = Toast.makeText(getApplicationContext(), "Side View", duration);
                    toast.show();
                    camera = 3;
                    break;
            }
        }
        else if (velocityX < -3500) {
            switch (camera) {
                case 1:
                    webView.loadUrl("http://149.201.4.25/html/stream2.html");
                    toast = Toast.makeText(getApplicationContext(), "Front View", duration);
                    toast.show();
                    camera = 2;
                    break;
                case 2:
                    webView.loadUrl("http://149.201.4.25/html/stream3.html");
                    toast = Toast.makeText(getApplicationContext(), "Side View", duration);
                    toast.show();
                    camera = 3;
                    break;
                case 3:
                    webView.loadUrl("http://149.201.4.25/html/stream4.html");
                    toast = Toast.makeText(getApplicationContext(), "Tool View, may be slow to load", duration);
                    toast.show();
                    camera = 4;
                    break;
                case 4:
                    webView.loadUrl("http://149.201.4.25/html/stream1.html");
                    toast = Toast.makeText(getApplicationContext(), "Workspace view", duration);
                    toast.show();
                    camera = 1;
                    break;
            }
        }
        return true;
    }
}
}


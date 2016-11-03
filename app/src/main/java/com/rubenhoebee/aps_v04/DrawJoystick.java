package com.rubenhoebee.aps_v04;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;

public class DrawJoystick extends AppCompatActivity {
    Activity_DrawJoystick DrawJoystickView;
    private int width,height;
    private static final String TAG = "MyActivity";
    private WebView webView;
    private float scale1, scale2;

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

        WebView webView = new WebView(this);
        setContentView(webView);
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
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        webView.draw(canvas);
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

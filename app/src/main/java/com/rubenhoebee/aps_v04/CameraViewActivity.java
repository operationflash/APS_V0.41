package com.rubenhoebee.aps_v04;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

public class CameraViewActivity extends AppCompatActivity {
    private float scale1, scale2;
    private final int duration = Toast.LENGTH_SHORT;
    private int camera, screenWidth, screenHeight;
    private GestureDetectorCompat mDetector;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_view);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        webView = (WebView) findViewById(R.id.CameraView);
        webView.getSettings().setJavaScriptEnabled(true);
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        scale1 = (float) 100*((float)screenWidth / (float)640);
        scale2 = (float) 100*((float)screenHeight / (float)480);
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
        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String toastText = intent.getStringExtra(MainActivity.TOAST_MESSAGE);
        String camNumber = intent.getStringExtra(MainActivity.CAM_MESSAGE);
        camera = Integer.parseInt(camNumber);
        webView.loadUrl(url);
        Toast toast = Toast.makeText(getApplicationContext(), toastText, duration);
        toast.show();
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        webView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return (mDetector.onTouchEvent(event) || super.onTouchEvent(event));
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        Toast toast;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

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

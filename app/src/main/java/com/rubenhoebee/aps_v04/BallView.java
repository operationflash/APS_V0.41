package com.rubenhoebee.aps_v04;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.Math;
import java.net.URI;
import java.net.URISyntaxException;

public class BallView extends SurfaceView implements SurfaceHolder.Callback {
    private double angle;
    private double x3;
    private double y3;
    private float midX;
    private float squareX;
    private float midY;
    private int count;
    private int x2;
    private int y2;
    private final int height;
    private int radius;
    private final int width;
    private final MyThread thread;
    private final Paint paint = new Paint();
    private float[] x={0,500,0,0,0,0,0,0,0,0};
    private float[] y={0,500,0,0,0,0,0,0,0,0};
    private int smallStepp = 10;
    private int bigStepp = 20;
    private WebSocketClient mWebSocketClient;

    public BallView(Context context, int w, int h) {
        super(context);
        width=w;
        height=h;
        thread=new MyThread(getHolder(),this);
        getHolder().addCallback(this);
        setFocusable(true);

    /**URI uri;
    try {
        uri = new URI("ws://149.201.4.25:1280/");
    } catch (URISyntaxException e) {
        e.printStackTrace();
        return;
    }
    mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            mWebSocketClient.send("Login::Android::App"); //Login to server with the android login credentials
            //mWebSocketClient.send("ContinuousMovement::On");
        }

        @Override
        public void onError (Exception e){

        }

        @Override
        public void onClose (int i, String s, boolean b){
        }

        @Override
        public void onMessage(String s) {
            final String message = s;
            Activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView PingTime = (TextView)findViewById(R.id.Ping);
                    String[] Split = message.split("::");
                    switch (Split[0]){
                        case "Ping":
                            break;
                        case "PosXYZ":
                            break;
                        case "RobotSpeed":
                            TextView rSpeed = (TextView)findViewById(R.id.rSpeed);
                            rSpeed.setText(Split[1]);
                            break;
                        case "CollisionDetection":
                            switch (Split[1]){
                                case "Enabled": // Not tested in real life, didn't want to force robot into a collision
                                    break;
                                case "Disabled":
                                    break;
                            }
                            return;
                        case "NOTICE":
                            switch (Split[1]){
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
                            break;
                    }
                }
            });
        }
    };
    mWebSocketClient.connect();*/
}

    @Override
    public void onDraw(Canvas canvas) {
        midX = (float) width*((float)3/(float)4);
        midY = (float)height*((float)2/(float)5);
        squareX = (float) width*((float)1/(float)4);
        radius = 250;
        super.onDraw(canvas);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        canvas.drawColor(Color.BLUE);//To make background
        canvas.drawCircle(midX,midY,radius,paint);
        canvas.drawRect(squareX - 10,midY + radius, squareX + 10, midY  - radius, paint);
        for (int i = 0; i < count; i++) {
            x2 = ((int) x[i] - (int) midX);
            y2 = ((int) y[i] - (int) midY);
            if ((Math.abs(x2) <= radius) && (Math.abs(y2) <= radius) && x[i] >= width / 2) { // Joystick circle
                if (Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2)) <= radius) {
                    canvas.drawBitmap(bitmap, x[i] - (bitmap.getWidth() / 2), y[i] - (bitmap.getHeight() / 2), null);
                }
            } else if (x[i] >= width / 2) { // Right part of screen
                angle = Math.atan2(y2, x2);
                if (angle >= 0 && angle <= 90) {
                    x3 = Math.cos(angle) * (double) radius + midX;
                    y3 = Math.sin(angle) * (double) radius + midY;
                } else if (angle <= -0.5 * Math.PI && angle >= -Math.PI) {
                    angle = angle + Math.PI;
                    x3 = -Math.cos(angle) * (double) radius + midX;
                    y3 = -Math.sin(angle) * (double) radius + midY;
                } else if (angle <= 0 && angle >= -0.5 * Math.PI) {
                    angle = angle + 0.5 * Math.PI;
                    x3 = Math.sin(angle) * (double) radius + midX;
                    y3 = -Math.cos(angle) * (double) radius + midY;
                }
                canvas.drawBitmap(bitmap, (int) x3 - (bitmap.getWidth() / 2), (int) y3 - (bitmap.getHeight() / 2), null);
            }
            if (x[i] < width / 2 && y[i] <= midY + radius && y[i] >= midY - radius) {
                canvas.drawBitmap(bitmap, squareX  - (bitmap.getWidth() / 2), y[i] - (bitmap.getHeight() / 2), null);
            }
            else if (x[i] < width / 2 && y[i] >= midY + radius){
                canvas.drawBitmap(bitmap, squareX  - (bitmap.getWidth() / 2), midY + radius - (bitmap.getHeight() / 2), null);
            }
            else if (x[i] < width / 2 && y[i] <= midY - radius) {
                canvas.drawBitmap(bitmap, squareX - (bitmap.getWidth() / 2), midY - radius - (bitmap.getHeight() / 2), null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        count = event.getPointerCount();
        if (count <=10) {
            for (int i = 0; i < count; i++) {
                x[i] = event.getX(i);
                y[i] = event.getY(i);
                if (x[i] < 0)
                    x[i] = 0;
                if (x[i] > width)
                    x[i] = width;
                if (y[i] < 0)
                    y[i] = 0;
                if (y[i] > height)
                    y[i] = height;
            }
        }
        return true;
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.startrun(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.startrun(false);
        //thread.stop();
    }
}
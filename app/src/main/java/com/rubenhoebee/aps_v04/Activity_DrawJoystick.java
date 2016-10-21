package com.rubenhoebee.aps_v04;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Handler;

public class Activity_DrawJoystick extends SurfaceView implements Runnable{
    Thread thread = null;
    boolean CanDraw = false, BackSet = false, Kicked = false, RobotControl = false;
    Paint backgroundPaint, textPaint, joyPaint, errorPaint;
    Canvas canvas;
    SurfaceHolder surfaceHolder;
    private static final String TAG = "MyActivity";
    private  int count, width, height, radius, joyradius, x2, y2,fontSize = 30, xV = 0, yV = 0, zV = 0;
    private float[] x={0,500,0,0,0,0,0,0,0,0}, y={0,500,0,0,0,0,0,0,0,0};
    private float midX,midY,squareX, roundX, textX;
    private double angle,x3,y3;
    private WebSocketClient mWebSocketClient;
    private String[] Split;
    private String ping ="Ping: 999", rCommand, rOldCommand;
    private String xCoor = "xCoor: ---", yCoor = "yCoor: ---", zCoor = "zCoor: ---";
    private String xAngle = "xAngle: ---", yAngle = "yAngle: ---", zAngle = "zAngle: ---";
    Activity activity;

    public Activity_DrawJoystick(Context context, int w, int h) {
        super(context);
        width = w;
        height = h;
        surfaceHolder = getHolder();
    }

    @Override
    public void run() {
        initialize();
        while (CanDraw){
            if(!surfaceHolder.getSurface().isValid()){
                continue;
            }
            if (BackSet == false){
                initBack();
            }
            draw(canvas);
        }
    }

    private void initialize(){
        prepPaint();
        radius = 250;
        joyradius = 75;
        roundX = (float)width*((float)5/(float)6);
        squareX = (float) width*((float)1/(float)8);
        midX = (float)width*((float)1/(float)2);
        midY = (float)height*((float)1/(float)2);
        textX = midX - 3 * fontSize;

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
                mWebSocketClient.send("Login::Android::App"); //Login to server with the android login credentials
                //mWebSocketClient.send("ContinuousMovement::On");
            }
            @Override
            public void onError (Exception e){
                Log.i("Websocket", "Error: " + e.getMessage());
            }

            @Override
            public void onClose (int i, String s, boolean b){
            }

            @Override
            public void onMessage(String s) {
                Log.i("Websocket", "onMessage: " + s);
                final String message = s;
                        Split = message.split("::");
                        switch (Split[0]) {
                            case "Ping":
                                ping = "Ping: " + Split[1];
                                break;
                            case "PosXYZ":
                                xCoor = "xCoor: " + Split[1];
                                yCoor = "yCoor: " + Split[2];
                                zCoor = "zCoor: " + Split[3];
                                xAngle = "xAngle: " + Split[4];
                                yAngle = "yAngle: " + Split[5];
                                zAngle = "zAngle: " + Split[6];
                                break;
                            case "CollisionDetection":
                                switch (Split[1]){
                                    case "Enabled":
                                        break;
                                    case "Disabled":
                                        break;
                                }
                                return;
                            case "NOTICE":
                                switch (Split[1]){
                                    case "No Connection to Robot Control Unit.":
                                        // turn robot to connected
                                        return;
                                    case "You were kicked from the server.":
                                        Kicked = true;
                                        // turn server icon to fail
                                        break;
                                    case "Admin took control over the robot.":
                                        Kicked = true;
                                        break;
                                }
                                break;
                }
            }
        };
        mWebSocketClient.connect();
        if (mWebSocketClient != null) {
            //mWebSocketClient.send("test");
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
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        control();
        if (count > 2){//Set limit to two fingers for controlling
            count = 2;
        }
        for (int i = 0; i < count; i++) {
            x2 = ((int) x[i] - (int) roundX);
            y2 = ((int) y[i] - (int) midY);
            if ((Math.abs(x2) <= radius) && (Math.abs(y2) <= radius) && x[i] >= width / 2) { // Joystick circle
                if (Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2)) <= radius) {
                    canvas.drawCircle(x[i], y[i], joyradius, joyPaint);
                }
            } else if (x[i] >= width / 2) { // Right part of screen
                angle = Math.atan2(y2, x2);
                if (angle >= 0 && angle <= 90) {
                    x3 = Math.cos(angle) * (double) radius + roundX;
                    y3 = Math.sin(angle) * (double) radius + midY;
                } else if (angle <= -0.5 * Math.PI && angle >= -Math.PI) {
                    angle = angle + Math.PI;
                    x3 = -Math.cos(angle) * (double) radius + roundX;
                    y3 = -Math.sin(angle) * (double) radius + midY;
                } else if (angle <= 0 && angle >= -0.5 * Math.PI) {
                    angle = angle + 0.5 * Math.PI;
                    x3 = Math.sin(angle) * (double) radius + roundX;
                    y3 = -Math.cos(angle) * (double) radius + midY;
                }
                canvas.drawCircle((int) x3, (int) y3, joyradius, joyPaint);
            }
            if (x[i] < width / 2 && y[i] <= midY + radius && y[i] >= midY - radius) {
                canvas.drawCircle(squareX, y[i], joyradius, joyPaint);
            }
            else if (x[i] < width / 2 && y[i] >= midY + radius){
                canvas.drawCircle(squareX, midY + radius, joyradius, joyPaint);
            }
            else if (x[i] < width / 2 && y[i] <= midY - radius) {
                canvas.drawCircle(squareX , midY - radius, joyradius, joyPaint);
            }
        }
        rCommand = "MoveL::" + xV + "::" + yV + "::"  + zV + "::0::0::0::Rel";
        surfaceHolder.unlockCanvasAndPost(canvas);
        if (mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) { //Ready to send data
                mWebSocketClient.send(rCommand);
        }

    }



    private void initBack() {
        canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT);
        surfaceHolder.unlockCanvasAndPost(canvas);
        BackSet = true;
    }

    private void control() {
        canvas.drawCircle(roundX,midY,radius,backgroundPaint);
        canvas.drawRect(squareX - 10,midY + radius, squareX + 10, midY  - radius, backgroundPaint);
        canvas.drawText(ping,textX,fontSize,textPaint);
        canvas.drawText(xCoor,textX,fontSize*2,textPaint);
        canvas.drawText(yCoor,textX,fontSize*3,textPaint);
        canvas.drawText(zCoor,textX,fontSize*4,textPaint);
        canvas.drawText(xAngle,textX,fontSize*5,textPaint);
        canvas.drawText(yAngle,textX,fontSize*6,textPaint);
        canvas.drawText(zAngle,textX,fontSize*7,textPaint);
        if (Kicked) {
            canvas.drawText("You no longer have control", textX - fontSize * 10, midY, errorPaint);
        }
        if (RobotControl) {

        }
    }

    private void prepPaint() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLUE);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        joyPaint = new Paint();
        joyPaint.setColor(Color.RED);
        joyPaint.setStyle(Paint.Style.FILL);
        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(fontSize);
        errorPaint = new Paint();
        errorPaint.setColor(Color.RED);
        errorPaint.setStyle(Paint.Style.FILL);
        errorPaint.setTextSize(fontSize*2);
    }

    public void pause() {
        CanDraw = false;
        mWebSocketClient.close();
        while(true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread = null;
    }

    public void resume(){
        CanDraw = true;
        if (mWebSocketClient != null) {
            mWebSocketClient.connect();
        }
        thread = new Thread(this);
        thread.start();
    }
}



/** Log.v(TAG, "Screen size = " + width +", " + height); */

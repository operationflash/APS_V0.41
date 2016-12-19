package com.rubenhoebee.aps_v04;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.WebView;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Handler;

public class Activity_DrawJoystick extends SurfaceView implements Runnable{
    Thread thread = null;
    boolean CanDraw = false, BackSet = false, Kicked = false, RobotControl = true, OutWorkspace = false;
    Paint backgroundPaint, textPaint, joyPaint, errorPaint, restJoyPaint;
    Canvas canvas;
    SurfaceHolder surfaceHolder;
    private static final String TAG = "MyActivity";
    private  int count, width, height, radius, joyradius, x2, y2,fontSize = 30;
    private float[] x={0,500,0,0,0,0,0,0,0,0}, y={0,500,0,0,0,0,0,0,0,0};
    private float midX,midY,squareX, roundX, textX;
    private double angle,x3,y3, xV = 0, yV = 0, joySpeed = 0, zV = 0, deadzone = 0;
    private WebSocketClient mWebSocketClient;
    private String[] Split;
    private String ping ="Ping: 999", rCommand, rOldCommand;
    private String xCoor = "xCoor: ---", yCoor = "yCoor: ---", zCoor = "zCoor: ---";
    private String xAngle = "xAngle: ---", yAngle = "yAngle: ---", zAngle = "zAngle: ---";
    private WebView webView;

    public Activity_DrawJoystick() { // Needed for google signed release APK (an empty function is needed for the class)
        super(null);
    } // Needed for android compilers mind of ease


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
            if (BackSet == false) {
                initBack();
            }
            draw(canvas);
        }
    }

    private void initialize(){
        prepPaint();
        radius = 250;
        joyradius = 75;
        deadzone = 0.2 * radius;
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
                //Log.i("Websocket", "onMessage: " + s);
                final String message = s;
                switch (s){
                    case "Ready to operate":
                        RobotControl = true;
                        break;
                }
                        Split = message.split("::");
                        switch (Split[0]) {
                            case "Ping":
                                OutWorkspace = false;
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
                                        RobotControl = false;
                                        break;
                                    case "You were kicked from the server.":
                                        Kicked = true;
                                        break;
                                    case "Admin took control over the robot.":
                                        Kicked = true;
                                        break;
                                }
                                break;
                            case "INFO":
                                switch (Split[1]){
                                    case "Access to robot denied.":
                                        Kicked = true;
                                        break;
                                }
                                break;
                            case "Processing":
                                switch (Split[1]){
                                    case "Target out of workspace.":
                                        OutWorkspace = true;
                                        break;
                                }
                                //ERROR::Robotstation do not listen.
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
            joySpeed = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));
            /** right joystick */
            if ((Math.abs(x2) <= radius) && (Math.abs(y2) <= radius) && x[i] >= width / 2) { // Joystick circle
                if (Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2)) <= deadzone){ // Deadzone
                    canvas.drawCircle(roundX, midY, joyradius, restJoyPaint);
                    xV = 0;
                    yV = 0;
                }
                else if (Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2)) <= radius) {
                    canvas.drawCircle(x[i], y[i], joyradius, joyPaint);
                    angle = Math.atan2(y2, x2);
                    if (angle >= 0 && angle <= 90) {
                        if ( x[i] < roundX) { // SW
                            xV = Math.sin(angle) * joySpeed;
                            yV = Math.cos(angle) * joySpeed;
                        }
                        else { // SE
                            xV = Math.sin(angle) * joySpeed;
                            yV = Math.cos(angle) * joySpeed;
                        }
                    }
                    else if (angle <= -0.5 * Math.PI && angle >= -Math.PI) { //NE
                        angle = angle + Math.PI;
                        xV = Math.sin(angle) * -joySpeed;
                        yV = Math.cos(angle) * -joySpeed;
                    }
                    else if (angle <= 0 && angle >= -0.5 * Math.PI) { // NW
                        angle = angle + 0.5 * Math.PI;
                        xV = Math.cos(angle) * -joySpeed;
                        yV = Math.sin(angle) * joySpeed;
                    }
                }
            } else if (x[i] >= width / 2) { // Right part of screen
                angle = Math.atan2(y2, x2);
                if (angle >= 0 && angle <= 90) {
                    x3 = Math.cos(angle) * radius + roundX;
                    y3 = Math.sin(angle) * radius + midY;
                    if ( x[i] < roundX) { // SW

                        xV = Math.sin(angle) * radius;
                        yV = Math.cos(angle) * radius;
                    }
                    else { // SE
                        xV = Math.sin(angle) * radius;
                        yV = Math.cos(angle) * radius;
                    }
                } else if (angle <= -0.5 * Math.PI && angle >= -Math.PI) { //NE
                    angle = angle + Math.PI;
                    x3 = -Math.cos(angle) * radius + roundX;
                    y3 = -Math.sin(angle) * radius + midY;
                    xV = Math.sin(angle) * -radius;
                    yV = Math.cos(angle) * -radius;
                } else if (angle <= 0 && angle >= -0.5 * Math.PI) { // NW
                    angle = angle + 0.5 * Math.PI;
                    x3 = Math.sin(angle) * radius + roundX;
                    y3 = -Math.cos(angle) * radius + midY;
                    xV = Math.cos(angle) * -radius;
                    yV = Math.sin(angle) * radius;
                }
                canvas.drawCircle((int) x3, (int) y3, joyradius, joyPaint);
            }
            /**  // Left joystick */
            if (x[i] < width / 2 && y[i] <= midY + 0.15 * radius && y[i] >= midY - 0.5 * deadzone) { // Deadzone
                canvas.drawCircle(squareX, midY, joyradius, restJoyPaint);
                zV = 0;
            }
            else if (x[i] < width / 2 && y[i] <= midY && y[i] >= midY - radius) { // Inside joystick square
                canvas.drawCircle(squareX, y[i], joyradius, joyPaint);
                zV = -radius * (y[i] - midY) / 250;
            }
            else if (x[i] < width / 2 && y[i] <= midY + radius && y[i] >= midY) { // Inside joystick square
                canvas.drawCircle(squareX, y[i], joyradius, joyPaint);
                zV = -radius * (y[i] - midY) / 250;;
            }
            else if (x[i] < width / 2 && y[i] >= midY + radius){ // Outside joystick square
                canvas.drawCircle(squareX, midY + radius, joyradius, joyPaint);
                zV = -radius;
            }
            else if (x[i] < width / 2 && y[i] <= midY - radius) { // Outside joystick square
                canvas.drawCircle(squareX , midY - radius, joyradius, joyPaint);
                zV = radius;
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
        if (OutWorkspace)
        {
            canvas.drawCircle(roundX,midY,radius,errorPaint);
            canvas.drawRect(squareX - 10,midY + radius, squareX + 10, midY  - radius, errorPaint);
        }
        else {
            canvas.drawCircle(roundX, midY, radius, backgroundPaint);
            canvas.drawRect(squareX - 10, midY + radius, squareX + 10, midY - radius, backgroundPaint);
        }
        canvas.drawText(ping,textX,fontSize,textPaint);
        canvas.drawText(xCoor,textX,fontSize*2,textPaint);
        canvas.drawText(yCoor,textX,fontSize*3,textPaint);
        canvas.drawText(zCoor,textX,fontSize*4,textPaint);
        canvas.drawText(xAngle,textX,fontSize*5,textPaint);
        canvas.drawText(yAngle,textX,fontSize*6,textPaint);
        canvas.drawText(zAngle,textX,fontSize*7,textPaint);
        if (RobotControl == false) {
            canvas.drawText("No connection to the robot", textX - fontSize * 10, midY, errorPaint);
        }
        else if (Kicked) {  //Needs work
            canvas.drawText("You no longer have control (needs work!)", textX - fontSize * 10, midY, errorPaint);
        }
    }

    private void prepPaint() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLUE);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        joyPaint = new Paint();
        joyPaint.setColor(Color.RED);
        joyPaint.setStyle(Paint.Style.FILL);
        restJoyPaint = new Paint();
        restJoyPaint.setColor(Color.rgb(102,0,0));
        restJoyPaint.setStyle(Paint.Style.FILL);
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

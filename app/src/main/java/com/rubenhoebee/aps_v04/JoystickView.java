package com.rubenhoebee.aps_v04;
// zerokol joystickView library

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class JoystickView extends AppCompatActivity {

    private Bitmap image;
    private ImageView imageView;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick_view);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        image = BitmapFactory.decodeResource(getResources(),R.drawable.ball);
        imageView.setImageBitmap(image);
        imageView2.setImageBitmap(image);
        int w=getWindowManager().getDefaultDisplay().getWidth()-25;
        int h=getWindowManager().getDefaultDisplay().getHeight()-25;
        BallView ballView =new BallView(this,w,h);
        setContentView(ballView);
    }
}

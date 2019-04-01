package com.example.sarang.reddotbluedot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Code from this program has been used from Beginning Android Games
    //Review SurfaceView, Canvas, continue

    GameSurface gameSurface;
    MediaPlayer mushroom;
    MediaPlayer end;
    MediaPlayer background;
    MediaPlayer collect;
    MediaPlayer hit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);
        mushroom = MediaPlayer.create(this,R.raw.mushroom);
        end = MediaPlayer.create(this,R.raw.end);
        background = MediaPlayer.create(this,R.raw.background);
        collect = MediaPlayer.create(this,R.raw.collect);
        hit = MediaPlayer.create(this,R.raw.hit);

    }

    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
        if(mushroom.isPlaying())
            mushroom.pause();
        if(end.isPlaying())
            end.pause();
        if(background.isPlaying())
            background.pause();
        if(collect.isPlaying())
            collect.pause();
        if(hit.isPlaying())
            hit.pause();

    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
        if(!background.isPlaying()) {
            background.start();
            background.setLooping(true);
        }
    }



    //----------------------------GameSurface Below This Line--------------------------
    public class GameSurface extends SurfaceView implements Runnable,SensorEventListener{


        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Player player;
        List<RedDot> redDots = new ArrayList<>();
        List<BlueDot> blueDots = new ArrayList<>();
        List<GreenDot> greenDots = new ArrayList<>();
        int vx = 0;
        int vy = 0;
        String textTimer ="";
        String textScore ="0";
        Paint paintTimeRemaining;
        Paint paintTimer;
        Paint paintTimerEraser;
        Paint paintScore;
        int screenWidth;
        int screenHeight;
//        double[]gravity = new double[3];
//        double[]linear_acceleration = new double[3];
        double sweepAngle = -360;
        int score = 0;
        Paint paintEnd;

        public GameSurface(Context context) {
            super(context);
            holder=getHolder();


            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            screenHeight=sizeOfScreen.y;

            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,accelerometerSensor,sensorManager.SENSOR_DELAY_NORMAL);

            paintTimeRemaining= new Paint();
            paintTimeRemaining.setTextSize(42);
            paintTimeRemaining.setColor(Color.WHITE);
            paintTimer = new Paint();
            paintTimer.setAntiAlias(true);
            paintTimer.setStyle(Paint.Style.FILL);
            paintTimer.setColor(Color.WHITE);
            paintTimerEraser = new Paint();
            paintTimerEraser.setAntiAlias(true);
            paintTimerEraser.setColor(Color.TRANSPARENT);
            paintTimerEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            paintScore = new Paint();
            paintScore.setTextSize(72);
            paintScore.setColor(Color.WHITE);
            paintEnd = new Paint();
            paintEnd.setTextSize(100);
            paintEnd.setColor(Color.WHITE);


            player = new Player((screenWidth/2),(screenHeight/2));
            for(int i = 0; i < (int)Math.random()*5+3; i++)
                redDots.add(new RedDot((int)(Math.random()*screenWidth-RedDot.radius*2)+RedDot.radius*2,-100));
            for(int i = 0; i < (int)Math.random()*4+1; i++)
                blueDots.add(new BlueDot((int)(Math.random()*screenWidth-BlueDot.radius*2)+BlueDot.radius*2,-100));
            for(int i = 0; i < (int)Math.random()*2; i++)
                greenDots.add(new GreenDot((int)(Math.random()*screenWidth-BlueDot.radius*2)+BlueDot.radius*2,-100));

        }

        @Override
        public void run() {
            while (running){
                if (!holder.getSurface().isValid())
                    continue;
                Canvas canvas= holder.lockCanvas();

                canvas.drawRGB(0,0,0);



                player.checkBoundaries(vx,vy,screenWidth,screenHeight);

                canvas.drawCircle((float)player.getXPos(),(float)player.getYPos(),(float)player.getRadius(),player.getColor());
                if(player.shield)
                    canvas.drawCircle((float)player.getXPos(),(float)player.getYPos(),(float)player.getRadius()*2,player.getShieldColor());

                for(Iterator<RedDot> iterator = redDots.iterator(); iterator.hasNext(); ) {
                    RedDot redDot = iterator.next();

                    redDot.addYPos(redDot.getVy());

                    canvas.drawCircle((float) redDot.getXPos(), (float) redDot.getYPos(), (float) redDot.getRadius(), redDot.getColor());
                    if(player.onCollisionEvent(redDot)){
                        if(player.shield){
                            player.shield = false;
                        }
                        else if(!player.injured && !player.shield) {
                            addScore(-1);
                            if(!hit.isPlaying() && !collect.isPlaying() && !mushroom.isPlaying()) {
                                hit.start();
                                player.color.setAlpha(150);
                            }
                        }

                        iterator.remove();
                        continue;
                    }

                    if(redDot.checkBoundaries(screenHeight)){
                        iterator.remove();
                        continue;
                    }
                }

                for(Iterator<BlueDot> iterator = blueDots.iterator(); iterator.hasNext(); ) {
                    BlueDot blueDot = iterator.next();

                    blueDot.addYPos(blueDot.getVy());

                    canvas.drawCircle((float) blueDot.getXPos(), (float) blueDot.getYPos(), (float) blueDot.getRadius(), blueDot.getColor());
                    if(player.onCollisionEvent(blueDot)){
                        addScore(1);
                        if(player.injured) {
                            player.injured = false;
                        }
                        if(player.color.getAlpha() != 255)
                            player.color.setAlpha(255);
                        if(!hit.isPlaying() && !collect.isPlaying() && !mushroom.isPlaying())
                            collect.start();
                        iterator.remove();
                        continue;
                    }


                    if(blueDot.checkBoundaries(screenHeight)){
                        iterator.remove();
                        continue;
                    }
                }

                for(Iterator<GreenDot> iterator = greenDots.iterator(); iterator.hasNext(); ) {
                    GreenDot greenDot = iterator.next();

                    greenDot.addYPos(greenDot.getVy());

                    canvas.drawCircle((float) greenDot.getXPos(), (float) greenDot.getYPos(), (float) greenDot.getRadius(), greenDot.getColor());
                    if(player.onCollisionEvent(greenDot)){
                        addScore(10);
                        if(player.injured) {
                            player.injured = false;
                        }
                        if(player.color.getAlpha() != 255)
                            player.color.setAlpha(255);
                        double rand = Math.random();
                        if(rand > .5)
                            greenDot.repel(redDots,1000, .5);
                        else
                            greenDot.shield(player);
                        if(!hit.isPlaying() && !collect.isPlaying() && !mushroom.isPlaying())
                            mushroom.start();
                        iterator.remove();
                        continue;
                    }

                    if(greenDot.checkBoundaries(screenHeight)){
                        iterator.remove();
                        continue;
                    }
                }
                canvas.drawArc(25, 25,125,125,270,(float)sweepAngle,true,paintTimer);
                canvas.drawCircle(75,75,40,paintTimerEraser);
                canvas.drawText(textTimer,53,87,paintTimeRemaining);
                canvas.drawText(textScore,screenWidth-53*2,87,paintScore);

                holder.unlockCanvasAndPost(canvas);
            }
            if (!holder.getSurface().isValid())
                return;
            Canvas canvas= holder.lockCanvas();
            canvas.drawRGB(0,0,0);
            setTextSizeForWidth(paintEnd,screenWidth-20,"Click anywhere to play again",100);
            canvas.drawText("Score: "+ score,400,screenHeight/2-100,paintEnd);
            canvas.drawText("Click anywhere to play again",10,screenHeight/2+100,paintEnd);
            holder.unlockCanvasAndPost(canvas);
            if(!end.isPlaying())
                end.start();
        }

        public void resume(){
            running=true;
            gameThread=new Thread(this);
            gameThread.start();

            if(!background.isPlaying()){
                background.start();
                background.setLooping(true);
            }
            final int milliseconds = 30000;
            new CountDownTimer(milliseconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    textTimer = ""+millisUntilFinished / 1000;

                    if(millisUntilFinished/1000 <= 10){
                        if(millisUntilFinished/1000%2 == 0) {
                          paintTimer.setColor(Color.parseColor("#fff3cd"));
                        }
                        else {
                            paintTimer.setColor(Color.WHITE);
                        }

                    }
                    if(millisUntilFinished/1000 <= 5){
                        paintTimer.setColor(Color.parseColor("#f8d7da"));
                    }
                }

                public void onFinish() {
                    running = false;
                    if(background.isPlaying() || background.isLooping()){
                        background.stop();
                    }
                    if(collect.isPlaying())
                        collect.stop();
                    if(hit.isPlaying())
                        hit.stop();
                }
            }.start();

            new CountDownTimer(milliseconds, 10) {

                public void onTick(long millisUntilFinished) {
                    sweepAngle = -((double)millisUntilFinished/milliseconds*360);

                    if(redDots.size()+blueDots.size()+greenDots.size() < (int)Math.random()*3+5){
                        for(int i = 0; i < (int)Math.random()*5+3; i++)
                            redDots.add(new RedDot((int)(Math.random()*screenWidth-RedDot.radius*4)+RedDot.radius*2,-100));
                        for(int i = 0; i < (int)Math.random()*4+1; i++)
                            blueDots.add(new BlueDot((int)(Math.random()*screenWidth-BlueDot.radius*4)+BlueDot.radius*2,-100));
                        for(int i = 0; i < (int)Math.random()*2; i++)
                            greenDots.add(new GreenDot((int)(Math.random()*screenWidth-BlueDot.radius*4)+BlueDot.radius*2,-100));
                    }

                    if(Math.random()*10<.05 && redDots.size()+blueDots.size()+greenDots.size() < 25){
                        redDots.add(new RedDot((int)(Math.random()*screenWidth-RedDot.radius*4)+RedDot.radius*2,0));
                    }
                    if(Math.random()*20<.05 && redDots.size()+blueDots.size()+greenDots.size() < 25){
                        blueDots.add(new BlueDot((int)(Math.random()*screenWidth-BlueDot.radius*4)+BlueDot.radius*2,0));
                    }
                    if(Math.random()*40<.05 && redDots.size()+blueDots.size()+greenDots.size() < 25){
                        greenDots.add(new GreenDot((int)(Math.random()*screenWidth-GreenDot.radius*4)+GreenDot.radius*2,0));
                    }
                }

                public void onFinish() {
                }
            }.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //tilt phone and change position
            // alpha is calculated as t / (t + dT)
            // with t, the low-pass filter's time-constant
            // and dT, the event delivery rate

//            final float alpha = 0.8f;
//
//            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//            linear_acceleration[0] = event.values[0] - gravity[0];
//            linear_acceleration[1] = event.values[1] - gravity[1];
//            linear_acceleration[2] = event.values[2] - gravity[2];
//

            //x-rotation should start at -45

            vx = (int)event.values[0]*15;
            player.addXPos(vx);
//            vy = (int)event.values[1]/15;
//            player.addYPos(vy);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if(running) {
                    player.vx *= 2;
                    for (RedDot redDot : redDots)
                        redDot.vy *= 2;
                    for (BlueDot blueDot : blueDots)
                        blueDot.vy *= 2;
                    for (GreenDot greenDot : greenDots)
                        greenDot.vy *= 2;
                }
                else{
                    score = 0;
                    redDots.clear();
                    blueDots.clear();
                    textScore = "0";
                    textTimer = "";
                    vx = 0;
                    vy = 0;
                    sweepAngle = -360;
                    player = new Player((screenWidth/2),(screenHeight/2));
                    resume();

                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                player.vx /= 2;
                for(RedDot redDot: redDots)
                    redDot.vy /=2;
                for(BlueDot blueDot: blueDots)
                    blueDot.vy /=2;
            }
            return super.onTouchEvent(event);
        }

        public void addScore(int score){
            this.score += score;
            if(this.score<0)
                this.score = 0;
            textScore = this.score+"";
        }

        public void setTextSizeForWidth(Paint paint, float desiredWidth, String text, float testTextSize) {
            // Get the bounds of the text, using our testTextSize.
            paint.setTextSize(testTextSize);
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            // Calculate the desired size as a proportion of our testTextSize.
            float desiredTextSize = testTextSize * desiredWidth / bounds.width();

            // Set the paint for that size.
            paint.setTextSize(desiredTextSize);
        }
    }//GameSurface
}//Activity
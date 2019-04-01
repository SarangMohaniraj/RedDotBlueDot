package com.example.sarang.reddotbluedot;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class RedDot implements Dot {
    int startX;
    int startY;
    int xPos = 0;
    int yPos = 0;
    int vx = 20;
    int vy = (int)(Math.random()*5)+5;
    static int radius = 26;
    Paint color = new Paint();
    Point center;
    Boolean hit = false;

    public RedDot(){
        super();
    }

    public RedDot(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;

        color.setStyle(Paint.Style.FILL);
        color.setColor(Color.RED);
        color.setAntiAlias(true);
        center = new Point(startX+xPos,startY+yPos);

    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public void setStartX(int startX) {
        this.startX = startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public void setStartY(int startY) {
        this.startY = startY;
    }

    @Override
    public int getXPos() {
        return startX + xPos;
    }

    @Override
    public int addXPos(int vx) {
        this.xPos -= vx;
        center.set(startX+xPos,startY+yPos);
        return vx;
    }

    @Override
    public int getYPos() {
        return startY + yPos;
    }

    @Override
    public int addYPos(int vy) {
        this.yPos += vy;
        center.set(startX+xPos,startY+yPos);
        return vy;
    }

    @Override
    public int getVx() {
        return vx;
    }

    @Override
    public void setVx(int vx) {
        this.vx += vx;
    }

    @Override
    public int getVy() {
        return vy;
    }

    @Override
    public void setVy(int vy) {
        this.vy += vy;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public Point getCenter() {
        return center;
    }

    @Override
    public void setCenter(Point center) {
        this.center = center;
    }

    @Override
    public Paint getColor() {
        return color;
    }

    @Override
    public void setColor(Paint color) {
        this.color = color;
    }

    public boolean checkBoundaries(int screenHeight){
        if(yPos + radius > screenHeight){
            center.set(startX+xPos,startY+yPos);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCollisionEvent(Dot dot) {
        if(!hit && (int)Math.hypot(this.center.x-dot.getCenter().x,this.center.y-dot.getCenter().y) <= this.radius + dot.getRadius()) {
            hit = true;
            return true;
        }
        return false;
    }
}

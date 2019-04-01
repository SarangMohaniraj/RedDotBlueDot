package com.example.sarang.reddotbluedot;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public interface Dot {

    public int getStartX();

    public void setStartX(int startX);

    public int getStartY();
    public void setStartY(int startY);

    public int getXPos();

    public int addXPos(int vx);

    public int getYPos();

    public int addYPos(int vy);

    public int getVx();

    public void setVx(int vx);

    public int getVy();

    public void setVy(int vy);

    public int getRadius();

    public void setRadius(int radius);

    public Point getCenter();

    public void setCenter(Point center);

    public Paint getColor();

    public void setColor(Paint color);

    public boolean onCollisionEvent(Dot dot);
}

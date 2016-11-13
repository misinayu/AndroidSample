package com.example.masanori_acer.avoidobstacle;

/**
 * Created by MASANORI on 2016/11/13.
 */

public class Obstacle extends GameObject {
    private int speed;

    public Obstacle(int left, int top, int width, int height, int speed) {
        super(left, top, width, height);
        setSpeed(speed);
    }

    public void move() {
        super.move(0, speed);
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }
}

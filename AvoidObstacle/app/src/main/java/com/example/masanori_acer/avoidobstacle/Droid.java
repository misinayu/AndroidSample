package com.example.masanori_acer.avoidobstacle;

/**
 * Created by MASANORI on 2016/11/13.
 */

public class Droid extends GameObject {
    private static final int SAFE_AREA = 30;

    public Droid(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    @Override
    public void move(int left, int top) {
        super.move(left / 2, -(top / 2));
    }

    public boolean collisionCheck(Obstacle obstacle){
        if ((this.getLeft() + SAFE_AREA < obstacle.getRight()) &&
                (this.getTop() + SAFE_AREA < obstacle.getBottom()) &&
                (this.getRight() - SAFE_AREA > obstacle.getLeft()) &&
                (this.getBottom() - SAFE_AREA > obstacle.getTop())){
            return true;
        }
        return false;
    }
}

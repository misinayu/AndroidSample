package com.example.masanori_acer.avoidobstacle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by MASANORI on 2016/11/13.
 */

public class AvoidObstacleView extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {
//    private static final int GOAL_HEIGHT = 150; // xhdpiの機種
//    private static final int START_HEIGHT = 150; // xhdpiの機種
//    private static final int GOAL_HEIGHT = 200; // xxhdpiの機種
//    private static final int START_HEIGHT = 200; // xxhdpiの機種
    private static final int GOAL_HEIGHT = 300; // xxxhdpiの機種
    private static final int START_HEIGHT = 300; // xxxhdpiの機種
    private static final int JUMP_HEIGHT = START_HEIGHT - 30;

    private static final int OUT_WIDTH = 50;
    private static final int DROID_POS = OUT_WIDTH + 50;

    private int mWidth;
    private int mHeight;

    private boolean mIsGoal = false;
    private boolean mIsGone = false;

    private boolean mIsAttached;
    private Thread mThread;

    private SurfaceHolder mHolder;
    private Canvas mCanvas = null;
    private Paint mPaint = null;
    private Path mGoalZone;
    private Path mStartZone;
    private Path mOutZoneL;
    private Path mOutZoneR;
    private Region mRegionGoalZone;
    private Region mRegionStartZone;
    private Region mRegionOutZoneL;
    private Region mRegionOutZoneR;

    private Region mRegionWholeScreen;

    private long startTime;
    private long endTime;

    private Bitmap mBitmapDroid; // ドロイド君用のビットマップ
    private Droid mDroid; // ドロイド君クラス

    private Bitmap mBitmapObstacle; // 障害物用のビットマップ
    private Obstacle mObstacle; // 障害物クラス

    private List<Obstacle> mObstacleList = new ArrayList<Obstacle>(20); // 障害物のリスト

    private Random mRand; // 乱数

    public AvoidObstacleView(Context context) {
        super(context);
        // SurfaceHolderの取得
        mHolder = getHolder();
        // SurfaceViewイベントの通知先の指定（自分のクラス）
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);

        mWidth = getWidth();
        mHeight = getHeight();

        Resources rsc = getResources();
        // ランチャーのドロイド君の画像Bitmapを生成
        mBitmapDroid = BitmapFactory.decodeResource(rsc, R.mipmap.ic_launcher);
        // 障害物の画像Bitmapを生成
        mBitmapObstacle = BitmapFactory.decodeResource(rsc, R.mipmap.rock);

        // 盤面のゾーン決め
        zoneDecide();

        // 乱数準備
        mRand = new Random();

        newDroid();
        newObstacle();

        mIsAttached = true;
        mThread = new Thread(this);
        mThread.start();
    }

    private void newObstacle() {
        Obstacle obstacle;

        mObstacleList.clear();

        for (int i = 0; i < 20; i++){
            // left座標を乱数で求める
            int left = mRand.nextInt(mWidth - (OUT_WIDTH * 2 + mBitmapObstacle.getWidth())) + OUT_WIDTH;
            // top座標を乱数で求める
            int top = mRand.nextInt(mHeight - mBitmapObstacle.getHeight() * 2);
            // 1から3の乱数生成
            int speed = mRand.nextInt(3) + 1;
            obstacle = new Obstacle(left, top, mBitmapObstacle.getWidth(), mBitmapObstacle.getHeight(), speed);
            mObstacleList.add(obstacle);
        }
    }

    private void newDroid() {
        mDroid = new Droid(DROID_POS, mHeight - JUMP_HEIGHT, mBitmapDroid.getWidth(), mBitmapDroid.getHeight());
        mIsGoal = false;
        mIsGone = false;
        startTime = System.currentTimeMillis();
    }

    private void zoneDecide() {
        mRegionWholeScreen = new Region(0, 0, mWidth, mHeight);
        mGoalZone = new Path();
        mGoalZone.addRect(OUT_WIDTH, 0, mWidth - OUT_WIDTH, GOAL_HEIGHT, Path.Direction.CW);
        mRegionGoalZone = new Region();
        mRegionGoalZone.setPath(mGoalZone, mRegionWholeScreen);

        mStartZone = new Path();
        mStartZone.addRect(OUT_WIDTH, mHeight - START_HEIGHT, mWidth - OUT_WIDTH, mHeight, Path.Direction.CW);
        mRegionStartZone = new Region();
        mRegionStartZone.setPath(mStartZone, mRegionWholeScreen);

        mOutZoneL = new Path();
        mOutZoneL.addRect(0, 0, OUT_WIDTH, mHeight, Path.Direction.CW);
        mRegionOutZoneL = new Region();
        mRegionOutZoneL.setPath(mOutZoneL, mRegionWholeScreen);

        mOutZoneR = new Path();
        mOutZoneR.addRect(mWidth - OUT_WIDTH, 0, mWidth, mHeight, Path.Direction.CW);
        mRegionOutZoneR = new Region();
        mRegionOutZoneR.setPath(mOutZoneR, mRegionWholeScreen);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Bitmapリソースをメモリから解放する
        if (mBitmapDroid != null){
            mBitmapDroid.recycle();
            mBitmapDroid = null;
        }
        if (mBitmapObstacle != null){
            mBitmapObstacle.recycle();
            mBitmapObstacle = null;
        }
        mIsAttached = false;
        while (mThread.isAlive());
    }

    @Override
    public void run() {
        while (mIsAttached){
            drawGameBoard();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mRegionStartZone.contains((int)event.getX(), (int)event.getY())){
                    // ドロイド君と障害物を出す
                    newDroid();
                    newObstacle();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void drawGameBoard() {
        // ボールを落としたか、障害物に衝突したか、あるいはボールしたとき
        if ((mIsGone) || (mIsGoal)){
            return;
        }

        mDroid.move(MainActivity.role, MainActivity.pitch);
        if (mDroid.getBottom() > mHeight){
            mDroid.setLocate(mDroid.getLeft(), (int)(mHeight - JUMP_HEIGHT));
        }

        try {
            for (Obstacle obstacle : mObstacleList){
                if (obstacle != null){
                    obstacle.move();
                }
            }
            mCanvas = getHolder().lockCanvas();
            mCanvas.drawColor(Color.LTGRAY);

            mPaint.setColor(Color.MAGENTA);
            mCanvas.drawPath(mGoalZone, mPaint);
            mPaint.setColor(Color.GRAY);
            mCanvas.drawPath(mStartZone, mPaint);
            mPaint.setColor(Color.BLACK);
            mCanvas.drawPath(mOutZoneL, mPaint);
            mCanvas.drawPath(mOutZoneR, mPaint);

            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(50);

            // Goal文字列
            mCanvas.drawText(getResources().getString(R.string.goal), (int)mWidth / 2 - 50, 100, mPaint);
            // Start文字列
            mCanvas.drawText(getResources().getString(R.string.start), (int)mWidth / 2 - 50, mHeight - 50, mPaint);

            if (mRegionOutZoneL.contains(mDroid.getCenterX(), mDroid.getCenterY())){
                mIsGone = true;
            }
            if (mRegionOutZoneR.contains(mDroid.getCenterX(), mDroid.getCenterY())){
                mIsGone = true;
            }
            if (mRegionGoalZone.contains(mDroid.getCenterX(), mDroid.getCenterY())){
                mIsGoal = true;
                // ゴールした
                String msg = goaled();
                mPaint.setColor(Color.WHITE);
                mCanvas.drawText(msg, OUT_WIDTH + 10, GOAL_HEIGHT - 100, mPaint);
            }
            // 隕石はスタートゾーンにかかると消える
            for (Obstacle obstacle : mObstacleList){
                if (mRegionStartZone.contains(obstacle.getLeft(), obstacle.getBottom())){
                    obstacle.setLocate(obstacle.getLeft(), 0);
                }
            }
            if (!mIsGoal){
                for (Obstacle obstacle : mObstacleList){
                    if (mDroid.collisionCheck(obstacle)){
                        String msg = getResources().getString(R.string.collision);
                        mPaint.setColor(Color.WHITE);
                        mCanvas.drawText(msg, OUT_WIDTH + 10, GOAL_HEIGHT - 100, mPaint);
                        mIsGoal = true;
                    }
                }
            }
            if (!((mIsGone) || (mIsGoal))){
                mPaint.setColor(Color.DKGRAY);
                for (Obstacle obstacle : mObstacleList){
                    mCanvas.drawBitmap(mBitmapObstacle, obstacle.getLeft(), obstacle.getTop(), null);
                }

                mCanvas.drawBitmap(mBitmapDroid, mDroid.getLeft(), mDroid.getTop(), null);
            }
            getHolder().unlockCanvasAndPost(mCanvas);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String goaled() {
        endTime = System.currentTimeMillis();
        // 経過時間
        long erapsedTime = endTime - startTime;
        int secTime = (int)(erapsedTime / 1000);
        return ("Goal! " + secTime + "秒");
    }
}

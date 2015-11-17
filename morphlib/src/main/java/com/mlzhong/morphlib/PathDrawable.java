package com.mlzhong.morphlib;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.List;

/**
 * Created by mlzhong on 2015/11/16.
 */
public class PathDrawable extends Drawable implements Animatable, Runnable {

    private long mStartTicks = 0;
    private boolean mIsRunning = false;
    private Rect mDrawBound = new Rect();

    List<List<PointFs>> mSrc;
    List<List<PointFs>> mDst;

    private Interpolator mInterpolator;
    private float mAnimProgress = 0;
    private int mAnimDuration = 500;

    public void setAnimDuration(int i){
        mAnimDuration = i;
    }


    Path mPath;
    Paint mPaint;

    //public float getAnimProgress(){
    //    return mAnimProgress;
    //}

    private void resetAnimation(){
        mStartTicks = AnimationUtils.currentAnimationTimeMillis();
        mAnimProgress = 0f;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mDrawBound = super.getBounds();
    }


    public PathDrawable(List<List<PointFs>> src,List<List<PointFs>> dst,Paint paint){
        mSrc = src;
        mDst = dst;
        if(mInterpolator == null)
            mInterpolator = new LinearInterpolator();
                    //AccelerateInterpolator();

        mPaint = paint;

    }


    public void setPoints(List<List<PointFs>> src,List<List<PointFs>> dst){
        mSrc = src;
        mDst = dst;
        mPath = null;
        scheduleSelf(this, AnimationUtils.currentAnimationTimeMillis() + (1000 / 60));
    }



    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        if(mPath == null) {
            makeSrcPath();
        }
        Rect padding = new Rect();
        super.getPadding(padding);
        canvas.translate(padding.left,
                padding.top);
        canvas.translate(0,
                (mDrawBound.height()-padding.top - padding.bottom) / 2);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }


    void updatePath(){
        if(mIsRunning) {

            long curTime = SystemClock.uptimeMillis();
            float value = Math.min(1f, (float) (curTime - mStartTicks) / mAnimDuration);

            mAnimProgress = mInterpolator.getInterpolation(value);
            //src-->dst
            makeMorphingPath(mAnimProgress);

        }else{
            makeSrcPath();
        }

    }

    void  makeMorphingPath(float progress){
        Path p = new Path();

        if(mSrc.size() >= mDst.size()){
            int maxx = mDst.size();
            for (int j = 0; j < mSrc.size(); j++) {
                List<PointFs> oneSrc = mSrc.get(j);
                List<PointFs> oneDst = null;
                if(j <  maxx){
                    oneDst = mDst.get(j);
                }
                Path pp = PathUtils.mergePath(oneSrc, oneDst, progress);
                if(pp != null) p.addPath(pp);
            }
        }else{
            int maxx = mSrc.size();
            for (int j = 0; j < mDst.size(); j++) {
                List<PointFs> oneSrc = null;
                List<PointFs> oneDst = mDst.get(j);
                if(j <  maxx){
                    oneSrc = mSrc.get(j);
                }
                Path pp = PathUtils.mergePath(oneSrc, oneDst, progress);
                if(pp != null) p.addPath(pp);
            }
        }

        mPath = p;
    }

    void makeSrcPath(){
        //src -->dst
        Path p = new Path();

        for (int j = 0; j < mSrc.size(); j++) {
            List<PointFs> onePath = mSrc.get(j);

            p.moveTo(onePath.get(0).pos.x, onePath.get(0).pos.y);
            for (int i = 1; i < onePath.size(); i++) {
                p.lineTo(onePath.get(i).pos.x, onePath.get(i).pos.y);
            }
        }
        p.close();
        mPath = p;
    }

    void makeDstPath(){
        //src -->dst
        Path p = new Path();

        for (int j = 0; j < mDst.size(); j++) {
            List<PointFs> onePath = mDst.get(j);

            p.moveTo(onePath.get(0).pos.x, onePath.get(0).pos.y);
            for (int i = 1; i < onePath.size(); i++) {
                p.lineTo(onePath.get(i).pos.x, onePath.get(i).pos.y);
            }
        }
        p.close();
        mPath = p;
    }

    @Override
    public void scheduleSelf(Runnable what, long when) {
        if(mIsRunning) {
            updatePath();
        }
        super.scheduleSelf(what, when);
    }

    @Override
    public void run() {
        invalidateSelf();
        scheduleSelf(this, AnimationUtils.currentAnimationTimeMillis() + (1000/60));
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            mIsRunning = true;
            resetAnimation();

            run();
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            unscheduleSelf(this);
            mIsRunning = false;
            mStartTicks = 0;
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int arg0) {
        //throw new UnsupportedOperationException();
    }

    @Override
    public void setColorFilter(ColorFilter arg0) {
        //throw new UnsupportedOperationException();
    }

}

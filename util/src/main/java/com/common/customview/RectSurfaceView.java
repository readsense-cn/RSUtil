package com.common.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author liyaotang
 * @date 2018/8/30
 *
 * 根据触点画矩形，可以通过
 *
 */
public class RectSurfaceView extends SurfaceView {

    private final String TAG = getClass().getSimpleName();

    /** 触点按下的坐标 */
    float downX = -1, downY = -1;
    /** 触点正在移动的坐标 */
    float moveX = -1, moveY = -1;
    /** 触点弹起的坐标 */
    float upX = -1, upY = -1;

    /** 最后截取的矩形的坐标 */
    float rectLeft = -1f, rectRight = -1f, rectTop = -1f, rectBottom = -1f;
    /** 最后截取的矩形的长宽 */
    float rectWidth = 0, rectHeight = 0;

    /** 是否打开画矩形 */
    boolean enableDrawRect = false;

    private SurfaceHolder holder;
    /** 通用Paint */
    private Paint mPaint;

    public RectSurfaceView(Context context) {
        super(context);
        init();
    }

    public RectSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** 初始化 */
    private void init() {
        holder = getHolder();

        mPaint = new Paint();
        // 设置无锯齿
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setAlpha(0xff);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //L.d(TAG, "onTouchEvent()");
        if (!enableDrawRect) return false;

        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = x;
                moveY = y;

                calcSidePosition();
                drawRect();
                break;
            case MotionEvent.ACTION_UP:
                upX = moveX = x;
                upY = moveY = y;

                rectWidth = Math.abs(downX - upX);
                rectHeight = Math.abs(downY - upY);

                calcSidePosition();
                drawRect();
                break;
        }
        return true;
    }

    /** 画矩形 */
    private void drawRect() {
        // 锁定整个View
        Canvas canvas = holder.lockCanvas();

        if (canvas != null) {
            // 清空界面
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            // 画矩形
            canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, mPaint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 画一个矩阵
     * @param rect 矩阵四条边的坐标
     * @param isClear 是否在画矩阵前，清空画布
     * @return
     */
    public boolean drawRect(Rect rect, boolean isClear) {
//        L.d(TAG, "left" + rect.left + " right" + rect.right + " top" + rect.top + " bottom" + rect.bottom);
        int width = getWidth();
        int height = getHeight();

        if (rect.left < 0 || rect.right < 0 || rect.top < 0 || rect.bottom < 0) {
            Log.e(TAG, "drawRect: wrong size, < 0");
            return false;
        }
        if (rect.left > width || rect.right > width || rect.top >height || rect.bottom > height) {
            Log.e(TAG, "drawRect: wrong size, > width or > height");
            return false;
        }

        rectLeft = rect.left;
        rectRight = rect.right;
        rectTop = rect.top;
        rectBottom = rect.bottom;

        rectWidth = Math.abs(rectLeft - rectRight);
        rectHeight = Math.abs(rectTop - rectBottom);

        // 锁定整个View，如果不设置区域，连续画多个框的时候，容易有一些框被刷掉，还不清楚原因
        Canvas canvas = holder.lockCanvas(rect);
        if (canvas != null) {
            // 清空界面
            if (isClear)
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            // 画矩形
            canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, mPaint);
            holder.unlockCanvasAndPost(canvas);
            return true;
        }

        return false;
    }

    /**
     * 画一个矩阵
     * @param rect 矩阵四条边的坐标
     * @param isClear 是否在画矩阵前，清空画布
     * @return
     */
    public boolean drawRect(Rect rect, boolean isClear, boolean isFill) {
        Paint paint = new Paint();
        paint = new Paint();
        // 设置无锯齿
        paint.setAntiAlias(true);
        paint.setColor(mPaint.getColor());
        if (isFill)
            paint.setStyle(Paint.Style.FILL);
        else
            paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setAlpha(0xff);

        Log.d(TAG, "left" + rect.left + " right" + rect.right + " top" + rect.top + " bottom" + rect.bottom);
        int width = getWidth();
        int height = getHeight();

        if (rect.left < 0 || rect.right < 0 || rect.top < 0 || rect.bottom < 0) {
            Log.e(TAG, "drawRect: wrong size, < 0");
            return false;
        }
        if (rect.left > width || rect.right > width || rect.top >height || rect.bottom > height) {
            Log.e(TAG, "drawRect: wrong size, > width or > height");
            return false;
        }

        rectLeft = rect.left;
        rectRight = rect.right;
        rectTop = rect.top;
        rectBottom = rect.bottom;

        rectWidth = Math.abs(rectLeft - rectRight);
        rectHeight = Math.abs(rectTop - rectBottom);

        // 锁定整个View
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            // 清空界面
            if (isClear)
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            // 画矩形
            canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint);
            holder.unlockCanvasAndPost(canvas);
            return true;
        }

        return false;
    }

    /** 清空界面 */
    public void clear() {
        // 锁定整个View
        Canvas canvas = holder.lockCanvas();

        if (canvas != null) {
            // 清空界面
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /** 计算矩形前后左右的坐标 */
    private void calcSidePosition() {
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        if (downX > moveX) {
            rectLeft = moveX;
            rectRight = downX;
        } else {
            rectLeft = downX;
            rectRight = moveX;
        }
        // 边缘限制
        if (rectLeft < 0) rectLeft = 0;
        if (rectLeft > viewWidth) rectLeft = viewWidth;
        if (rectRight < 0) rectRight = 0;
        if (rectRight > viewWidth) rectRight = viewWidth;


        if (downY > moveY) {
            rectTop = moveY;
            rectBottom = downY;
        } else {
            rectTop = downY;
            rectBottom = moveY;
        }
        // 边缘限制
        if (rectTop < 0) rectTop = 0;
        if (rectTop > viewHeight) rectTop = viewHeight;
        if (rectBottom < 0) rectBottom = 0;
        if (rectBottom > viewHeight) rectBottom = viewHeight;

    }

    /** 使能截取矩形 */
    public void enableDrawRect() {
        enableDrawRect = true;
        setClickable(true);
    }

    /** 使不能截取矩形 */
    public void disableDrawRect() {
        enableDrawRect = false;
        setClickable(false);
        clear();
    }

    /** 获取最后截取矩形的四边坐标，float[4], rectLeft, rectTop, rectRight, rectBottom */
    public float[] getLastPosition() {
        float[] rect = {rectLeft, rectTop, rectRight, rectBottom};
        return rect;
    }

    /** 获取最后截取矩形 */
    public Rect getLastRect() {
        return new Rect((int)rectLeft, (int)rectTop, (int)rectRight, (int)rectBottom);
    }

    /** 按 width/height 的比例缩放，最后截取矩形 */
    public Rect getLastRectOnRatio(int width, int height) {
        if (width <= 0 || height <= 0)
            return null;
        if (rectLeft < 0 || rectTop < 0 || rectRight < 0 || rectBottom < 0 )
            return null;


        int viewWidth = getWidth();
        int viewHeight = getHeight();
        if (viewWidth <= 0 || viewHeight <= 0)
            return null;

        int left = (int)rectLeft;
        int top = (int)rectTop;
        int right = (int)rectRight;
        int bottom = (int)rectBottom;

        left = left * width / viewWidth;
        top = top * height / viewHeight;
        right = right * width / viewWidth;
        bottom = bottom * height / viewHeight;

        Log.d(TAG, "width: " + (right - left) + "  height: " + (bottom - top));

        return new Rect(left, top, right, bottom);
    }

    /** 设置边框颜色，默认是 {@link Color#YELLOW} */
    public void setBorderColor(int color) {
        mPaint.setColor(color);
    }

}

package com.lvzhongyi.arclib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by lvzhongyi on 2016/11/17.
 */

public class BallView extends View {

    private Paint paint;

    private Bitmap progressRes;//圆球的资源图片

    private Scroller scroller;
    private OnSmoothScrollListener listener;

    public BallView(Context context) {
        this(context, null);
    }

    public BallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        paint = new Paint();
    }

    protected void setProgressRes(Bitmap bitmap) {
        progressRes = bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    public void setListener(OnSmoothScrollListener listener) {
        this.listener = listener;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            if (listener != null) {
                listener.onSmoothScroll(scroller.getCurrX());
                postInvalidate();
            }
        }
    }

    /**
     * 平滑滑动
     *
     * @param start    起始值
     * @param distance 滑动距离
     */
    public void smoothScrollLevel(int start, int distance) {
        scroller.startScroll(start, 0, distance, 0, 200);
        Log.v("距离:", start + "+++" + distance + "--");
        postInvalidate();
    }

    public interface OnSmoothScrollListener {
        void onSmoothScroll(int currentX);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(progressRes, 0, 0, paint);
    }
}

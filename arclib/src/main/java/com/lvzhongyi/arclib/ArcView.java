package com.lvzhongyi.arclib;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by lvzhongyi on 2016/11/17.
 */

public class ArcView extends View {
    private int color;//当前使用颜色
    private boolean needShadow;//需要阴影

    private Paint paint;
    private Path path;
    private PointF pStart; // 起始点
    private PointF pCtrl; // 控制点
    private PointF pEnd; // 终止点

    private int padding;

    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        path = new Path();
        pStart = new PointF();
        pCtrl = new PointF();
        pEnd = new PointF();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);//默认占最大

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            int width = right;
            int height = bottom;
            pStart.set(padding, height - padding);
            //横着的三角形和竖着的三角形是相似三角形
            //先求出竖着的三角形的比例关系
            float jd = (height - padding * 2) / (float) ((height - padding * 2) + (width - padding * 2));
            //再反求出上面坐标离中线的距离
            int marginR = (int) (jd * (height / 2 - padding));
            pCtrl.set(width / 2 - marginR, padding);
            pEnd.set(width - padding, padding);

            Log.v("坐标点:", "起始点x:" + pStart.x + " y:" + pStart.y + " 控制点x:" + pCtrl.x + " y:" + pCtrl.y + " 结束点x:" + pEnd.x + " y:" + pEnd.y);

        }
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setShadow(boolean shadow) {
        this.needShadow = shadow;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画2阶贝塞尔曲线
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        if (needShadow) {
            BlurMaskFilter maskFilter = new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID);
            paint.setMaskFilter(maskFilter);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        } else {
            setLayerType(LAYER_TYPE_NONE, null);
        }
        path.moveTo(pStart.x, pStart.y);
        path.quadTo(pCtrl.x, pCtrl.y, pEnd.x, pEnd.y);
        canvas.drawPath(path, paint);
    }
}

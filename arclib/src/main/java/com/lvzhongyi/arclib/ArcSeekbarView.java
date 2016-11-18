package com.lvzhongyi.arclib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.math.BigDecimal;

/**
 * Created by lvzhongyi on 2016/11/17.
 */

public class ArcSeekbarView extends FrameLayout implements BallView.OnSmoothScrollListener {
    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

    private BallView ballView;
    private ArcView arcView;
    private Bitmap srcNormalRes;//圆球的资源图片
    private Bitmap srcPressedRes;
    private int w;//圆球的直径
    private int r;//圆球半径

    private PointF pStart; // 起始点
    private PointF pCtrl; // 控制点
    private PointF pEnd; // 终止点
    private PointF circleCenter; // 球的坐标


    private int marginLeft;
    private int marginTop;

    private boolean touching = false;//手指状态
    private float currentX = -1f; // 当前x坐标，用于控制圆球位置
    private int widthSize;
    private int heightSize;

    private float level = 6f; // 设置档次
    private int currentLevel = 0; // 当前档次

    private int colorNormal = 0;//常规颜色
    private int colorPressed = 0;//按下颜色

    private volatile long timeTag;
    private OnProgressChangedListener listener;

    public ArcSeekbarView(Context context) {
        super(context);
        throw new RuntimeException("no background resources");
    }

    public ArcSeekbarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcSeekbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int res1 = attrs.getAttributeResourceValue(NAMESPACE, "srcNormal", 0);
        int res2 = attrs.getAttributeResourceValue(NAMESPACE, "srcPressed", 0);
        srcNormalRes = BitmapFactory.decodeResource(context.getResources(), res1);
        srcPressedRes = BitmapFactory.decodeResource(context.getResources(), res2);
        if (colorNormal == 0) {
            int colorNormal_ = attrs.getAttributeResourceValue(NAMESPACE, "colorNormal", 0);
            if (colorNormal_ != 0) {
                colorNormal = context.getResources().getColor(colorNormal_);
            }
        }
        if (colorPressed == 0) {
            int colorPressed_ = attrs.getAttributeResourceValue(NAMESPACE, "colorPressed", 0);
            if (colorPressed_ != 0) {
                colorPressed = context.getResources().getColor(colorPressed_);
            }
        }
        level = attrs.getAttributeIntValue(NAMESPACE, "level", 6);
        pStart = new PointF();
        pCtrl = new PointF();
        pEnd = new PointF();
        circleCenter = new PointF();

        w = srcNormalRes.getWidth();
        r = w / 2;
    }

    public void setListener(OnProgressChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);
        arcView = (ArcView) getChildAt(0);
        ballView = (BallView) getChildAt(1);
        ballView.setProgressRes(srcNormalRes);
        ballView.setListener(this);
        arcView.setColor(colorNormal);
        arcView.setPadding(w / 2);
        measureChild(arcView, widthSize, heightSize);
        measureChild(ballView, w, w);
//        Log.v("宽高", "width:" + widthSize + " height:" + heightSize);
        pStart.set(w, heightSize - w);
        //横着的三角形和竖着的三角形是相似三角形
        //先求出竖着的三角形的比例关系
        float jd = (heightSize - w * 2) / (float) ((heightSize - w * 2) + (widthSize - w * 2));
        //再反求出上面坐标离中线的距离
        int marginR = (int) (jd * (heightSize / 2 - w));
        pCtrl.set(widthSize / 2 - marginR, w);
        pEnd.set(widthSize - w, w);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            Log.v("parent", left + "-" + top + "-" + right + "-" + bottom + " r:" + r);
            marginLeft = left;
            marginTop = top;
            arcView.layout(0, 0, right-left, bottom-top);
            moveBall(r);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touching = true;
                float downX = event.getX();
                float downY = event.getY();
                return (Math.abs(downX - circleCenter.x) < r && Math.abs(downY - circleCenter.y) < r);
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                float moveX = event.getX();
//                Log.v("触摸点", " X:" + moveX + " Y:" + moveY);
                currentX = moveX; // 通过x坐标改变圆球的位置
                moveBall(currentX);
                break;
            case MotionEvent.ACTION_UP:
                touching = false;
                // 当手指移出或者离开View时，圆球平滑滑到最近的档次
                currentLevel = getLevel(currentX);
                Log.v("算出的档次", "" + currentLevel);
                ballView.smoothScrollLevel((int) currentX,
                        (int) ((widthSize - w) / (level - 1) * currentLevel - (currentX - r)));
                currentX += (int) ((widthSize - w) / (level - 1) * currentLevel - (currentX - r));
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void onOrOff(boolean b) {
        if (b) {
            ballView.setProgressRes(srcPressedRes);
            arcView.setColor(colorPressed);
            arcView.setShadow(true);
        } else {
            ballView.setProgressRes(srcNormalRes);
            arcView.setColor(colorNormal);
            arcView.setShadow(false);

        }
        arcView.postInvalidate();
        ballView.postInvalidate();
    }

    /**
     * 计算档次
     *
     * @param x 横坐标
     * @return 档次
     */
    private int getLevel(float x) {
        float ratio = ((currentX - r) / (widthSize - w)) * (level - 1);
//        JLog.v("x" + x + "  right" + right_ + "  left" + left_ + "  ratio" + ratio + " width" + getWidth());
        // 计算距离哪个档次最近
        int result = new BigDecimal(ratio).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        if (result < 0) {
            result = 0;
        } else if (result > (level - 1)) {
            result = (int) (level - 1);
        }
        Log.v("档次计算", "x:" + x + "属于" + result + "档次");
        return result;
    }

    /**
     * 改变球的位置
     *
     * @param currentX 横坐标
     */
    private void moveBall(float currentX) {
        float t = ((currentX - r) / (widthSize - w));
        if (currentX > widthSize - r) t = 1;
        if (currentX < r) t = 0.0f;
        float p0 = pStart.x - r;
        float pc = pCtrl.x - r;
        float p1 = pEnd.x + r;
        float x = (1 - t) * (1 - t) * p0 + 2 * t * (1 - t) * pc + t * t * p1;
        p0 = pStart.y + r;
        pc = pCtrl.y - r;
        p1 = pEnd.y - r;
        float y = (1 - t) * (1 - t) * p0 + 2 * t * (1 - t) * pc + t * t * p1;
        circleCenter.set(x, y);
//        Log.v("hhhhh", "改变球的位置 currentX" + currentX + " x" + x + " y" + y + " t" + t + " r" + r);
        ballView.layout(
                (int) (circleCenter.x - r),
                (int) (circleCenter.y - r),
                (int) (circleCenter.x + r),
                (int) (circleCenter.y + r)
        );
    }

    @Override
    public void onSmoothScroll(int currentX) {
        moveBall(currentX);
        if (System.currentTimeMillis() - timeTag > 500) {
            if (listener != null) {
                if (touching) {
                    listener.onProgressChanging(currentLevel);
                } else {
                    listener.onProgressChanging(currentLevel);
                    listener.onProgressChanged(currentLevel);
                }
            }
        }
        timeTag = System.currentTimeMillis();
    }

    public void subCurrentLevel() {
        if (currentLevel > 0) {
            --currentLevel;
            ballView.smoothScrollLevel((int) currentX,
                    (int) ((widthSize - w) / (level - 1) * currentLevel - (currentX - r)));
            currentX += (int) ((widthSize - w) / (level - 1) * currentLevel - (currentX - r));
        }
    }

    public void addCurrentLevel() {
        if ((float) currentLevel < this.level - 1.0F) {
            ++currentLevel;
            ballView.smoothScrollLevel((int) currentX,
                    (int) ((widthSize - w) / (level - 1) * currentLevel - (currentX - r)));
            currentX += (int) ((widthSize - w) / (level - 1) * currentLevel - (currentX - r));
        }
    }

    /**
     * 滑动接口
     */
    public interface OnProgressChangedListener {
        void onProgressChanging(int level);

        void onProgressChanged(int level);
    }

    /**
     * 得到当前的档次
     *
     * @return
     */
    public int getCurrentLevel() {
        return currentLevel;
    }
}

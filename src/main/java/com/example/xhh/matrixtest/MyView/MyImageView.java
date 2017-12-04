package com.example.xhh.matrixtest.MyView;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by 栾桂明 on 2017/12/1.
 * 实现图片点击放大的自定义view
 * ScaleGestureDetector :检测手势变化的接口 在手势变化的时候会调用内部的方法
 * OnGlobalLayoutListener：检测视图树加载状态的接口
 * OnTouchListener：图片的touch的监听
 */

public class MyImageView extends android.support.v7.widget.AppCompatImageView implements
        ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener
        , ViewTreeObserver.OnGlobalLayoutListener {
    /**
     * 设置日志打印的标识字符串
     */
    private static final String TAG = MyImageView.class.getSimpleName();
    /**
     * 指定能放大的倍数
     */
    public static final float SCALE_MAX = 4.0f;
    /**
     * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
     */
    private float initScale = 1.0f;
    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    private boolean once = true;
    /**
     * 手势检测对象
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    /**
     * 实现缩放的具体的对象
     */
    private Matrix mScaleMatrix = new Matrix();

    public MyImageView(Context context) {
        super(context, null);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//       设置图片的拉伸类型
        super.setScaleType(ScaleType.MATRIX);
//        初始化手势检测对象
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
//        设置touch监听
        this.setOnTouchListener(this);
    }

    /***
     * 缩放的手势的处理逻辑
     * @param detector  手势处理对象
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2,
                    getHeight() / 2);
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /***
     * 对缩放手势的开始的状态进行监听
     * @param detector  手势处理对象
     * @return
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    /***
     * 对缩放手势的结束状态进行监听
     * @param detector 手势处理对象
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /**
     * 在接触到view的时候触发的事件
     *
     * @param v     触发事件的view
     * @param event 触发的时候对象的手势
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mScaleGestureDetector.onTouchEvent(event);

    }

    /**
     * 在全局布局状态或视图可见性时调用的回调方法
     * 视图树内的变化
     */
    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            Log.e(TAG, d.getIntrinsicWidth() + " , " + d.getIntrinsicHeight());
            int width = getWidth();
            int height = getHeight();
            // 拿到图片的宽和高
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            // 如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
            if (dw > width && dh <= height) {
                scale = width * 1.0f / dw;
            }
            if (dh > height && dw <= width) {
                scale = height * 1.0f / dh;
            }
            // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
            if (dw > width && dh > height) {
                scale = Math.min(dw * 1.0f / width, dh * 1.0f / height);
            }
            initScale = scale;
            // 图片移动至屏幕中心
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix
                    .postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            once = false;
        }

    }

    /**
     * 当view添加到窗口的时候调用这个方法
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /***
     * 当view取消和当前的窗口的联系的时候调用这个方法
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }
}

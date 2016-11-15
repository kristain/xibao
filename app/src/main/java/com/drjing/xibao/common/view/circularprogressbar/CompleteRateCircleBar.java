package com.drjing.xibao.common.view.circularprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.drjing.xibao.common.utils.DisplayUtils;

public class CompleteRateCircleBar extends View {

    private static final String TAG = "com.drjing.xibao.common.view.circularprogressbar.CompleteRateCircleBar";
    private RectF mColorWheelRectangle = new RectF();
    private RectF mDefaultWheelRectangle = new RectF();
    /**
     * 默认圆画笔对象
     */
    private Paint mDefaultWheelPaint;
    /**
     * 进度圆画笔对象
     */
    private Paint mColorWheelPaint;


    private float mColorWheelRadius;

    private float circleStrokeWidth;

    private float pressExtraStrokeWidth;

    private String mText;

    private int mCount;
    /**
     * 进度在圆周中的百分比
     */
    private float mSweepAnglePer;
    private float mSweepAngle;

    /**
     * 进度百分比字体大小
     */
    private int mTextSize;
    BarAnimation anim;

    /**
     * 进度数值文案画笔对象
     */
    private Paint textPaint;

    /**
     * title文案画笔对象
     */
    private Paint titletextPaint;


    public CompleteRateCircleBar(Context context) {
        super(context);
        init(null, 0);
    }

    public CompleteRateCircleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CompleteRateCircleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {
        circleStrokeWidth = DisplayUtils.dip2px(getContext(), 8);
        pressExtraStrokeWidth = DisplayUtils.dip2px(getContext(), 1);
        mTextSize = DisplayUtils.dip2px(getContext(), 8);

        //设置进度圆画笔对象
        mColorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorWheelPaint.setColor(0xFFFF0000);
        //mColorWheelPaint.setStyle(Style.FILL_AND_STROKE);
        //mColorWheelPaint.setStrokeWidth(1);
        mColorWheelPaint.setStyle(Style.STROKE);
        mColorWheelPaint.setStrokeWidth(circleStrokeWidth);

        //设置默认圆画笔对象
        mDefaultWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDefaultWheelPaint.setColor(0xFF728484);
        mDefaultWheelPaint.setStyle(Style.STROKE);
        mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth);

        // 设置进度数值文案画笔对象属性
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setColor(0xFFFF0000);
        textPaint.setStyle(Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textPaint.setTextSize(mTextSize);


        titletextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        titletextPaint.setColor(0xFFFF0000);
        titletextPaint.setStyle(Style.FILL_AND_STROKE);
        titletextPaint.setTextAlign(Paint.Align.LEFT);
        titletextPaint.setTextSize(mTextSize);

        mText = "0";
        mSweepAngle = 0;

        anim = new BarAnimation();
        anim.setDuration(2000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mDefaultWheelRectangle, -90, 360, true, mDefaultWheelPaint);
        canvas.drawArc(mColorWheelRectangle, -90, mSweepAnglePer, false, mColorWheelPaint);
        Rect bounds = new Rect();
        String textstr = mCount + "％";
        textPaint.getTextBounds(textstr, 0, textstr.length(), bounds);
        Rect bounds2 = new Rect();
        titletextPaint.getTextBounds("完成率", 0, "完成率".length(), bounds2);
        canvas.drawText(
                textstr + "",
                mDefaultWheelRectangle.centerX() - mTextSize,
                mDefaultWheelRectangle.centerY(),
                textPaint);
        canvas.drawText(
                "完成率",
                mDefaultWheelRectangle.centerX() - mTextSize*3/2,
                mDefaultWheelRectangle.centerY()+ mTextSize,
                titletextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(width, min);
        mColorWheelRadius = min - circleStrokeWidth - pressExtraStrokeWidth;
        mColorWheelRectangle.set(circleStrokeWidth + pressExtraStrokeWidth , circleStrokeWidth + pressExtraStrokeWidth,
                mColorWheelRadius, mColorWheelRadius );
       // mColorWheelRectangle.offsetTo(451, circleStrokeWidth + pressExtraStrokeWidth + 5);
        mDefaultWheelRectangle.set(circleStrokeWidth + pressExtraStrokeWidth , circleStrokeWidth + pressExtraStrokeWidth ,
                mColorWheelRadius , mColorWheelRadius );
       // mDefaultWheelRectangle.offsetTo(440, circleStrokeWidth + pressExtraStrokeWidth - 6);
    }


    @Override
    public void setPressed(boolean pressed) {
        if (pressed) {
            mColorWheelPaint.setStrokeWidth(circleStrokeWidth + pressExtraStrokeWidth);
            mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth + pressExtraStrokeWidth);
        } else {
            mColorWheelPaint.setStrokeWidth(circleStrokeWidth);
            mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth);
        }
        super.setPressed(pressed);
        this.invalidate();
    }

    public void startCustomAnimation() {
        this.startAnimation(anim);
    }

    public void setText(String text) {
        mText = text;
        this.startAnimation(anim);
    }

    public void setSweepAngle(float sweepAngle) {
        mSweepAngle = sweepAngle;

    }


    public class BarAnimation extends Animation {
        /**
         * Initializes expand collapse animation, has two types, collapse (1) and expand (0).
         * 1 will collapse view and set to gone
         */
        public BarAnimation() {

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                mSweepAnglePer = interpolatedTime * mSweepAngle;
                mCount = (int) (interpolatedTime * Float.parseFloat(mText));
            } else {
                mSweepAnglePer = mSweepAngle;
                mCount = Integer.parseInt(mText);
            }
            postInvalidate();
        }
    }

}

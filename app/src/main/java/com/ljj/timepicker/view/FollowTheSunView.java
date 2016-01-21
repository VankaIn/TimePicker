package com.ljj.timepicker.view;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ljj.timepicker.R;


/**
 * Created by Gunter on 2015/01/14.
 */
public class FollowTheSunView extends View {

    private float mInnerRadius, mOuterRadius;
    private float centerX, centerY;//圆心的横纵坐标
    private float mArcWidth; //圆弧的宽度
    private float mTextSize; //文本的大小
    private int mPaintColor; //文本的颜色
    private float mTextSpacing; //文本之间的最小间距
    private Rect[] mTextRects; //文本的矩形
    private String[] mTexts; //文本的矩形
    private int mBackgroundColor; //文本的颜色
    private double perArc = 2 * Math.PI / 360;//1角度所占的弧度数
    private int dayColor = 0xFFFFFFFF;
    private int amColor = 0xFFD9ECFA;
    private int pmColor = 0xFFFFF0CD;
    private float dialRadius = getDip(4);

    private RectF mRectF;
    private Paint mPaint;

    //以下三个数值的区间为 0 ~ 24*4*moveUnit
    private float amStart;
    private float amPeriod;
    private float dayPeriod;

    private float moveUnit;//移动的最小单位，即一个小格

    public FollowTheSunView(Context context) {
        this(context, null);
    }

    public FollowTheSunView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FollowTheSunView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FollowTheSunView);
        mInnerRadius = typedArray.getDimension(R.styleable.FollowTheSunView_innerRadius, getDip(100));
        mArcWidth = typedArray.getDimension(R.styleable.FollowTheSunView_arcWidth, getDip(20));
        mOuterRadius = typedArray.getDimension(R.styleable.FollowTheSunView_outerRadius, getDip(130));
        mTextSize = typedArray.getDimension(R.styleable.FollowTheSunView_textSize, getSp(16));
        mPaintColor = typedArray.getColor(R.styleable.FollowTheSunView_paintColor, 0xff000000);
        mBackgroundColor = typedArray.getColor(R.styleable.FollowTheSunView_backgroundColor, 0xffffffff);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        //一小格占用的角度数
        moveUnit = (float) 360 / (float) (24 * 4);

        //每个文本之间的最小间隔角度为8*moveUnit
        mTextSpacing = 8 * moveUnit;

        //以下三个数据以一小格为单位
        amStart = 10 * 4 * moveUnit;
        amPeriod = 6 * 4 * moveUnit;
        dayPeriod = 7 * 4 * moveUnit;

        initText();

        mRectF = new RectF();
    }

    //获取三个文本占用空间的大小
    private void initText() {
        mTexts = new String[]{"AM", "DAY", "PM"};
        mTextRects = new Rect[3];
        Paint textPaint = new Paint();
        textPaint.setTextSize(mTextSize);
        for (int i = 0; i < mTextRects.length; i++) {
            mTextRects[i] = new Rect();
            textPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mTextRects[i]);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;
        mRectF.set(centerX - mInnerRadius, centerY - mInnerRadius,
                centerX + mInnerRadius, centerY + mInnerRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float pmStart = amStart + amPeriod + dayPeriod;

        draw(canvas, amStart, amPeriod, amColor);
        draw(canvas, amStart + amPeriod, dayPeriod, dayColor);
        draw(canvas, pmStart, amStart + 360 - pmStart, pmColor);
        drawText(canvas);
        canvas.drawCircle(centerX, centerY, getDip(5), mPaint);
    }

    private float getDip(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private float getSp(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

    /**
     * 时钟共24个大格子，每个大格子有4个小格子
     *
     * @param canvas
     * @param start  开始,以小格子为单位
     * @param during 间段，同样以小格子为单位
     * @param color  圆弧的颜色
     */
    private void draw(Canvas canvas, float start, float during, int color) {

        //画圆弧 -- Day：0xfff，AM：0x4400CCC5，PM：0x44E8BF6A
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mArcWidth);
        mPaint.setColor(color);
        canvas.drawArc(mRectF, start, during, false, mPaint);

        //画半径
        mPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 0.6, getResources().getDisplayMetrics()));
        mPaint.setColor(mPaintColor);
        canvas.drawLine(centerX, centerY,
                getX(mInnerRadius - mArcWidth / 2, start),
                getY(mInnerRadius - mArcWidth / 2, start), mPaint);



        //画圆点
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getX(mOuterRadius, start),
                getY(mOuterRadius, start),
                dialRadius, mPaint);

    }

    //角度转成弧度
    private double change2Radian(float angle) {
        return 2 * Math.PI / 360 * angle;
    }

    //画文本
    private void drawText(Canvas canvas) {

        float r = mInnerRadius / 2;

        //AM角度
        float angle_0 = amStart + amPeriod / 2;
        //DAY角度
        float angle_1 = amStart + amPeriod + dayPeriod / 2;
        //PM角度
        float angle_2 = amStart + amPeriod + dayPeriod + (360 - amPeriod - dayPeriod) / 2;

        //判断文本有没有重合
        if ((angle_1 - angle_0) < mTextSpacing) {
            float spacing = mTextSpacing - (angle_1 - angle_0);
            angle_0 = angle_0 - spacing / 2;
            angle_1 = angle_0 + mTextSpacing;
        } else if ((angle_2 - angle_1) < mTextSpacing) {
            float spacing = mTextSpacing - (angle_2 - angle_1);
            angle_2 = angle_2 + spacing / 2;
            angle_1 = angle_2 - mTextSpacing;
        } else if (angle_0 + 360 - angle_2 < mTextSpacing){
            float spacing = mTextSpacing - (angle_0 + 360 - angle_2);
            angle_0 = angle_0 + spacing / 2;
            angle_2 = angle_0 - mTextSpacing;
        }

        float amX = getX(r, angle_0) - mTextRects[0].width() / 2;
        float amY = getY(r, angle_0) - mTextRects[0].height() / 2;
        float dayX = getX(r, angle_1) - mTextRects[1].width() / 2;
        float dayY = getY(r, angle_1) - mTextRects[1].height() / 2;
        float pmX = getX(r, angle_2) - mTextRects[2].width() / 2;
        float pmY = getY(r, angle_2) - mTextRects[2].height() / 2;

        mPaint.setColor(mBackgroundColor);
        canvas.drawRect(amX, amY, amX + mTextRects[0].width(), amY + mTextRects[0].height(), mPaint);
        canvas.drawRect(dayX, dayY, dayX + mTextRects[1].width(), dayY + mTextRects[1].height(), mPaint);
        canvas.drawRect(pmX, pmY, pmX + mTextRects[2].width(), pmY + mTextRects[2].height(), mPaint);

        mPaint.setColor(mPaintColor);
        mPaint.setTextSize(mTextSize);
        //画“AM”文本
        canvas.drawText(mTexts[0], amX,
                amY + mTextRects[0].height(), mPaint);
        //画“DAY”文本
        canvas.drawText(mTexts[1], dayX,
                dayY + mTextRects[1].height(), mPaint);
        //画“PM”文本
        canvas.drawText(mTexts[2], pmX,
                pmY + mTextRects[2].height(), mPaint);
    }

    //根据半径和角度获取横坐标
    private float getX(float radius, float angle) {
        return (float) (centerX + Math.cos(change2Radian(angle)) * radius);
    }

    private float getY(float radius, float angle) {
        return (float) (centerY + Math.sin(change2Radian(angle)) * radius);
    }

    /**
     * 设置时间点，区间为 0 ~ 24 * 4
     *
     * @param amStart   AM的开始时间
     * @param amPeriod  AM的时间段
     * @param dayPeriod DAY的时间段
     */
    public void setDate(int amStart, int amPeriod, int dayPeriod) {
        update(amStart * moveUnit, amPeriod * moveUnit, dayPeriod * moveUnit);
    }

    private void update(float amStart, float amPeriod, float dayPeriod) {
        this.amStart = amStart;
        this.amPeriod = amPeriod;
        this.dayPeriod = dayPeriod;
        invalidateView();
    }

    /**
     * 平滑移动到指定的时间点，区间为 0 ~ 24 * 4
     *
     * @param amStart   AM的开始时间
     * @param amPeriod  AM的时间段
     * @param dayPeriod DAY的时间段
     */
    public void smoothMoveTo(int amStart, int amPeriod, int dayPeriod) {
        State stateStart = new State(this.amStart, this.amPeriod, this.dayPeriod);
        State stateEnd = new State(amStart * moveUnit, amPeriod * moveUnit, dayPeriod * moveUnit);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new TypeEvaluator() {
            @Override
            public Object evaluate(float v, Object o, Object t1) {
                State stateStart = (State) o;
                State stateEnd = (State) t1;
                float amStart = stateStart.getAmStart() + v * (stateEnd.getAmStart() - stateStart.getAmStart());
                float amPeriod = stateStart.getAmPeriod() + v * (stateEnd.getAmPeriod() - stateStart.getAmPeriod());
                float dayPeriod = stateStart.getDayPeriod() + v * (stateEnd.getDayPeriod() - stateStart.getDayPeriod());
                update(amStart, amPeriod, dayPeriod);
                return new State(amStart, amPeriod, dayPeriod);
            }
        }, stateStart, stateEnd);
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    private void invalidateView() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    //状态的内部类，供属性动画使用
    private class State {

        private float amStart;
        private float amPeriod;
        private float dayPeriod;

        public State(float amStart, float amPeriod, float dayPeriod) {
            this.amStart = amStart;
            this.amPeriod = amPeriod;
            this.dayPeriod = dayPeriod;
        }

        public float getAmStart() {
            return amStart;
        }

        public void setAmStart(float amStart) {
            this.amStart = amStart;
        }

        public float getAmPeriod() {
            return amPeriod;
        }

        public void setAmPeriod(float amPeriod) {
            this.amPeriod = amPeriod;
        }

        public float getDayPeriod() {
            return dayPeriod;
        }

        public void setDayPeriod(float dayPeriod) {
            this.dayPeriod = dayPeriod;
        }
    }

}

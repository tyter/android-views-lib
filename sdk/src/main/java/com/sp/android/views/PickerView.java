package com.sp.android.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PickerView extends View {

    public static final int DIVIDER_HIDDEN = 0;
    public static final int DIVIDER_SIMPLE = 1;
    public static final int DIVIDER_FULL = 2;

    private static final int ANIMATE_INTERVAL = 10;
    private static final int ANIMATE_TIME = 300;

    private static final int SCROLL_MSG = 0;

    /**
     * properties set by xml and java code.
     */
    private float mLineHeight;

    private float mHighlightTextSize;
    private int mHighlightTextColor;

    private float mTextSize;
    private int mTextColor;

    private int mDividerDisplay;
    private float mDividerWidth;
    private int mDividerColor;

    private boolean mLoop;

    /**
     * user data.
     */
    private List<String> mList;
    private int mSelected;
    private int mVirtualSelected;

    /**
     * run time data.
     */
    private float mLastDownY;
    private boolean mDrag;
    private float mOffset;

    private Paint mHighlightPaint;
    private Paint mPaint;
    private Paint mDividerPaint;

    private Timer mTimer;
    private SmoothTimerTask mSmoothTask;
    private SmoothHandler mSmoothHandler;

    public PickerView(Context context) {
        super(context);

        mLineHeight = 90;

        mHighlightTextSize = 48;
        mHighlightTextColor = 0xff333333;

        mTextSize = 32;
        mTextColor = 0xff666666;

        mDividerDisplay = DIVIDER_HIDDEN;
        mDividerWidth = 1.0f;
        mDividerColor = 0xff666666;

        mLoop = false;

        init(null);
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public PickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PickerView);
            mLineHeight = typedArray.getDimension(R.styleable.PickerView_lineHeight, 90);

            mHighlightTextSize = typedArray.getDimension(R.styleable.PickerView_highlightTextSize, 48);
            mHighlightTextColor = typedArray.getColor(R.styleable.PickerView_highlightTextColor, 0xff333333);

            mTextSize = typedArray.getDimension(R.styleable.PickerView_textSize, 32);
            mTextColor = typedArray.getColor(R.styleable.PickerView_textColor, 0xff666666);

            mDividerDisplay = typedArray.getInt(R.styleable.PickerView_dividerDisplay, DIVIDER_HIDDEN);
            mDividerWidth = typedArray.getFloat(R.styleable.PickerView_dividerWidth, 1.0f);
            mDividerColor = typedArray.getColor(R.styleable.PickerView_dividerColor, 0xff666666);

            mLoop = typedArray.getBoolean(R.styleable.PickerView_loop, false);

            typedArray.recycle();
        }

        mList = new ArrayList<>();
        mSelected = 0;
        mVirtualSelected = 0;

        mLastDownY = 0;
        mDrag = false;
        mOffset = 0;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setAntiAlias(true);
        mHighlightPaint.setTypeface(Typeface.MONOSPACE);
        mHighlightPaint.setTextAlign(Paint.Align.CENTER);
        mHighlightPaint.setColor(mHighlightTextColor);
        mHighlightPaint.setTextSize(mHighlightTextSize);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.MONOSPACE);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mHighlightTextSize);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setStyle(Paint.Style.FILL);
        mDividerPaint.setTextAlign(Paint.Align.CENTER);
        mDividerPaint.setColor(mDividerColor);

        mTimer = new Timer();
        mSmoothTask = null;
        mSmoothHandler = new SmoothHandler(this);
    }

    public void setList(List<String> list) {
        if (list != null) {
            mList = list;
            if (mSelected >= mList.size()) {
                mSelected = 0;
            }
            mVirtualSelected = mSelected;
            invalidate();
        }
    }

    public void setSelected(int selected) {
        mSelected = selected;
        if (mSelected >= mList.size()) {
            mSelected = 0;
        }
        mVirtualSelected = selected;
    }

    public int getSelected() {
        return mSelected;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawList(canvas, false);
        drawList(canvas, true);
        drawDivider(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;

            case MotionEvent.ACTION_UP:
                onActionUp(event);
                break;
        }
        return true;
    }

    private void onActionDown(MotionEvent event) {
        if (mSmoothTask != null) {
            mSmoothTask.cancel();
            mSmoothTask = null;
        }
        mLastDownY = event.getY();
        mDrag = true;
    }

    private void onActionMove(MotionEvent event) {
        if (mDrag) {
            mOffset += event.getY() - mLastDownY;
            setSelectedAndOffset();
            mLastDownY = event.getY();
            invalidate();
        }
    }

    private void onActionUp(MotionEvent event) {
        if (mDrag) {
            mDrag = false;
            mOffset += event.getY() - mLastDownY;

            if (Math.abs(mOffset) < 0.0001) {
                mOffset = 0;
            }
            setSelectedAndOffset();
            scrollToSelected();
        }
    }

    private void drawList(Canvas canvas, boolean center) {
        int viewHeight = getMeasuredHeight();
        int viewWidth = getMeasuredWidth();
        do {
            int lineCount = getLineCount();
            if (lineCount == 0) {
                break;
            }

            if (mList.size() == 0) {
                break;
            }

            Paint paint = center ? mHighlightPaint : mPaint;
            if (center) {
                canvas.clipRect(0, viewHeight / 2 - mLineHeight / 2, viewWidth, viewHeight / 2 + mLineHeight / 2);
            }

            draw(canvas, paint, 0, 0);
            int index = getCenterPosition(lineCount);
            for (int i = 0; i <= index; ++i) {
                draw(canvas, paint, i + 1, -1);
            }

            for (int i = index + 1; i <= lineCount; ++i) {
                draw(canvas, paint, i - index, 1);
            }
        } while (false);
    }

    private void draw(Canvas canvas, Paint paint, int distance, int tag) {
        String text = getText(distance, tag);

        if (!TextUtils.isEmpty(text)) {
            int viewHeight = getMeasuredHeight();
            int viewWidth = getMeasuredWidth();
            float x = viewWidth / 2;
            float y = viewHeight / 2 + mOffset;

            float offset = getAbsOffset(distance, tag);
            float minScale = mTextSize / mHighlightTextSize;
            float binomialFactor = getBinomialFactor(0.0f, 1.0f, 1.3f * mLineHeight, minScale);
            float scale = getScale(binomialFactor, 1.0f, offset);
            scale = Math.max(scale, minScale);

            y += ((distance * tag) * mLineHeight);

            Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
            float baseline = (float) (y - (fmi.ascent / 2.0 + fmi.descent / 2.0 + fmi.leading / 2.0));

            canvas.save();
            canvas.scale(scale, scale, x, baseline);
            canvas.drawText(text, x, baseline, paint);
            canvas.restore();
        }
    }

    private void drawDivider(Canvas canvas) {
        do {
            if (mDividerDisplay == DIVIDER_HIDDEN) {
                break;
            }
            int viewHeight = getMeasuredHeight();
            int viewWidth = getMeasuredWidth();
            int lineCount = viewHeight / (int) mLineHeight;
            if (lineCount == 0) {
                break;
            }

            float x = viewWidth / 2;
            float y = viewHeight / 2 - mLineHeight / 2;

            float width = viewWidth * mDividerWidth;

            if (mDividerDisplay == DIVIDER_SIMPLE) {
                canvas.drawLine(x - width / 2, y, x + width / 2, y, mDividerPaint);
                y += mLineHeight;
                canvas.drawLine(x - width / 2, y, x + width / 2, y, mDividerPaint);
            } else if (mDividerDisplay == DIVIDER_FULL) {
                float yOffset = y;
                int index = (lineCount - 1) / 2;
                for (int i = 0; i < index; ++i) {
                    canvas.drawLine(x - width / 2, yOffset, x + width / 2, yOffset, mDividerPaint);
                    yOffset -= mLineHeight;
                }

                yOffset = y;
                for (int i = index + 1; i < lineCount; ++i) {
                    yOffset += mLineHeight;
                    canvas.drawLine(x - width / 2, yOffset, x + width / 2, yOffset, mDividerPaint);
                }
            }
        } while (false);
    }

    private void setSelectedAndOffset() {
        if (mOffset > 0) {
            int step = (int)(mOffset / mLineHeight);
            float rest = mOffset % mLineHeight;
            if (rest > mLineHeight / 2) {
                step++;
            }
            mOffset = mOffset - mLineHeight * step;
            decreaseSelected(step);
        } else {
            int step = (int)(-mOffset / mLineHeight);
            float rest = mOffset % mLineHeight;
            if (rest < -mLineHeight / 2) {
                step++;
            }
            mOffset = mOffset + mLineHeight * step;
            increaseSelected(step);
        }
    }

    private void scrollToSelected() {
        float offset = mOffset;
        if (!isLoop()) {
            offset += (mSelected - mVirtualSelected) * mLineHeight;
        }
        if (offset != 0) {
            mVirtualSelected = mSelected;
            mOffset = offset;
            mSmoothTask = new SmoothTimerTask(mSmoothHandler, offset);
            mTimer.schedule(mSmoothTask, 0, ANIMATE_INTERVAL);
        }
    }

    private float getBinomialFactor(float startX, float startY, float endX, float endY) {
        double factor = (endY - startY) / (Math.pow(endX, 2) - Math.pow(startX, 2));
        return (float)factor;
    }

    private float getScale(float binomialFactor, float binomialConstant, float x) {
        double scale = binomialFactor * Math.pow(x, 2) + binomialConstant;
        return (float)scale;
    }

    private float getAbsOffset(int distance, int tag) {
        return Math.abs((distance * tag) * mLineHeight + mOffset);
    }

    private void decreaseSelected(int count) {
        mVirtualSelected -= count;
        mSelected -= count;
        if (isLoop()) {
            if (mSelected < 0) {
                while (mSelected < 0) {
                    mSelected = (mSelected + mList.size()) % mList.size();
                }
            }
        } else {
            mSelected = Math.max(0, mSelected);
        }
    }

    private void increaseSelected(int count) {
        mVirtualSelected += count;
        mSelected += count;
        if (isLoop()) {
            if (mSelected >= mList.size()) {
                mSelected = mSelected % mList.size();
            }
        } else {
            mSelected = Math.min(mSelected, mList.size() - 1);
        }
    }

    private void setVirtualSelected(int selected) {
        mVirtualSelected = selected;
    }

    private int getVirtualSelected() {
        return mVirtualSelected;
    }

    private int getLineCount() {
        int viewHeight = getMeasuredHeight();
        return viewHeight / (int) mLineHeight;
    }

    private int getCenterPosition(int lineCount) {
        if (lineCount <= 0) {
            return -1;
        }
        return (lineCount - 1) / 2;
    }

    private String getText(int distance, int tag) {
        int select = getVirtualSelected() + (distance * tag);
        if (isLoop()) {
            if (select < 0) {
                while (select < 0) {
                    select = (select + mList.size()) % mList.size();
                }
            } else if (select >= mList.size()){
                select = select % mList.size();
            }
        }
        if (select >= 0 && select < mList.size()) {
            return mList.get(select);
        }
        return null;
    }





    public float getLineHeight() {
        return mLineHeight;
    }

    public void setLineHeight(float lineHeight) {
        mLineHeight = lineHeight;
        invalidate();
    }

    public float getHighlightTextSize() {
        return mHighlightTextSize;
    }

    public void setHighlightTextSize(float highlightTextSize) {
        mHighlightTextSize = highlightTextSize;
        mHighlightPaint.setTextSize(mHighlightTextSize);
        mPaint.setTextSize(mHighlightTextSize);
        invalidate();
    }

    public int getHighlightTextColor() {
        return mHighlightTextColor;
    }

    public void setHighlightTextColor(int highlightTextColor) {
        mHighlightTextColor = highlightTextColor;
        mHighlightPaint.setColor(mHighlightTextColor);
        invalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        invalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mPaint.setColor(mTextColor);
        invalidate();
    }

    public int getDividerDisplay() {
        return mDividerDisplay;
    }

    public void setDividerDisplay(int dividerDisplay) {
        mDividerDisplay = dividerDisplay;
        invalidate();
    }

    public float getDividerWidth() {
        return mDividerWidth;
    }

    public void setDividerWidth(float dividerWidth) {
        mDividerWidth = dividerWidth;
        invalidate();
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(int dividerColor) {
        mDividerColor = dividerColor;
        mDividerPaint.setColor(mDividerColor);
        invalidate();
    }

    public boolean isLoop() {
        return mLoop;
    }

    public void setLoop(boolean loop) {
        mLoop = loop;
    }





    private class SmoothTimerTask extends TimerTask {
        private Handler handler;
        private float smoothOffset;
        private float current;

        public SmoothTimerTask(Handler handler, float smoothOffset) {
            this.handler = handler;
            this.smoothOffset = smoothOffset;
            this.current = 0;
        }

        @Override
        public void run() {
            current += ANIMATE_INTERVAL;
            current = Math.min(current, ANIMATE_TIME);
            float value = quartOut(current, smoothOffset, -smoothOffset, ANIMATE_TIME);
            Message msg = handler.obtainMessage(SCROLL_MSG, (int)value, 0);
            handler.sendMessage(msg);

            if (current == ANIMATE_TIME) {
                cancel();
                mSmoothTask = null;
            }
        }

        private float cubicOut(float t, float b, float c, float d) {
            t = (t / d) - 1;
            return c * (t * t * t + 1) + b;
        }

        private float quartOut(float t, float b, float c, float d) {
            t = (t / d) - 1;
            return -c * (t * t * t * t - 1) + b;
        }
    }

    private static class SmoothHandler extends Handler {
        private WeakReference<PickerView> pickView;

        public SmoothHandler(PickerView view) {
            pickView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            PickerView view = pickView.get();
            if (view != null && msg.what == SCROLL_MSG) {
                view.mOffset = msg.arg1;
                view.invalidate();
            }
        }
    }
}

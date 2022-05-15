/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Add Device Activity prompts the user to add a device.
 * Once the user inputs device name and device ID, they are taken to the Authentication Fragment
 * to enter the 6 digit code sent to their email address for authentication. Once the authentication
 * done, they are taken to success , where they are prompted
 * to return to the Dashboard screen.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2020-08-12
 */

package com.centri.centri_master_app.custom.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;

public class HalfGauge extends com.centri.centri_master_app.custom.views.AbstractGauge {

    private float needleStart = 0;
    private float needleEnd = 180;
    private float currentAngle = 0;
    private float startAngle = 180;
    private float sweepAngle = 180;
    private Integer needleAngleNext;
    private Handler handler = new Handler();
    private boolean enableBackGroundShadow = true;
    private boolean enableNeedleShadow = true;
    private boolean drawValue = false;


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public HalfGauge(Context context) {
        super(context);
        init();
    }

    public HalfGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HalfGauge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HalfGauge(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        getGaugeBackGround().setStrokeWidth(100f);
        //add BG Shadow
        //drawShadow();

        setPadding(20f);


    }

    private void drawShadow() {
        if (enableBackGroundShadow) {
            getGaugeBackGround().setShadowLayer(15.0f, 0f, 5.0f, 0X50000000);
            setLayerType(LAYER_TYPE_HARDWARE, getGaugeBackGround());

        }
        if (enableNeedleShadow) {
            //add Needle Shadow
            getNeedlePaint().setShadowLayer(10.f, 0f, 5.0f, 0X50000000);
            setLayerType(LAYER_TYPE_HARDWARE, getNeedlePaint());
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null)
            return;

        //Add shadow
        drawShadow();

        canvas.save();
        canvas.translate((getWidth() / 2f) - ((getRectRight() / 2f) * getScaleRatio()), getHeight() / 2f - 50f * getScaleRatio());
        canvas.scale(getScaleRatio(), getScaleRatio());
        canvas.drawArc(getRectF(), startAngle, sweepAngle, false, getGaugeBackGround());
        drawRange(canvas);
        canvas.restore();

        canvas.save();
        canvas.translate((getWidth() / 2f) - ((getRectRight() / 2f) * getScaleRatio()), getHeight() / 2f - 50f * getScaleRatio());
        canvas.scale(getScaleRatio(), getScaleRatio());
        canvas.rotate(getNeedleAngle(), getRectRight() / 2f, getRectBottom() / 2f);
        canvas.drawLine(-30f, 400f / 2f, 400f / 2f, 400f / 2f, getNeedlePaint());
        //canvas.drawCircle(205f,205f,10f,getNeedlePaint());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawOval(190f, 190f, 210f, 210f, getNeedlePaint());

        }
        canvas.restore();


        if(drawValue) {
            canvas.save();
            canvas.translate((getWidth() / 2f) - ((getRectRight() / 2f) * getScaleRatio()), getHeight() / 2f - 50f * getScaleRatio());
            canvas.scale(getScaleRatio(), getScaleRatio());
            canvas.drawText((int) getValue() + "%", 200f, 240f, getTextPaint());
            canvas.restore();
        }


        //draw Text Value
        //drawValueText(canvas);
        //drawMinValue
        //drawMinValue(canvas);
        //drawMaxValue
        //drawMaxValue(canvas);
    }

    private void drawValueText(Canvas canvas) {
        canvas.save();
        canvas.translate((getWidth() / 2f) - ((getRectRight() / 2f) * getScaleRatio()), getHeight() / 2f - 50f * getScaleRatio());
        canvas.scale(getScaleRatio(), getScaleRatio());
        canvas.drawText((int)getValue() + "%", 200f, 240f, getTextPaint());
        canvas.restore();
    }

    private void drawMinValue(Canvas canvas) {
        canvas.save();
        canvas.translate((getWidth() / 2f) - ((getRectRight() / 2f) * getScaleRatio()), getHeight() / 2f - 50f * getScaleRatio());
        canvas.scale(getScaleRatio(), getScaleRatio());
        //canvas.rotate(26, 10f, 130f);
        canvas.drawText(getMinValue() + "", 10f + getPadding(), 220f, getRangeValue());
        canvas.restore();
    }

    private void drawMaxValue(Canvas canvas) {
        canvas.save();
        canvas.translate((getWidth() / 2f) - ((getRectRight() / 2f) * getScaleRatio()), getHeight() / 2f - 50f * getScaleRatio());
        canvas.scale(getScaleRatio(), getScaleRatio());
        //canvas.rotate(-26, 390f, 130f);
        canvas.drawText(getMaxValue() + "", 390f - getPadding(), 220f, getRangeValue());
        canvas.restore();
    }

    private void drawRange(Canvas canvas) {
        for (com.centri.centri_master_app.custom.views.Range range : getRanges()) {
            float startAngle = calculateStartAngle(range.getFrom());
            float sweepAngle = calculateSweepAngle(range.getFrom(), range.getTo());
            canvas.drawArc(getRectF(), startAngle, sweepAngle, false, getRangePaint(range.getColor()));
        }
    }

    private float calculateStartAngle(double from) {
        return sweepAngle / 100 * getCalculateValuePercentage(from) + startAngle;
    }

    private float calculateSweepAngle(double from, double to) {
        return sweepAngle / 100 * getCalculateValuePercentage(to) - sweepAngle / 100 * getCalculateValuePercentage(from) + 0.5f;

    }

    public int getNeedleAngle() {
        if (needleAngleNext != null) {
            if (needleAngleNext != currentAngle) {
                if (needleAngleNext < currentAngle)
                    currentAngle--;
                else
                    currentAngle++;
                handler.postDelayed(runnable, 5);
            }
        } else {
            currentAngle = (needleEnd - needleStart) / 100 * getCalculateValuePercentage() + needleStart;
        }

        return (int) currentAngle;
    }

    @Override
    public void setValue(double value) {
        super.setValue(value);
        needleAngleNext = (int) ((needleEnd - needleStart) / 100 * getCalculateValuePercentage() + needleStart);
    }


    protected Paint getRangePaint(int color) {
        Paint range = new Paint();
        range.setColor(color);
        range.setAntiAlias(true);
        range.setStyle(Paint.Style.STROKE);
        range.setStrokeWidth(getGaugeBackGround().getStrokeWidth());
        return range;
    }

    protected Paint getRangeValue() {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GRAY);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(15f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        return textPaint;
    }

    public boolean isEnableBackGroundShadow() {
        return enableBackGroundShadow;
    }

    public void setEnableBackGroundShadow(boolean enableBackGroundShadow) {
        this.enableBackGroundShadow = enableBackGroundShadow;
    }

    public boolean isEnableNeedleShadow() {
        return enableNeedleShadow;
    }

    public void setEnableNeedleShadow(boolean enableNeedleShadow) {
        this.enableNeedleShadow = enableNeedleShadow;
    }
    public boolean isDrawValue() {
        return drawValue;
    }

    public void setDrawValue(boolean drawValue) {
        this.drawValue = drawValue;
    }
}

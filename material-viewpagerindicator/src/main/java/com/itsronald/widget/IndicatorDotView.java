package com.itsronald.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * A circular dot used to indicate a page in a ViewPager.
 */
public class IndicatorDotView extends ImageView {

    //region Constants

    @Dimension
    protected static int DEFAULT_DOT_RADIUS_DIP = 4;
    @ColorInt
    protected static int DEFAULT_DOT_COLOR = Color.parseColor("#75FFFFFF");
    @StyleableRes
    protected static int DEFAULT_COLOR_STYLEABLE_ID = R.styleable.IndicatorDotView_dotColor;

    //endregion

    @NonNull
    private final ShapeDrawable dot = new ShapeDrawable(new OvalShape());

    //region Constructors

    public IndicatorDotView(@NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public IndicatorDotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public IndicatorDotView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public IndicatorDotView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    //endregion

    private void init(@NonNull Context context,
                      @Nullable AttributeSet attrs,
                      int defStyleAttr,
                      int defStyleRes) {
        TypedArray attributes = context
                .obtainStyledAttributes(attrs, R.styleable.IndicatorDotView, defStyleAttr, defStyleRes);

        final float scale = getResources().getDisplayMetrics().density;
        final int defaultDotRadius = (int) (DEFAULT_DOT_RADIUS_DIP * scale + 0.5);
        final int dotRadius = attributes.getDimensionPixelSize(R.styleable.IndicatorDotView_dotRadius, defaultDotRadius);
        setRadius(dotRadius);

        final int dotColor = attributes.getColor(DEFAULT_COLOR_STYLEABLE_ID, DEFAULT_DOT_COLOR);
        setColor(dotColor);

        attributes.recycle();

        setImageDrawable(dot);
    }

    //region Accessors

    /**
     * Set the preferred dot radius for this view.
     *
     * @param newRadius The new radius, in pixels.
     */
    void setRadius(@Px int newRadius) {
        final int diameter = newRadius * 2;
        dot.setIntrinsicWidth(diameter);
        dot.setIntrinsicHeight(diameter);
    }

    /**
     * Set a new dot color for this view.
     *
     * @param color The new color for the dot.
     */
    void setColor(@ColorInt int color) {
        dot.getPaint().setColor(color);
    }

    //endregion
}

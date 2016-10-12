package com.itsronald.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * A circular dot used to indicate a page in a ViewPager.
 */
class IndicatorDotView extends ImageView {

    //region Constants
    @NonNull
    private static final String TAG = "IndicatorDotView";

    @Dimension
    static final int DEFAULT_DOT_RADIUS_DIP = 4;
    @ColorInt
    static final int DEFAULT_DOT_COLOR = Color.GRAY;
    @ColorInt
    static final int DEFAULT_UNSELECTED_DOT_COLOR = DEFAULT_DOT_COLOR;
    @ColorInt
    static final int DEFAULT_SELECTED_DOT_COLOR = Color.WHITE;

    //endregion

    @NonNull
    private final ShapeDrawable dot = new ShapeDrawable(new OvalShape());
    @Px
    private int dotRadius;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
        dotRadius = attributes.getDimensionPixelSize(R.styleable.IndicatorDotView_dotRadius, defaultDotRadius);
        setRadius(dotRadius);

        final int dotColor = attributes.getColor(R.styleable.IndicatorDotView_dotColor, DEFAULT_DOT_COLOR);
        setColor(dotColor);

        attributes.recycle();

        setImageDrawable(dot);
    }

    //region Accessors

    @Px
    int getRadius() {
        return dotRadius;
    }

    /**
     * Set the preferred dot radius for this view.
     *
     * @param newRadius The new radius, in pixels.
     */
    void setRadius(@Px int newRadius) {
        dotRadius = newRadius;

        final int diameter = newRadius * 2;
        dot.setIntrinsicWidth(diameter);
        dot.setIntrinsicHeight(diameter);

        invalidate();
    }

    /**
     * Get the current dot color for this view.
     *
     * @return The current color value for the dot.
     */
    @ColorInt
    int getColor() {
        return dot.getPaint().getColor();
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

    //region Reveal animation

    /**
     * Start a Material reveal animation of this view with no delay.
     */
    void reveal() {
        reveal(0);
    }

    /**
     * Start a Material reveal animation of this view with a specified start delay.
     *
     * @param startDelay The start delay for the animation, in milliseconds.
     */
    void reveal(long startDelay) {
        final int centerX = getWidth() / 2;
        final int centerY = getHeight() / 2;
        final long animationDuration = getContext().getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            startAnimatorReveal(centerX, centerY, animationDuration, startDelay);
        } else {
            startScaleReveal(centerX, centerY, animationDuration);
        }
    }

    /**
     * Start a reveal animation of this view.
     *
     * @param centerX           The X point from which the animation starts.
     * @param centerY           The Y point from which the animation starts.
     * @param animationDuration How long the animation should last.
     * @param startDelay        The start delay for the animation, in milliseconds.
     * @see #reveal()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startAnimatorReveal(int centerX, int centerY, long animationDuration, long startDelay) {
        final Animator animator = revealAnimator(centerX, centerY);

        animator.setDuration(animationDuration);
        animator.setStartDelay(startDelay);

        setVisibility(VISIBLE);
        animator.start();
    }

    /**
     * Create the API-level appropriate animator for this indicator's reveal.
     *
     * @param centerX The X point from which the animation starts.
     * @param centerY The Y point from which the animation starts.
     *
     * @see #startAnimatorReveal(int, int, long, long)
     */
    private Animator revealAnimator(int centerX, int centerY) {
        final Animator animator;
        /*
         * The standard material circular reveal animation is exactly what we want.
         *
         * Since this view is already circular, a scale animation effectively simulates the
         * material circular reveal on earlier API versions.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils
                    .createCircularReveal(this, centerX, centerY, 0, dotRadius);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1);
            final PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1);
            animator = ObjectAnimator.ofPropertyValuesHolder(this, scaleX, scaleY);
        } else {
            final Animator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0, 1);
            final Animator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0, 1);

            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY);
            animator = animatorSet;
        }
        return animator;
    }

    /**
     * Start a fallback reveal animation of this view for API versions < Honeycomb.
     * <p>
     * It looks the same as the Material Circular Reveal animation (because this is already
     * a circle) but is fractionally slower to start.
     *
     * @param centerX           The X point from which the animation starts.
     * @param centerY           The Y point from which the animation starts.
     * @param animationDuration How long the animation should last.
     * @see #reveal()
     */
    private void startScaleReveal(int centerX, int centerY, long animationDuration) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, centerX, centerY);

        scaleAnimation.setFillBefore(true);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setFillEnabled(true);

        scaleAnimation.setDuration(animationDuration);
        scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        setVisibility(VISIBLE);
        startAnimation(scaleAnimation);
    }

    //endregion
}

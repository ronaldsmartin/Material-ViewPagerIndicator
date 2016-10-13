/*
 * Copyright (C) 2016 Ronald Martin <hello@itsronald.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 10/12/16 11:22 PM.
 */

package com.itsronald.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.ImageView;

/**
 * A circular dot used to indicate a page in a ViewPager.
 */
class IndicatorDotView extends ImageView {

    //region Constants
    @NonNull
    private static final String TAG = "IndicatorDotView";

    @Dimension
    static final int DEFAULT_DOT_RADIUS_DIP = 3;
    @ColorInt
    static final int DEFAULT_DOT_COLOR = Color.LTGRAY;
    @ColorInt
    static final int DEFAULT_UNSELECTED_DOT_COLOR = DEFAULT_DOT_COLOR;
    @ColorInt
    static final int DEFAULT_SELECTED_DOT_COLOR = Color.WHITE;

    static final long REVEAL_ANIM_DURATION = 100;   // 100 ms

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
     * Animation: Reveal this view, starting from the center.
     *
     * @return An animator that reveals this view from its center.
     */
    @NonNull
    Animator revealAnimator() {
        final int centerX = getWidth() / 2;
        final int centerY = getHeight() / 2;

        final Animator animator = revealAnimator(centerX, centerY);
        animator.setDuration(REVEAL_ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(VISIBLE);
            }
        });
        return animator;
    }

    /**
     * Create the API-level appropriate animator for this indicator's reveal.
     *
     * @param centerX The X point from which the animation starts.
     * @param centerY The Y point from which the animation starts.
     *
     * @see #revealAnimator()
     */
    @NonNull
    private Animator revealAnimator(int centerX, int centerY) {
        final Animator animator;
        /*
         * The standard material circular reveal animation is exactly what we want.
         *
         * Since this view is already circular, a scale animation effectively simulates the
         * material circular reveal on earlier API versions.
         */
        final int oldScale = 0, newScale = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils
                    .createCircularReveal(this, centerX, centerY, 0, dotRadius);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final PropertyValuesHolder scaleX = PropertyValuesHolder
                    .ofFloat(View.SCALE_X, oldScale, newScale);
            final PropertyValuesHolder scaleY = PropertyValuesHolder
                    .ofFloat(View.SCALE_Y, oldScale, newScale);
            animator = ObjectAnimator.ofPropertyValuesHolder(this, scaleX, scaleY);
        } else {
            final Animator scaleX = ObjectAnimator.ofFloat(this, "scaleX", oldScale, newScale);
            final Animator scaleY = ObjectAnimator.ofFloat(this, "scaleY", oldScale, newScale);

            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY);
            animator = animatorSet;
        }
        return animator;
    }

    //endregion

    //region Slide animation

    /**
     * Animation: Slide this view to another location on the screen.
     *
     * @param toX The horizontal coordinate to which this view should move, in this view's
     *            coordinate space.
     * @param toY The vertical coordinate to which this view should move, in this view's
     *            coordinate space.
     * @return An animator that will move this view to (toX, toY) when started.
     */
    @NonNull
    Animator slideAnimator(final float toX, final float toY, final long animationDuration) {
        final float fromX = getTranslationX();
        final float fromY = getTranslationY();

        final Animator animator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final PropertyValuesHolder translationX = PropertyValuesHolder
                    .ofFloat(View.TRANSLATION_X, fromX, toX);
            final PropertyValuesHolder translationY = PropertyValuesHolder
                    .ofFloat(View.TRANSLATION_Y, fromY, toY);
            animator = ObjectAnimator.ofPropertyValuesHolder(this, translationX, translationY);
        } else {
            final Animator translationXAnimator = ObjectAnimator
                    .ofFloat(this, "translationX", fromX, toX);
            final Animator translationYAnimator = ObjectAnimator
                    .ofFloat(this, "translationY", fromY, toY);

            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(translationXAnimator, translationYAnimator);
            animator = animatorSet;
        }
        animator.setDuration(animationDuration);

        return animator;
    }

    //endregion
}

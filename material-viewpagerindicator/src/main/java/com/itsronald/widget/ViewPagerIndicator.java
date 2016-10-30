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
 * Last modified 10/29/16 11:37 PM.
 */

package com.itsronald.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.annotation.Retention;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * ViewPagerIndicator is a non-interactive indicator of the current, next,
 * and previous pages of a {@link ViewPager}. It is intended to be used as a
 * child view of a ViewPager widget in your XML layout.
 *
 * Add it as a child of a ViewPager in your layout file and set its
 * android:layout_gravity to TOP or BOTTOM to pin it to the top or bottom
 * of the ViewPager.
 */
@ViewPager.DecorView
public class ViewPagerIndicator extends ViewGroup {

    //region Constants

    @NonNull
    private static final String TAG = "ViewPagerIndicator";
    
    private static final long DOT_SLIDE_ANIM_DURATION = 150;    // 150 ms.

    /**
     * Indicator animation styles.
     * <p>
     * You can use {@link #getAnimationStyle()} and {@link #setAnimationStyle(int)} in conjunction
     * with these values or the {@code animationStyle} XML styleable attribute to control the
     * indicator's page change animations.
     */
    @IntDef({ANIMATION_STYLE_NONE, ANIMATION_STYLE_INK, ANIMATION_STYLE_SCALE})
    @Retention(SOURCE)
    public @interface AnimationStyle {}

    /**
     * Indicator animation style: The selected dot changes colors with no animation.
     */
    @AnimationStyle
    public static final int ANIMATION_STYLE_NONE = 0;
    /**
     * Indicator animation style: The selected dot slides along a dynamic path that collapses
     * behind it.
     * <p>
     * This is the default animation style.
     */
    @AnimationStyle
    public static final int ANIMATION_STYLE_INK = 1;
    /**
     * Indicator animation style: The selected dot is slightly larger than the unselected dots.
     * When the page changes, the dot for the new page grows and changes color, while the dot for
     * the old page shifts to match the other unselected page dots.
     */
    @AnimationStyle
    public static final int ANIMATION_STYLE_SCALE = 2;

    //endregion

    //region ViewPager

    @NonNull
    private final PageListener pageListener = new PageListener();
    @Nullable
    private ViewPager viewPager;
    @Nullable
    private WeakReference<PagerAdapter> pagerAdapterRef;

    //endregion

    //region Indicator Dots
    @Dimension
    static final int DEFAULT_DOT_PADDING_DIP = 9;

    @NonNull
    private final List<IndicatorDotView> indicatorDots = new ArrayList<>();
    @NonNull
    private final List<IndicatorDotPathView> dotPaths = new ArrayList<>();
    private IndicatorDotView selectedDot;   // @NonNull, but initialized in init().
    @Px
    private int dotPadding;
    @Px
    private int dotRadius;
    @ColorInt
    private int unselectedDotColor;
    @ColorInt
    private int selectedDotColor;
    private int animationStyle;

    //endregion

    //region State

    private int gravity = Gravity.CENTER_VERTICAL;
    private int lastKnownCurrentPage = -1;
    private float lastKnownPositionOffset = -1;
    private boolean isUpdatingPositions = false;
    private boolean isUpdatingIndicator = false;
    private boolean selectedDotNeedsLayout = true;

    //endregion


    //region Constructors

    public ViewPagerIndicator(Context context) {
        super(context);
        init(context, null, 0 ,0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(@NonNull Context context,
                      @Nullable AttributeSet attrs,
                      int defStyleAttr,
                      int defStyleRes) {
        TypedArray attributes = context
                .obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator, defStyleAttr, defStyleRes);

        gravity = attributes.getInt(R.styleable.ViewPagerIndicator_android_gravity, gravity);

        final float scale = getResources().getDisplayMetrics().density;

        final int defaultDotPadding = (int) (DEFAULT_DOT_PADDING_DIP * scale + 0.5);
        dotPadding = attributes
                .getDimensionPixelSize(R.styleable.ViewPagerIndicator_dotPadding, defaultDotPadding);

        final int defaultDotRadius = (int) (IndicatorDotView.DEFAULT_DOT_RADIUS_DIP * scale + 0.5);
        dotRadius = attributes.getDimensionPixelSize(
                R.styleable.ViewPagerIndicator_dotRadius,
                defaultDotRadius
        );

        unselectedDotColor = attributes.getColor(
                R.styleable.ViewPagerIndicator_unselectedDotColor,
                IndicatorDotView.DEFAULT_UNSELECTED_DOT_COLOR
        );
        selectedDotColor = attributes.getColor(
                R.styleable.ViewPagerIndicator_selectedDotColor,
                IndicatorDotView.DEFAULT_SELECTED_DOT_COLOR
        );

        animationStyle = attributes.getInt(
                R.styleable.ViewPagerIndicator_animationStyle,
                ANIMATION_STYLE_INK
        );

        attributes.recycle();

        selectedDot = new IndicatorDotView(context);
        selectedDot.setColor(selectedDotColor);
        selectedDot.setRadius(dotRadius);
    }

    //endregion


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightPadding = getPaddingTop() + getPaddingBottom();
        final int childHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                heightPadding, LayoutParams.WRAP_CONTENT);

        final int widthPadding = getPaddingLeft() + getPaddingRight();
        final int childWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                widthPadding, LayoutParams.WRAP_CONTENT);

        // Measure subviews.
        selectedDot.measure(childWidthSpec, childHeightSpec);
        for (IndicatorDotView indicatorDot : indicatorDots) {
            indicatorDot.measure(childWidthSpec, childHeightSpec);
        }
        for (IndicatorDotPathView dotPath : dotPaths) {
            dotPath.measure(childWidthSpec, childHeightSpec);
        }

        // Calculate measurement for this view.
        final int width;
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            /*
             * Due to the implementation of onMeasure() in ViewPager, this case will be called if
             * vertical layout_gravity is specified on this view. Since the Material Design spec
             * usually positions dot indicators like this at the bottom of pages, that means this
             * case will be called almost all the time.
             */
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            final int dotCount = indicatorDots.size();
            final int totalDotWidth = selectedDot.getMeasuredWidth() * dotCount;
            final int totalDotPadding = dotPadding * (dotCount - 1);
            final int minWidth = ViewCompat.getMinimumWidth(this);
            width = Math.max(minWidth, totalDotWidth + totalDotPadding + widthPadding);
        }

        final int height;
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            final int indicatorHeight = selectedDot.getMeasuredHeight();
            final int minHeight = ViewCompat.getMinimumHeight(this);
            height = Math.max(minHeight, indicatorHeight + heightPadding);
        }

        final int childState = ViewCompat.getMeasuredHeightAndState(selectedDot);
        final int measuredHeight = ViewCompat.resolveSizeAndState(height, heightMeasureSpec,
                childState);
        setMeasuredDimension(width, measuredHeight);
    }

    @Override
    public void requestLayout() {
        if (!isUpdatingIndicator) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        refresh();
    }

    private void refresh() {
        if (viewPager != null) {
            updateIndicators(viewPager.getCurrentItem(), viewPager.getAdapter());

            final float offset = lastKnownPositionOffset >= 0 ? lastKnownPositionOffset : 0;
            updateIndicatorPositions(lastKnownCurrentPage, offset, true);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        // See:
        // https://android.googlesource.com/platform/frameworks/support/+/nougat-release/v4/java/android/support/v4/view/PagerTitleStrip.java#244
        super.onAttachedToWindow();

        final ViewParent parent = getParent();
        if (!(parent instanceof ViewPager)) {
            throw new IllegalStateException(
                    "ViewPagerIndicator must be a direct child of a ViewPager.");
        }

        final ViewPager pager = (ViewPager) parent;
        viewPager = pager;

        final PagerAdapter adapter = pager.getAdapter();
        pager.addOnPageChangeListener(pageListener);
        pager.addOnAdapterChangeListener(pageListener);

        final PagerAdapter lastAdapter = pagerAdapterRef != null ? pagerAdapterRef.get() : null;
        updateAdapter(lastAdapter, adapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        // See:
        // https://android.googlesource.com/platform/frameworks/support/+/nougat-release/v4/java/android/support/v4/view/PagerTitleStrip.java#263
        super.onDetachedFromWindow();
        if (viewPager != null) {
            updateAdapter(viewPager.getAdapter(), null);
            viewPager.removeOnPageChangeListener(pageListener);
            viewPager.removeOnAdapterChangeListener(pageListener);
            viewPager = null;
        }
    }

    /**
     * Update the ViewPager adapter being observed by the indicator. The
     * <p>
     * Taken from:
     * https://android.googlesource.com/platform/frameworks/support/+/nougat-release/v4/java/android/support/v4/view/PagerTitleStrip.java#319
     *
     * @param oldAdapter The previous adapter being tracked by the indicator.
     * @param newAdapter The previous adapter that should be tracked by the indicator.
     */
    private void updateAdapter(@Nullable PagerAdapter oldAdapter,
                               @Nullable PagerAdapter newAdapter) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(pageListener);
            pagerAdapterRef = null;
        }
        if (newAdapter != null) {
            newAdapter.registerDataSetObserver(pageListener);
            pagerAdapterRef = new WeakReference<>(newAdapter);
        }
        if (viewPager != null) {
            lastKnownCurrentPage = -1;
            lastKnownPositionOffset = -1;
            updateIndicators(viewPager.getCurrentItem(), newAdapter);
            requestLayout();
        }
    }

    private void updateIndicators(int currentPage, @Nullable PagerAdapter pagerAdapter) {
        isUpdatingIndicator = true;

        final int pageCount = pagerAdapter == null ? 0 : pagerAdapter.getCount();
        updateDotCount(pageCount);

        lastKnownCurrentPage = currentPage;

        if (!isUpdatingPositions) {
            updateIndicatorPositions(currentPage, lastKnownPositionOffset, false);
        }

        isUpdatingIndicator = false;
    }

    private void updateDotCount(int newDotCount) {
        final LayoutParams layoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        // Add unselected dots to layout.
        int dotCount = indicatorDots.size();
        if (dotCount < newDotCount) {

            while (dotCount++ != newDotCount) {
                final IndicatorDotView newDot = new IndicatorDotView(getContext());
                newDot.setRadius(dotRadius);
                newDot.setColor(unselectedDotColor);
                indicatorDots.add(newDot);
                addViewInLayout(newDot, -1, layoutParams, true);
            }
        } else if (dotCount > newDotCount) {
            final List<IndicatorDotView> removedDots =
                    new ArrayList<>(indicatorDots.subList(newDotCount, dotCount));
            for (IndicatorDotView removedDot : removedDots) {
                removeViewInLayout(removedDot);
            }
            indicatorDots.removeAll(removedDots);
        }

        // Make sure there is one fewer path than there are dots.
        updatePathCount(newDotCount - 1);

        // Add selected dot to layout.
        if (newDotCount > 0) {
            addViewInLayout(selectedDot, -1, layoutParams, true);
        } else {
            removeViewInLayout(selectedDot);
        }
    }

    private void updatePathCount(final int newPathCount) {
        int pathCount = dotPaths.size();
        if (pathCount < newPathCount) {
            final LayoutParams layoutParams =
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            while (pathCount++ != newPathCount) {
                final IndicatorDotPathView newPath = new IndicatorDotPathView(
                        getContext(), getUnselectedDotColor(), getDotPadding(), getDotRadius()
                );
                newPath.setVisibility(INVISIBLE);
                dotPaths.add(newPath);
                addViewInLayout(newPath, -1, layoutParams, true);
            }
        } else if (pathCount > newPathCount && newPathCount >= 0) {
            final List<IndicatorDotPathView> pathsToRemove =
                    new ArrayList<>(dotPaths.subList(newPathCount, pathCount));
            for (IndicatorDotPathView dotPath : pathsToRemove) {
                removeViewInLayout(dotPath);
            }
            dotPaths.removeAll(pathsToRemove);
        }
    }

    /**
     * Taken from:
     * https://android.googlesource.com/platform/frameworks/support/+/nougat-release/v4/java/android/support/v4/view/PagerTitleStrip.java#336
     *
     * @param currentPage    The index of the page we are on in the ViewPager.
     * @param positionOffset The offset of the current page from horizontal center.
     * @param forceUpdate    Whether or not to force an update
     */
    private void updateIndicatorPositions(int currentPage, float positionOffset, boolean forceUpdate) {
        if (currentPage != lastKnownCurrentPage && viewPager != null) {
            updateIndicators(currentPage, viewPager.getAdapter());
        } else if (!forceUpdate && positionOffset == lastKnownPositionOffset) {
            return;
        }

        isUpdatingPositions = true;

        final int dotWidth = 2 * dotRadius;
        final int top = calculateIndicatorDotTop();
        final int bottom = top + dotWidth;
        int left = calculateIndicatorDotStart();
        int right = left + dotWidth;
        for (int i = 0,
             dotCount = indicatorDots.size(),
             pathCount = dotPaths.size(); i < dotCount; ++i) {
            final IndicatorDotView dotView = indicatorDots.get(i);
            dotView.layout(left, top, right, bottom);

            if (i < pathCount)  {
                final IndicatorDotPathView dotPath = dotPaths.get(i);
                dotPath.layout(left, top, left + dotPath.getMeasuredWidth(), bottom);
            }

            if (i == currentPage && selectedDotNeedsLayout) {
                selectedDot.layout(left, top, right, bottom);
                selectedDotNeedsLayout = false;
            }

            left = right + dotPadding;
            right = left + dotWidth;
        }
        selectedDot.bringToFront();

        lastKnownPositionOffset = positionOffset;
        isUpdatingPositions = false;
    }

    /**
     * Calculate the starting vertical position for the line of indicator dots.
     * @return The first Y coordinate where the indicator dots start.
     */
    @Px
    private int calculateIndicatorDotTop() {
        final int top;
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        switch (verticalGravity) {
            default:
            case Gravity.CENTER_VERTICAL:
                top = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 - getDotRadius();
                break;
            case Gravity.TOP:
                top = getPaddingTop();
                break;
            case Gravity.BOTTOM:
                top = getHeight() - getPaddingBottom() - 2 * getDotRadius();
                break;
        }
        return top;
    }

    /**
     * Calculate the starting horizontal position for the line of indicator dots.
     * Assumes dots are centered horizontally.
     *
     * @return The first X coordinate where the indicator dots start.
     */
    @Px
    private int calculateIndicatorDotStart() {
        /*
         * Calculate the start position by starting from the center of the view and moving left
         * for half of the dots.
         */
        final int dotCount = indicatorDots.size();
        final float halfDotCount = dotCount / 2f;

        final int dotWidth = 2 * dotRadius;
        final float totalDotWidth = dotWidth * halfDotCount;
        // # dot gaps = (numDots - 1), so # dot gaps / 2 = (numDots - 1) / 2 = halfDotCount - 0.5.
        final float halfDotPaddingCount = Math.max(halfDotCount - 0.5f, 0);
        final float totalDotPaddingWidth = dotPadding * halfDotPaddingCount;

        int startPosition = getWidth() / 2;
        startPosition -= totalDotWidth + totalDotPaddingWidth;
        return startPosition;
    }

    @Nullable
    private Animator getPageChangeAnimator(final int lastPageIndex, final int newPageIndex) {
        switch (getAnimationStyle()) {
            case ANIMATION_STYLE_INK:
                return getInkPageChangeAnimator(lastPageIndex, newPageIndex);
            case ANIMATION_STYLE_SCALE:
                Log.w(TAG, "Unimplemented");
            case ANIMATION_STYLE_NONE:
            default:
                return null;
        }
    }

    @Nullable
    private Animator getInkPageChangeAnimator(final int lastPageIndex, final int newPageIndex) {
        final IndicatorDotPathView dotPath = getDotPathForPageChange(lastPageIndex, newPageIndex);
        final IndicatorDotView lastDot = getDotForPage(lastPageIndex);

        if (dotPath == null || lastDot == null) {
            final String warning = dotPath == null ? "dotPath is null!" : "lastDot is null!";
            Log.w(TAG, warning);
            return null;
        }

        final Animator connectPathAnimator = dotPath.connectPathAnimator();
        connectPathAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dotPath.setVisibility(VISIBLE);
                lastDot.setVisibility(INVISIBLE);
            }
        });

        final long dotSlideDuration = DOT_SLIDE_ANIM_DURATION;
        final Animator selectedDotSlideAnimator =
                selectedDotSlideAnimator(newPageIndex, dotSlideDuration, 0);

        final int pathDirection = getPathDirectionForPageChange(lastPageIndex, newPageIndex);
        final Animator retreatPathAnimator = dotPath.retreatConnectedPathAnimator(pathDirection);

        final Animator dotRevealAnimator = lastDot.revealAnimator();
        dotRevealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dotPath.setVisibility(INVISIBLE);
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(connectPathAnimator).before(selectedDotSlideAnimator);
        animatorSet.play(retreatPathAnimator).after(selectedDotSlideAnimator);
        animatorSet.play(dotRevealAnimator).with(retreatPathAnimator);

        return animatorSet;
    }

    @NonNull
    private Animator selectedDotSlideAnimator(int newPageIndex,
                                              long animationDuration,
                                              long startDelay) {
        final Rect newPageDotRect = new Rect();
        final IndicatorDotView newPageDot = getDotForPage(newPageIndex);
        if (newPageDot != null) {
            newPageDot.getDrawingRect(newPageDotRect);
            offsetDescendantRectToMyCoords(newPageDot, newPageDotRect);
            offsetRectIntoDescendantCoords(selectedDot, newPageDotRect);
        }
        final float toX = newPageDotRect.left;
        final float toY = newPageDotRect.top;

        final Animator animator = selectedDot.slideAnimator(toX, toY, animationDuration);
        animator.setStartDelay(startDelay);

        return animator;
    }

    private void layoutPageChangeImmediate(int newPageIndex) {
        final Rect dotRect = new Rect();
        final IndicatorDotView newPageDot = getDotForPage(newPageIndex);
        if (newPageDot != null) {
            newPageDot.getDrawingRect(dotRect);
            offsetDescendantRectToMyCoords(newPageDot, dotRect);
            selectedDot.layout(dotRect.left, dotRect.top, dotRect.right, dotRect.bottom);
        }
    }

    /**
     * Watches the ViewPager for changes, updating the indicator as needed.
     */
    private class PageListener extends DataSetObserver
            implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

        private int scrollState;

        @Override
        public void onChanged() {
            super.onChanged();
            refresh();
        }

        //region ViewPager.OnPageChangeListener

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // No action necessary - animations will be triggered only when the page has changed
            // completely.
        }

        @Override
        public void onPageSelected(int position) {
            final Animator pageChangeAnimator =
                    getPageChangeAnimator(lastKnownCurrentPage, position);

            if (scrollState == ViewPager.SCROLL_STATE_IDLE
                    && viewPager != null) {
                // Only update the text here if we're not dragging or settling.
                refresh();
            }

            if (pageChangeAnimator == null) {
                layoutPageChangeImmediate(position);
            } else {
                pageChangeAnimator.start();
            }

            lastKnownCurrentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            scrollState = state;
        }

        //endregion

        //region ViewPager.OnAdapterChangeListener

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager,
                                     @Nullable PagerAdapter oldAdapter,
                                     @Nullable PagerAdapter newAdapter) {
            updateAdapter(oldAdapter, newAdapter);
        }

        //endregion
    }


    //region Accessors

    @Nullable
    private IndicatorDotView getDotForPage(int pageIndex) {
        if (pageIndex > indicatorDots.size() - 1 || pageIndex < 0) return null;
        return indicatorDots.get(pageIndex);
    }

    @Nullable
    private IndicatorDotPathView getDotPathForPageChange(int oldPageIndex, int newPageIndex) {
        if (oldPageIndex < 0 || newPageIndex < 0
                || oldPageIndex == newPageIndex)
            return null;

        final int dotPathIndex = oldPageIndex < newPageIndex ? oldPageIndex : newPageIndex;
        return dotPathIndex >= dotPaths.size() ? null : dotPaths.get(dotPathIndex);
    }

    @IndicatorDotPathView.PathDirection
    private int getPathDirectionForPageChange(int oldPageIndex, int newPageIndex) {
        return oldPageIndex < newPageIndex ? IndicatorDotPathView.PATH_DIRECTION_RIGHT :
                IndicatorDotPathView.PATH_DIRECTION_LEFT;
    }

    /**
     * Get the {@link Gravity} used to position dots within the indicator.
     * Only the vertical gravity component is used.
     *
     * @see #setGravity(int)
     */
    public int getGravity() {
        return gravity;
    }

    /**
     * Set the {@link Gravity} used to position dots within the indicator.
     * Only the vertical gravity component is used.
     *
     * @param newGravity {@link Gravity} constant for positioning indicator dots.
     *
     * @see #getGravity()
     */
    public void setGravity(int newGravity) {
        gravity = newGravity;
        requestLayout();
    }

    /**
     * Get the current spacing between each indicator dot.
     *
     * @return The distance between each indicator dot, in pixels.
     *
     * @see #setDotPadding(int)
     */
    @Px
    public int getDotPadding() {
        return dotPadding;
    }

    /**
     * Set the spacing between each indicator dot.
     *
     * @param newDotPadding The distance to use between each indicator dot, in pixels.
     *
     * @see #getDotPadding()
     */
    public void setDotPadding(@Px int newDotPadding) {
        if (dotPadding == newDotPadding) return;
        if (newDotPadding < 0) newDotPadding = 0;

        dotPadding = newDotPadding;
        invalidate();
        requestLayout();
    }

    /**
     * Get the current radius of each indicator dot.
     *
     * @return The radius of each indicator dot, in pixels.
     *
     * @see #setDotRadius(int)
     */
    @Px
    public int getDotRadius() {
        return dotRadius;
    }

    /**
     * Set the radius of each indicator dot.
     *
     * @param newRadius The new radius to use for each indicator dot.
     *
     * @see #getDotRadius()
     */
    public void setDotRadius(@Px int newRadius) {
        if (dotRadius == newRadius) return;
        if (newRadius < 0) newRadius = 0;

        dotRadius = newRadius;
        for (IndicatorDotView indicatorDot : indicatorDots) {
            indicatorDot.setRadius(dotRadius);
        }
        invalidate();
        requestLayout();
    }

    /**
     * Get the current color for unselected indicator dots.
     *
     * @return The unselected dot color.
     *
     * @see #setUnselectedDotColor(int)
     */
    @ColorInt
    public int getUnselectedDotColor() {
        return unselectedDotColor;
    }

    /**
     * Set the current color for unselected indicator dots.
     *
     * @param color The new unselected dot color to use.
     *
     * @see #getUnselectedDotColor()
     */
    public void setUnselectedDotColor(@ColorInt int color) {
        unselectedDotColor = color;
        for (IndicatorDotView indicatordot : indicatorDots) {
            indicatordot.setColor(color);
            indicatordot.invalidate();
        }
    }

    /**
     * Get the current color for selected indicator dots.
     *
     * @return The selected dot color.
     *
     * @see #setUnselectedDotColor(int)
     */
    @ColorInt
    public int getSelectedDotColor() {
        return selectedDotColor;
    }

    /**
     * Set the current color for selected indicator dots.
     *
     * @param color The new selected dot color to use.
     *
     * @see #getSelectedDotColor()
     */
    public void setSelectedDotColor(@ColorInt int color) {
        selectedDotColor = color;
        if (selectedDot != null) {
            selectedDot.setColor(color);
            selectedDot.invalidate();
        }
    }

    /**
     * Get the current animation style used for page changes.
     *
     * @return The {@link AnimationStyle} currently being used when the indicator changes pages.
     *
     * @see #setAnimationStyle(int)
     */
    @AnimationStyle
    public int getAnimationStyle() {
        return animationStyle;
    }

    /**
     * Set a new page change animation style for the indicator.
     *
     * @param animationStyle The animation to use when the ViewPager page is changed.
     *                       Supported values can be found in {@link AnimationStyle}.
     *
     * @see #getAnimationStyle()
     */
    public void setAnimationStyle(@AnimationStyle int animationStyle) {
        this.animationStyle = animationStyle;
        invalidate();
        requestLayout();
    }

    //endregion
}

package com.itsronald.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static com.itsronald.widget.IndicatorDotView.DEFAULT_DOT_COLOR;
import static com.itsronald.widget.IndicatorDotView.DEFAULT_DOT_RADIUS_DIP;
import static com.itsronald.widget.ViewPagerIndicator.DEFAULT_DOT_PADDING_DIP;


/**
 * Reproduces the material animation in which the following occurs:
 *
 * 1. Two dots join to form a path.
 * 2. Starting from one of the original two dot positions, the path shinks toward the other dot.
 * 3. Only one of the original two dots remains.
 *
 * Before starting this view's animation, its two dots should invisibly replace two adjacent dots
 * on the indicator itself.
 */
public class IndicatorDotPathView extends ViewGroup {

    static final long PATH_STRETCH_ANIM_DURATION = 300;

    @Px
    private int dotPadding;
    @Px
    private int dotRadius;

    @NonNull
    private final IndicatorDotView startDot;
    @NonNull
    private final IndicatorDotView endDot;

    /** Portion of the path that stretches out from startDot toward endDot. */
    @NonNull
    private final DotPathSegment startPathSegment;
    /** Portion of the path that stretches out from endDot toward startDot. */
    @NonNull
    private final DotPathSegment endPathSegment;
    /** Portion of the path that grows from where startPathDot and endPathDot meet. */
    @NonNull
    private final ImageView pathCenter;
    @NonNull
    private final ShapeDrawable centerPathShape = new ShapeDrawable(new RectShape());;

    public IndicatorDotPathView(@NonNull Context context) {
        super(context);

        final float scale = context.getResources().getDisplayMetrics().density;
        this.dotPadding = (int) (DEFAULT_DOT_PADDING_DIP * scale + 0.5);
        this.dotRadius = (int) (DEFAULT_DOT_RADIUS_DIP * scale + 0.5);

        this.startDot = new IndicatorDotView(context);
        this.endDot = new IndicatorDotView(context);
        this.startPathSegment = new DotPathSegment(context);
        this.endPathSegment = new DotPathSegment(context);

        this.pathCenter = new ImageView(context);
        this.pathCenter.setImageDrawable(centerPathShape);

        init(context, DEFAULT_DOT_COLOR);
    }

    public IndicatorDotPathView(@NonNull Context context,
                                @ColorInt int dotColor,
                                @Px int dotPadding,
                                @Px int dotRadius) {
        this(context);

        this.dotPadding = dotPadding;
        this.dotRadius = dotRadius;

        init(context, dotColor);
    }

    public IndicatorDotPathView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        final float scale = context.getResources().getDisplayMetrics().density;
        this.dotPadding = (int) (DEFAULT_DOT_PADDING_DIP * scale + 0.5);
        this.dotRadius = (int) (DEFAULT_DOT_RADIUS_DIP * scale + 0.5);

        this.startDot = new IndicatorDotView(context);
        this.endDot = new IndicatorDotView(context);
        this.startPathSegment = new DotPathSegment(context);
        this.endPathSegment = new DotPathSegment(context);

        this.pathCenter = new ImageView(context);
        this.pathCenter.setImageDrawable(centerPathShape);

        init(context, DEFAULT_DOT_COLOR);
    }

    public IndicatorDotPathView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final float scale = context.getResources().getDisplayMetrics().density;
        this.dotPadding = (int) (DEFAULT_DOT_PADDING_DIP * scale + 0.5);
        this.dotRadius = (int) (DEFAULT_DOT_RADIUS_DIP * scale + 0.5);

        this.startDot = new IndicatorDotView(context);
        this.endDot = new IndicatorDotView(context);
        this.startPathSegment = new DotPathSegment(context);
        this.endPathSegment = new DotPathSegment(context);

        this.pathCenter = new ImageView(context);
        this.pathCenter.setImageDrawable(centerPathShape);

        init(context, DEFAULT_DOT_COLOR);
    }

    private void init(@NonNull Context context,
                      @ColorInt int dotColor) {
        final LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(startDot, -1, layoutParams);
        addView(endDot, -1, layoutParams);
        addView(startPathSegment, -1, layoutParams);
        addView(endPathSegment, -1, layoutParams);
        addView(pathCenter, -1, layoutParams);
        pathCenter.setVisibility(GONE);

        setDotColor(dotColor);
        setDotPadding(dotPadding);
        setDotRadius(dotRadius);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Layout children, starting from the left.
        final int dotDiameter = 2 * dotRadius;
        final int top = getPaddingTop();
        final int bottom = top + dotDiameter;
        int left = getPaddingLeft();

        startDot.layout(left, top, left + dotDiameter, bottom);
        startPathSegment.layout(left, top, left + dotDiameter, bottom);

        left += dotRadius;
        pathCenter.layout(left, top, left + dotPadding + dotDiameter, bottom);

        left += dotRadius + dotPadding;
        endDot.layout(left, top, left + dotDiameter, bottom);
        endPathSegment.layout(left, top, left + dotDiameter, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthPadding = getPaddingLeft() + getPaddingRight();
        final int childWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                widthPadding, LayoutParams.WRAP_CONTENT);
        final int heightPadding = getPaddingTop() + getPaddingBottom();
        final int childHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                heightPadding, LayoutParams.WRAP_CONTENT);

        startDot.measure(childWidthSpec, childHeightSpec);
        startPathSegment.measure(childWidthSpec, childHeightSpec);
        endDot.measure(childWidthSpec, childHeightSpec);
        endPathSegment.measure(childWidthSpec, childHeightSpec);
        pathCenter.measure(childWidthSpec, childHeightSpec);

        // Calculate measurement for this view.
        final int width;
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            final int totalDotWidth = startDot.getMeasuredWidth() + endDot.getMeasuredWidth();
            final int minWidth = ViewCompat.getMinimumWidth(this);
            width = Math.max(minWidth, totalDotWidth + dotPadding + widthPadding);
        }

        final int height;
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            final int indicatorHeight = startDot.getMeasuredHeight();
            final int minHeight = ViewCompat.getMinimumHeight(this);
            height = Math.max(minHeight, indicatorHeight + heightPadding);
        }

        final int childState = ViewCompat.getMeasuredHeightAndState(startDot);
        final int measuredHeight = ViewCompat.resolveSizeAndState(height, heightMeasureSpec,
                childState);
        setMeasuredDimension(width, measuredHeight);
    }

    //region Accessors

    @ColorInt
    int getDotColor() {
        // Relies on the invariant that all subviews are the same color.
        return startDot.getColor();
    }

    void setDotColor(@ColorInt int dotColor) {
        startDot.setColor(dotColor);
        endDot.setColor(dotColor);
        startPathSegment.setColor(dotColor);
        endPathSegment.setColor(dotColor);
        centerPathShape.getPaint().setColor(dotColor);
        invalidate();
    }

    @Px
    int getDotPadding() {
        return dotPadding;
    }

    void setDotPadding(@Px int dotPadding) {
        this.dotPadding = dotPadding;
        centerPathShape.setIntrinsicWidth(dotPadding + 2 * dotRadius);
        invalidate();
        requestLayout();
    }

    @Px
    int getDotRadius() {
        return dotRadius;
    }

    void setDotRadius(@Px int dotRadius) {
        startDot.setRadius(dotRadius);
        endDot.setRadius(dotRadius);
        startPathSegment.setRadius(dotRadius);
        endPathSegment.setRadius(dotRadius);

        final int dotDiameter = 2 * dotRadius;
        centerPathShape.setIntrinsicWidth(dotPadding + dotDiameter);
        centerPathShape.setIntrinsicHeight(dotDiameter);

        invalidate();
        requestLayout();
    }

    //endregion

    public Animator dotPathConnectionAnimator() {
        final Rect startSegmentBounds = viewRectInNeighborCoords(startPathSegment, endPathSegment);
        final Rect endSegmentBounds = viewRectInNeighborCoords(endPathSegment, startPathSegment);

        final int startSegmentToX = endSegmentBounds.centerX() < 0 ?
                endSegmentBounds.left : endSegmentBounds.right;
        final int startSegmentToY = endSegmentBounds.centerY() < 0 ?
                endSegmentBounds.top : endSegmentBounds.bottom;
        final int endSegmentToX = startSegmentBounds.centerX() < 0 ?
                startSegmentBounds.left : startSegmentBounds.right;
        final int endSegmentToY = startSegmentBounds.centerY() < 0 ?
                startSegmentBounds.top : startSegmentBounds.bottom;

        final Animator startSegmentAnimator = startPathSegment
                .stretchAnimator(PATH_STRETCH_ANIM_DURATION, startSegmentToX, startSegmentToY);
        final Animator endSegmentAnimator = endPathSegment
                .stretchAnimator(PATH_STRETCH_ANIM_DURATION, endSegmentToX, endSegmentToY);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(startSegmentAnimator, endSegmentAnimator, centerPathAnimator());
        return animatorSet;
    }

    private Animator centerPathAnimator() {
        final float fromScale = 0f, toScale = 1f;

        ObjectAnimator growAnimator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final PropertyValuesHolder scaleYProperty = PropertyValuesHolder
                    .ofFloat(View.SCALE_Y, fromScale, toScale);
            growAnimator = ObjectAnimator.ofPropertyValuesHolder(pathCenter, scaleYProperty);
        } else {
            growAnimator = ObjectAnimator.ofFloat(pathCenter, "scaleY", fromScale, toScale);
        }
        // Start growing when the two ends of the path meet in the middle.
        final long animationDuration = PATH_STRETCH_ANIM_DURATION / 2;
        growAnimator.setStartDelay(animationDuration);
        growAnimator.setDuration(animationDuration);

        growAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                pathCenter.setVisibility(VISIBLE);
            }
        });

        return growAnimator;
    }

    /**
     *
     * @param view The view whose bounds to offset into neighbor's coords.
     * @param neighbor The view into whose coordinate space to offset view's bounds.
     * @return The bounds of view in neighbor's coordinate space.
     */
    private Rect viewRectInNeighborCoords(@NonNull View view, @NonNull View neighbor) {
        final Rect bounds = new Rect();
        view.getDrawingRect(bounds);
        offsetDescendantRectToMyCoords(view, bounds);
        offsetRectIntoDescendantCoords(neighbor, bounds);
        return bounds;
    }

    private static class DotPathSegment extends IndicatorDotView {

        DotPathSegment(@NonNull Context context) {
            super(context);
        }

        //region Path creation

        /**
         *
         * @param animationDuration How long the animation should take, in milliseconds.
         * @param toX Where to stretch this view horizontally in this view's coordinate space.
         * @param toY Where to stretch this view vertically in this view's coordinate space.
         * @return An animator.
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public Animator stretchAnimator(long animationDuration, final float toX, final float toY) {
            // Since the provided coordinates are in this view's coordinate space, the absolute distance
            // to the coordinate is the value of the coordinate itself.
            final float distanceX = Math.abs(toX) + (toX < 0 ? getWidth() : 0);
            final float distanceY = Math.abs(toY) + (toY < 0 ? getHeight() : 0);

            final float scaleX = distanceX / getWidth();
            final float scaleY = distanceY / getHeight();
            final float originalScale = 1;

            Animator animator;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                final PropertyValuesHolder scaleXProperty =
                        PropertyValuesHolder.ofFloat(View.SCALE_X, originalScale, scaleX);
                final PropertyValuesHolder scaleYProperty =
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, originalScale, scaleY);
                animator = ObjectAnimator.ofPropertyValuesHolder(this, scaleXProperty, scaleYProperty);
            } else {
                final Animator scaleXAnimator =
                        ObjectAnimator.ofFloat(this, "scaleX", originalScale, scaleX);
                final Animator scaleYAnimator =
                        ObjectAnimator.ofFloat(this, "scaleY", originalScale, scaleY);

                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
                animator = animatorSet;
            }

            animator.setDuration(animationDuration);

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setStretchAnimatorPivot(toX, toY);
                }
            });

            return animator;
        }

        private void setStretchAnimatorPivot(float toX, float toY) {
            // Ensure pivot is within the view's bounds.
            float pivotX = getWidth() - Math.max(0,  Math.min(toX, getWidth()));
            float pivotY = getHeight() - Math.max(0, Math.min(toY, getHeight()));

            setPivotX(pivotX);
            setPivotY(pivotY);
        }

        //endregion
    }
}

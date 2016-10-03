package com.itsronald.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

    @NonNull
    private static final String TAG = "ViewPagerIndicator";

    //region ViewPager

    @NonNull
    private final PageListener pageListener = new PageListener();
    @Nullable
    private ViewPager viewPager;
    @Nullable
    private WeakReference<PagerAdapter> pagerAdapterRef;

    //endregion

    //region Indicator Dots

    @NonNull
    private List<IndicatorDotView> indicatorDots = new ArrayList<>();
    private IndicatorDotView selectedDot;   // @NonNull, but initialized in init().
    @Px
    private int dotRadius;
    @ColorInt
    private int unselectedDotColor;
    @ColorInt
    private int selectedDotColor;

    //endregion

    //region State

    private int lastKnownCurrentPage = -1;
    private int lastKnownPositionOffset = -1;

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

        final float scale = getResources().getDisplayMetrics().density;
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

        attributes.recycle();

        selectedDot = new IndicatorDotView(context, null, defStyleAttr, defStyleRes);
        selectedDot.setColor(selectedDotColor);
        selectedDot.setRadius(dotRadius);
    }

    //endregion

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
        // TODO: Implement
        Log.w(TAG, "Unimplemented");

        final int pageCount = pagerAdapter == null ? 0 : pagerAdapter.getCount();
        Log.d(TAG, "Num pages: " + pageCount);

        lastKnownCurrentPage = currentPage;
        Log.d(TAG, "Current page: " + lastKnownCurrentPage);
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
        // TODO: Implement
        Log.w(TAG, "Unimplemented");

        if (currentPage != lastKnownCurrentPage && viewPager != null) {
            updateIndicators(currentPage, viewPager.getAdapter());
        } else if (!forceUpdate && positionOffset == lastKnownPositionOffset) {
            return;
        }
    }

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
            if (positionOffset > 0.5f) {
                // Consider ourselves to be on the next page when we're 50% of the way there.
                position++;
            }
            updateIndicatorPositions(position, positionOffset, false);
        }

        @Override
        public void onPageSelected(int position) {
            if (scrollState == ViewPager.SCROLL_STATE_IDLE
                    && viewPager != null) {
                // Only update the text here if we're not dragging or settling.
                refresh();
            }
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

    @Px
    public int getDotRadius() {
        return dotRadius;
    }

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

    @ColorInt
    public int getUnselectedDotColor() {
        return unselectedDotColor;
    }

    public void setUnselectedDotColor(@ColorInt int color) {
        unselectedDotColor = color;
        for (IndicatorDotView indicatordot : indicatorDots) {
            indicatordot.setColor(color);
            indicatordot.invalidate();
        }
    }

    @ColorInt
    public int getSelectedDotColor() {
        return selectedDotColor;
    }

    public void setSelectedDotColor(@ColorInt int color) {
        selectedDotColor = color;
        if (selectedDot != null) {
            selectedDot.setColor(color);
            selectedDot.invalidate();
        }
    }

    //endregion
}

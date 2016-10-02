package com.itsronald.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 *
 */
public class ViewPagerIndicator extends ViewGroup {

    @Nullable
    private ViewPager viewPager;

    private int pageCount = 0;


    //region Constructors

    public ViewPagerIndicator(Context context) {
        super(context);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //endregion

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    private class PageListener extends DataSetObserver
            implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

        //region ViewPager.OnPageChangeListener

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        //endregion

        //region ViewPager.OnAdapterChangeListener

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

        }

        //endregion
    }


    //region Accessors


    //endregion
}

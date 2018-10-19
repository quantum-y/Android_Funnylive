package com.coco.livestreaming.app.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.ui.adapter.UrlImagePagerAdapter;

/**
 * Created by ssk on 2014/11/12.
 * <p/>
 * A PageIndicator is responsible to show an visual indicator on the total views
 * number and the current visible view.
 */

public class PageIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    private int mIndicatorGap;
    private int mDrawableResIdActive, mDrawableResIdInactive;

    private int mCurrentActiveId = 0;
    private int mCount;

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PageIndicator, 0, 0);
        try {
            mIndicatorGap = typedArray.getDimensionPixelOffset(R.styleable.PageIndicator_indicatorGap, 0);
            mDrawableResIdActive = typedArray.getResourceId(R.styleable.PageIndicator_drawableActive, R.drawable.pager_indicator_active);
            mDrawableResIdInactive = typedArray.getResourceId(R.styleable.PageIndicator_drawableInactive, R.drawable.pager_indicator_inactive);
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Bind the indicator to test2 ViewPager.
     *
     * @param viewPager ViewPager
     */
    public void setViewPager(ViewPager viewPager) {
        if (mViewPager == viewPager) {
            notifyDataSetChanged();
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(this);
        notifyDataSetChanged();

    }

    /**
     * Notify the indicator that the fragment list has changed.
     */
    public void notifyDataSetChanged() {
        removeAllViews();
        final PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter instanceof UrlImagePagerAdapter) {
            mCount = ((UrlImagePagerAdapter) adapter).getRealCount();
        } else {
            mCount = adapter.getCount();
        }
        for (int i = 0; i < mCount; i++) {
            ImageView imageView = new ImageView(mViewPager.getContext());
            imageView.setImageResource(mDrawableResIdInactive);
//            imageView.setScaleX(0.5f);
//            imageView.setScaleY(0.5f);
            addView(imageView);
        }
        setActiveIndex(mViewPager.getCurrentItem());
    }

    void setActiveIndex(int activeId) {
        if (mCurrentActiveId == activeId) {
            return;
        }

        if ((mCurrentActiveId % mCount) < getChildCount()) {
            ((ImageView) getChildAt(mCurrentActiveId % mCount)).setImageResource(mDrawableResIdInactive);
        }
        mCurrentActiveId = activeId;
        ((ImageView) getChildAt(mCurrentActiveId % mCount)).setImageResource(mDrawableResIdActive);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        setActiveIndex(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}

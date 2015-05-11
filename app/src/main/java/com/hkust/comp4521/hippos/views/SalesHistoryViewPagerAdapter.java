package com.hkust.comp4521.hippos.views;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hkust.comp4521.hippos.datastructures.Commons;

import java.util.List;

/**
 * Created by TC on 4/27/2015.
 */
public class SalesHistoryViewPagerAdapter extends PagerAdapter {
    private List<View> mListViews;

    public CharSequence getPageTitle(int position) {
        return Commons.getSalesHistoryTabs()[position];
    }

    public SalesHistoryViewPagerAdapter(List<View> mListViews) {
        this.mListViews = mListViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) 	{
        container.removeView(mListViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListViews.get(position), 0);
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        return  mListViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}

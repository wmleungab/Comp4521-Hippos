package com.hkust.comp4521.hippos.views;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hkust.comp4521.hippos.datastructures.Commons;

import java.util.List;

/**
 * Created by TC on 4/27/2015.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private List<View> mListViews;
    private String[] mTabsName;

    public CharSequence getPageTitle(int position) {
        return mTabsName[position];
    }

    public ViewPagerAdapter(List<View> mListViews, String[] mTabsName) {
        this.mListViews = mListViews;
        this.mTabsName = mTabsName;
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

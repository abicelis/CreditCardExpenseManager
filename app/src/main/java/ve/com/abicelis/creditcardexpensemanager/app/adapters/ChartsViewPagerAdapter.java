package ve.com.abicelis.creditcardexpensemanager.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.app.fragments.ChartCategoryFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.ChartExpenseFragment;

/**
 * Created by abice on 30/1/2017.
 */

public class ChartsViewPagerAdapter extends FragmentStatePagerAdapter {

    //DATA
    private List<String> mTitleList;
    private List<Fragment> mFragmentList;

    public ChartsViewPagerAdapter(FragmentManager fm, List<String> titleList, List<Fragment> fragmentList) {
        super(fm);
        mTitleList = titleList;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}

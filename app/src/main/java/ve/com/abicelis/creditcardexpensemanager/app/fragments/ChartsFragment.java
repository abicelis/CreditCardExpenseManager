package ve.com.abicelis.creditcardexpensemanager.app.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.ChartsViewPagerAdapter;

/**
 * Created by abice on 30/1/2017.
 */

public class ChartsFragment extends Fragment {

    //UI
    private ViewPager mGraphsViewpager;
    private TabLayout mTabLayout;

    //DATA
    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_graphs));
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initLists();

        View rootView = inflater.inflate(R.layout.fragment_charts, container, false);

        ChartsViewPagerAdapter adapter = new ChartsViewPagerAdapter(getActivity().getSupportFragmentManager(), titleList, fragmentList);
        mGraphsViewpager = (ViewPager) rootView.findViewById(R.id.charts_viewpager);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.charts_tab_layout);

        mGraphsViewpager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mGraphsViewpager);

        return rootView;
    }

    private void initLists() {
        titleList.add("DAILY TOTALS");
        titleList.add("BY CATEGORY");
        fragmentList.add(new ChartExpenseFragment());
        fragmentList.add(new ChartCategoryFragment());
    }
}

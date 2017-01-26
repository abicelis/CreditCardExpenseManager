package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.NavigationDrawerFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.ExpenseListFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.OverviewFragment;

public class HomeActivity extends AppCompatActivity {

    //UI
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpToolbar();

        //Load the default fragment (overview_fragment.java)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.home_content_frame, new OverviewFragment()).commit();
        }

    }


    private void setUpToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        //Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Sync drawer-toolbar state
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,  mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_closed);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //Load the NavigationDrawerFragment, pass the drawerLayout
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drawer_fragment);
        drawerFragment.setDrawerLayout(mDrawerLayout);


//        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.home_collapsing);
//        AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(R.id.home_appbar);
//        mToolbar = (Toolbar) rootView.findViewById(R.id.home_toolbar);
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
//                    isShow = true;
//                } else if(isShow) {
//                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
//
//                    isShow = false;
//                }
//            }
//        });
    }

}

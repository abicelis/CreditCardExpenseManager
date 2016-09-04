package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.OverviewFragment;

public class HomeActivity extends AppCompatActivity {

    //UI
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Load the default fragment (overview_fragment.java)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.home_content_frame, new OverviewFragment()).commit();
        }

        //Load the navigationDrawer, hook it (sync its animation state) with the toolbar
//        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drawer_fragment);
//        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawerFragment.setUpDrawer(R.id.nav_drawer_fragment, drawerLayout, toolbar);

    }

}

package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CreateExpenseDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.LineChartFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.NavigationDrawerFragment;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.CreditPeriod;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;
import ve.com.abicelis.creditcardexpensemanager.model.Payment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnDismissListener {

    //Data
    int activeCreditCardId = -1;
    CreditCard activeCreditCard = null;
    ExpenseManagerDAO dao;

    //UI
    LineChartFragment chartFragment;
    RecyclerView recyclerViewExpenses;
    LinearLayoutManager layoutManager;
    ExpensesAdapter adapter;
    Toolbar toolbar;
    FloatingActionMenu fabMenu;
    FloatingActionButton fabNewExpense;
    FloatingActionButton fabNewExpenseCamera;
    SwipeRefreshLayout swipeRefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);
        setContentView(R.layout.activity_home_with_drawer);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.home_chart_container, new LineChartFragment()).commit();
        }

        //TODO: quitar los mocks y las vainas que si get(0) en los credit cards o periods
        //leer de los sharedpreferences el creditcadr active...?
        //stuff like that
        //ah que el dialog de select el active cc refresque esta actividad, o la actividad que sea que este displayed...?


        loadDao();

        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(this, Constants.ACTIVE_CC_ID);
            activeCreditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
        }catch(SharedPreferenceNotFoundException | CreditCardNotFoundException e) {
            if(dao.getCreditCardList().size() > 0) {
                activeCreditCard = dao.getCreditCardList().get(0);
                activeCreditCardId = activeCreditCard.getId();
                SharedPreferencesUtils.setInt(this, Constants.ACTIVE_CC_ID, activeCreditCard.getId());
            } else {
                //There are no credit cards in the system, must create a credit card
                Intent addCCIntent = new Intent(this, AddCreditCardActivity.class);
                startActivity(addCCIntent);
                finish();
            }
        }catch (CreditPeriodNotFoundException e) {
            Toast.makeText(HomeActivity.this, "Megapeo en oncreate, creditperiod not found! Hay que handle esto y crear el credirperiod con un util o algo", Toast.LENGTH_SHORT).show();
        }

        //Handler handler = new Handler();
        //final Runnable r = new Runnable() {
        //    public void run() {
                setUpDrawer();
                setUpExpensesRecyclerView();
        //    }
        //};
        //handler.post(r);


        setUpSwipeRefresh();
        setUpToolbar();
        setUpFab();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        chartFragment = (LineChartFragment) getSupportFragmentManager().findFragmentById(R.id.home_chart_container);
    }


    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getApplicationContext());
    }
    public boolean refreshData() {
        loadDao();
        int oldCount = 0;
        if(activeCreditCard != null)
            oldCount = activeCreditCard.getCreditPeriods().get(0).getExpenses().size();

        try {
            activeCreditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
        }catch (CreditCardNotFoundException | CreditPeriodNotFoundException e) {
            Toast.makeText(this, "Sorry, there has been a problem refreshing the data", Toast.LENGTH_SHORT).show();
        }

        return (activeCreditCard.getCreditPeriods().get(0).getExpenses().size() == oldCount+1);
    }

    public void refreshExpensesAndChart() {
        if(chartFragment != null)
            chartFragment.refreshChartData();
        else
            Toast.makeText(this, "Error on onDismiss, chartFragment == null!", Toast.LENGTH_SHORT).show();

        if(refreshData()) {
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(1, activeCreditCard.getCreditPeriods().get(0).getExpenses().size()-1);
            layoutManager.scrollToPosition(0);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.home_fab_new_expense:
                fabMenu.close(true);
                showCreateExpenseDialog();
                break;
            case R.id.home_fab_new_expense_camera:
                Intent intent = new Intent(this, OcrCreateExpenseActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setUpDrawer() {
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drawer_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drawer_fragment, drawerLayout, toolbar, activeCreditCard, dao);
    }



    private void setUpExpensesRecyclerView() {

        recyclerViewExpenses = (RecyclerView) findViewById(R.id.home_recycler_expenses);

        adapter = new ExpensesAdapter(getApplicationContext(), this, activeCreditCard.getCreditPeriods().get(0).getExpenses());
        recyclerViewExpenses.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewExpenses.setLayoutManager(layoutManager);
    }

    private void setUpSwipeRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.home_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        refreshData();
                                                        adapter.notifyDataSetChanged();
                                                        swipeRefreshLayout.setRefreshing(false);
                                                    }
                                                }
        );
    }

    private void setUpToolbar() {

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_collapsing);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.home_appbar);
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work

                    isShow = false;
                }
            }
        });
    }

    private void setUpFab() {
        fabMenu = (FloatingActionMenu) findViewById(R.id.home_fab_menu);
        fabNewExpense = (FloatingActionButton) findViewById(R.id.home_fab_new_expense);
        fabNewExpenseCamera = (FloatingActionButton) findViewById(R.id.home_fab_new_expense_camera);

        fabNewExpense.setOnClickListener(this);
        fabNewExpenseCamera.setOnClickListener(this);
    }



    private void showCreateExpenseDialog() {
        FragmentManager fm = getSupportFragmentManager();
        CreateExpenseDialogFragment dialog = CreateExpenseDialogFragment.newInstance(getResources().getString(R.string.dialog_create_expense_title), dao);
        dialog.show(fm, "fragment_dialog_create_expense");
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        refreshExpensesAndChart();
    }



}

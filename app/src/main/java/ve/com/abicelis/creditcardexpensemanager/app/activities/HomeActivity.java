package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
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
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.CreditPeriod;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;
import ve.com.abicelis.creditcardexpensemanager.model.Payment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnDismissListener {

    //Data
    CreditCard activeCreditCard = null;
    List<CreditCard> creditCards = new ArrayList<>();
    List<CreditPeriod> creditPeriods = new ArrayList<>();
    List<Expense> expenses = new ArrayList<>();
    List<Payment> payments = new ArrayList<>();
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

        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                getData();
                setUpDrawer();
                setUpExpensesRecyclerView();
            }
        };
        handler.post(r);


        setUpSwipeRefresh();
        setUpToolbar();
        setUpFab();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        chartFragment = (LineChartFragment) getSupportFragmentManager().findFragmentById(R.id.home_chart_container);
    }

    private ExpenseManagerDAO getDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getApplicationContext());
        return dao;
    }

    private void getData() {
        getDao();

        creditCards.clear();
        creditCards.addAll(dao.getCreditCardList());

        if(creditCards.size() > 0)
            activeCreditCard = creditCards.get(0);

        creditPeriods.clear();
        creditPeriods.addAll(dao.getCreditPeriodListFromCard(creditCards.get(0).getId()));

        expenses.clear();
        expenses.addAll(dao.getExpensesFromCreditPeriod(creditPeriods.get(0).getId()));

        payments.clear();
        payments.addAll(dao.getPaymentsFromCreditPeriod(creditPeriods.get(0).getId()));
    }


    public boolean refreshExpenses() {
        getDao();
        int oldCount = expenses.size();

        expenses.clear();
        expenses.addAll(dao.getExpensesFromCreditPeriod(creditPeriods.get(0).getId()));

        return (expenses.size() == oldCount+1);
    }

    public void refreshExpensesAndChart() {
        if(chartFragment != null)
            chartFragment.refreshChartData();
        else
            Toast.makeText(this, "Error on onDismiss, chartFragment == null!", Toast.LENGTH_SHORT).show();

        if(refreshExpenses()) {
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(1, expenses.size()-1);
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

        adapter = new ExpensesAdapter(getApplicationContext(), this, expenses);
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
                                                        refreshExpenses();
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

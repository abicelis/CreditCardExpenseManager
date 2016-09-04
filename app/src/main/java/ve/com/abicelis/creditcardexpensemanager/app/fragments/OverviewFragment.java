package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.OcrCreateExpenseActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CreateExpenseDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.ExpensesViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 3/9/2016.
 */
public class OverviewFragment extends Fragment {

    //Data
    int activeCreditCardId = -1;
    CreditCard activeCreditCard = null;
    List<Expense> creditCardExpenses = new ArrayList<>();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.home_chart_container, new LineChartFragment()).commit();
        }

        loadDao();

        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);
            refreshData();
        }catch(SharedPreferenceNotFoundException e) {
            //This shouldn't happen
            Toast.makeText(getActivity(), "Megapeo en oncreate, SharedPreferenceNotFoundException CreditCardNotFoundException", Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        setUpExpensesRecyclerView(rootView);
        setUpSwipeRefresh(rootView);
        setUpToolbar(rootView);
        setUpFab(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.post(new Runnable() {
            @Override
            public void run() {
                chartFragment = (LineChartFragment) getFragmentManager().findFragmentById(R.id.home_chart_container);
            }
        });
    }

    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }



    private void setUpExpensesRecyclerView(View rootView) {

        recyclerViewExpenses = (RecyclerView) rootView.findViewById(R.id.home_recycler_expenses);

        ExpensesViewHolder.ExpenseDeletedListener listener = new ExpensesViewHolder.ExpenseDeletedListener() {
            @Override
            public void OnExpenseDeleted(int position) {
                try {

                    dao.deleteExpense(creditCardExpenses.get(position).getId());
                    creditCardExpenses.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                    refreshChart();
                }catch (CouldNotDeleteDataException e) {
                    Toast.makeText(getActivity(), "There was an error deleting the expense!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        //TODO: Fix this hack (HomeActivity) getActivity().. maybe use an interface?
        adapter = new ExpensesAdapter(getContext(),getActivity(), creditCardExpenses, listener);
        recyclerViewExpenses.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewExpenses.setLayoutManager(layoutManager);
    }

    private void setUpSwipeRefresh(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.home_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        refreshRecyclerView();
                                                        refreshChart();
                                                        swipeRefreshLayout.setRefreshing(false);
                                                    }
                                                }
        );
    }

    private void setUpToolbar(View rootView) {

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.home_collapsing);
        AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(R.id.home_appbar);
        toolbar = (Toolbar) rootView.findViewById(R.id.home_toolbar);
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

    private void setUpFab(View rootView) {
        fabMenu = (FloatingActionMenu) rootView.findViewById(R.id.home_fab_menu);
        fabNewExpense = (FloatingActionButton) rootView.findViewById(R.id.home_fab_new_expense);
        fabNewExpenseCamera = (FloatingActionButton) rootView.findViewById(R.id.home_fab_new_expense_camera);

        fabNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                showCreateExpenseDialog();
            }
        });
        fabNewExpenseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OcrCreateExpenseActivity.class);
                startActivity(intent);
            }
        });
    }


    public void refreshData() {
        try {
            activeCreditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);

            //Clear the list and refresh it with new data, this must be done so the adapter
            // doesn't lose track of creditCardExpenses object when overwriting
            // activeCreditCard.getCreditPeriods().get(0).getExpenses();
            creditCardExpenses.clear();
            creditCardExpenses.addAll(activeCreditCard.getCreditPeriods().get(0).getExpenses());

        }catch (CreditCardNotFoundException | CreditPeriodNotFoundException e) {
            Toast.makeText(getActivity(), "Sorry, there has been a problem refreshing the data", Toast.LENGTH_SHORT).show();
        }
    }


    public void refreshRecyclerView() {
        loadDao();

        int oldExpensesCount = creditCardExpenses.size();
        refreshData();
        int newExpensesCount = creditCardExpenses.size();


        //TODO: in the future, expenses wont necessarily be added with date=now,
        //TODO: meaning they wont always be added on recyclerview position = 0
        //If a new expense was added
        if(newExpensesCount == oldExpensesCount+1) {
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(1, activeCreditCard.getCreditPeriods().get(0).getExpenses().size()-1);
            layoutManager.scrollToPosition(0);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private void refreshChart(){
        //TODO: this is probably a hack.. maybe a listener is needed here?
        //Refresh chartFragment
        if(chartFragment != null)
            chartFragment.refreshData();
        else
            Toast.makeText(getActivity(), "Error on onDismiss, chartFragment == null!", Toast.LENGTH_SHORT).show();
    }

    private void showCreateExpenseDialog() {
        FragmentManager fm = getFragmentManager();
        CreateExpenseDialogFragment dialog = CreateExpenseDialogFragment.newInstance(getResources().getString(R.string.dialog_create_expense_title), dao, activeCreditCard.getCreditPeriods().get(0).getId());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshRecyclerView();
                refreshChart();
            }
        });
        dialog.show(fm, "fragment_dialog_create_expense");
    }


}

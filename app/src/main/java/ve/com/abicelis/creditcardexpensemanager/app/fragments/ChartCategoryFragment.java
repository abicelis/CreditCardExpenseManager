package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.CreditPeriod;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by abice on 30/1/2017.
 */

public class ChartCategoryFragment extends Fragment {


    //UI
    private PieChartView chart;

    //DATA
    private boolean chartIsVisible = false;
    private int activeCreditCardId;
    private ExpenseManagerDAO dao;
    private PieChartData data;
    CreditPeriod creditPeriod;
    CreditCard creditCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_chart_categories, container, false);
        chart = (PieChartView) rootView.findViewById(R.id.chart);

        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);

        }catch(SharedPreferenceNotFoundException e) {
            //This shouldn't happen
            Toast.makeText(getActivity(), "Megapeo en oncreate, SharedPreferenceNotFoundException CreditCardNotFoundException", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !chartIsVisible) {
            refreshData();
            chart.startDataAnimation(10000);
            chartIsVisible = true;
        }
        else {
        }
    }



    public void refreshData() {

        int numCategories = ExpenseCategory.values().length;
        BigDecimal[] expenseByCategory = new BigDecimal[numCategories];
        List<SliceValue> sliceValues = new ArrayList<>();


        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity());


        //Refresh list from DB
        try {
            creditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
            creditPeriod = creditCard.getCreditPeriods().get(0);
        }catch(CreditCardNotFoundException | CreditPeriodNotFoundException e) {
            Toast.makeText(getContext(), "ERROR getting data for chart", Toast.LENGTH_SHORT).show();
        }

        //Initialize expenseByCategory
        for (int i = 0; i < numCategories; ++i) {
            expenseByCategory[i] = new BigDecimal(0);
        }

        //Iterate through expenses, accumulate expenses by category inside expenseByCategory array
        for (Expense expense : creditPeriod.getExpenses())
             expenseByCategory[expense.getExpenseCategory().getIndex()] = expenseByCategory[expense.getExpenseCategory().getIndex()].add(expense.getAmount());



        for (int i = 0; i < numCategories; ++i) {
            //SliceValue sliceValue = new SliceValue(expenseByCategory[i].floatValue(), ContextCompat.getColor(getContext(), ExpenseCategory.values()[i].getColor() ));
            SliceValue sliceValue = new SliceValue(20, ContextCompat.getColor(getContext(), ExpenseCategory.values()[i].getColor() ));
            sliceValue.setTarget(expenseByCategory[i].floatValue());
            sliceValue.setLabel(ExpenseCategory.values()[i].getFriendlyName());
            sliceValues.add(sliceValue);
        }


        //Setup chart
        data = new PieChartData(sliceValues);
        data.setHasLabels(true);
        //data.setHasLabelsOutside(true);
        chart.setPieChartData(data);
    }
}

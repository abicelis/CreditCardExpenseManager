package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CheckCreditPeriodLimitDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.SelectableCreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.DateUtils;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.app.utils.TextUtils;
import ve.com.abicelis.creditcardexpensemanager.app.views.HorizontalBar;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.DailyExpense;

/**
 * Created by abice on 4/10/2016.
 */

public class OverviewFragment extends Fragment {

    //CONST
    private static final String TAG = OverviewFragment.class.getSimpleName();

    //DATA
    int activeCreditCardId = -1;
    CreditCard activeCreditCard = null;
    ExpenseManagerDAO dao;

    //UI
    SelectableCreditCardViewHolder holder;
    View headerCreditCardContainer;
    HorizontalBar creditDatePeriodBar;
    HorizontalBar creditBalanceBar;
    TextView extraInfo;
    ScrollView scrollViewContainer;
    View errNoCC;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_overview));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDao();

        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);
            try {
                activeCreditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
            }catch (CreditCardNotFoundException e ) {
                Toast.makeText(getActivity(), "Sorry, there was a problem loading the Credit Card", Toast.LENGTH_SHORT).show();
            }catch (CreditPeriodNotFoundException e) {
                createACurrentCreditPeriod();
            }
        }catch(SharedPreferenceNotFoundException e) {
            //This shouldn't happen
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        headerCreditCardContainer = view.findViewById(R.id.list_item_credit_card_container);
        creditDatePeriodBar = (HorizontalBar) view.findViewById(R.id.frag_overview_credit_date_period_bar);
        creditBalanceBar = (HorizontalBar) view.findViewById(R.id.frag_overview_credit_balance_bar);
        extraInfo = (TextView) view.findViewById(R.id.frag_overview_extra_info);
        scrollViewContainer = (ScrollView) view.findViewById(R.id.frag_overview_body_scroll_view_container);
        errNoCC = view.findViewById(R.id.frag_overview_err_no_cc);

        refreshUI();

        return view;
    }

    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }


    private void refreshUI() {
        loadDao();

        //Hide all
        scrollViewContainer.setVisibility(View.GONE);
        headerCreditCardContainer.setVisibility(View.GONE);
        errNoCC.setVisibility(View.GONE);

        if (activeCreditCard != null) {
            try {

                scrollViewContainer.setVisibility(View.VISIBLE);
                headerCreditCardContainer.setVisibility(View.VISIBLE);

                /* DatePeriod bar */
                Calendar today = Calendar.getInstance();
                Calendar startDate = activeCreditCard.getCreditPeriods().get(0).getStartDate();
                Calendar endDate = activeCreditCard.getCreditPeriods().get(0).getEndDate();
                int daysBetweenStartAndToday = DateUtils.getDaysBetween(startDate, today);
                int daysInPeriod = activeCreditCard.getCreditPeriods().get(0).getTotalDaysInPeriod();
                int datePeriodPercentage;
                if(daysInPeriod > 0)
                    datePeriodPercentage = (int)(100*((float)daysBetweenStartAndToday/daysInPeriod));
                else
                    datePeriodPercentage = 0;

                creditDatePeriodBar.setProgressPercentage(datePeriodPercentage);
                creditDatePeriodBar.setTextLo(DateUtils.getDayShortMonthString(startDate));
                creditDatePeriodBar.setTextHi(DateUtils.getDayShortMonthString(endDate));
                if(today.getTimeInMillis() <= endDate.getTimeInMillis()) {   //If today falls in viewed creditperiod, show the date.
                    creditDatePeriodBar.setTextBar(DateUtils.getDayShortMonthString(today));
                }

                /* Balance bar */
                int creditLimit = activeCreditCard.getCreditPeriods().get(0).getCreditLimit().toBigInteger().intValue();
                int expensesTotal = activeCreditCard.getCreditPeriods().get(0).getExpensesTotal().toBigInteger().intValue();
                String currencyCode = activeCreditCard.getCurrency().getCode();
                int balancePercentage;
                if(creditLimit > 0)
                    balancePercentage = (int)(100*((float)expensesTotal/creditLimit));
                else
                    balancePercentage = 0;

                creditBalanceBar.setProgressPercentage(balancePercentage);
                creditBalanceBar.setTextHi(creditLimit + " " + currencyCode);
                if(expensesTotal > 0)
                    creditBalanceBar.setTextBar(Integer.toString(expensesTotal) + " " + currencyCode);
                creditBalanceBar.setTextLo("0 " + currencyCode);


                extraInfo.setText(TextUtils.fromHtml(generateExtraInfo()));

                //Setup cc data
                holder = new SelectableCreditCardViewHolder(headerCreditCardContainer);
                holder.setData(getContext(), activeCreditCard, 0);
            }catch (Exception e) {
                Toast.makeText(getActivity(), "Problem refreshing card data", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

        } else {
            errNoCC.setVisibility(View.VISIBLE);
        }
    }

    private String generateExtraInfo() {

//        <string name="fragment_overview_credit_extra_info_spendiest_day">The spendiest day in this period is %1$s, you spent %2$s on $3$s.</string>
//        <string name="fragment_overview_credit_extra_info_spendiest_category">The %1$s category represents %2$d%% if this month\'s expenses.</string>
//                <string name="fragment_overview_credit_extra_info_average_per_day">You spend an average of %1$s a day.</string>
//        <string name="fragment_overview_credit_extra_info_to_spend_average">You have %1$s left to spend, that is an average of %2$s a day.</string>

        List<String> extraInfos = new ArrayList<>();
        String result = "";
        String info1 = getResources().getString(R.string.fragment_overview_credit_extra_info_spendiest_day);
        String info2 = getResources().getString(R.string.fragment_overview_credit_extra_info_spendiest_category);
        String info3 = getResources().getString(R.string.fragment_overview_credit_extra_info_average_per_day);
        String info4 = getResources().getString(R.string.fragment_overview_credit_extra_info_to_spend_average);

        BigDecimal maxDailyExpense = new BigDecimal(0);
        String maxDailyExpenseDate = null;
        List<DailyExpense> dailyExpenses = activeCreditCard.getCreditPeriods().get(0).getDailyExpenses();
        for(DailyExpense de : dailyExpenses) {
            if(de.getAmount().compareTo(maxDailyExpense) == 1) {  //If current larger than max
                maxDailyExpense = de.getAmount();
                maxDailyExpenseDate = de.getFormattedDate();
            }
        }

        if(maxDailyExpenseDate != null)
            extraInfos.add(String.format(Locale.getDefault(), info1, maxDailyExpenseDate, maxDailyExpense.toBigInteger().toString(), activeCreditCard.getCurrency().getCode()));





        List<BigDecimal> expensesByCategory = activeCreditCard.getCreditPeriods().get(0).getExpensesByCategory();
        BigDecimal expenseTotal = activeCreditCard.getCreditPeriods().get(0).getExpensesTotal();
        BigDecimal maxCategory = new BigDecimal(0);
        String maxCategoryName = "";



        if(expenseTotal.compareTo(BigDecimal.ZERO) == 1) {

            for(int i = 0; i < ExpenseCategory.values().length; i++) {
                if(expensesByCategory.get(i).compareTo(maxCategory) == 1) {  //If current larger than max
                    maxCategory = expensesByCategory.get(i);
                    maxCategoryName = ExpenseCategory.getByExpenseCategoryId(i).getFriendlyName();
                }
            }

            BigDecimal percentOfTotalMaxCategory = maxCategory.divide(expenseTotal, 2, RoundingMode.HALF_UP);
            percentOfTotalMaxCategory = percentOfTotalMaxCategory.multiply(new BigDecimal(100));
            extraInfos.add(String.format(Locale.getDefault(), info2, maxCategoryName,  percentOfTotalMaxCategory.toBigInteger().toString()));
        }



        Calendar today = Calendar.getInstance();
        Calendar startDate = activeCreditCard.getCreditPeriods().get(0).getStartDate();
        Calendar endDate = activeCreditCard.getCreditPeriods().get(0).getEndDate();
        int daysBetweenStartAndToday = DateUtils.getDaysBetween(startDate, today);
        int daysBetweenTodayAndEnd = DateUtils.getDaysBetween(today, endDate);
        BigDecimal expensesTotal = activeCreditCard.getCreditPeriods().get(0).getExpensesTotal();
        BigDecimal creditLimit = activeCreditCard.getCreditPeriods().get(0).getCreditLimit();
        BigDecimal creditToSpend = creditLimit.subtract(expensesTotal);
        String currencyCode = activeCreditCard.getCurrency().getCode();

        if(expensesTotal.compareTo(BigDecimal.ZERO) == 1 && daysBetweenStartAndToday > 0) {
            BigDecimal average = expensesTotal.divide(new BigDecimal(daysBetweenStartAndToday), 1, RoundingMode.HALF_UP);
            average = average.setScale(1, RoundingMode.HALF_UP);
            extraInfos.add(String.format(Locale.getDefault(), info3, average.toPlainString(), currencyCode));
        }

        if(creditToSpend.compareTo(BigDecimal.ZERO) == 1 && daysBetweenTodayAndEnd > 0) {
            BigDecimal averageToSpend = creditToSpend.divide(new BigDecimal(daysBetweenTodayAndEnd), 1, RoundingMode.HALF_UP);
            averageToSpend = averageToSpend.setScale(1, RoundingMode.HALF_UP);
            creditToSpend = creditToSpend.setScale(1, RoundingMode.HALF_UP);
            extraInfos.add(String.format(Locale.getDefault(), info4, creditToSpend.toPlainString(), averageToSpend.toPlainString(), currencyCode));
        }





        if(extraInfos.size() > 0) {
            result += "&#8226; " + extraInfos.get(0);
            for (int i = 1; i < extraInfos.size(); i++) {
                result += "<br/>&#8226; " + extraInfos.get(i);
            }
        }

        return result;
    }


    private void createACurrentCreditPeriod() {
        //If there is no current credit period, create one

        try {
            //Get the credit card's currency and the credit period limit of the previous period
            CreditCard cc = dao.getCreditCard(activeCreditCardId);
            BigDecimal previousCreditPeriodLimit = dao.getCreditPeriodListFromCard(activeCreditCardId).get(0).getCreditLimit();

            //Call dialog, ask user if previousCreditPeriodLimit is okay
            FragmentManager fm = getActivity().getSupportFragmentManager();
            CheckCreditPeriodLimitDialogFragment dialog = CheckCreditPeriodLimitDialogFragment.newInstance(
                    dao,
                    cc,
                    previousCreditPeriodLimit);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    try {
                        activeCreditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
                        refreshUI();
                    }catch (Exception e) {
                        Toast.makeText(getActivity(), "Error refreshing credit card data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show(fm, "fragment_dialog_new_credit_period");

        } catch (CreditCardNotFoundException e) {
            Toast.makeText(getActivity(), "Error getting credit card data", Toast.LENGTH_SHORT).show();
        }

    }

}

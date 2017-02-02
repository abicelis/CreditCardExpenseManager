package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CheckCreditPeriodLimitDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.DateUtils;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardLayoutRes;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by abice on 4/10/2016.
 */

public class OverviewFragment extends Fragment {

    //Data
    int activeCreditCardId = -1;
    CreditCard activeCreditCard = null;
    ExpenseManagerDAO dao;

    //UI
    CreditCardViewHolder holder;
    TextView creditLimit;
    TextView creditSpent;
    TextView startEndDates;
    View headerCreditCardContainer;

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
            Toast.makeText(getActivity(), "Megapeo en oncreate, SharedPreferenceNotFoundException CreditCardNotFoundException", Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        creditLimit = (TextView) view.findViewById(R.id.frag_overview_credit_limit);
        creditSpent = (TextView) view.findViewById(R.id.frag_overview_credit_spent);
        startEndDates = (TextView) view.findViewById(R.id.frag_overview_start_end_dates);
        headerCreditCardContainer = view.findViewById(R.id.list_item_credit_card_container);

        refreshUIWithCardData();

        return view;
    }

    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }


    private void refreshUIWithCardData() {
        loadDao();

        if (activeCreditCard != null) {
            try {
                creditLimit.setText(activeCreditCard.getCreditPeriods().get(0).getCreditLimit().toPlainString());
                creditSpent.setText(activeCreditCard.getCreditPeriods().get(0).getExpensesTotal().toPlainString());
                String startEndStr = DateUtils.getShortDateString(activeCreditCard.getCreditPeriods().get(0).getStartDate()) + "\n\r " +
                        DateUtils.getShortDateString(activeCreditCard.getCreditPeriods().get(0).getEndDate());
                startEndDates.setText(startEndStr);
            }catch (Exception e) {
                Toast.makeText(getActivity(), "Problem refreshing card data", Toast.LENGTH_SHORT).show();
            }

            //Setup cc data
            holder = new CreditCardViewHolder(headerCreditCardContainer);
            holder.setData(getContext(), CreditCardLayoutRes.LAYOUT_BIG, activeCreditCard, 0);
        }
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
                        refreshUIWithCardData();
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

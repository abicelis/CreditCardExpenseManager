package ve.com.abicelis.creditcardexpensemanager.app.dialogs;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.CreditCardAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.ExpenseListFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class SelectCreditCardDialogFragment extends AppCompatDialogFragment {

    //Constants
    private static final String TAG = "SelectCCDialogFrag";

    //UI
    private DialogInterface.OnDismissListener mOnDismissListener = null;
    private RecyclerView mRecyclerView;
    private CreditCardAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    //DATA
    private List<CreditCard> mCreditCardList;
    private CreditCard selectedCreditCard = null;


    public SelectCreditCardDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SelectCreditCardDialogFragment newInstance(List<CreditCard> creditCardList) {
        SelectCreditCardDialogFragment frag = new SelectCreditCardDialogFragment();
        frag.setCreditCardList(creditCardList);
        return frag;
    }

    private void setCreditCardList(List<CreditCard> creditCardList) {
        mCreditCardList = creditCardList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_select_credit_card, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpCCRecyclerView(view);
    }


    private void setUpCCRecyclerView(View view) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.dialog_select_cc_recycler);

        CreditCardViewHolder.CreditCardSelectedListener ccSelectedListener = new CreditCardViewHolder.CreditCardSelectedListener() {
            @Override
            public void OnCreditCardSelected(CreditCard creditCard) {

                SharedPreferencesUtils.setInt(getContext(), Constants.ACTIVE_CC_ID, creditCard.getId());
                getFragmentManager().beginTransaction().replace(R.id.home_content_frame, new ExpenseListFragment()).commit();
                dismiss();
            }
        };

        mAdapter = new CreditCardAdapter(getContext().getApplicationContext(), mCreditCardList, ccSelectedListener);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

}


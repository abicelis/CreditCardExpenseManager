package ve.com.abicelis.creditcardexpensemanager.app.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Locale;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 10/8/2016.
 */
public class CheckCreditPeriodLimitDialogFragment extends AppCompatDialogFragment implements View.OnClickListener  {

    //CONSTS
    private static String TAG_ARGS_CREDIT_CARD = "TAG_ARGS_CREDIT_CARD";
    private static String TAG_ARGS_PREVIOUS_CREDIT_LIMIT = "TAG_ARGS_PREVIOUS_CREDIT_LIMIT";

    //DB
    private ExpenseManagerDAO mDao;

    //UI
    private DialogInterface.OnDismissListener mOnDismissListener;
    private TextView mCreditPeriodLimitTitle;
    private EditText mCreditPeriodLimitAmount;
    private Button mCreateButton;

    //DATA
    private CreditCard mCreditCard;
    private BigDecimal mPreviousCreditPeriodLimit;


    public CheckCreditPeriodLimitDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static CheckCreditPeriodLimitDialogFragment newInstance(ExpenseManagerDAO dao, CreditCard creditCard, BigDecimal previousCreditPeriodLimit) {
        CheckCreditPeriodLimitDialogFragment frag = new CheckCreditPeriodLimitDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(TAG_ARGS_CREDIT_CARD, creditCard);
        args.putSerializable(TAG_ARGS_PREVIOUS_CREDIT_LIMIT, previousCreditPeriodLimit);
        frag.setArguments(args);
        frag.setDao(dao);
        return frag;
    }

    public void setDao(ExpenseManagerDAO dao) {
        mDao = dao;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_new_credit_period, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.dialog_new_credit_period_title);
        getDialog().setCancelable(false);

        // Fetch arguments from bundle and set title
        mCreditCard = (CreditCard) getArguments().getSerializable(TAG_ARGS_CREDIT_CARD);
        mPreviousCreditPeriodLimit = (BigDecimal) getArguments().getSerializable(TAG_ARGS_PREVIOUS_CREDIT_LIMIT);

        if(mPreviousCreditPeriodLimit == null || mCreditCard == null) {
            Toast.makeText(getActivity(), "Error, wrong arguments passed. Dismissing dialog.", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        // Get fields from view
        mCreditPeriodLimitTitle = (TextView) view.findViewById(R.id.dialog_new_credit_period_limit_title);
        mCreditPeriodLimitAmount = (EditText) view.findViewById(R.id.dialog_new_credit_period_limit_amount);
        mCreateButton = (Button) view.findViewById(R.id.dialog_new_credit_period_button_create);

        // Set onClick listener
        mCreateButton.setOnClickListener(this);

        mCreditPeriodLimitAmount.setText(mPreviousCreditPeriodLimit.toPlainString());
        mCreditPeriodLimitTitle.setText(String.format(
                Locale.getDefault(),
                getResources().getText(R.string.dialog_new_credit_period_limit_title).toString(),
                mCreditCard.getCurrency().getCode()));

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.dialog_new_credit_period_button_create:

                //Verify the credit limit value
                String amount = mCreditPeriodLimitAmount.getText().toString();
                if(amount.equals("") || new BigDecimal(amount).compareTo(new BigDecimal(0)) == 0) {
                    Toast.makeText(getContext(), getResources().getString(R.string.dialog_new_credit_period_error_bad_amount), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    mDao.insertCurrentCreditPeriod(mCreditCard.getId(), mCreditCard.getClosingDay(), new BigDecimal(amount));
                    Toast.makeText(getActivity(), "Credit Period successfully created", Toast.LENGTH_SHORT).show();
                }catch (CouldNotInsertDataException e) {
                    Toast.makeText(getActivity(), "Could not create Credit Period", Toast.LENGTH_SHORT).show();
                }

                dismiss();
                break;
        }
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

}

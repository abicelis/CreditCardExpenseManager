package ve.com.abicelis.creditcardexpensemanager.app.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDbHelper;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseType;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 9/8/2016.
 */
public class CreateExpenseDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    //DB
    private ExpenseManagerDAO mDao;

    //UI
    private EditText mAmountText;
    private EditText mDescriptionText;
    private Button mCancelButton;
    private Button mCreateButton;

    public CreateExpenseDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateExpenseDialogFragment newInstance(String title, ExpenseManagerDAO dao) {
        CreateExpenseDialogFragment frag = new CreateExpenseDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
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
        return inflater.inflate(R.layout.dialog_create_expense, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get fields from view
        mAmountText = (EditText) view.findViewById(R.id.dialog_expense_edit_amount);
        mDescriptionText = (EditText) view.findViewById(R.id.dialog_expense_edit_description);
        mCancelButton = (Button) view.findViewById(R.id.dialog_expense_button_cancel);
        mCreateButton = (Button) view.findViewById(R.id.dialog_expense_button_create);

        // Set onClick listeners
        mCancelButton.setOnClickListener(this);
        mCreateButton.setOnClickListener(this);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        // Show soft keyboard automatically and request focus to field
        mAmountText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.dialog_expense_button_create:
                Expense expense = new Expense(1, mDescriptionText.getText().toString(), new byte[0],
                        new BigDecimal(mAmountText.getText().toString()), Currency.VEF,
                        Calendar.getInstance(), ExpenseCategory.CLOTHING, ExpenseType.ORDINARY);

                try {
                    mDao.insertExpense(0, expense);
                    dismiss();
                } catch (CouldNotInsertDataException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.dialog_expense_button_cancel:
                this.dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}

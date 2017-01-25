package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.ExpenseDetailActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.DateUtils;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 7/8/2016.
 */
public class ExpensesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public ExpensesAdapter mAdapter;
    private Fragment mFragment;
    private ExpenseDeletedListener mListener = null;

    //UI
    private RelativeLayout mContainer;
    private TextView mAmount;
    private TextView mDescription;
    private TextView mDate;
    private ImageView mImage;
    private TextView mCategory;
    private TextView mType;
    private ImageView mDeleteIcon;
    //private ImageView mEditIcon;

    //DATA
    private Expense mCurrent;
    private int mCreditPeriodId;
    private int mExpensePosition;

    public ExpensesViewHolder(View itemView) {
        super(itemView);

        mContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_expenses_container);
        mAmount = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_amount);
        mDescription = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_description);
        mDate = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_date);
        mImage = (ImageView) itemView.findViewById(R.id.list_item_expenses_img_image);
        mCategory = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_category);
        mType = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_type);
        mDeleteIcon = (ImageView) itemView.findViewById(R.id.list_item_expenses_img_delete);
        //mEditIcon = (ImageView) itemView.findViewById(R.id.list_item_expenses_img_edit);
    }

    public void setData(ExpensesAdapter adapter, Fragment fragment, Expense current, int creditPeriodId, int position) {
        mAdapter = adapter;
        mFragment = fragment;
        this.mCurrent = current;
        mCreditPeriodId = creditPeriodId;
        this.mExpensePosition = position;

        this.mAmount.setText(current.getAmount().toPlainString() + " " + current.getCurrency().getCode());
        this.mDescription.setText(current.getDescription());
        this.mDate.setText(DateUtils.getRelativeTimeSpanString(current.getDate()));

        this.mCategory.setText(current.getExpenseCategory().getFriendlyName());
        ((GradientDrawable)this.mCategory.getBackground()).setColor(ContextCompat.getColor(mFragment.getContext(), current.getExpenseCategory().getColor()));

        this.mType.setText(current.getExpenseType().getShortName());
        ((GradientDrawable)this.mType.getBackground()).setColor(ContextCompat.getColor(mFragment.getContext(), current.getExpenseType().getColor()));


        if(current.getThumbnail() != null && current.getThumbnail().length > 0)
            this.mImage.setImageBitmap(ImageUtils.getBitmap(current.getThumbnail()));
        else
            this.mImage.setImageResource(R.drawable.icon_expense);

    }

    public void setListeners() {
        mDeleteIcon.setOnClickListener(this);
        //mEditIcon.setOnClickListener(this);
        mContainer.setOnClickListener(this);
    }

    public void setOnExpenseDeletedListener(ExpenseDeletedListener listener) {
        mListener = listener;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.list_item_expenses_container:
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(mImage, mFragment.getResources().getString(R.string.transition_name_expense_detail_image));
                //pairs[1] = new Pair<View, String>(mAmount,  mActivity.getResources().getString(R.string.transition_name_expense_detail_amount));
                //pairs[2] = new Pair<View, String>(mDescription,  mActivity.getResources().getString(R.string.transition_name_expense_detail_description));
                //pairs[3] = new Pair<View, String>(mDate,  mActivity.getResources().getString(R.string.transition_name_expense_detail_date));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mFragment.getActivity(), pairs);
                Intent expenseDetailIntent = new Intent(mFragment.getActivity(), ExpenseDetailActivity.class);
                expenseDetailIntent.putExtra(ExpenseDetailActivity.INTENT_EXTRAS_EXPENSE, mCurrent);
                expenseDetailIntent.putExtra(ExpenseDetailActivity.INTENT_EXTRAS_CREDIT_PERIOD_ID, mCreditPeriodId);
                mFragment.startActivityForResult(expenseDetailIntent, Constants.EXPENSE_DETAIL_ACTIVITY_REQUEST_CODE, options.toBundle());

                break;

            case R.id.list_item_expenses_img_delete:

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            mListener.OnExpenseDeleted(mExpensePosition);
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getActivity());
                builder.setTitle(R.string.dialog_delete_expense_title)
                        .setMessage(R.string.dialog_delete_expense_message)
                        .setPositiveButton((R.string.dialog_delete_expense_button_yes), listener)
                        .setNegativeButton((R.string.dialog_delete_expense_button_no), null)
                        .show();
                break;

            //case R.id.list_item_expenses_img_edit:
            //    break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public interface ExpenseDeletedListener {
        void OnExpenseDeleted(int position);
    }
}

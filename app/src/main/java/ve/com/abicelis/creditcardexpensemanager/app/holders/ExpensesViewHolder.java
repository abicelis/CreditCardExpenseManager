package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.ExpenseDetailActivity;
import ve.com.abicelis.creditcardexpensemanager.app.activities.HomeActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 7/8/2016.
 */
public class ExpensesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ExpensesAdapter mAdapter;
    private Context mContext;
    private HomeActivity mActivity;

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
    private Expense current;
    private int position;

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

    public void setData(ExpensesAdapter adapter, Context context, HomeActivity activity, Expense current, int position) {
        this.mAdapter = adapter;
        this.mContext = context;
        this.mActivity = activity;
        this.current = current;
        this.position = position;

        this.mAmount.setText(current.getAmount().toPlainString() + " " + current.getCurrency().getCode());
        this.mDescription.setText(current.getDescription());
        this.mDate.setText(current.getShortDateString());

        this.mCategory.setText(current.getExpenseCategory().getFriendlyName());
        ((GradientDrawable)this.mCategory.getBackground()).setColor(ContextCompat.getColor(mContext, current.getExpenseCategory().getColor()));

        this.mType.setText(current.getExpenseType().getShortName());
        ((GradientDrawable)this.mType.getBackground()).setColor(ContextCompat.getColor(mContext, current.getExpenseType().getColor()));


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


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.list_item_expenses_container:
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(mImage, mActivity.getResources().getString(R.string.transition_name_expense_detail_image));
                //pairs[1] = new Pair<View, String>(mAmount,  mActivity.getResources().getString(R.string.transition_name_expense_detail_amount));
                //pairs[2] = new Pair<View, String>(mDescription,  mActivity.getResources().getString(R.string.transition_name_expense_detail_description));
                //pairs[3] = new Pair<View, String>(mDate,  mActivity.getResources().getString(R.string.transition_name_expense_detail_date));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, pairs);
                Intent expenseDetailIntent = new Intent(mActivity, ExpenseDetailActivity.class);
                expenseDetailIntent.putExtra("expense", current);
                mActivity.startActivity(expenseDetailIntent, options.toBundle());

                break;

            case R.id.list_item_expenses_img_delete:

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {
                            new ExpenseManagerDAO(mContext).deleteExpense(current.getId());
                            mAdapter.removeExpense(position);
                            mAdapter.notifyItemRemoved(position);
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                            mActivity.refreshExpensesAndChart();
                        }catch (CouldNotDeleteDataException e) {
                            Toast.makeText(mActivity, "There was an error deleting this expense!", Toast.LENGTH_SHORT).show();
                        }

                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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
}

package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.ExpenseDetailActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 7/8/2016.
 */
public class ExpensesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ExpensesAdapter mAdapter;
    private Context mContext;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private TextView mAmount;
    private TextView mDescription;
    private TextView mDate;
    private ImageView mImage;
    private ImageView mDeleteIcon;
    private ImageView mEditIcon;

    //DATA
    private Expense current;
    private int position;

    public ExpensesViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.list_item_expenses_container);
        mAmount = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_amount);
        mDescription = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_description);
        mDate = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_date);
        mImage = (ImageView) itemView.findViewById(R.id.list_item_expenses_img_image);
        mDeleteIcon = (ImageView) itemView.findViewById(R.id.list_item_expenses_img_delete);
        mEditIcon = (ImageView) itemView.findViewById(R.id.list_item_expenses_img_edit);
    }

    public void setData(ExpensesAdapter adapter, Context context, Activity activity, Expense current, int position) {
        this.mAdapter = adapter;
        this.mContext = context;
        this.mActivity = activity;
        this.current = current;
        this.position = position;

        this.mAmount.setText(current.getAmount().toPlainString() + " " + current.getCurrency().getCode());
        this.mDescription.setText(current.getDescription());
        this.mDate.setText(current.getDate().getTime().toString());


        if(current.getThumbnail().length > 0)
            this.mImage.setImageBitmap(ImageUtils.getBitmap(current.getThumbnail()));
        else
            this.mImage.setImageResource(R.drawable.expense_icon);

    }

    public void setListeners() {
        mDeleteIcon.setOnClickListener(this);
        mEditIcon.setOnClickListener(this);
        mContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.list_item_expenses_container:
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(mImage, mActivity.getResources().getString(R.string.transition_name_expense_detail_image));
                pairs[1] = new Pair<View, String>(mAmount,  mActivity.getResources().getString(R.string.transition_name_expense_detail_amount));
                pairs[2] = new Pair<View, String>(mDescription,  mActivity.getResources().getString(R.string.transition_name_expense_detail_description));
                pairs[3] = new Pair<View, String>(mDate,  mActivity.getResources().getString(R.string.transition_name_expense_detail_date));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, pairs);
                Intent expenseDetailIntent = new Intent(mActivity, ExpenseDetailActivity.class);
                expenseDetailIntent.putExtra("expense", current);
                mActivity.startActivity(expenseDetailIntent, options.toBundle());

                break;
            default:
                Toast.makeText(mDeleteIcon.getContext(), "You clicked on something on list_item_expenses!", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}

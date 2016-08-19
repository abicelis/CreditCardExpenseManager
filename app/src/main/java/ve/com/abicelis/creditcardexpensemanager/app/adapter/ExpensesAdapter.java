package ve.com.abicelis.creditcardexpensemanager.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holder.ExpensesViewHolder;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 7/8/2016.
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesViewHolder> {

    private List<Expense> mExpenses;
    private LayoutInflater mInflater;
    private Context mContext;

    public ExpensesAdapter(Context context, List<Expense> expenses) {
        mContext = context;
        mExpenses = expenses;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ExpensesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.list_item_expenses, parent, false);
        return new ExpensesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpensesViewHolder holder, int position) {
        Expense current = mExpenses.get(position);
        holder.setData(this, mContext, current, position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }
}

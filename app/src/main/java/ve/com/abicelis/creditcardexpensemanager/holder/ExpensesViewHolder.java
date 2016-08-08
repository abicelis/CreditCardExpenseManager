package ve.com.abicelis.creditcardexpensemanager.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.adapter.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 7/8/2016.
 */
public class ExpensesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    //UI
    private ExpensesAdapter adapter;
    private TextView amount;
    private TextView description;
    private ImageView delete;
    private ImageView edit;

    //Data
    private Expense current;
    private int position;

    public ExpensesViewHolder(View itemView) {
        super(itemView);

        amount = (TextView) itemView.findViewById(R.id.txt_amount);
        description = (TextView) itemView.findViewById(R.id.txt_description);
        delete = (ImageView) itemView.findViewById(R.id.img_delete);
        edit = (ImageView) itemView.findViewById(R.id.img_edit);
    }

    public void setData(ExpensesAdapter adapter, Expense current, int position) {
        this.adapter = adapter;
        this.current = current;
        this.position = position;

        this.amount.setText(current.getAmount().toPlainString() + " " + current.getCurrency().getCode());
        this.description.setText(current.getDescription());
    }

    public void setListeners() {
        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(delete.getContext(), "You clicked on something!", Toast.LENGTH_SHORT).show();
    }
}

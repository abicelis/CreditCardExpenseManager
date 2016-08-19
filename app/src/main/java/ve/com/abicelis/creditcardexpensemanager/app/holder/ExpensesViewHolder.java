package ve.com.abicelis.creditcardexpensemanager.app.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapter.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 7/8/2016.
 */
public class ExpensesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ExpensesAdapter mAdapter;
    private Context mContext;

    //UI
    private TextView amount;
    private TextView description;
    private TextView date;
    private ImageView expenseImage;
    private ImageView delete;
    private ImageView edit;

    //DATA
    private Expense current;
    private int position;

    public ExpensesViewHolder(View itemView) {
        super(itemView);

        amount = (TextView) itemView.findViewById(R.id.txt_amount);
        description = (TextView) itemView.findViewById(R.id.txt_description);
        date = (TextView) itemView.findViewById(R.id.txt_date);
        expenseImage = (ImageView) itemView.findViewById(R.id.img_image);
        delete = (ImageView) itemView.findViewById(R.id.img_delete);
        edit = (ImageView) itemView.findViewById(R.id.img_edit);
    }

    public void setData(ExpensesAdapter adapter, Context context, Expense current, int position) {
        this.mAdapter = adapter;
        this.mContext = context;
        this.current = current;
        this.position = position;

        this.amount.setText(current.getAmount().toPlainString() + " " + current.getCurrency().getCode());
        this.description.setText(current.getDescription());
        this.date.setText(current.getDate().getTime().toString());

        if(current.getImage().length > 0)
            this.expenseImage.setImageBitmap(ImageUtils.getBitmap(current.getImage()));
        else
            this.expenseImage.setImageResource(R.drawable.expense_icon);
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

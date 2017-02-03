package ve.com.abicelis.creditcardexpensemanager.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holders.SelectableCreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class SelectableCreditCardAdapter extends RecyclerView.Adapter<SelectableCreditCardViewHolder>  {

    private List<CreditCard> mCreditCards;
    private LayoutInflater mInflater;
    private Context mContext;
    private SelectableCreditCardViewHolder.SelectableCreditCardSelectedListener mCCSelectedListener;

    public SelectableCreditCardAdapter(Context context, List<CreditCard> creditCards) {
        mContext = context;
        mCreditCards = creditCards;
        mInflater = LayoutInflater.from(context);
    }

    public void setCreditCardSelectedListener(SelectableCreditCardViewHolder.SelectableCreditCardSelectedListener ccSelectedListener) {
        mCCSelectedListener = ccSelectedListener;
    }


    @Override
    public SelectableCreditCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_credit_card_selectable, parent, false);
        return new SelectableCreditCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectableCreditCardViewHolder holder, int position) {
        CreditCard current = mCreditCards.get(position);
        holder.setData(mContext, current, position);

        if(mCCSelectedListener != null) {
            holder.setListeners();
            holder.setOnCreditCardSelectedListener(mCCSelectedListener);
        }
        else
            Toast.makeText(mContext, "Warning: mCCSelectedListener == null!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mCreditCards.size();
    }
}

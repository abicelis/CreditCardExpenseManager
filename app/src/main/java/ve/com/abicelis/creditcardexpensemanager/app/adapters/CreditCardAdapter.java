package ve.com.abicelis.creditcardexpensemanager.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardViewHolder>  {

    private List<CreditCard> mCreditCards;
    private LayoutInflater mInflater;
    private Context mContext;
    private CreditCardViewHolder.CreditCardSelectedListener mCCSelectedListener;
    public CreditCardAdapter(Context context, List<CreditCard> creditCards, CreditCardViewHolder.CreditCardSelectedListener ccSelectedListener) {
        mContext = context;
        mCreditCards = creditCards;
        mCCSelectedListener = ccSelectedListener;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public CreditCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.list_item_credit_card, parent, false);
        return new CreditCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreditCardViewHolder holder, int position) {
        CreditCard current = mCreditCards.get(position);
        holder.setData(mContext, current, position);
        holder.setListeners();
        holder.setOnCreditCardSelectedListener(mCCSelectedListener);
    }

    @Override
    public int getItemCount() {
        return mCreditCards.size();
    }
}

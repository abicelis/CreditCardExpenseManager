package ve.com.abicelis.creditcardexpensemanager.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardLayoutRes;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardViewHolder>  {

    private List<CreditCard> mCreditCards;
    private LayoutInflater mInflater;
    private Context mContext;
    private CreditCardLayoutRes mLayoutRes;
    private CreditCardViewHolder.CreditCardSelectedListener mCCSelectedListener;

    public CreditCardAdapter(Context context, List<CreditCard> creditCards, CreditCardLayoutRes layoutRes) {
        mContext = context;
        mCreditCards = creditCards;
        mLayoutRes = layoutRes;
        mInflater = LayoutInflater.from(context);
    }

    public void setCreditCardSelectedListener(CreditCardViewHolder.CreditCardSelectedListener ccSelectedListener) {
        mCCSelectedListener = ccSelectedListener;
    }


    @Override
    public CreditCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (mLayoutRes) {
            case LAYOUT_BIG:
                view = mInflater.inflate(R.layout.list_item_credit_card_big, parent, false);
                break;
            case LAYOUT_DETAILED:
                view = mInflater.inflate(R.layout.list_item_credit_card_detailed, parent, false);
                break;
            default:
                Toast.makeText(mContext, "Warning: invalid CreditCardLayoutRes!", Toast.LENGTH_SHORT).show();
        }
        return new CreditCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreditCardViewHolder holder, int position) {
        CreditCard current = mCreditCards.get(position);
        holder.setData(mContext, mLayoutRes, current, position);

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

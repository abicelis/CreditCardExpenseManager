package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class SelectableCreditCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    CreditCardSelectedListener mListener = null;

    //DATA
    private CreditCard mCurrent;
    private int mPosition;

    //UI
    private RelativeLayout container;
    private TextView bankName;
    private TextView alias;
    private TextView currency;
    private TextView cardNumber;
    private TextView creditCardLabel;
    private TextView cardExpirationLabel;
    private TextView cardExpiration;
    private ImageView cardType;
    private ImageView cardChip;


    public SelectableCreditCardViewHolder(View itemView) {
        super(itemView);

        container = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_container);
        bankName = (TextView) itemView.findViewById(R.id.list_item_credit_card_bank_name);
        alias = (TextView) itemView.findViewById(R.id.list_item_credit_card_alias);
        currency = (TextView) itemView.findViewById(R.id.list_item_credit_card_currency);
        cardNumber = (TextView) itemView.findViewById(R.id.list_item_credit_card_number);
        creditCardLabel = (TextView) itemView.findViewById(R.id.list_item_credit_card_label);
        cardExpirationLabel = (TextView) itemView.findViewById(R.id.list_item_credit_card_expiration_label);
        cardExpiration = (TextView) itemView.findViewById(R.id.list_item_credit_card_expiration);
        cardType = (ImageView) itemView.findViewById(R.id.list_item_credit_card_type);
        cardChip = (ImageView) itemView.findViewById(R.id.list_item_credit_card_chip);

        //LAYOUT_DETAILED views


    }


    public void setData(Context context, CreditCard current, int position) {
        mContext = context;
        mCurrent = current;
        mPosition = position;

        bankName.setText(current.getBankName());
        alias.setText(current.getCardAlias());
        cardNumber.setText(current.getCardNumber());
        currency.setText(current.getCurrency().getCode());
        cardExpiration.setText(current.getShortCardExpirationString());

        container.setBackground(current.getCreditCardBackground().getBackgroundDrawable(context));
        bankName.setTextColor(current.getCreditCardBackground().getTextColor(context));
        alias.setTextColor(current.getCreditCardBackground().getTextColor(context));
        currency.setTextColor(current.getCreditCardBackground().getTextColor(context));
        cardNumber.setTextColor(current.getCreditCardBackground().getTextColor(context));
        cardExpiration.setTextColor(current.getCreditCardBackground().getTextColor(context));
        creditCardLabel.setTextColor(current.getCreditCardBackground().getTextColor(context));
        cardExpirationLabel.setTextColor(current.getCreditCardBackground().getTextColor(context));

        switch(current.getCardType()) {
            case AMEX:
                cardType.setImageResource(R.drawable.logo_amex);
                break;
            case DISCOVER:
                cardType.setImageResource(R.drawable.logo_discover);
                break;
            case MASTERCARD:
                cardType.setImageResource(R.drawable.logo_mastercard);
                break;
            case VISA:
                cardType.setImageResource(R.drawable.logo_visa);
                break;
        }


    }

    public void setListeners() {
        container.setOnClickListener(this);
    }

    public void setOnCreditCardSelectedListener(CreditCardSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.list_item_credit_card_container:
                if(mListener != null)
                    mListener.OnCreditCardSelected(mCurrent);
                break;
        }
    }


    public interface CreditCardSelectedListener {
        void OnCreditCardSelected(CreditCard creditCard);
    }

}

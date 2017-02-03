package ve.com.abicelis.creditcardexpensemanager.model;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardBackground;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;

/**
 * Created by Alex on 6/8/2016.
 */
public class CreditCard implements Serializable {

    private int id;
    private String cardAlias;
    private String bankName;
    private String cardNumber;
    private Currency currency;
    private CreditCardType cardType;
    private Calendar cardExpiration;
    private int closingDay;         // Fecha de corte: Fecha de cierre de operaciones del mes, para efectos de registros y cobros.
    private int dueDay;             // Fecha Limite de Pago: Fecha máxima para el próximo pago sin generar incumplimiento e intereses de mora.
    private CreditCardBackground creditCardBackground;


    private Map<Integer, CreditPeriod> creditPeriods;

    public CreditCard(String cardAlias, String bankName, String cardNumber, @NonNull Currency currency, @NonNull CreditCardType cardType, @NonNull Calendar cardExpiration, int closingDay, int dueDay, @NonNull CreditCardBackground creditCardBackground) {
        this.cardAlias = cardAlias;
        this.bankName = bankName;
        this.cardNumber = cardNumber;
        this.currency = currency;
        this.cardType = cardType;

        this.cardExpiration = Calendar.getInstance();
        this.cardExpiration.setTimeZone(cardExpiration.getTimeZone());
        this.cardExpiration.setTimeInMillis(cardExpiration.getTimeInMillis());

        this.dueDay = dueDay;
        this.closingDay = closingDay;
        this.creditCardBackground = creditCardBackground;
    }

    public CreditCard(int id, String cardAlias, String bankName, String cardNumber, @NonNull Currency currency, @NonNull CreditCardType cardType, @NonNull Calendar cardExpiration, int closingDay, int dueDay, @NonNull CreditCardBackground creditCardBackground) {
        this(cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay, creditCardBackground);
        this.id = id;
    }

    public CreditCard(int id, String cardAlias, String bankName, String cardNumber, @NonNull Currency currency, @NonNull CreditCardType cardType, @NonNull Calendar cardExpiration, int closingDay, int dueDay, @NonNull CreditCardBackground creditCardBackground, @NonNull Map<Integer, CreditPeriod> creditPeriods) {
        this(id, cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay, creditCardBackground);
        this.creditPeriods = creditPeriods;
    }



    public int getId() {
        return id;
    }


    public String getCardAlias() {
        return cardAlias;
    }

    public void setCardAlias(String cardAlias) {
        this.cardAlias = cardAlias;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public CreditCardType getCardType() {
        return cardType;
    }

    public void setCardType(CreditCardType cardType) {
        this.cardType = cardType;
    }

    public Calendar getCardExpiration() {
        return cardExpiration;
    }

    public void setCardExpiration(Calendar cardExpiration) {
        this.cardExpiration = Calendar.getInstance();
        this.cardExpiration.setTimeZone(cardExpiration.getTimeZone());
        this.cardExpiration.setTimeInMillis(cardExpiration.getTimeInMillis());
    }

    public String getShortCardExpirationString() {
        if(cardExpiration == null)
            return "-/-";

        SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.getDefault());
        return formatter.format(cardExpiration.getTime());
    }

    public String getLongCardExpirationString() {
        if(cardExpiration == null)
            return "-";

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return formatter.format(cardExpiration.getTime());
    }

    public int getClosingDay() {
        return closingDay;
    }

    public void setClosingDay(int closingDay) {
        this.closingDay = closingDay;
    }

    public int getDueDay() {
        return dueDay;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }

    public CreditCardBackground getCreditCardBackground() {
        return creditCardBackground;
    }

    public Map<Integer, CreditPeriod> getCreditPeriods() {
        return creditPeriods;
    }

    public void setCreditPeriods(Map<Integer, CreditPeriod> creditPeriods) {
        this.creditPeriods = creditPeriods;
    }

    /**
     * Returns a List of Credit Cards based on the number of existing CreditCardBackgrounds, instantiated with default values
     * @return List<CreditCard>
     */
    public static List<CreditCard> getCreditCardBackgroundTypesList(Context context) {
        List<CreditCard> creditCards = new ArrayList<>();

        String defaultAlias = context.getResources().getString(R.string.activity_add_new_cc_alias);
        String defaultBankName = context.getResources().getString(R.string.activity_add_new_cc_bank);
        String defaultCardNumber = context.getResources().getString(R.string.activity_add_new_cc_number);
        Calendar defaultExpiration = Calendar.getInstance();
        defaultExpiration.add(Calendar.YEAR, 5);

        for (CreditCardBackground c : CreditCardBackground.values()) {
            creditCards.add(new CreditCard(defaultAlias, defaultBankName, defaultCardNumber, Currency.USD, CreditCardType.AMEX, defaultExpiration, 0, 0, c));
        }

        return creditCards;
    }

    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " cardAlias=" + cardAlias + "\r\n" +
                " cardNumber=" + cardNumber + "\r\n" +
                " currency=" + currency + "\r\n" +
                " cardType=" + cardType + "\r\n" +
                " cardExpiration=" + cardExpiration + "\r\n" +
                " closingDay=" + closingDay + "\r\n" +
                " dueDay=" + dueDay + "\r\n" +
                " creditPeriods=" + creditPeriods;
    }
}

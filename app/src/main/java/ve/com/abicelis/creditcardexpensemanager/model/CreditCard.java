package ve.com.abicelis.creditcardexpensemanager.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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

    private Map<Integer, CreditPeriod> creditPeriods;

    public CreditCard(String cardAlias, String bankName, String cardNumber, Currency currency, CreditCardType cardType, Calendar cardExpiration, int closingDay, int dueDay) {
        this.cardAlias = cardAlias;
        this.bankName = bankName;
        this.cardNumber = cardNumber;
        this.currency = currency;
        this.cardType = cardType;
        this.cardExpiration = cardExpiration;
        this.dueDay = dueDay;
        this.closingDay = closingDay;
    }

    public CreditCard(int id, String cardAlias, String bankName, String cardNumber, Currency currency, CreditCardType cardType, Calendar cardExpiration, int closingDay, int dueDay) {
        this(cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay);
        this.id = id;
    }

    public CreditCard(int id, String cardAlias, String bankName, String cardNumber, Currency currency, CreditCardType cardType, Calendar cardExpiration, int closingDay, int dueDay, Map<Integer, CreditPeriod> creditPeriods) {
        this(id, cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay);
        this.cardExpiration = Calendar.getInstance();
        this.cardExpiration.setTimeZone(cardExpiration.getTimeZone());
        this.cardExpiration.setTimeInMillis(cardExpiration.getTimeInMillis());
    }




    public int getId() {
        return id;
    }

    public String getCardAlias() {
        return cardAlias;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Currency getCurrency() {
        return currency;
    }

    public CreditCardType getCardType() {
        return cardType;
    }

    public Calendar getCardExpiration() {
        return cardExpiration;
    }

    public String getShortCardExpirationString() {
        SimpleDateFormat yearFormatter = new SimpleDateFormat("yy", Locale.getDefault());
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.getDefault());
        return yearFormatter.format(cardExpiration.getTime()) + "/" + monthFormatter.format(cardExpiration.getTime());
    }

    public int getClosingDay() {
        return closingDay;
    }

    public int getDueDay() {
        return dueDay;
    }

    public Map<Integer, CreditPeriod> getCreditPeriods() {
        return creditPeriods;
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

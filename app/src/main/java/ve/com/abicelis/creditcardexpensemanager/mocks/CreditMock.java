package ve.com.abicelis.creditcardexpensemanager.mocks;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseType;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.CreditPeriod;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;
import ve.com.abicelis.creditcardexpensemanager.model.Payment;

/**
 * Created by Alex on 7/8/2016.
 */
public class CreditMock {

    public static CreditCard getCreditCardMock() {

        Map<Integer, CreditPeriod> creditPeriods = new HashMap<>();

        CreditPeriod cp = getCreditPeriodMock();
        creditPeriods.put(new Integer(0), cp);

        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.YEAR, 1);

        return new CreditCard(1,
                "Mercantil Master Dorada",
                "Mercantil",
                "1234-5678-1234-5678",
                Currency.VEF,
                CreditCardType.MASTERCARD,
                expiry,
                20,
                8,
                creditPeriods);
    }


    private static CreditPeriod getCreditPeriodMock() {

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense (1, "Expense 1", new byte[0], new BigDecimal(300), Currency.VEF, Calendar.getInstance(), ExpenseCategory.CLOTHING, ExpenseType.ORDINARY));
        expenses.add(new Expense (2, "Expense 2", new byte[0], new BigDecimal(600), Currency.VEF, Calendar.getInstance(), ExpenseCategory.CLOTHING, ExpenseType.ORDINARY));
        expenses.add(new Expense (3, "Expense 3", new byte[0], new BigDecimal(700), Currency.VEF, Calendar.getInstance(), ExpenseCategory.CLOTHING, ExpenseType.ORDINARY));

        List<Payment> payments = new ArrayList<>();
        payments.add(new Payment (1, "Payment 1", new BigDecimal(1000), Currency.VEF, Calendar.getInstance()));

        return new CreditPeriod(1,
                CreditPeriod.PERIOD_NAME_LARGEST_MONTH,
                startDate,
                endDate,
                new BigDecimal(60000),
                expenses,
                payments);
    }

}

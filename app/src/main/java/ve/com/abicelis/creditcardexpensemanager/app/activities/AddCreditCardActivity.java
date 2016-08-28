package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 27/8/2016.
 */
public class AddCreditCardActivity extends AppCompatActivity implements View.OnClickListener {

    //TAGS
    private static final String CALENDAR_EXPIRATION_TAG = "1";

    //DATA
    boolean cameFromWelcomeScreen = false;
    Calendar cardExpirationCal = null;
    List<Currency> currencies;
    List<CreditCardType> cardTypes;
    List<Integer> days;

    //UI
    Toolbar toolbar;
    EditText cardBankName;
    EditText cardAlias;
    EditText cardNumber;
    EditText cardExpiration;
    Spinner cardCurrency;
    Spinner cardType;
    Spinner cardClosingDay;
    Spinner cardDueDay;
    Button buttonAddCreditCard;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);

        toolbar = (Toolbar) findViewById(R.id.add_cc_toolbar);
        cardBankName = (EditText) findViewById(R.id.add_cc_edit_bank);
        cardAlias = (EditText) findViewById(R.id.add_cc_edit_alias);
        cardNumber = (EditText) findViewById(R.id.add_cc_edit_number);
        cardExpiration = (EditText) findViewById(R.id.add_cc_edit_expiration);
        cardCurrency = (Spinner) findViewById(R.id.add_cc_spinner_currency);
        cardType = (Spinner) findViewById(R.id.add_cc_spinner_type);
        cardClosingDay = (Spinner) findViewById(R.id.add_cc_edit_closing);
        cardDueDay = (Spinner) findViewById(R.id.add_cc_edit_due);
        buttonAddCreditCard = (Button) findViewById(R.id.add_cc_button_add_cc);
        buttonAddCreditCard.setOnClickListener(this);

        setUpToolbar();
        setUpPickers();
        setUpSpinners();

        //Check if intent comes with cameFromWelcomeScreen = true;
        cameFromWelcomeScreen = true;
    }


    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_add_new_cc_title));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpPickers() {
        cardExpiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                if(cardExpirationCal == null) {
                                    cardExpirationCal = Calendar.getInstance();
                                    cardExpirationCal.set(Calendar.HOUR_OF_DAY, 0);
                                    cardExpirationCal.set(Calendar.MINUTE, 0);
                                    cardExpirationCal.set(Calendar.SECOND, 0);
                                    cardExpirationCal.set(Calendar.MILLISECOND, 0);
                                }

                                cardExpirationCal.set(year, monthOfYear, dayOfMonth);
                                cardExpiration.setText(year + "/" + (monthOfYear+1) + "/" + dayOfMonth);
                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        //.setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
                        //.setDateRange(minDate, null)
                        .setDoneText(getResources().getString(R.string.activity_add_new_cc_expiration_datepicker_button_select))
                        .setCancelText(getResources().getString(R.string.activity_add_new_cc_expiration_datepicker_button_cancel));
                cdp.show(getSupportFragmentManager(), CALENDAR_EXPIRATION_TAG);
            }
        });
    }

    private void setUpSpinners() {
        currencies = new ArrayList<>(Arrays.asList(Currency.values()));
        ArrayAdapter currencyAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardCurrency.setAdapter(currencyAdapter);

        cardTypes = new ArrayList<>(Arrays.asList(CreditCardType.values()));
        ArrayAdapter cardTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, cardTypes);
        cardTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardType.setAdapter(cardTypeAdapter);

        days = new ArrayList<>();
        for(int i=1; i<=28;i++) {
            days.add(new Integer(i));
        }

        ArrayAdapter closingDayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, days);
        closingDayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardClosingDay.setAdapter(closingDayAdapter);

        ArrayAdapter dueDayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, days);
        dueDayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardDueDay.setAdapter(dueDayAdapter);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if(i == R.id.add_cc_button_add_cc) {
            handleNewCardCreation();
        }
    }

    @Override
    public void onBackPressed() {
        if(cameFromWelcomeScreen) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_add_new_cc_exit_dialog_title))
                .setMessage(getResources().getString(R.string.activity_add_new_cc_exit_dialog_message))
                .setPositiveButton(getResources().getString(R.string.activity_add_new_cc_exit_dialog_button_exit),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_add_new_cc_exit_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                })
                .create();
            dialog.show();
        }
    }

    private void handleNewCardCreation() {
        String alias = cardAlias.getText().toString();
        if(alias.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_alias), Toast.LENGTH_SHORT).show();
            return;
        }
        int closing = days.get(cardClosingDay.getSelectedItemPosition());
        int due = days.get(cardDueDay.getSelectedItemPosition());
        if(closing == due) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_closing_due_days), Toast.LENGTH_SHORT).show();
            return;
        }

        String bankName = cardBankName.getText().toString();
        String number = cardNumber.getText().toString();
        Currency currency = currencies.get(cardCurrency.getSelectedItemPosition());
        CreditCardType type = cardTypes.get(cardType.getSelectedItemPosition());


        ExpenseManagerDAO dao = new ExpenseManagerDAO(this);
        try {
            dao.insertCreditCard(new CreditCard(alias, bankName, number, currency, type, cardExpirationCal, closing, due));
        }catch(CouldNotInsertDataException e) {
            Toast.makeText(this, "There was a problem inserting the credit card!", Toast.LENGTH_SHORT).show();
        }
        Intent goHomeIntent = new Intent(this, HomeActivity.class);
        startActivity(goHomeIntent);
        finish();
    }
}

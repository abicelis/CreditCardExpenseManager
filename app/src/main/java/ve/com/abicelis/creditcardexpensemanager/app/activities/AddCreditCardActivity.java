package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.CreditCardAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardBackground;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 27/8/2016.
 */
public class AddCreditCardActivity extends AppCompatActivity {

    //TAGS
    private static final String CALENDAR_EXPIRATION_TAG = "1";

    //DATA
    boolean cameFromWelcomeScreen = false;
    Calendar cardExpirationCal = null;
    List<Currency> currencies;
    List<CreditCardType> cardTypes;
    List<Integer> days;
    List<CreditCard> mCreditCardList;
    CreditCardBackground selectedCreditCardBackground = null;

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
    RecyclerView mRecyclerView;
    CreditCardAdapter mAdapter;
    LinearLayoutManager mLayoutManager;


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
        mRecyclerView = (RecyclerView) findViewById(R.id.add_cc_recycler);
        buttonAddCreditCard = (Button) findViewById(R.id.add_cc_button_add_cc);
        buttonAddCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNewCardCreation();
            }
        });

        setUpToolbar();
        setUpPickers();
        setUpSpinners();
        setUpCCRecyclerView();
        setUpRecyclerUpdater();

        //Check if intent comes with cameFromWelcomeScreen = true;
        cameFromWelcomeScreen = true;
    }


    private void setUpToolbar() {
        toolbar.setTitle(getResources().getString(R.string.activity_add_new_cc_title));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);

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

    private void setUpCCRecyclerView() {

        mCreditCardList = CreditCard.getCreditCardBackgroundTypesList(this);
        CreditCardViewHolder.CreditCardSelectedListener ccSelectedListener = new CreditCardViewHolder.CreditCardSelectedListener() {
            @Override
            public void OnCreditCardSelected(CreditCard creditCard) {
                Toast.makeText(AddCreditCardActivity.this, "Background selected", Toast.LENGTH_SHORT).show();
                selectedCreditCardBackground = creditCard.getCreditCardBackground();
            }
        };

        mAdapter = new CreditCardAdapter(getApplicationContext(), mCreditCardList, ccSelectedListener);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }


    private void setUpRecyclerUpdater() {

        cardBankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    c.setBankName(cardBankName.getText().toString());
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        cardAlias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    c.setCardAlias(cardAlias.getText().toString());
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    c.setCardNumber(cardNumber.getText().toString());
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        cardExpiration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(cardExpirationCal != null) {
                    for (CreditCard c : mCreditCardList) {
                        c.setCardExpiration(cardExpirationCal);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });


        cardCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CreditCard c : mCreditCardList) {
                    c.setCurrency(currencies.get(cardCurrency.getSelectedItemPosition()));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        cardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CreditCard c : mCreditCardList) {
                    c.setCardType(cardTypes.get(cardType.getSelectedItemPosition()));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

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

        if(selectedCreditCardBackground == null) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_cc_background_not_selected), Toast.LENGTH_SHORT).show();
            return;
        }

        String bankName = cardBankName.getText().toString();
        String number = cardNumber.getText().toString();
        Currency currency = currencies.get(cardCurrency.getSelectedItemPosition());
        CreditCardType type = cardTypes.get(cardType.getSelectedItemPosition());


        ExpenseManagerDAO dao = new ExpenseManagerDAO(this);
        try {
            dao.insertCreditCard(new CreditCard(alias, bankName, number, currency, type, cardExpirationCal, closing, due, selectedCreditCardBackground));
        }catch(CouldNotInsertDataException e) {
            Toast.makeText(this, "There was a problem inserting the credit card!", Toast.LENGTH_SHORT).show();
        }
        Intent goHomeIntent = new Intent(this, HomeActivity.class);
        startActivity(goHomeIntent);
        finish();
    }
}

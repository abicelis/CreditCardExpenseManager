package ve.com.abicelis.creditcardexpensemanager.app;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.adapter.ExpensesAdapter;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.mocks.CreditMock;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

public class HomeActivity extends AppCompatActivity {

    //Data
    CreditCard creditCard;
    ExpenseManagerDAO dao;

    //UI
    RecyclerView recyclerViewExpenses;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        creditCard = CreditMock.getCreditCardMock();

        setUpExpensesRecyclerView();

        setUpToolbar();





        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                dao = new ExpenseManagerDAO(getApplicationContext());

                try {
                    dao.insertCreditCard(creditCard);
                }catch (CouldNotInsertDataException e) {
                    e.printStackTrace();
                }

                List<CreditCard> creditCards = dao.getCreditCardList();

                System.out.println(creditCards.get(0).getCardAlias());
            }
        };
        handler.postDelayed(r, 1000);
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitle("CC Expense Mgr");
    }

    private void setUpExpensesRecyclerView() {


        recyclerViewExpenses = (RecyclerView) findViewById(R.id.home_recycler_expenses);

        ExpensesAdapter adapter = new ExpensesAdapter(getApplicationContext(), creditCard.getCreditPeriods().get(0).getExpenses());
        recyclerViewExpenses.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewExpenses.setLayoutManager(layoutManager);
    }
}

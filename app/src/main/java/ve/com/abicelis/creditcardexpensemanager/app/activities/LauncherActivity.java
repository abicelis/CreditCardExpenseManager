package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;

/**
 * Created by Alex on 26/8/2016.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExpenseManagerDAO dao = new ExpenseManagerDAO(this.getApplicationContext());
        Intent intent;

        //if(dao.getCreditCardList().size() == 0)
            intent = new Intent(this, WelcomeActivity.class);
         //else
         //   intent = new Intent(this, HomeActivity.class);

        startActivity(intent);
        finish();
    }
}

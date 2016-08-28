package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import ve.com.abicelis.creditcardexpensemanager.R;

/**
 * Created by Alex on 26/8/2016.
 */
public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    Button addCCButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Change the status bar color!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.welcome_activity_status_bar));
        }

        addCCButton = (Button) findViewById(R.id.activity_welcome_add_credit_card);
        addCCButton.setOnClickListener(this);


        //TODO: eventually this will be gone
        findViewById(R.id.activity_welcome_go_home).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.activity_welcome_add_credit_card:
                Intent addCCIntent = new Intent(WelcomeActivity.this, AddCreditCardActivity.class);
                startActivity(addCCIntent);
                finish();
                break;

            //TODO: eventually this will be gone
            case R.id.activity_welcome_go_home:
                Intent goHomeIntent = new Intent(WelcomeActivity.this, HomeActivity.class);
                startActivity(goHomeIntent);
                finish();
                break;
        }
    }
}

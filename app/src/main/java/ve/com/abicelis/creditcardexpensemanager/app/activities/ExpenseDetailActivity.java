package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 19/8/2016.
 */
public class ExpenseDetailActivity extends AppCompatActivity implements  View.OnClickListener {

    //UI
    private ImageView mImage;
    private TextView mAmount;
    private TextView mDescription;
    private TextView mDate;
    private Expense mExpense;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //Enable Lollipop Material Design transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);


        //Try to get the expense, sent from the caller activity
        try {
            mExpense = (Expense) getIntent().getExtras().get("expense");
        } catch (Exception e) {
            Toast.makeText(this, "Error: missing Expense", Toast.LENGTH_SHORT).show();
            Handler h = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                       supportFinishAfterTransition();
                }
            };
            h.postDelayed(r, 1000);
        }


        mImage = (ImageView) findViewById(R.id.expense_detail_image);
        mAmount = (TextView) findViewById(R.id.expense_detail_amount);
        mDescription = (TextView) findViewById(R.id.expense_detail_description);
        mDate = (TextView) findViewById(R.id.expense_detail_date);


        mAmount.setText(mExpense.getAmount().toPlainString());
        mDescription.setText(mExpense.getDescription());
        mDate.setText(mExpense.getDate().getTime().toString());


        try {
            Bitmap image = mExpense.getFullImage();
            this.mImage.setImageBitmap(image);
            mImage.setOnClickListener(this);
        } catch (FileNotFoundException e) {
            if(mExpense.getThumbnail() != null && mExpense.getThumbnail().length > 0)
                this.mImage.setImageBitmap(ImageUtils.getBitmap(mExpense.getThumbnail()));
            else
                this.mImage.setImageResource(R.drawable.icon_expense);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();     //When user backs out, transition back!
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.expense_detail_image) {

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, mImage, getResources().getString(R.string.transition_name_expense_detail_image));
            Intent imageViewerIntent = new Intent(this, ImageViewerActivity.class);
            imageViewerIntent.putExtra("imagePath", mExpense.getFullImagePath());
            startActivity(imageViewerIntent, options.toBundle());
        }
    }
}

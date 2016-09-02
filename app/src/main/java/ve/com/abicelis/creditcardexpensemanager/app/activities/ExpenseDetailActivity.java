package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 19/8/2016.
 */
public class ExpenseDetailActivity extends AppCompatActivity implements  View.OnClickListener {

    //UI
    private Toolbar mToolbar;
    private ImageView mImage;
    private TextView mAmount;
    private TextView mDescription;
    private TextView mDate;
    private TextView mCategory;
    private TextView mType;
    private Button mEdit;
    private Button mDelete;

    //DATA
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

        mToolbar = (Toolbar) findViewById(R.id.expense_detail_toolbar);
        mImage = (ImageView) findViewById(R.id.expense_detail_image);
        mAmount = (TextView) findViewById(R.id.expense_detail_amount);
        mDescription = (TextView) findViewById(R.id.expense_detail_description);
        mDate = (TextView) findViewById(R.id.expense_detail_date);
        mCategory = (TextView) findViewById(R.id.expense_detail_category);
        mType = (TextView) findViewById(R.id.expense_detail_type);
        mEdit = (Button) findViewById(R.id.expense_detail_btn_edit);
        mDelete = (Button) findViewById(R.id.expense_detail_btn_delete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(ExpenseDetailActivity.this)
                        .setTitle(R.string.dialog_delete_expense_title)
                        .setMessage(R.string.dialog_delete_expense_message)
                        .setPositiveButton(R.string.dialog_delete_expense_button_yes,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    new ExpenseManagerDAO(ExpenseDetailActivity.this).deleteExpense(mExpense.getId());
                                    finish();
                                } catch(CouldNotDeleteDataException e) {
                                    Toast.makeText(ExpenseDetailActivity.this, getResources().getString(R.string.activity_expense_detail_error_cant_delete_expense), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_delete_expense_button_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        setUpToolbar();
        setUpExpenseDetails();
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getResources().getString(R.string.activity_expense_detail_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpExpenseDetails() {
        mAmount.setText(mExpense.getAmount().toPlainString());
        mDescription.setText(mExpense.getDescription());
        mDate.setText(mExpense.getDate().getTime().toString());

        this.mCategory.setText(mExpense.getExpenseCategory().getFriendlyName());
        ((GradientDrawable)this.mCategory.getBackground()).setColor(ContextCompat.getColor(this, mExpense.getExpenseCategory().getColor()));

        this.mType.setText(mExpense.getExpenseType().getShortName());
        ((GradientDrawable)this.mType.getBackground()).setColor(ContextCompat.getColor(this, mExpense.getExpenseType().getColor()));

        try {
            Bitmap image = mExpense.getFullImage();
            mImage.setImageBitmap(image);
            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ExpenseDetailActivity.this, mImage, getResources().getString(R.string.transition_name_expense_detail_image));
                    Intent imageViewerIntent = new Intent(ExpenseDetailActivity.this, ImageViewerActivity.class);
                    imageViewerIntent.putExtra("imagePath", mExpense.getFullImagePath());
                    startActivity(imageViewerIntent, options.toBundle());
                }
            });
        } catch (FileNotFoundException e) {
            if(mExpense.getThumbnail() != null && mExpense.getThumbnail().length > 0) {
                mImage.setImageBitmap(ImageUtils.getBitmap(mExpense.getThumbnail()));
                mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ExpenseDetailActivity.this, getResources().getString(R.string.activity_expense_detail_error_cant_find_image), Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expense_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.expense_detail_menu_edit_image:
                Toast.makeText(this, "Under construction :)", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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

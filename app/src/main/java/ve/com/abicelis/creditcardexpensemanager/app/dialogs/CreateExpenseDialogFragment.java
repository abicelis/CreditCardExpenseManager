package ve.com.abicelis.creditcardexpensemanager.app.dialogs;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseType;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;

/**
 * Created by Alex on 9/8/2016.
 */
public class CreateExpenseDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    //Constants
    private static final int RC_HANDLE_CAMERA_PERM = 2;                 // Permission request codes need to be < 256
    private static final String TAG = "CreateExpenseDialogFrag";
    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = 400;
    private static final int IMAGE_COMPRESSION_PERCENTAGE = 30;
    private static final int REQUEST_IMAGE_CAPTURE = 123;
    private static final int RESULT_OK = -1;

    //DB
    private ExpenseManagerDAO mDao;

    //UI
    private EditText mAmountText;
    private EditText mDescriptionText;
    private Button mCancelButton;
    private Button mCreateButton;
    private ImageView mImage;

    //DATA
    private Bitmap expenseImage;
    private byte[] expenseImageBytes = new byte[0];

    public CreateExpenseDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateExpenseDialogFragment newInstance(String title, ExpenseManagerDAO dao) {
        CreateExpenseDialogFragment frag = new CreateExpenseDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        frag.setDao(dao);
        return frag;
    }

    public void setDao(ExpenseManagerDAO dao) {
        mDao = dao;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_expense, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get fields from view
        mAmountText = (EditText) view.findViewById(R.id.dialog_expense_edit_amount);
        mDescriptionText = (EditText) view.findViewById(R.id.dialog_expense_edit_description);
        mCancelButton = (Button) view.findViewById(R.id.dialog_expense_button_cancel);
        mCreateButton = (Button) view.findViewById(R.id.dialog_expense_button_create);
        mImage = (ImageView) view.findViewById(R.id.dialog_expense_img_image);

        // Set onClick listeners
        mCancelButton.setOnClickListener(this);
        mCreateButton.setOnClickListener(this);
        mImage.setOnClickListener(this);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        // Show soft keyboard automatically and request focus to field
        mAmountText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.dialog_expense_img_image:
                handleExpenseImageCapture();
                break;

            case R.id.dialog_expense_button_create:
                createNewExpense();
                break;

            case R.id.dialog_expense_button_cancel:
                this.dismiss();
                break;

        }
    }

    private void handleExpenseImageCapture() {
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            doCaptureImageFromCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void doCaptureImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }



    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = getActivity();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

//        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.ok, listener)
//                .show();
    }


    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so launch camera
            doCaptureImageFromCamera();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }


    private void createNewExpense() {

        String amount = mAmountText.getText().toString();
        String description = mDescriptionText.getText().toString();
        if(amount.equals("") || new BigDecimal(amount).compareTo(new BigDecimal(0)) == 0) {
            Toast.makeText(getContext(), getResources().getString(R.string.dialog_create_expense_error_bad_amount), Toast.LENGTH_SHORT).show();
            return;
        }

        if(description.isEmpty()) {
            description = "-";
        }

        Expense expense = new Expense(1, description, expenseImageBytes,
                new BigDecimal(mAmountText.getText().toString()), Currency.VEF,
                Calendar.getInstance(), ExpenseCategory.CLOTHING, ExpenseType.ORDINARY);

        try {
            mDao.insertExpense(0, expense);
            dismiss();
        } catch (CouldNotInsertDataException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(data != null ) {
                expenseImage = (Bitmap) data.getExtras().get("data");
                expenseImage = Bitmap.createScaledBitmap(expenseImage, IMAGE_WIDTH, IMAGE_HEIGHT, true);
                expenseImageBytes = ImageUtils.toCompressedByteArray(expenseImage, IMAGE_COMPRESSION_PERCENTAGE);


                mImage.setImageBitmap(expenseImage);

            } else {
                Toast.makeText(getContext(), "Camera data returned null", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "OnActivityResult but reqCode =" +requestCode + " and resCode=" + resultCode, Toast.LENGTH_SHORT).show();

        }
    }
}

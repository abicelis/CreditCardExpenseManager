package ve.com.abicelis.creditcardexpensemanager.app.dialogs;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.ImageCropperActivity;
import ve.com.abicelis.creditcardexpensemanager.app.utils.ImageUtils;
import ve.com.abicelis.creditcardexpensemanager.app.utils.PermissionUtils;
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
    private static final String TAG_ARGS_TITLE = "tagArgsTitle";
    private static final String TAG_ARGS_PERIOD_ID = "tagArgsPeriodId";
    private static final String TAG = "CreateExpenseDialogFrag";
    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = 400;
    private static final int IMAGE_COMPRESSION_PERCENTAGE = 30;
    private static final int REQUEST_IMAGE_CAPTURE = 123;
    private static final int REQUEST_IMAGE_CROP = 124;
    private static final int RESULT_OK = -1;

    //DB
    private ExpenseManagerDAO mDao;

    //UI
    private DialogInterface.OnDismissListener mOnDismissListener;
    private EditText mAmountText;
    private EditText mDescriptionText;
    private Button mCancelButton;
    private Button mCreateButton;
    private ImageView mImage;

    //DATA
    int creditperiodId = -1;
    Bitmap expenseImageThumbnail;
    byte[] expenseImageThumbnailBytes;
    private Uri imageUri;
    private String imagePath = null;

    public CreateExpenseDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateExpenseDialogFragment newInstance(String title, ExpenseManagerDAO dao, int creditperiodId) {
        CreateExpenseDialogFragment frag = new CreateExpenseDialogFragment();
        Bundle args = new Bundle();
        args.putString(TAG_ARGS_TITLE, title);
        args.putInt(TAG_ARGS_PERIOD_ID, creditperiodId);
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
        String title = getArguments().getString(TAG_ARGS_TITLE, "Enter Name");
        getDialog().setTitle(title);
        creditperiodId =  getArguments().getInt(TAG_ARGS_PERIOD_ID, -1);
        if(creditperiodId == -1) {
            Toast.makeText(getActivity(), "Error, wrong creditPeriod id passed", Toast.LENGTH_SHORT).show();
            dismiss();
        }

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
                handleNewExpenseCreation();
                break;

            case R.id.dialog_expense_button_cancel:
                if(imagePath != null) {
                    try {
                        new File(imagePath).delete();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Damn son, there was a problem deleting the unneeded picture!", Toast.LENGTH_SHORT).show();
                    }
                }
                this.dismiss();
                break;

        }
    }

    private void handleExpenseImageCapture() {
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        String[] nonGrantedPermissions;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            nonGrantedPermissions = PermissionUtils.checkIfPermissionsAreGranted(getContext(), Manifest.permission.CAMERA);
        else
            nonGrantedPermissions = PermissionUtils.checkIfPermissionsAreGranted(getContext(), Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(nonGrantedPermissions == null)
            dispatchTakePictureIntent();
        else {
            Log.w(TAG, "Camera permission is not granted. Requesting permission");
            PermissionUtils.requestPermissions(this, nonGrantedPermissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    private void dispatchTakePictureIntent() {

        File expensesDir = new File(getActivity().getExternalFilesDir(null), "expenses/");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Create an /expenses dir if it doesnt exist
        try {
            createDirIfNotExists(expensesDir);
        } catch (IOException ex) {
            Toast.makeText(getActivity(), "Sorry! There was a problem while creating the image directory", Toast.LENGTH_SHORT).show();
        }

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFileInDir(expensesDir);
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Sorry! There was a problem while creating the image", Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null) {
                try {
                    imagePath = photoFile.getPath();
                    imageUri = FileProvider.getUriForFile(getContext(), "ve.com.abicelis.creditcardexpensemanager.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    //HACK: Before starting the camera activity on pre-lolipop devices, make sure to grant permissions to all packages that need it
                    List<ResolveInfo> resInfoList = getContext().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        getContext().grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Sorry! There was a problem with the image", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Toast.makeText(getActivity(), "Sorry! Cant take images if there's not a camera app installed", Toast.LENGTH_SHORT).show();

        }
    }

    private void createDirIfNotExists(File directory) throws IOException, SecurityException  {
        if (directory.mkdir()){
            File nomedia = new File(directory, ".nomedia");
            nomedia.createNewFile();
        }
    }

    private File createImageFileInDir(File directory) throws IOException, SecurityException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", directory);

        // Save a file: path for use with ACTION_VIEW intents
        //String mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        //Check if all permissions have been granted
        if (grantResults.length != 0) {
            boolean permissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;
                    break;
                }
            }

            if(permissionsGranted) {
                Log.d(TAG, "Camera permission granted - initialize the camera source");
                // We have permission, so launch camera
                dispatchTakePictureIntent();
                return;
            }
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        };


        //TODO: fix this message
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {
                //Go to the imageCropper activity
                Intent cropImageIntent = new Intent(getActivity(), ImageCropperActivity.class);
                cropImageIntent.putExtra(ImageCropperActivity.IMAGE_PATH, imagePath);
                startActivityForResult(cropImageIntent, REQUEST_IMAGE_CROP);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {

            try {
                expenseImageThumbnail = BitmapFactory.decodeFile(imagePath);
                expenseImageThumbnail = ImageUtils.scaleBitmap(expenseImageThumbnail, 120);
                expenseImageThumbnailBytes = ImageUtils.toByteArray(expenseImageThumbnail);

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Peos!", Toast.LENGTH_SHORT).show();
            }
            mImage.setImageBitmap(expenseImageThumbnail);

        } else {
            Toast.makeText(getContext(), "OnActivityResult but reqCode =" +requestCode + " and resCode=" + resultCode, Toast.LENGTH_SHORT).show();

        }
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }


    private void handleNewExpenseCreation() {

        String amount = mAmountText.getText().toString();
        String description = mDescriptionText.getText().toString();
        if(amount.equals("") || new BigDecimal(amount).compareTo(new BigDecimal(0)) == 0) {
            Toast.makeText(getContext(), getResources().getString(R.string.dialog_create_expense_error_bad_amount), Toast.LENGTH_SHORT).show();
            return;
        }

        if(description.isEmpty()) {
            description = "-";
        }

        Expense expense = new Expense(description, expenseImageThumbnailBytes, imagePath,
                new BigDecimal(mAmountText.getText().toString()), Currency.VEF,
                Calendar.getInstance(), ExpenseCategory.CLOTHING, ExpenseType.ORDINARY);

        try {
            mDao.insertExpense(creditperiodId, expense);
            dismiss();
        } catch (CouldNotInsertDataException e) {
            e.printStackTrace();
        }
    }


}

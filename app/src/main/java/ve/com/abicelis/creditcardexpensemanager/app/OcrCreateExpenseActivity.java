package ve.com.abicelis.creditcardexpensemanager.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.anim.FadeAnimator;
import ve.com.abicelis.creditcardexpensemanager.ocr.OcrDetectorProcessor;
import ve.com.abicelis.creditcardexpensemanager.ocr.OcrGraphic;
import ve.com.abicelis.creditcardexpensemanager.ocr.camera.CameraSource;
import ve.com.abicelis.creditcardexpensemanager.ocr.camera.CameraSourcePreview;
import ve.com.abicelis.creditcardexpensemanager.ocr.camera.GraphicOverlay;

/**
 * Activity for the Ocr Detecting app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCreateExpenseActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "OcrCreateExpenseActvty";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private static int STATUS_BAR_HEIGHT_OFFSET;
    private static int RESIZER_MARGIN = 50;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private OcrDetectorProcessor mDetectorProcessor;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;


    //UI
    TextView txtDate;
    TextView txtDescription;
    TextView txtTotal;
    FrameLayout mContainer;
    LinearLayout mOcrWindowContainer;
    View mOcrWindow;
    ImageView mOcrWindowResizer;
    Point resizerCenterOffset;
    Point resizerMinPosition;
    Point resizerMaxPosition;
    Point containerCenter;

    FloatingActionButton fabNext;
    FloatingActionButton fabPrev;

    //DATA
    List<TextView> fieldsToFill = new ArrayList<>();
    int currentField = -1;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ocr_create_expense);


        //Capture Views
        txtDate = (TextView) findViewById(R.id.create_expense_txt_receipt_date);
        txtDescription = (TextView) findViewById(R.id.create_expense_txt_receipt_description);
        txtTotal = (TextView) findViewById(R.id.create_expense_txt_receipt_total);
        mContainer = (FrameLayout) findViewById(R.id.create_expense_container);
        mOcrWindowContainer = (LinearLayout) findViewById(R.id.create_expense_ocr_window_container);
        mOcrWindow = findViewById(R.id.create_expense_ocr_window);
        mOcrWindowResizer = (ImageView) findViewById(R.id.create_expense_ocr_window_resizer);
        fabNext = (FloatingActionButton) findViewById(R.id.create_expense_fab_next);
        fabPrev = (FloatingActionButton) findViewById(R.id.create_expense_fab_prev);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);


        // Set good defaults for capturing text.
        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        //gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        //scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        Snackbar.make(mGraphicOverlay, "Capture the Receipt's data using your camera!", Snackbar.LENGTH_LONG).show();



        //Set receipt's date
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        txtDate.setText("Date: " + df.format(new Date()));

        //Set up fields to fill
        fieldsToFill.add(txtDescription);
        fieldsToFill.add(txtTotal);
        activateNextField();

        //Set up fabs
        fabNext.setOnClickListener(this);
        fabPrev.setOnClickListener(this);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        calculateStatusBarHeightOffset();
        setupOcrWindowResizer();

        mDetectorProcessor.setOcrWindowBoundingBox(calculateViewBoundingBox(mOcrWindow));
        mDetectorProcessor.setOcrWindowContainerBoundingBox(calculateViewBoundingBox(mOcrWindowContainer));

    }

    private void calculateStatusBarHeightOffset() {
        //Calculate statusbar height offset
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        STATUS_BAR_HEIGHT_OFFSET = (dm.heightPixels - mContainer.getMeasuredHeight());
        Log.d (TAG, "STATUSBAR OFFSET = " + STATUS_BAR_HEIGHT_OFFSET);
    }

    private void setupOcrWindowResizer() {
        resizerCenterOffset = new Point();
        resizerCenterOffset.x = mOcrWindowResizer.getMeasuredWidth()/2;
        resizerCenterOffset.y = mOcrWindowResizer.getMeasuredHeight()/2;
        mOcrWindowResizer.setOnTouchListener(this);


        //Position the widget on the lower right corner of the mOcrWindow
        mOcrWindowResizer.setX(mOcrWindow.getRight() - resizerCenterOffset.x);
        mOcrWindowResizer.setY(mOcrWindow.getBottom() - resizerCenterOffset.y);

        //Calculate max and min positions for the resizer
        Point containerLRCorner = new Point();
        containerCenter = new Point();
        containerLRCorner.x = mOcrWindowContainer.getRight();
        containerLRCorner.y = mOcrWindowContainer.getBottom();
        containerCenter.x = mOcrWindowContainer.getLeft() + (mOcrWindowContainer.getWidth() / 2);
        containerCenter.y = mOcrWindowContainer.getTop() + (mOcrWindowContainer.getHeight() / 2);

        resizerMinPosition = new Point(containerCenter.x + RESIZER_MARGIN, containerCenter.y + RESIZER_MARGIN);
        resizerMaxPosition = new Point(containerLRCorner.x - RESIZER_MARGIN, containerLRCorner.y - RESIZER_MARGIN);
    }

    private Rect calculateViewBoundingBox(View view){

        int[] loc = new int[2];
        view.getLocationInWindow(loc);
        loc[1] += STATUS_BAR_HEIGHT_OFFSET/2;        //Apply statusbar offset!
        return new Rect(loc[0], loc[1], loc[0] + view.getWidth(), loc[1] + view.getHeight());
    }



    private void setCurrentFieldValue(String value) {
        fieldsToFill.get(currentField).setText(value);
    }

    private void activateNextField() {
        if(currentField == -1) { //Starting
            currentField++;
            FadeAnimator.startAnimation(fieldsToFill.get(currentField));
        } else if(currentField < (fieldsToFill.size()-1)) {
            FadeAnimator.stopAnimation(fieldsToFill.get(currentField));
            currentField++;
            FadeAnimator.startAnimation(fieldsToFill.get(currentField));
        }
        else {
            Toast.makeText(this, "No more fields!", Toast.LENGTH_SHORT).show();
        }
    }

    private void activatePreviousField() {
        if(currentField > 0) {
            FadeAnimator.stopAnimation(fieldsToFill.get(currentField));
            --currentField;
            FadeAnimator.startAnimation(fieldsToFill.get(currentField));
        }
        else {
            Toast.makeText(this, "No more fields!", Toast.LENGTH_SHORT).show();
        }
    }





    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //boolean b = scaleGestureDetector.onTouchEvent(e);

        //boolean c = gestureDetector.onTouchEvent(e);

        return super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // Create the TextRecognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        // Set the TextRecognizer's Processor.
        mDetectorProcessor = new OcrDetectorProcessor(mGraphicOverlay);
        textRecognizer.setProcessor(mDetectorProcessor);

        // Check if the TextRecognizer is operational.
        if(!textRecognizer.isOperational()) {
            Log.w(TAG, "TextRecognizer dependencies are not yet available");

            // Check for low storage. If there is no storage, the native library will not be
            // downloaded, so detection will not become operational

            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if(hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_SHORT).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Create the mCameraSource using the TextRecognizer.
        mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(15.0f)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
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
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,true);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

//    /**
//     * onTap is called to speak the tapped TextBlock, if any, out loud.
//     *
//     * @param rawX - the raw position of the tap
//     * @param rawY - the raw position of the tap.
//     * @return true if the tap was on a TextBlock
//     */
//    private boolean onTap(float rawX, float rawY) {
//        // Speak the text when the user taps on screen.
//        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
//        TextBlock text = null;
//        if(graphic != null) {
//            text = graphic.getTextBlock();
//            if (text != null && text.getValue() != null) {
//                Log.d(TAG, "text data is being spoken! " + text.getValue());
//
//                Toast.makeText(this, text.getValue(), Toast.LENGTH_SHORT).show();
//
//                //// Set the current field the tapped text.
//                setCurrentFieldValue(text.getValue());
//                activateNextField();
//
//            }
//            else {
//                Log.d(TAG, "text data is null");
//            }
//        }
//        else {
//            Log.d(TAG,"no text detected");
//        }
//        return text != null;
//    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.create_expense_fab_next:
                activateNextField();
                break;
            case R.id.create_expense_fab_prev:
                activatePreviousField();
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int X = (int) motionEvent.getRawX();
        int Y = (int) motionEvent.getRawY();


        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                X = (X < resizerMinPosition.x ? resizerMinPosition.x : X);
                X = (X > resizerMaxPosition.x ? resizerMaxPosition.x : X);
                Y = (Y < resizerMinPosition.y ? resizerMinPosition.y : Y);
                Y = (Y > resizerMaxPosition.y ? resizerMaxPosition.y : Y);

                view.setX(X - resizerCenterOffset.x);
                view.setY(Y - resizerCenterOffset.y - STATUS_BAR_HEIGHT_OFFSET);

                mOcrWindow.setLeft(containerCenter.x - (X - containerCenter.x));
                mOcrWindow.setTop(containerCenter.y - (Y - containerCenter.y));
                mOcrWindow.setRight(X);
                mOcrWindow.setBottom(Y - STATUS_BAR_HEIGHT_OFFSET);

                //Log.d(TAG, "ACTION_MOVE X=" + X + " Y=" + Y + " mOcrWindow X=" + mOcrWindow.getX() + " mOcrWindow Y=" + mOcrWindow.getY()
                //        + " mOcrWindow left=" + mOcrWindow.getBottom() + " mOcrWindow right=" + mOcrWindow.getRight());
                break;

            case MotionEvent.ACTION_UP:
                mDetectorProcessor.setOcrWindowBoundingBox(calculateViewBoundingBox(mOcrWindow));
                break;
        }

        return true;
    }

    /*private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }*/

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (mCameraSource != null) {
                mCameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }
}

package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import java.io.IOException;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.utils.PermissionUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDbHelper;


/**
 * Created by abice on 3/2/2017.
 */

public class AppPreferenceFragment extends PreferenceFragmentCompatDividers {

    //CONSTS
    private static final int RC_WRITE_STORAGE_PERM_EXPORT = 39;                        // Permission request codes need to be < 256
    private static final int RC_WRITE_STORAGE_PERM_IMPORT = 40;                        // Permission request codes need to be < 256
    public static final String TAG = AppPreferenceFragment.class.getSimpleName();



    //DATA
    private ExpenseManagerDbHelper dbHelper;

    //UI
    private Preference mExportData;
    private Preference mImportData;

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        mExportData = findPreference("preferences_export");
        mImportData = findPreference("preferences_import");

        dbHelper = new ExpenseManagerDbHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        mExportData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                // Check for external storage permissions
                String[] nonGrantedPermissions = PermissionUtils.checkIfPermissionsAreGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(nonGrantedPermissions != null) {
                    requestPermissions(nonGrantedPermissions, RC_WRITE_STORAGE_PERM_EXPORT);
                }else {
                    handleExportAction();
                }
                return false;
            }
        });

        mImportData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                // Check for external storage permissions
                String[] nonGrantedPermissions = PermissionUtils.checkIfPermissionsAreGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(nonGrantedPermissions != null) {
                    requestPermissions(nonGrantedPermissions, RC_WRITE_STORAGE_PERM_IMPORT);
                }else {
                    handleImportAction();
                }
                return false;
            }
        });


        try {
            return super.onCreateView(inflater, container, savedInstanceState);
        } finally {
            // Uncomment this if you want to change the dividers' style
            // setDividerPreferences(DIVIDER_PADDING_CHILD | DIVIDER_CATEGORY_AFTER_LAST | DIVIDER_CATEGORY_BETWEEN);
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

        if (requestCode != RC_WRITE_STORAGE_PERM_EXPORT && requestCode != RC_WRITE_STORAGE_PERM_IMPORT) {
            Toast.makeText(getActivity(), "Error processing permissions", Toast.LENGTH_SHORT).show();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode){
                case RC_WRITE_STORAGE_PERM_EXPORT:
                    handleExportAction();
                    break;
                case RC_WRITE_STORAGE_PERM_IMPORT:
                    handleImportAction();
                    break;
            }
            return;
        }


        //No permission was given, alert user
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.fragment_preferences_no_permissions_dialog_title)
                .setMessage(R.string.fragment_preferences_no_permissions_dialog_text)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //getActivity().onBackPressed();
                    }
                })
                .show();
//      Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
//      " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

    }


    private void handleExportAction() {
        try {
            if(dbHelper.exportDatabase())
                Toast.makeText(getActivity(), "Data exported successfully", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Couldn't export data", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Toast.makeText(getActivity(), "Error exporting data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImportAction() {
        try {
            if(dbHelper.importDatabase())
                Toast.makeText(getActivity(), "Data imported successfully", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Backup data not found in sdcard", Toast.LENGTH_SHORT).show();

        }catch (IOException e){
            Toast.makeText(getActivity(), "Error importing data", Toast.LENGTH_SHORT).show();
        }
    }

}

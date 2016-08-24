package ve.com.abicelis.creditcardexpensemanager.app.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 21/8/2016.
 */
public class PermissionUtils {

    /**
     * Returns a boolean indicating if a permission has or hasnt been granted
     * @param context The activity Context
     * @param permission Permission to be checked for grant
     * @return boolean indicating grant status
     */
    static boolean checkIfPermissionIsGranted(Context context, String permission) {
        int rc = ActivityCompat.checkSelfPermission(context, permission);
        return (rc == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Returns a list of permissions which have not been granted or null if all
     * permissions have been granted
     * @param context The activity Context
     * @param permissions Array of permissions to check for grant
     * @return Array of non-granted permissions or null if all is good
     */
    public static String[] checkIfPermissionsAreGranted(Context context, String ... permissions) {

        List<String> nonGrantedPermissions = new ArrayList<>();
        for (String permission: permissions) {
            int rc = ActivityCompat.checkSelfPermission(context, permission);
            if(rc != PackageManager.PERMISSION_GRANTED) {
                nonGrantedPermissions.add(permission);
            }
        }

        if(nonGrantedPermissions.size() == 0)
            return null;
        else
            return nonGrantedPermissions.toArray(new String[nonGrantedPermissions.size()]);
    }


    public static void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {

        //Not using ActivityCompat.requestPermissions() since im asking permissions from a fragment
        fragment.requestPermissions(permissions, requestCode);
    }

    //TODO: request permissions from activitycompat?
//    public static void requestPermissions(Fragment fragment, String[] permissions) {
//
//        //Not using ActivityCompat.requestPermissions() since im asking permissions from a fragment
//        fragment.requestPermissions(permissions, RC_HANDLE_CAMERA_PERM);
//    }


}

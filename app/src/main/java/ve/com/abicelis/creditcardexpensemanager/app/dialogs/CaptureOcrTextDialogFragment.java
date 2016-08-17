package ve.com.abicelis.creditcardexpensemanager.app.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * Created by Alex on 10/8/2016.
 */
public class CaptureOcrTextDialogFragment extends AppCompatDialogFragment {

    //Interface to be implemented by caller activity
    public interface CaptureOcrTextDialogListener {
        void onFinishCaptureOcrTextDialog(String inputText);
    }

    public CaptureOcrTextDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static CaptureOcrTextDialogFragment newInstance(String title, String fieldName, String fieldContent) {
        CaptureOcrTextDialogFragment frag = new CaptureOcrTextDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("fieldName", fieldName);
        args.putString("fieldContent", fieldContent);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final String fieldName = getArguments().getString("fieldName");
        final String fieldContent = getArguments().getString("fieldContent");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Insert text: \r\n\"" + fieldContent + "\"\r\n in Recepit's field: " + fieldName + "?");
        alertDialogBuilder.setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Return response to activity
                CaptureOcrTextDialogListener activity = (CaptureOcrTextDialogListener) getActivity();
                activity.onFinishCaptureOcrTextDialog(fieldContent);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

}

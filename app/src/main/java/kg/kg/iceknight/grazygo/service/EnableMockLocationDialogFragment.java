package kg.kg.iceknight.grazygo.service;

/**
 * Created by bborbuev on 2/10/18.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import kg.kg.iceknight.grazygo.R;

/**
 * Dialog that is shown to the user if he has not enabled mock locations. All it
 * does is open the normal android settings page for that option.
 *
 * @author Ryan
 *
 */
public class EnableMockLocationDialogFragment {

    public final static int MOCKDIALOG = 426252;

    public static Dialog createDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage("You must enable \'Allow mock locations\' to use this app");
        builder.setPositiveButton("Enable now", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent().setClassName("com.android.settings", "com.android.settings.DevelopmentSettings"));
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setCancelable(false);

        return builder.create();
    }

}


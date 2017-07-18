package com.example.android.projectten_inventoryapp;

/**
 * Created by StanleyPC on 6. 7. 2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;

public class PermissionDialog {
//It is took from StackOverFlow topic about adding permissions

    public static final int READ_IN_STORAGE_PERM = 0;
    public static void askAboutPermission(final String Message, final Context context, final String Permiss) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);


        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(context.getString(R.string.requirment_permiss));
        alertBuilder.setMessage(Message + "." + context.getString(R.string.request_permiss));
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Permiss},
                                READ_IN_STORAGE_PERM);

                    }
                });


        AlertDialog alert = alertBuilder.create();
        alert.show();

    }
}
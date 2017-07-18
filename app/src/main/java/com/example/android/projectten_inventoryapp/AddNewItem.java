package com.example.android.projectten_inventoryapp;

/**
 * Created by StanleyPC on 6. 7. 2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.projectten_inventoryapp.Data.ItemContractor.ItemInsertion;

public class AddNewItem extends DialogFragment {

    private String xItemImage;

    private void addNewItem(String itemName, Integer itemPieces, Integer itemPrice, String itemImage) {
        ContentValues itemValues = new ContentValues();

        itemValues.put(ItemInsertion.ITEM_NAME, itemName);
        itemValues.put(ItemInsertion.ITEM_PIECES, itemPieces);
        itemValues.put(ItemInsertion.ITEM_PRICE, itemPrice);

        if (!"".equals(itemImage))
            itemValues.put(ItemInsertion.ITEM_IMAGE, itemImage);
        getActivity().getContentResolver().insert(ItemInsertion.URI_PATH, itemValues);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultsData) {
        super.onActivityResult(requestCode, resultCode, resultsData);

        if (requestCode == PermissionDialog.READ_IN_STORAGE_PERM && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = resultsData.getData();
            xItemImage = selectedImage.toString();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inf = getActivity().getLayoutInflater();

        final View insertData = inf.inflate(R.layout.add_new_item, null);

        Button uploadImageBtn = (Button) insertData.findViewById(R.id.button_upload_image);
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (checkIfReadExternaStoregeOK(getActivity())) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                                    PermissionDialog.READ_IN_STORAGE_PERM);
                }
            }});

        final Dialog callDialog = builder.setView(insertData)
                .setPositiveButton(R.string.add_item_button, null)
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddNewItem.this.getDialog().cancel();
                    }
                })
                .create();

        callDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Boolean holdDialog = false;

                        EditText changeItemName = (EditText) insertData.findViewById(R.id.name);
                        EditText changeItemPieces = (EditText) insertData.findViewById(R.id.pieces);
                        EditText changeItemPrice = (EditText) insertData.findViewById(R.id.price);

                        String itemNameSTR = changeItemName.getText().toString().trim();
                        String itemPiecesSTR = changeItemPieces.getText().toString().trim();
                        String itemPriceSTR = changeItemPrice.getText().toString().trim();

                        if (TextUtils.isEmpty(itemNameSTR)  || TextUtils.isEmpty(itemPiecesSTR) || TextUtils.isEmpty(itemPriceSTR)) {
                            Toast.makeText(getActivity(), getString(R.string.empty_item_fields), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Integer itemPieces = Integer.parseInt(changeItemPieces.getText().toString().trim());
                            Integer itemPrice = Integer.parseInt(changeItemPrice.getText().toString().trim());
                            addNewItem(itemNameSTR, itemPieces, itemPrice, xItemImage);
                            holdDialog = true;
                        }

                        if(holdDialog)
                            callDialog.dismiss();
                    }
                });
            }
        });

        return callDialog;
    }


    private boolean checkIfReadExternaStoregeOK(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        PermissionDialog.askAboutPermission(context.getString(R.string.next_storage), context, Manifest.permission.READ_EXTERNAL_STORAGE);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionDialog.READ_IN_STORAGE_PERM);
                    }
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permiss[], int[] sendResults) {
        switch (requestCode) {
            case PermissionDialog.READ_IN_STORAGE_PERM: {
                if (sendResults.length > 0 && sendResults[0] == PackageManager.PERMISSION_GRANTED) {
                                 startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PermissionDialog.READ_IN_STORAGE_PERM);
                } else {
                                       Toast.makeText(getActivity(), getString(R.string.error_permiss), Toast.LENGTH_LONG).show();
                }
            }
            }
    }
}
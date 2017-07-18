package com.example.android.projectten_inventoryapp;

/**
 * Created by StanleyPC on 6. 7. 2017.
 */

import android.Manifest;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.example.android.projectten_inventoryapp.Data.ItemContractor.ItemInsertion;

public class ChangeItemValues extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEM_BUFFER = 0;

    private Uri xActualURI;

    private EditText xChangeNameTextV;
    private EditText xChangePiecesTextV;
    private EditText xChangePriceTextV;
    private ImageView xChangeImageV;
    private Uri xImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_added_item);

        Intent intent = getIntent();
        xActualURI = intent.getData();
        setTitle(getString(R.string.change_name_of_item));
        getLoaderManager().initLoader(EXISTING_ITEM_BUFFER, null, this);

        xChangeNameTextV = (EditText) findViewById(R.id.change_item_name);
        xChangePiecesTextV = (EditText) findViewById(R.id.change_pieces);
        xChangePriceTextV = (EditText) findViewById(R.id.change_price);
        xChangeImageV = (ImageView) findViewById(R.id.image);

        xChangeImageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (askAboutPermission(ChangeItemValues.this)) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PermissionDialog.READ_IN_STORAGE_PERM);}
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        xChangeNameTextV.setText("");
        xChangePiecesTextV.setText(Integer.toString(0));
        xChangePriceTextV.setText(Integer.toString(0));
        xChangeImageV.setImageDrawable(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] viewValues = {ItemInsertion._ID, ItemInsertion.ITEM_NAME, ItemInsertion.ITEM_PIECES, ItemInsertion.ITEM_PRICE, ItemInsertion.ITEM_IMAGE};

        return new CursorLoader(this, xActualURI, viewValues, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int itemNameCol = cursor.getColumnIndex(ItemInsertion.ITEM_NAME);
            int itemPiecesCol = cursor.getColumnIndex(ItemInsertion.ITEM_PIECES);
            int itemPriceCol = cursor.getColumnIndex(ItemInsertion.ITEM_PRICE);
            int itemImageCol = cursor.getColumnIndex(ItemInsertion.ITEM_IMAGE);

            String name = cursor.getString(itemNameCol);
            Integer pieces = cursor.getInt(itemPiecesCol);
            Integer price = cursor.getInt(itemPriceCol);
            String imageURI = cursor.getString(itemImageCol);

            xChangeNameTextV.setText(name);
            xChangePiecesTextV.setText(Integer.toString(pieces));
            xChangePriceTextV.setText(Integer.toString(price));
            if(imageURI!=null) {
                xChangeImageV.setImageURI(Uri.parse(imageURI));
            }
        }}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(ChangeItemValues.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int insertedData, int finalData, Intent data) {
        super.onActivityResult(insertedData, finalData, data);

        if (insertedData == PermissionDialog.READ_IN_STORAGE_PERM && finalData == Activity.RESULT_OK) {
            Uri controlledImage = data.getData();
            xImageURI = Uri.parse(controlledImage.toString());
            xChangeImageV.setImageURI(controlledImage);
        }
    }


    public void clickOnSaveButton(View view) {
        writeItemData();
        finish();
    }

    private void writeItemData() {
        String name = xChangeNameTextV.getText().toString().trim();
        Integer pieces = Integer.parseInt(xChangePiecesTextV.getText().toString().trim());
        Integer price = Integer.parseInt(xChangePriceTextV.getText().toString().trim());


        ContentValues values = new ContentValues();
        values.put(ItemInsertion.ITEM_NAME, name);

        Bitmap firstImage = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round);
        Bitmap bitmap = ((BitmapDrawable) xChangeImageV.getDrawable()).getBitmap();
        if(!comparison(firstImage,bitmap) && xImageURI != null) {values.put(ItemInsertion.ITEM_IMAGE, xImageURI.toString());}

        values.put(ItemInsertion.ITEM_PIECES, pieces);
        values.put(ItemInsertion.ITEM_PRICE, price);

        int rowSFinnished = getContentResolver().update(xActualURI, values, null, null);

        if (rowSFinnished == 0) {
            Toast.makeText(this, getString(R.string.error_update_item), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.item_update_done), Toast.LENGTH_LONG).show();
        }
    }

    private boolean comparison(Bitmap firstMethod, Bitmap secondMethod) {
        ByteBuffer firstBuffer = ByteBuffer.allocate(firstMethod.getHeight()*firstMethod.getRowBytes());
        firstMethod.copyPixelsToBuffer(firstBuffer);

        ByteBuffer secondBuffer = ByteBuffer.allocate(secondMethod.getHeight()*secondMethod.getRowBytes());
        secondMethod.copyPixelsToBuffer(secondBuffer);

        return Arrays.equals(firstBuffer.array(), secondBuffer.array());
    }

    public void onEraseButton(View view) {
        EraseDialogConf();
    }

    private void EraseDialogConf() {
        AlertDialog.Builder masterQ = new AlertDialog.Builder(this);
        masterQ.setMessage(getString(R.string.delete_Q));
        masterQ.setPositiveButton(getString(R.string.button_erase),
                new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                eraseItem();
            }
        });
        masterQ.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alertDialog = masterQ.create();
        alertDialog.show();
    }

    private void eraseItem() {
        int rowsDeleted = getContentResolver().delete(xActualURI, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.error_delete_item), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.item_deleted), Toast.LENGTH_LONG).show();
        }
        finish();
    }


//Assisted code from StackOverTheFlow

    @Override
    public void onRequestPermissionsResult(int requestCode, String permiss[], int[] resultsOK) {
        switch (requestCode) {
            case PermissionDialog.READ_IN_STORAGE_PERM: {
                if (resultsOK.length > 0 && resultsOK[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                                    PermissionDialog.READ_IN_STORAGE_PERM);
                } else {
                    Toast.makeText(ChangeItemValues.this, getString(R.string.error_permiss), Toast.LENGTH_LONG).show();
                }
            }}}

    private boolean askAboutPermission(final Context context) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PermissionDialog.askAboutPermission(context.getString(R.string.next_storage), context, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionDialog.READ_IN_STORAGE_PERM);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void orderFunction(View view) {
        String itemName = xChangeNameTextV.getText().toString().trim();
        String mailMsg = mailSummary(itemName);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject) + " " + itemName);
        intent.putExtra(Intent.EXTRA_TEXT, mailMsg);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String mailSummary(String itemName) {
        String message = "";
        message += getString(R.string.first_line) + "\n\n";
        message += getString(R.string.mail_body) + "\n\n\n";
        message += getString(R.string.name_of_item) + " " + itemName + "\n\n";
        message += getString(R.string.best_regards);
        return message;
    }
}
package com.hkust.comp4521.hippos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hkust.comp4521.hippos.datastructures.Category;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.utils.ImageRetriever;
import com.hkust.comp4521.hippos.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EditInventoryActivity extends AppCompatActivity {

    // activity
    private Context mContext;

    // Mode Flags
    private int currentMode = -1;
    public static int MODE_NEW_INVENTORY = 0;
    public static int MODE_EDIT_INVENTORY = 1;
    public static int MODE_SELECT_PICTURE = 2;

    // Views
    private RelativeLayout mActionBar;
    private Spinner categorySpinner;
    private ImageView ivHeroImage;
    private EditText etItemName, etItemPrice, etItemStock;
    private ImageButton btnFinish;
    private TextView actionBarTitle;

    // Data
    private Uri selectedFileUri, outputFileUri;
    private Inventory mItem;
    private File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inventory);

        mContext = this;
        currentMode = MODE_NEW_INVENTORY;

        // Change action bar theme
        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mActionBar));

        initViews();

        // get information from previous activity (for editing inventory)
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            int invId = bundle.getInt(Inventory.INVENTORY_INV_ID);
            mItem = Commons.getInventory(invId);

            // setup info to edit text
            etItemName.setText(mItem.getName());
            etItemPrice.setText(mItem.getPrice()+"");
            etItemStock.setText(mItem.getStock()+"");
            categorySpinner.setSelection(Commons.getCategoryIndex(mItem.getCategory()));

            // setup image view
            new ImageRetriever(ivHeroImage, mItem.getImage(), getResources().getDrawable(R.mipmap.placeholder)).execute();

            // setup mode flag
            currentMode = MODE_EDIT_INVENTORY;
            actionBarTitle.setText(getString(R.string.title_activity_edit_inventory));
        }

    }

    private void initViews() {
        // text fields
        ivHeroImage = (ImageView) findViewById(R.id.iv_edit_inventory_hero_image);
        etItemName = (EditText) findViewById(R.id.et_edit_inventory_item_name);
        etItemPrice = (EditText) findViewById(R.id.et_edit_inventory_item_price);
        etItemStock = (EditText) findViewById(R.id.et_edit_inventory_item_stock);
        actionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
        actionBarTitle.setText(getResources().getString(R.string.title_activity_edit_inventory_new));

        // buttons
        btnFinish = (ImageButton) findViewById(R.id.ib_edit_inventory_complete_item);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMode == MODE_NEW_INVENTORY) {
                    createNewInventory();
                } else if(currentMode == MODE_EDIT_INVENTORY) {
                    editInventory();
                }
            }
        });

        // preview image
        ivHeroImage.setScaleType(ImageView.ScaleType.CENTER);
        ivHeroImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        // category spinner
        categorySpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<Category> spinnerAdapter = new ArrayAdapter<Category>(EditInventoryActivity.this, android.R.layout.simple_spinner_dropdown_item, Commons.getCategoryList());
        categorySpinner.setAdapter(spinnerAdapter);
    }

    private void editInventory() {
        // get values from view
        try {
            String name = etItemName.getText().toString();
            Double price = Double.parseDouble(etItemPrice.getText().toString());
            int stock = Integer.parseInt(etItemStock.getText().toString());
            final int category = ((Category) categorySpinner.getSelectedItem()).getID();

            RestClient.getInstance(mContext).updateInventory(mItem.getId(), name, price, stock, mItem.getStatus(), category, new RestListener<Inventory>() {
                @Override
                public void onSuccess(Inventory inventory) {
                    Toast.makeText(mContext, "Inventory " + inventory.getName() + " updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(int status) {
                    Toast.makeText(mContext, "Failed: status code=" + status, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } catch(NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void createNewInventory() {
        // get values from view
        String name = etItemName.getText().toString();
        Double price = Double.parseDouble(etItemPrice.getText().toString());
        int stock = Integer.parseInt(etItemStock.getText().toString());
        int category = ((Category) categorySpinner.getSelectedItem()).getID();

        RestClient.getInstance(mContext).createInventory(name, price, stock, selectedFile, category, new RestListener<Inventory>() {
            @Override
            public void onSuccess(Inventory inventory) {
                Toast.makeText(mContext, "Inventory " + inventory.getName() + " created!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int status) {
                Toast.makeText(mContext, "Failed: status code=" + status, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // For selecting image from device
    // Solution suggested from: http://stackoverflow.com/questions/27873894/dialog-screen-like-whatsapp-profile-photo-dialog-screen
    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(Commons.APP_ROOT_PATH);
        root.mkdirs();
        final String fname = ImageUtils.getUniqueImageFilename(".jpg");
        final File sdImageMainDirectory = new File(root, fname);
        selectedFile = sdImageMainDirectory;
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, MODE_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == MODE_SELECT_PICTURE)
            {
                final boolean isCamera;
                if(data == null)
                    isCamera = true;
                else
                {
                    final String action = data.getAction();
                    if(action == null)
                    {
                        isCamera = false;
                    }
                    else
                    {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if(isCamera)
                    selectedImageUri = outputFileUri;
                else
                    selectedImageUri = data == null ? null : data.getData();

                selectedFileUri = selectedImageUri;

                // change image preview
                Bitmap mBitmap = null;
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedFileUri);
                    mBitmap = ImageUtils.getSizedBitmap(mBitmap, 512);
                    selectedFile = ImageUtils.writeBitmapToFile(mBitmap, ImageUtils.UPLOAD_IMAGE_PATH);
                    ivHeroImage.setImageBitmap(mBitmap);

                    if(currentMode == MODE_EDIT_INVENTORY) {
                        uploadImageToServer();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImageToServer() {
        RestClient.getInstance(mContext).updateInventoryImage(mItem.getId(), selectedFile, new RestListener<Inventory>() {
            @Override
            public void onSuccess(Inventory inventory) {
                // Delete the old image
                ImageUtils.deleteFile(mItem);
            }

            @Override
            public void onFailure(int status) {
                Toast.makeText(mContext, "Image not uploaded!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

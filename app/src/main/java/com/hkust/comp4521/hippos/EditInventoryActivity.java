package com.hkust.comp4521.hippos;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.utils.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EditInventoryActivity extends AppCompatActivity {

    // Mode Flags
    public static int MODE_NEW_INVENTORY = 0;
    public static int MODE_EDIT_INVENTORY = 1;
    public static int MODE_SELECT_PICTURE = 2;

    // Views
    private RelativeLayout mActionBar;
    private ImageView ivHeroImage;
    private EditText etItemName;
    private ImageButton btnFinish;

    // Data
    private Uri selectedFileUri, outputFileUri;
    private Inventory mItem;
    private File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inventory);

        // Change action bar theme
        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        TintedStatusBar.changeStatusBarColor(this, TintedStatusBar.getColorFromTag(mActionBar));

        // Init views
        ivHeroImage = (ImageView) findViewById(R.id.iv_edit_inventory_hero_image);
        etItemName = (EditText) findViewById(R.id.et_edit_inventory_item_name);
        btnFinish = (ImageButton) findViewById(R.id.ib_edit_inventory_complete_item);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFile != null)
                    Toast.makeText(EditInventoryActivity.this, selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });

        // get information from previous activity
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            int invId = bundle.getInt(Inventory.INVENTORY_INV_ID);
            mItem = Commons.getInventory(invId);

            // setup info to edit text
            etItemName.setText(mItem.getName());
        }

        ivHeroImage.setScaleType(ImageView.ScaleType.CENTER);
        ivHeroImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // For selecting image from device
    // Solution suggested from: http://stackoverflow.com/questions/27873894/dialog-screen-like-whatsapp-profile-photo-dialog-screen
    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(ImageUtils.IMAGE_ROOT_PATH);
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
                FileOutputStream out = null;
                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedFileUri);
                    // save to file for upload
                    out = new FileOutputStream(ImageUtils.UPLOAD_IMAGE_PATH);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                    ivHeroImage.setImageBitmap(mBitmap);
                    selectedFile = new File(ImageUtils.UPLOAD_IMAGE_PATH);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

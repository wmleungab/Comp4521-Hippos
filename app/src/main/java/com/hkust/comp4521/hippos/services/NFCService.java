package com.hkust.comp4521.hippos.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.nio.charset.Charset;

public class NFCService {

    public Activity mActivity;
    public Context mContext;
    private PackageManager mPackageManager;
    private NfcAdapter mNfcAdapter;
    private Uri[] mFileUris;

    public NFCService(Activity act, Context con) {
        mActivity = act;
        mContext = con;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
        mPackageManager = mContext.getPackageManager();
    }

    public boolean isNFCAvailable() {
        if (!mPackageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(mContext, "The device does not has NFC feature.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mNfcAdapter == null) {
            Toast.makeText(mContext, "NFC is not available", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(mContext, "NFC is not enabled", Toast.LENGTH_LONG).show();
            mContext.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            return false;
        }
        return true;
    }

    public NfcAdapter getNfcAdapter() {
        return mNfcAdapter;
    }
}

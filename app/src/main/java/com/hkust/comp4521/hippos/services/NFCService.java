package com.hkust.comp4521.hippos.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.widget.Toast;

public class NFCService {

	Activity aty;
	Context ctx;
	NfcAdapter adapter;
	Tag mytag;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	NFCReadTagListener readTagListener;
	NFCWriteTagListener writeTagListener;
	boolean writeMode;
	
	public NFCService(Activity pAct, Context pCon) {
		ctx = pCon;
		aty = pAct;
		
		// get nfc adapter for context
		adapter = NfcAdapter.getDefaultAdapter(ctx);

		// catch tag action by activity instead
		pendingIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, ctx.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };
	}
	

	public void write(String text, Tag tag) throws IOException, FormatException {
		NdefRecord[] records = { createRecord(text) };
		NdefMessage message = new NdefMessage(records);
		// Get an instance of Ndef for the tag.
		Ndef ndef = Ndef.get(tag);
		// Enable I/O
		ndef.connect();
		// Write the message
		ndef.writeNdefMessage(message);
		// Close the connection
		ndef.close();
	}
	
	public boolean tagDetected() {
		return (mytag != null);
	}
	

	private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
		String lang = "en";
		byte[] textBytes = text.getBytes();
		byte[] langBytes = lang.getBytes("US-ASCII");
		int langLength = langBytes.length;
		int textLength = textBytes.length;
		byte[] payload = new byte[1 + langLength + textLength];

		// set status byte (see NDEF spec for actual bits)
		payload[0] = (byte) langLength;

		// copy langbytes and textbytes into payload
		System.arraycopy(langBytes, 0, payload, 1, langLength);
		System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

		NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

		return recordNFC;
	}


	public void WriteModeOn() {
		if(writeMode == false && adapter != null) {
			writeMode = true;
			adapter.enableForegroundDispatch(aty, pendingIntent, writeTagFilters, null);
		}
	}

	public void WriteModeOff() {
		if(writeMode == true && adapter != null) {
			writeMode = false;
			adapter.disableForegroundDispatch(aty);
		}
	}

	/**
	 * Background task for reading the data. Do not block the UI thread while reading. 
	 * 
	 * @author Ralf Wondratschek
	 *
	 */
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
	    @Override
	    protected String doInBackground(Tag... params) {
	        Tag tag = params[0];
	        Ndef ndef = Ndef.get(tag);
	        if (ndef == null) {
	            // NDEF is not supported by this Tag. 
	            return null;
	        }
	        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
	        NdefRecord[] records = null;
	        if(ndefMessage != null)		{
	        	records = ndefMessage.getRecords();
		        for (NdefRecord ndefRecord : records) {
		            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
		                try {
		                    return readText(ndefRecord);
		                } catch (UnsupportedEncodingException e) {
		        			e.printStackTrace();
		                }
		            }
		        }
	        }
	        return null;
	    }
	     
	    private String readText(NdefRecord record) throws UnsupportedEncodingException {
	        /*
	         * See NFC forum specification for "Text Record Type Definition" at 3.2.1 
	         * 
	         * http://www.nfc-forum.org/specs/
	         * 
	         * bit_7 defines encoding
	         * bit_6 reserved for future use, must be 0
	         * bit_5..0 length of IANA language code
	         */
	 
	        byte[] payload = record.getPayload();
	 
	        // Get the Text Encoding
	        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
	 
	        // Get the Language Code
	        int languageCodeLength = payload[0] & 0063;
	         
	        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
	        // e.g. "en"
	         
	        // Get the Text
	        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
	    }
	     
	    @Override
	    protected void onPostExecute(String result) {
	        if (result != null) {
	            if(readTagListener != null)
	            	readTagListener.onTagRead(result);
	        } else {
	            if(readTagListener != null)
	            	readTagListener.onError();
	        }
	    }
	}

	public Tag discoverTag(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			//Toast.makeText(ctx,ctx.getString(R.string.ok_detection),Toast.LENGTH_LONG).show();
			return mytag;
		}
		return null;
	}
	
	public void readTag(NFCReadTagListener cbListener) {
		readTagListener = cbListener;
		new NdefReaderTask().execute(mytag);
	}
	
	public void writeTag(String textStr, NFCWriteTagListener cbListener) {
		writeTagListener = cbListener;
		try {
			write(textStr, mytag);
			//Toast.makeText(ctx, ctx.getString(R.string.ok_writing), Toast.LENGTH_LONG).show();
			writeTagListener.onTagWrite(textStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(ctx, "ERROR:" + e, Toast.LENGTH_SHORT).show();
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(ctx, "ERROR:" + e, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(ctx, "ERROR:" + e, Toast.LENGTH_SHORT).show();
		}
	}

	public interface NFCReadTagListener {
		public void onTagRead(String readStr);
		public void onError();
		
	}
	
	public interface NFCWriteTagListener {
		public void onTagWrite(String writeStr);
	}
}

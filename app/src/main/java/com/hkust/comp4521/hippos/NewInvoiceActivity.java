package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hkust.comp4521.hippos.database.InvoiceDB;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.Invoice;
import com.hkust.comp4521.hippos.datastructures.InvoiceInventory;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.NFCService;
import com.hkust.comp4521.hippos.services.TintedStatusBar;
import com.hkust.comp4521.hippos.views.InvoiceInventoryListAdapter;
import com.hkust.comp4521.hippos.views.SwipeDismissRecyclerViewTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewInvoiceActivity extends AppCompatActivity {

    // Activity-related
    private Activity mActivity;
    private Context mContext;
    private InvoiceInventoryListAdapter mAdapter;
    private boolean inventoryDataReady = false;

    // Views
    private RelativeLayout mActionBar;
    private ImageButton btnAddFromInventoryList, btnCompleteSales;
    MaterialDialog mSalesConfirmDialog;
    private EditText etPrice, etPaid;

    // Services
    private NFCService mNFC;

    // Data
    private Invoice mInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice);

        mActivity = this;
        mContext = this;
        mNFC = new NFCService(mActivity, mContext);

        mActionBar = (RelativeLayout) findViewById(R.id.actionBar);
        btnAddFromInventoryList = (ImageButton) findViewById(R.id.ib_new_invoice_add_from_inv_list);
        btnCompleteSales = (ImageButton) findViewById(R.id.ib_new_invoice_complete);
        TintedStatusBar.changeStatusBarColor(mActivity, TintedStatusBar.getColorFromTag(mActionBar));

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        mAdapter = new InvoiceInventoryListAdapter(mContext, Commons.MODE_NEW_INVOICE);
        recList.setAdapter(mAdapter);
        //recList.setItemAnimator(new DefaultItemAnimator());
        recList.setItemAnimator(null);

        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        recList,
                        new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                //for (int position : reverseSortedPositions) {
                                    mAdapter.getInvoiceInventories().remove(reverseSortedPositions[0]);
                                //}
                                // do not call notifyItemRemoved for every item, it will cause gaps on deleting items
                                //mAdapter.notifyDataSetChanged();
                                mAdapter.notifyItemRemoved(reverseSortedPositions[0]);
                            }
                        });
        recList.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        recList.addOnScrollListener(touchListener.makeScrollListener());

        btnAddFromInventoryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, InventoryListActivity.class);
                i.putExtra("selection_mode", true);
                startActivityForResult(i, InventoryListActivity.MODE_SELECT_INVENTORY);
            }
        });
        btnCompleteSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate view for dialog
                LayoutInflater mInflater = getLayoutInflater().from(mContext);
                View vv = mInflater.inflate(R.layout.dialog_sales_confirm_new, null);
                mSalesConfirmDialog = new MaterialDialog.Builder(mActivity)
                        .customView(vv, false)
                        .autoDismiss(false)
                        .positiveText(mContext.getString(R.string.dialog_okay))
                        .negativeText(mContext.getString(R.string.dialog_cancel))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                if (etPaid.getText().toString().trim().length() > 0) {
                                    // check if price is valid first
                                    double totalPrice = Double.parseDouble(etPrice.getText().toString());
                                    double paidPrice = Double.parseDouble(etPaid.getText().toString());
                                    if (totalPrice > paidPrice) {
                                        etPaid.setError("Please input a valid price");
                                    } else {
                                        generateInvoiceRecord(totalPrice, paidPrice);
                                        dialog.dismiss();
                                    }
                                } else {
                                    etPaid.setError("Please input a price!");
                                }
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                // Put price data into the view
                double price = mAdapter.getInvoiceInventoriesTotal();
                TextView tvTotal = (TextView) vv.findViewById(R.id.tv_sales_confirms_item_total);
                tvTotal.setText(price + "");
                etPrice = (EditText) vv.findViewById(R.id.et_sales_confirm_final_charge);
                etPrice.setText(price + "");
                etPaid = (EditText) vv.findViewById(R.id.et_sales_confirm_paid);
                etPaid.setText("");

                List<InvoiceInventory> invList = mAdapter.getInvoiceInventories();
                LinearLayout tableLayout1 = (LinearLayout) vv.findViewById(R.id.tableLayout1);
                for(InvoiceInventory inv : invList) {
                    String iName = inv.getInventory().getName();
                    double iPrice = inv.getPrice();
                    int iQuant = inv.getQuantity();

                    TableRow tR = new TableRow(mContext);
                    tR.setPadding(5, 5, 5, 5);

                    TextView tV_txt1 = new TextView(mContext);
                    TextView tV_txt2 = new TextView(mContext);
                    TextView tV_txt3 = new TextView(mContext);


                    tV_txt1.setText(iName);
                    tV_txt2.setText(iPrice+"");
                    tV_txt2.setText(iQuant+"");

                    tR.addView(tV_txt1);
                    tR.addView(tV_txt2);

                    tableLayout1.addView(tR);
                }
            }
        });

        // Initialize Inventory List
        Commons.recoverLoginInformation(mContext, new Commons.onInitializedListener() {
            @Override
            public void onInitialized() {
                Commons.initializeInventoryList(new Commons.onInitializedListener() {
                    @Override
                    public void onInitialized() {
                        inventoryDataReady = true;
                    }
                });
            }
        });
    }

    private void generateInvoiceRecord(double totalPrice, double paidPrice) {
        // Setup new invoice object
        List<InvoiceInventory> invList = mAdapter.getInvoiceInventories();
        Invoice invoice = new Invoice();
        invoice.setInvoiceInventories(invList);
        invoice.setUser(Commons.getUser().name);
        invoice.setEmail(Commons.getUser().email);
        invoice.setTotalPrice(mAdapter.getInvoiceInventoriesTotal());
        invoice.setFinalPrice(totalPrice);
        invoice.setPaid(paidPrice);
        invoice.setDateTime(invoice.generateCurrentDatetimeString());
        // if the device is online, try to upload to server first
        if(Commons.ONLINE_MODE) {
            RestClient.getInstance().createInvoice(invoice, new RestListener<Invoice>() {
                @Override
                public void onSuccess(Invoice invoice) {
                    Toast.makeText(mContext, "Invoice created!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mContext, SalesDetailsActivity.class);
                    SalesDetailsActivity.setCurrentInvoice(invoice);
                    startActivity(i);
                }

                @Override
                public void onFailure(int status) {

                }
            });
        }
        InvoiceDB invoiceHelper = InvoiceDB.getInstance();
        //invoiceHelper.insert(invoice);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == InventoryListActivity.MODE_SELECT_INVENTORY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                Inventory inv = Commons.getInventory(b.getInt(Inventory.INVENTORY_INV_ID));
                mAdapter.addItem(new InvoiceInventory(inv, 1));
            }
        }
    }

    // NFC intent received actions
    @Override
    protected void onNewIntent(Intent intent) {
        if(inventoryDataReady && mNFC.discoverTag(intent) != null) {
            mNFC.readTag(new NFCService.NFCReadTagListener() {
                @Override
                public void onTagRead(String readStr) {
                    try {
                        JSONObject nfcJSON = new JSONObject(readStr);
                        Toast.makeText(mContext, readStr, Toast.LENGTH_SHORT).show();
                        Inventory inv = Commons.getInventory(nfcJSON.getInt(Inventory.INVENTORY_INV_ID));
                        mAdapter.addItem(new InvoiceInventory(inv, 1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, mContext.getString(R.string.nfc_error_msg), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onError() {
                    Toast.makeText(mContext, mContext.getString(R.string.nfc_error_msg), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mNFC.WriteModeOff();
        overridePendingTransition(R.anim.none, android.R.anim.fade_out);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNFC.WriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        mNFC.WriteModeOn();
    }
}

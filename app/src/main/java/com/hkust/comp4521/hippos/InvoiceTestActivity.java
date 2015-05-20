package com.hkust.comp4521.hippos;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hkust.comp4521.hippos.datastructures.Invoice;
import com.hkust.comp4521.hippos.datastructures.User;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;

import java.util.List;


public class InvoiceTestActivity extends ActionBarActivity implements View.OnClickListener {
    RestClient rc;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_test);

        findViewById(R.id.btn_invoice_get).setOnClickListener(this);
        findViewById(R.id.get_all_invoice_btn).setOnClickListener(this);
        findViewById(R.id.cre_inv_btn).setOnClickListener(this);
        findViewById(R.id.update_invoice_btn).setOnClickListener(this);


        String email = "wmleungab@gmail.com";
        String password = "456123";

        rc = RestClient.getInstance();
        rc.login(email, password, new RestListener<User>() {
            @Override
            public void onSuccess(User user) {
                userId = user.id;
            }

            @Override
            public void onFailure(int status) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invoice_test, menu);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_invoice_get: {
                EditText ev = (EditText) findViewById(R.id.editText7);
                String content = ev.getText().toString();

                rc.getInvoice(Integer.parseInt(content), new RestListener<Invoice>() {
                    @Override
                    public void onSuccess(Invoice invoice) {
                        TextView tv = (TextView) findViewById(R.id.textView3);
                        tv.setText("ID: " + invoice.getId() + "\n" +
                                "total_price: " + invoice.getTotalPrice() + "\n" +
                                "final_price: " + invoice.getFinalPrice() + "\n" +
                                "date_time: " + invoice.getDateTime() + "\n" +
                                "user_id: " + invoice.getUser() + "\n" +
                                "content: " + invoice.getContent() + "\n" +
                                "email: " + invoice.getEmail() + "\n" +
                                "status: " + invoice.getStatus());
                    }

                    @Override
                    public void onFailure(int status) {
                        if (status == 3) {
                            TextView tv = (TextView) findViewById(R.id.textView3);
                            tv.setText("Invalid");
                        }
                    }
                });
                break;
            }
            case R.id.get_all_invoice_btn: {
                rc.getAllInvoice(new RestListener<List<Invoice>>() {
                    @Override
                    public void onSuccess(List<Invoice> invoices) {
                        TextView tv = (TextView) findViewById(R.id.textView3);
                        tv.setText("size: " + invoices.size() +
                                " ID: " + invoices.get(0).getId() + "\n" +
                                "total_price: " + invoices.get(0).getTotalPrice() + "\n" +
                                "final_price: " + invoices.get(0).getFinalPrice() + "\n" +
                                "date_time: " + invoices.get(0).getDateTime() + "\n" +
                                "user_id: " + invoices.get(0).getUser() + "\n" +
                                "content: " + invoices.get(0).getContent() + "\n" +
                                "email: " + invoices.get(0).getEmail() + "\n" +
                                "status: " + invoices.get(0).getStatus());
                    }

                    @Override
                    public void onFailure(int status) {

                    }
                });
                break;
            }
            case R.id.cre_inv_btn: {
                double t_price = 80.0;
                double f_price = 20.0;
                String content = "{ 2:3 6:2 }";
                String email = "yolwyman@gmail.com";
                rc.createInvoice(t_price, f_price, content, email, new RestListener<Invoice>() {
                    @Override
                    public void onSuccess(Invoice invoice) {
                        TextView tv = (TextView) findViewById(R.id.textView3);
                        tv.setText("ID: " + invoice.getId() + "\n" +
                                "total_price: " + invoice.getTotalPrice() + "\n" +
                                "final_price: " + invoice.getFinalPrice() + "\n" +
                                "date_time: " + invoice.getDateTime() + "\n" +
                                "user_id: " + invoice.getUser() + "\n" +
                                "content: " + invoice.getContent() + "\n" +
                                "email: " + invoice.getEmail() + "\n" +
                                "status: " + invoice.getStatus());
                    }

                    @Override
                    public void onFailure(int status) {
                        switch (status) {
                            case 4: {
                                TextView tv = (TextView) findViewById(R.id.textView3);
                                tv.setText("Invalid Email");
                            }
                        }
                    }
                });


                break;
            }
            case R.id.update_invoice_btn: {
                double t_price = 80.0;
                double f_price = 30.0;
                String content = "{ 2:4 6:2 }";
                String email = "yolwyman@gmail.com";
                EditText ev = (EditText) findViewById(R.id.editText7);
                String s = ev.getText().toString();

                rc.updateInvoice(Integer.parseInt(s), t_price, f_price, content, email, 1, new RestListener<Invoice>() {
                    @Override
                    public void onSuccess(Invoice invoice) {
                        TextView tv = (TextView) findViewById(R.id.textView3);
                        tv.setText("ID: " + invoice.getId() + "\n" +
                                "total_price: " + invoice.getTotalPrice() + "\n" +
                                "final_price: " + invoice.getFinalPrice() + "\n" +
                                "date_time: " + invoice.getDateTime() + "\n" +
                                "user_id: " + invoice.getUser() + "\n" +
                                "content: " + invoice.getContent() + "\n" +
                                "email: " + invoice.getEmail() + "\n" +
                                "status: " + invoice.getStatus());
                    }

                    @Override
                    public void onFailure(int status) {
                        switch (status) {
                            case 4: {
                                TextView tv = (TextView) findViewById(R.id.textView3);
                                tv.setText("Invalid Email");
                                break;
                            }
                            case 3: {
                                TextView tv = (TextView) findViewById(R.id.textView3);
                                tv.setText("Invalid ,not exist or no update");
                                break;
                            }
                        }
                    }
                });


                break;
            }
        }
    }
}

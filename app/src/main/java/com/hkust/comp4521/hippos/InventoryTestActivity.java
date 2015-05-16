package com.hkust.comp4521.hippos;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hkust.comp4521.hippos.datastructures.NetInventory;
import com.hkust.comp4521.hippos.datastructures.User;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;

import java.util.List;


public class InventoryTestActivity extends ActionBarActivity implements View.OnClickListener {
    RestClient rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_test);

        findViewById(R.id.invent_create_btn).setOnClickListener(this);
        findViewById(R.id.invent_get_btn).setOnClickListener(this);
        findViewById(R.id.get_all_inv_btn).setOnClickListener(this);
        findViewById(R.id.update_inve).setOnClickListener(this);
        String email = "wmleungab@gmail.com";
        String password = "456123";
        rc = new RestClient();
        rc.login(email, password, new RestListener<User>() {
            @Override
            public void onSuccess(User user) {

            }

            @Override
            public void onFailure(int status) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory_test, menu);
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
            case R.id.invent_create_btn: {
                String name = "Transformer Comic";
                int stock = 10;
                double price = 50.0;
                int category = 5;


                rc.createInventory(name, price, stock, category, new RestListener<NetInventory>() {

                    @Override
                    public void onSuccess(NetInventory netInventory) {
                        TextView tv = (TextView) findViewById(R.id.textView333);
                        tv.setText("name: " + netInventory.getName() + "\n" +
                                "Price: " + netInventory.getPrice() + "\n" +
                                "Stock: " + netInventory.getStock() + "\n" +
                                "Status: " + netInventory.getStatus() + "\n" +
                                "TimeStamp: " + netInventory.getTimeStamp() + "\n" +
                                "Category: " + netInventory.getCategory() + "\n");
                    }

                    @Override
                    public void onFailure(int status) {

                    }
                });
                break;
            }
            case R.id.invent_get_btn: {
                EditText ed = (EditText) findViewById(R.id.editText3);

                rc.getInventory(Integer.parseInt(ed.getText().toString()), new RestListener<NetInventory>() {
                    @Override
                    public void onSuccess(NetInventory netInventory) {
                        TextView tv = (TextView) findViewById(R.id.textView333);
                        tv.setText("name: " + netInventory.getName() + "\n" +
                                "Price: " + netInventory.getPrice() + "\n" +
                                "Stock: " + netInventory.getStock() + "\n" +
                                "Status: " + netInventory.getStatus() + "\n" +
                                "TimeStamp: " + netInventory.getTimeStamp() + "\n" +
                                "Category: " + netInventory.getCategory() + "\n");
                    }

                    @Override
                    public void onFailure(int status) {
                        if (status == 3) {
                            TextView tv = (TextView) findViewById(R.id.textView333);
                            tv.setText("Invalid");
                        }
                    }
                });
                break;
            }
            case R.id.get_all_inv_btn: {
                rc.getAllInventory(new RestListener<List<NetInventory>>() {

                    @Override
                    public void onSuccess(List<NetInventory> netInventories) {
                        TextView tv = (TextView) findViewById(R.id.textView333);
                        tv.setText("Size of list= " + netInventories.size() + "\n" +
                                "name: " + netInventories.get(0).getName() + "\n" +
                                "Price: " + netInventories.get(0).getPrice() + "\n" +
                                "Stock: " + netInventories.get(0).getStock() + "\n" +
                                "Status: " + netInventories.get(0).getStatus() + "\n" +
                                "TimeStamp: " + netInventories.get(0).getTimeStamp() + "\n" +
                                "Category: " + netInventories.get(0).getCategory() + "\n");
                    }

                    @Override
                    public void onFailure(int status) {

                    }
                });
                break;
            }
            case R.id.update_inve: {
                int num = 25;
                String name = "Transformer Comic 2";
                int stock = 10;
                double price = 60.5;
                int category = 5;
                rc.updateInventory(num, name, price, stock, 1, category, new RestListener<NetInventory>() {
                    @Override
                    public void onSuccess(NetInventory netInventory) {
                        TextView tv = (TextView) findViewById(R.id.textView333);
                        tv.setText("name: " + netInventory.getName() + "\n" +
                                "Price: " + netInventory.getPrice() + "\n" +
                                "Stock: " + netInventory.getStock() + "\n" +
                                "Status: " + netInventory.getStatus() + "\n" +
                                "TimeStamp: " + netInventory.getTimeStamp() + "\n" +
                                "Category: " + netInventory.getCategory() + "\n");
                    }

                    @Override
                    public void onFailure(int status) {

                    }
                });
                break;
            }
        }

    }
}

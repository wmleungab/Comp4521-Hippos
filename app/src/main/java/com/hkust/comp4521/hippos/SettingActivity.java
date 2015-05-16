package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hkust.comp4521.hippos.datastructures.Category;
import com.hkust.comp4521.hippos.datastructures.User;
import com.hkust.comp4521.hippos.rest.RestClient;

public class SettingActivity extends Activity implements View.OnClickListener {
    User responseUser;
    RestClient rc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();


    }

    private void init() {
        rc = new RestClient();
        findViewById(R.id.setting_btn).setOnClickListener(this);
        findViewById(R.id.setting_btn2).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
        String email = "wmleungab@gmail.com";
        String password = "456123";
//        RestClient.get().login(user.email, user.password, new Callback<Response>() {
//            @Override
//            public void success(Response response, Response response2) {
//
//                ((TextView)findViewById(R.id.setting_textView)).setText( response.getBody().toString()+"\n"+response2.getBody().toString());
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });

        if (view.getId() == R.id.setting_btn) rc.login(email, password);
        if (view.getId() == R.id.setting_btn2) {
            Category c = rc.createCategory("BLURAY");
            if (c != null) {
                TextView tv = (TextView) findViewById(R.id.setting_textView);
                tv.setText(c.getID() + "");
            }
        }

    }
}

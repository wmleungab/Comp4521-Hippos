package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hkust.comp4521.hippos.datastructures.User;
import com.hkust.comp4521.hippos.rest.Response_User;
import com.hkust.comp4521.hippos.rest.RestClient;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SettingActivity extends Activity implements View.OnClickListener {
    User responseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();


    }

    private void init() {
        findViewById(R.id.setting_btn).setOnClickListener(this);
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
        String password = "45123";
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
        RestClient.get().login(email, password, new Callback<Response_User>() {
            @Override
            public void success(Response_User responseUser, Response response) {
                if (!responseUser.error)
                    ((TextView) findViewById(R.id.setting_textView)).setText(responseUser.apiKey);
                else ((TextView) findViewById(R.id.setting_textView)).setText(responseUser.message);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        // ((TextView)findViewById(R.id.setting_textView)).setText(n_user.apiKey);
    }
}

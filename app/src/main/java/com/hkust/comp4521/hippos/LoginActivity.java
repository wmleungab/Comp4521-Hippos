package com.hkust.comp4521.hippos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.User;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;
import com.hkust.comp4521.hippos.services.PreferenceService;
import com.hkust.comp4521.hippos.services.ThreadService;


public class LoginActivity extends AppCompatActivity {

    // Views
    private LinearLayout loginLayout;
    private ImageView logoView;
    private EditText serverAddr, userName, password;
    private ImageButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init views
        serverAddr = (EditText) findViewById(R.id.et_login_server);
        userName = (EditText) findViewById(R.id.et_login_user);
        password = (EditText) findViewById(R.id.et_login_password);
        logoView = (ImageView) findViewById(R.id.iv_login_icon);
        loginLayout = (LinearLayout) findViewById(R.id.ll_login_textfields);

        // setup listener
        btnLogin = (ImageButton) findViewById(R.id.ib_login_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preLoginAnimation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private void preLoginAnimation()
    {
        // Move logo to center
        RelativeLayout root = (RelativeLayout) findViewById( R.id.rl_login_layout );
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics( dm );
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPos[] = new int[2];
        logoView.getLocationOnScreen( originalPos );

        int xDest = dm.widthPixels/2;
        xDest -= (logoView.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (logoView.getMeasuredHeight()/2) - statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation( 0, xDest - originalPos[0] , 0, yDest - originalPos[1] );
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // delayed login for smoother transition
                ThreadService.delayedStart(LoginActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        login();
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(600);
        anim.setFillAfter(true);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        logoView.startAnimation(anim);

        // Hide other layouts
        applyFadeAnimation(loginLayout, 0);
    }

    private void login() {
        // login to slim server
        RestClient instance = RestClient.getInstance();
        final String email = userName.getText().toString();
        final String pw = password.getText().toString();

        instance.login(email, pw, new RestListener<User>() {
            @Override
            public void onSuccess(User user) {
                Commons.setUser(user);
                // store login info to SharedPreferences
                PreferenceService.saveStringValue(PreferenceService.KEY_SERVER_LOCATION, serverAddr.getText().toString());
                PreferenceService.saveStringValue(PreferenceService.KEY_LOGIN_USERNAME, email);
                PreferenceService.saveStringValue(PreferenceService.KEY_LOGIN_PASSWORD, pw);
                // Initialize Inventory List
                Commons.initializeInventoryList(new Commons.onInventoryListInitializedListener() {
                    @Override
                    public void onInitialized() {
                        Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, R.anim.none);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(int status) {

            }
        });
    }

    public void applyFadeAnimation(View view, float fade) {
        AlphaAnimation alpha = new AlphaAnimation(1 - fade, fade);
        alpha.setDuration(300);
        alpha.setFillAfter(true);
        view.startAnimation(alpha);
    }
}

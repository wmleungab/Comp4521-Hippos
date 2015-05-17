package com.hkust.comp4521.hippos;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hkust.comp4521.hippos.datastructures.Category;
import com.hkust.comp4521.hippos.datastructures.User;
import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.rest.RestListener;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CategoryTestActivity extends Activity implements View.OnClickListener {
    User responseUser;
    RestClient rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_test);

        init();


    }

    private void init() {
        rc = new RestClient();
        findViewById(R.id.setting_btn).setOnClickListener(this);
        findViewById(R.id.setting_btn2).setOnClickListener(this);
        findViewById(R.id.get_all_cat).setOnClickListener(this);
        findViewById(R.id.getACat_btn).setOnClickListener(this);
        findViewById(R.id.updateCat_btn).setOnClickListener(this);
        findViewById(R.id.upload_btn).setOnClickListener(this);
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

        if (view.getId() == R.id.setting_btn) rc.login(email, password, new RestListener<User>() {
            @Override
            public void onSuccess(User user) {

            }

            @Override
            public void onFailure(int status) {

            }
        });
        if (view.getId() == R.id.setting_btn2) {
            rc.createCategory("BLURAY", new RestListener<Category>() {
                @Override
                public void onSuccess(Category category) {
                    if (category != null) {
                        TextView tv = (TextView) findViewById(R.id.setting_textView);
                        tv.setText(category.getID() + "");
                    }
                }

                @Override
                public void onFailure(int status) {

                }
            });
        }
        if (view.getId() == R.id.get_all_cat) {
            rc.getAllCategory(new RestListener<List<Category>>() {
                @Override
                public void onSuccess(List<Category> categories) {
                    if (categories != null) {
                        String s = "";
                        for (Category c : categories) {
                            s += c.getID() + " " + c.getName() + " ";
                        }
                        TextView tv = (TextView) findViewById(R.id.setting_textView);
                        tv.setText(" " + categories.size() + " " + s);

                    }
                }

                @Override
                public void onFailure(int status) {

                }
            });
        }

        if (view.getId() == R.id.getACat_btn) {
            EditText et = (EditText) findViewById(R.id.editText);
            String content = et.getText().toString();
            rc.getCategory(Integer.parseInt(content), new RestListener<Category>() {

                @Override
                public void onSuccess(Category category) {
                    TextView tv = (TextView) findViewById(R.id.setting_textView);
                    tv.setText(" " + category.getName());
                }

                @Override
                public void onFailure(int status) {
                    if (status == 3) {
                        TextView tv = (TextView) findViewById(R.id.setting_textView);
                        tv.setText("Invalid");
                    }
                }
            });
        }
        if (view.getId() == R.id.updateCat_btn) {
            EditText et1 = (EditText) findViewById(R.id.editText);
            String number = et1.getText().toString();
            EditText et2 = (EditText) findViewById(R.id.editText2);
            String content = et2.getText().toString();

            rc.updateCategory(Integer.parseInt(number), content, new RestListener<Category>() {

                @Override
                public void onSuccess(Category category) {
                    TextView tv = (TextView) findViewById(R.id.setting_textView);
                    tv.setText(" " + category.getName());
                }

                @Override
                public void onFailure(int status) {
                    if (status == 3) {
                        TextView tv = (TextView) findViewById(R.id.setting_textView);
                        tv.setText("update unsucessful");
                    }

                }
            });
        }
        if (view.getId() == R.id.upload_btn) {
            try {
                InputStream is = getAssets().open("dog_doll.jpg");

                AssetFileDescriptor descriptor = getAssets().openFd("dog_doll.jpg");
                FileReader reader = new FileReader(descriptor.getFileDescriptor());


                int l = is.available();
                Toast.makeText(this, l + "", Toast.LENGTH_LONG);
                Log.i("setting activity", "" + l);
                rc.fileUpload("dog", "jpg", is, new RestListener<String>() {

                    @Override
                    public void onSuccess(String s) {
                        TextView tv = (TextView) findViewById(R.id.setting_textView);
                        tv.setText("upload sucess");
                    }

                    @Override
                    public void onFailure(int status) {
                        TextView tv = (TextView) findViewById(R.id.setting_textView);
                        tv.setText("upload unsucessful");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

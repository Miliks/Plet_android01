package com.example.ldobriakova.plet_03;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import static android.app.PendingIntent.getActivity;

/**
 *
 * Login Activity Class
 *
 */
public class LoginActivity extends Activity {
    // Progress Dialog Object
    ProgressDialog prgDialog;
    // Error Msg TextView Object
    TextView errorMsg;
    Boolean isTeacher;
    // Passwprd Edit View Object
    EditText pwdET, usernameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Fabric.with(this, new Crashlytics());
        Bundle extras = getIntent().getExtras();
        isTeacher = extras.getBoolean("isTeacher");
        Log.d("What is returned on activity Login view ......", isTeacher.toString());
        usernameET = findViewById(R.id.username);
        // Find Password Edit View control by ID
        pwdET = findViewById(R.id.password);

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setIndeterminate(true);

    }
    @Override
    public void onBackPressed()
    {
        Intent startPersIntent = new Intent(getApplicationContext(), StartActivity.class);

        startActivity(startPersIntent);

    }


    public void attemptLogin(final View view) {
        Integer isTeacherInt;
        if(isTeacher)
            isTeacherInt = 1;
        else
            isTeacherInt = 0;
        String userNameView = usernameET.getText().toString();
        String passwordView = pwdET.getText().toString();
       // Log.d("MILA in go to register WIFI hotspot", "MIIIIIIIIIIIIII");
        if (!userNameView.isEmpty() && !passwordView.isEmpty()) {
            enableProgressDialog(true);


            RegisterAPI.getInstance(this).loginAPI(userNameView, passwordView,isTeacherInt, new RegisterAPI.RegistrationCallback() {
                @Override
                public void onResponse(String str) {
                    try {
                        enableProgressDialog(false);
                        JSONObject jsonResponse = new JSONObject(str);
                        String result = jsonResponse.getString("result");
                        //Log.d("What is returned on activity Login view ......", result);
                        if (result.equals("OK")) {
                            if(isTeacher){
                                Intent i = new Intent(LoginActivity.this, ListGroups.class);
                                String userName = usernameET.getText().toString();
                                i.putExtra("userName", userName);
                                prgDialog.dismiss();
                                startActivity(i);
                            }
                             else {
                                Intent i = new Intent(LoginActivity.this, SelectChild.class);
                                String userName = usernameET.getText().toString();
                                i.putExtra("userName", userName);
                                Log.d("What is returned on activity Login view ......", userName);
                                prgDialog.dismiss();
                                startActivity(i);
                            }
                        } else {
                            cleanText(jsonResponse.getString("message"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(RegistrationResponse.RegistrationError error) {
                    enableProgressDialog(false);
                }

                @Override
                public void onNetworkError() {
                    enableProgressDialog(false);
                }
            });
        } else
            Toast.makeText(getApplicationContext(), "No data inserted", Toast.LENGTH_LONG).show();
    }

    private void enableProgressDialog(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enable)
                    prgDialog.show();
                else
                    prgDialog.hide();
            }
        });
    }

    private void cleanText(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                usernameET.getText().clear();
                pwdET.getText().clear();
            }
        });
    }

    /**
     * Method gets triggered when Register button is clicked
     *
     * @param view
     */
    public void navigatetoRegisterActivity(View view) {

        Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
        i.putExtra("isTeacher",isTeacher);
        startActivity(i);

    }


    public void forgetPwd(View view) {
        String url = "http://plet.cloud.reply.eu/PletPlatform/Account/ResetPasswordMobile.aspx";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


}

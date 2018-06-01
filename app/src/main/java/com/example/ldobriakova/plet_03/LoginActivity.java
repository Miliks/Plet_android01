package com.example.ldobriakova.plet_03;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

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
    // Passwprd Edit View Object
    EditText pwdET, usernameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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


    public void attemptLogin(final View view) {

        String userNameView = usernameET.getText().toString();
        String passwordView = pwdET.getText().toString();
        if(!userNameView.isEmpty()&&!passwordView.isEmpty()) {
            enableProgressDialog(true);

                RegisterAPI.getInstance(this).userLogin(userNameView, passwordView, new RegisterAPI.RegistrationCallback() {
                @Override
                    public void onResponse(String str) {
                    try {
                enableProgressDialog(false);
                JSONObject jsonResponse = new JSONObject(str);
                String result = jsonResponse.getString("result");
                //Log.d("What is returned on activity Login view ......", result);
                if (result.equals("OK")) {
                    Log.d("attemptToLogin", "SUCCESSSSSSSS!!!!!..");
                    Intent i = new Intent(LoginActivity.this, SelectBaby.class);
                    String userName = usernameET.getText().toString();
                    i.putExtra("userName", userName);
                    prgDialog.dismiss();
                    startActivity(i);
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
}
else
    Toast.makeText(getApplicationContext(), "No data inserted", Toast.LENGTH_LONG).show();
 }
    private void enableProgressDialog(final boolean enable)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enable)
                    prgDialog.show();
                else
                    prgDialog.hide();
            }
        });
    }
    private void cleanText(final String message)
    {
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
   public void navigatetoRegisterActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(),RegisterActivity.class);
       loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       startActivity(loginIntent);
    }
    private void navigatetoWelcomerActivity(){
        runOnUiThread(new Runnable(){
        @Override
        public void run() {
            startActivity(new Intent(LoginActivity.this,Welcome.class));
        }
    });

    }
    private void navigatetoBabyActivity(){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,SelectBaby.class));
            }
        });

    }
//TODO
// //Verify if this is a correct way to quit app
    public void quitApp(View view) {
        //finishAndRemoveTask();
        finishAffinity();
        System.exit(0);
    }




}
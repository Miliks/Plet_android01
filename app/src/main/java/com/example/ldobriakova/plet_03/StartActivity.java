package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

/**
 *
 * Start Activity Class
 *
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
       // Fabric.with(this, new Crashlytics());


    }
    @Override
    public void onBackPressed()
    {
        finishAffinity();
        System.exit(0);

    }


       /**
     * Method gets triggered when Register button is clicked
     *
     * @param view
     */
   /* public void navigatetoRegisterActivity(View view) {
        Intent loginIntent = new Intent(getApplicationContext(), RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }*/


   /* public void forgetPwd(View view) {
        String url = "http://plet.cloud.reply.eu/PletPlatform/Account/ResetPasswordMobile.aspx";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }*/


    public void personalPlay(View view) {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.putExtra("isTeacher",false);
        startActivity(i);
    }

    public void groupPlay(View view) {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.putExtra("isTeacher",true);
        startActivity(i);
    }
}

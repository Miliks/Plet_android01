package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectBaby extends Activity {
    private Spinner spinnerBaby;
    private Button goFwd, addBaby, delete_user;
    private ListView lv;
    ArrayList<HashMap<String, String>> babyList;
    String result;
    ProgressDialog progressDialog;
    String myEtText;
    JSONObject json = new JSONObject();
    JSONArray babylist = new JSONArray();
    List<String> list = new ArrayList<String>();
    List<String> listMag = new ArrayList<String>();
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(SelectBaby.this,LoginActivity.class);
         startActivity(i);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baby_layout);
        spinnerBaby = (Spinner) findViewById(R.id.spinnerBaby);
        goFwd = (Button)findViewById(R.id.goFwd);
        addBaby = (Button)findViewById(R.id.addBaby);
        delete_user = (Button)findViewById(R.id.delete_user);
        Bundle extras = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);

        if (extras != null) {

            myEtText = extras.getString("userName");
            Log.d("User to query in onCreate.....=",myEtText);

        }

        getBabies(myEtText);
       }

    private void enableProgressDialog(final boolean enable)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enable)
                    progressDialog.show();
                else
                    progressDialog.hide();
            }
        });
    }

    private void updateUISpinner( final List<String> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addListenerOnSpinnerBabySelection();
                addItemOnSpinneraddBaby(list);
                addListenerOnButtonFwd();
            }
        });
    }

    private void addListenerOnSpinnerBabySelection() {
        Log.d("We are in listener on spinner","....");
        spinnerBaby = (Spinner)findViewById(R.id.spinnerBaby);
        spinnerBaby.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    public void deleteUser(View view) {

        deleteFromDB();
    }

   private void deleteFromDB() {
       String userName = myEtText;
       RegisterAPI.getInstance(this).userDelete(userName, new RegisterAPI.RegistrationCallback() {
           @Override
           public void onResponse(String str) {
               try {
                   enableProgressDialog(false);
                   JSONObject jsonResponse = new JSONObject(str);
                   String result = jsonResponse.getString("result");
                   //Log.d("What is returned on activity Login view ......", result);
                   if (result.equals("OK")) {
                       Log.d("attemptToDelete", "SUCCESSSSSSSS!!!!!..");
                       //Reload activity
                       cleanText("The user have been deleted succesfuly!");
                       Intent i = new Intent(SelectBaby.this,LoginActivity.class);
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
    private void cleanText(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            }
        });
    }
   private void addItemOnSpinneraddBaby(List<String> selection) {
        spinnerBaby = (Spinner) findViewById(R.id.spinnerBaby);

        Log.d("LArray returned ========", selection.toString());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,selection);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBaby.setAdapter(dataAdapter);

    }

    public void getBabies(String myEtText){
        enableProgressDialog(true);

        RegisterAPI.getInstance(this).queryBaby(myEtText,  new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    //Log.d("Parsing JSON, object  = ",jsonResponse.toString());
                    JSONObject result_obj = jsonResponse.getJSONObject("content");
                    //Log.d("Parsing JSON, object Content = ",result_obj.toString());
                    result = jsonResponse.getString("result");
                    //result = jsonResponse.getString("result");
                    //Log.d("What is returned on get baby query ......", result);
                    if(result.equals("OK"))
                    {

                        try {
                            //TODO
                            //Need to handle empty list returned in case there is no babies registered on the user
                            //json = new JSONObject(result);
                            babylist = result_obj.getJSONArray("BabyList");
                            if(babylist.length()>0) {
                                Log.d("What is returned on get baby query ......", babylist.getString(0) + "-------" + babylist.length());
                                for (int i = 0; i < babylist.length(); i++) {
                                    JSONObject b = babylist.getJSONObject(i);
                                    Log.d("Inside cycle for ......", b.toString());
                                    String babyAlias = b.getString("Baby_Alias");
                                    listMag.add(babyAlias);
                                    Log.d("Array returned1111 ========", listMag.toString());
                                }
                                updateUISpinner(listMag);
                            }
                            else
                            {
                                Log.d("Array returned1111 ========", "No BABIES...........");
                                updateUISpinner(listMag);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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
        Log.d("LArray returned 22222222222========", listMag.toString());

    }

    private void addListenerOnButtonFwd() {


        goFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //Add association with the current session and the babyID selected

                navigatetoWelcome(v);
            }
        });
    }

    public void navigateAddBaby(View view){
        Intent registerBaby = new Intent(getApplicationContext(),RegisterBaby.class);
        registerBaby.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        registerBaby.putExtra("userName",myEtText);
        startActivity(registerBaby);
    }
    private void navigatetoWelcome(View view) {
        Intent welcome = new Intent(getApplicationContext(),Welcome.class);
        welcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        welcome.putExtra("userName",myEtText);
        String babyAl = spinnerBaby.getSelectedItem().toString();
        welcome.putExtra("babyAlias", babyAl);
        progressDialog.dismiss();
        startActivity(welcome);
    }
    public void quitApp(View view) {
        //finishAndRemoveTask();
        /*Intent intent = new Intent();
        Bundle data = new Bundle();

        data.putboo

        intent.putExtra("data", data);
        setResult(2,intent );*/
        finishAffinity();
        System.exit(0);
    }

    public void navigateRemoveBaby(View view) {
        Intent remModBaby = new Intent(getApplicationContext(),RemoveModBaby.class);
        remModBaby.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        remModBaby.putExtra("userName",myEtText);
        String babyAl = spinnerBaby.getSelectedItem().toString();
        remModBaby.putExtra("babyAlias",babyAl);
        Log.d("LArray returned 22222222222========", babyAl + "  " + myEtText);
        progressDialog.dismiss();
        startActivity(remModBaby);
    }

    public void backtoLogin(View view) {
        Intent i = new Intent(SelectBaby.this,LoginActivity.class);
        startActivity(i);
    }
}

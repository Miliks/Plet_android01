package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectChild extends Activity {
    private Spinner spinnerBaby;
    private ListView listChild;
    private Button goFwd;
    private ListView lv;
    ArrayList<HashMap<String, String>> babyList;
    String result;
    ProgressDialog progressDialog;
    String myEtText, child_alias;
    JSONObject json = new JSONObject();
    JSONArray babylist = new JSONArray();
    List<String> listComplete = new ArrayList<String>();
    List<String> listMag = new ArrayList<String>();
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(SelectChild.this,LoginActivity.class);
        startActivity(i);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_child);
        listChild = (ListView) findViewById(R.id.childList);
        goFwd = (Button)findViewById(R.id.goFwd);
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
                //addListenerOnSpinnerBabySelection();
                addListenerOnChildClick();
                //addListenerOnChildSelection();
                addItemOnSpinneraddBaby(list);
                //addItemOnSpinneraddBabyCompleted(listCompleted);
                //addListenerOnButtonFwd();
            }
        });
    }

    private void addListenerOnChildClick() {
        listChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 child_alias = listChild.getItemAtPosition(position).toString();

            }
        });

    }
private void addListenerOnChildSelection(){
        listChild.setOnItemSelectedListener(new CustomOnItemSelectedListener());
}
    private void addListenerOnSpinnerBabySelection() {
        Log.d("We are in listener on spinner","....");


        spinnerBaby.setOnItemSelectedListener(new CustomOnItemSelectedListener());
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
      //  listChild = (ListView) findViewById(R.id.childList);

        //CustomAdapter childAdapter = new CustomAdapter(this, android.R.layout.activity_list_item,R.id.child_alias,selection);
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.activity_list_item,selection);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview,R.id.child_alias,selection);

        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.activity_list_item,selection);

        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinnerBaby.setAdapter(dataAdapter);
        listChild.setAdapter(dataAdapter);

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
                                    // Log.d("Inside cycle for ......", b.toString());
                                    String babyAlias = b.getString("Baby_Alias");
                                    // String babyGender = b.getString("Baby_Gender");
                                    //String babyDB = b.getString("Baby_Birthdate");
                                    listMag.add(babyAlias);
                                    //listComplete.add(babyAlias + "_" + babyGender + "_"+babyDB);
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
        // Log.d("LArray returned 22222222222========", listMag.toString());

    }

   /* private void addListenerOnButtonFwd() {
        goFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //Add association with the current session and the babyID selected
                goToProfile(v);
            }
        });
    }*/


    /*private void navigatetoWelcome(View view) {
        Intent welcome = new Intent(getApplicationContext(),Welcome.class);
        welcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        welcome.putExtra("userName",myEtText);
        String babyAl = spinnerBaby.getSelectedItem().toString();
        welcome.putExtra("babyAlias", babyAl);
        progressDialog.dismiss();
        startActivity(welcome);
    }*/


    public void backtoLogin(View view) {
        Intent i = new Intent(SelectChild.this,LoginActivity.class);
        startActivity(i);
    }


    public void goToProfile(View view) {
        Intent i = new Intent(getApplicationContext(),UserProfile.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("userName",myEtText);
        progressDialog.dismiss();
        startActivity(i);
    }


    public void navigatetoHome(View view) {
        Intent welcome = new Intent(getApplicationContext(),Welcome.class);
        welcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        welcome.putExtra("userName",myEtText);
        //String babyAl = spinnerBaby.getSelectedItem().toString();
       // String babyAl = listChild.getSelectedItem().toString();

        //welcome.putExtra("babyAlias", babyAl);
        welcome.putExtra("babyAlias", child_alias);
        progressDialog.dismiss();
        startActivity(welcome);
    }
}
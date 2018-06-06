package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;

public class RemoveModBaby extends Activity {
    private Spinner spinnerBaby;
    private SpinnerAdapter newAdapter;
    Child[] child = new Child[10];
    private Button removeBaby, modifyBaby;
    private ListView lv;
    ArrayList<HashMap<String, String>> babyList;
    String result, result_baby, childGender, childBD;
    String myEtText;
    String babyAlias;
    JSONObject json = new JSONObject();
    JSONArray babylist = new JSONArray();
    List<String> list = new ArrayList<String>();
    List<String> listMag = new ArrayList<String>();
    List<String> onlyAlias = new ArrayList<String>();
    String ITEM_KEY = "key";
    //SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    ProgressDialog progressDialog;
    ProgressDialog babyRemovalResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baby_rem_modif);
        Bundle extras = getIntent().getExtras();
        babyRemovalResult = new ProgressDialog(this);
        babyRemovalResult.setMessage("Baby removal is in progress");
        babyRemovalResult.setIndeterminate(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);

        if (extras != null) {

            myEtText = extras.getString("userName");
           // babyAlias = extras.getString("babyAlias");
           // Log.d("User to query in onCreate in RemoveModify Baby.....=",myEtText);

        }

        getBabies(myEtText);
        Log.d("List of babies on remove/modify available 111111  .....=",list.toString());
       // this.adapter = new SimpleAdapter(SelectBaby.this, arraylist, R.layout.row, new String[] { ITEM_KEY }, new int[] { R.id.list_value });
    }
    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(RemoveModBaby.this,SelectBaby.class);
        i.putExtra("userName",myEtText);
        startActivity(i);

    }

    private void enableProgressDialog(final boolean enable)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enable) {
                    progressDialog.show();
                    //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }
                else
                    progressDialog.hide();
            }
        });
    }

    private void enableBabyRemovalDialog(final boolean enable)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enable)
                    babyRemovalResult.show();
                else
                    babyRemovalResult.hide();
            }
        });
    }

    private void updateUISpinner( final List<String> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*if(!list.isEmpty()){
                int i = list.size();

                for (int j=0; j<i;j++){

                String fullString = list.get(j);
                    Log.d("alias ========", fullString);
                    int counter = fullString.indexOf("+");
                    Log.d("counter ========", String.valueOf(counter));

                        String alias = fullString.substring(0,counter);
                        onlyAlias.add(alias);

                }}*/
                //Log.d("updateUISpinner ========", String.valueOf(i));
                addListenerOnSpinnerBabySelection();
                addItemOnSpinneraddBaby(list);
                //addItemOnSpinneraddBaby(list,onlyAlias);
                //addListenerOnButtonFwd();
            }
        });
    }

    private void addListenerOnSpinnerBabySelection() {
        Log.d("We are in listener on spinner","....");
        spinnerBaby = (Spinner)findViewById(R.id.spinnerBaby);
        spinnerBaby.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

   /*private void addItemOnSpinneraddBaby(List<String> selection) {
        spinnerBaby = (Spinner) findViewById(R.id.spinnerBaby);
        //newAdapter = new SpinnerAdapter(RemoveModBaby.this, simple_spinner_item,child);
        Log.d("LArray returned ========", selection.toString());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, simple_spinner_item,selection);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBaby.setAdapter(dataAdapter);

    }*/
    private void addItemOnSpinneraddBaby(List<String> selection) {
        spinnerBaby = (Spinner) findViewById(R.id.spinnerBaby);
        //newAdapter = new SpinnerAdapter(RemoveModBaby.this, simple_spinner_item,child);
        Log.d("selection ========", selection.toString());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, simple_spinner_item,selection);
       // ArrayAdapter<String> onlyAliasadapter = new ArrayAdapter<String>(this,simple_spinner_item, onlyAlias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // onlyAliasadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBaby.setAdapter(dataAdapter);
       // childGender =

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
                    result_baby = jsonResponse.getString("result");
                    //result = jsonResponse.getString("result");
                    //Log.d("What is returned on get baby query ......", result);
                    if(result_baby.equals("OK")) {
                        if (!result_baby.isEmpty()) {
                            try {
                                //TODO
                                //Need to handle empty list returned in case there is no babies registered on the user
                                //json = new JSONObject(result);
                                babylist = result_obj.getJSONArray("BabyList");
                                Log.d("What is returned on get baby query ......", babylist.getString(0) + "-------" + babylist.length());
                                for (int i = 0; i < babylist.length(); i++) {
                                    JSONObject b = babylist.getJSONObject(i);
                                    Log.d("Inside cycle for ......", b.toString());
                                    String babyAlias = b.getString("Baby_Alias");
                                     String babyGender = b.getString("Baby_Gender");
                                    String babyDB = b.getString("Baby_Birthdate");
                                    listMag.add(babyAlias);
                                    list.add(babyAlias+" "+babyGender+" "+babyDB);
                                    /*child[i] = new Child();
                                    child[i].setAlias(babyAlias);
                                    child[i].setChild_birthDay(babyDB);
                                    child[i].setGender(babyGender);*/

                                    //listComplete.add(babyAlias + "_" + babyGender + "_"+babyDB);
                                }
                                //Log.d("Array returned1111 ========", listMag.toString());
                                //Log.d("Array returned1111 ========", child.toString());
                               // int count = listMag.size();
                                //Child[] child = new Child[count];
                                //for (int i=0; i<count; i++) {

                                //}
                                updateUISpinner(list);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                    else{
                        Log.d("Inside cycle for ......", "No babies associated with this user");
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



    public void modifyBaby(View view){
        Intent modifyBaby = new Intent(getApplicationContext(),ModifyBaby.class);
        modifyBaby.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        modifyBaby.putExtra("userName",myEtText);
        String fullString = spinnerBaby.getSelectedItem().toString();
        int i = fullString.indexOf(" ");
        Log.d("Modify Baby index i  ......", fullString + "...." + String.valueOf(i));
        babyAlias = fullString.substring(0,i);
        Log.d("Modify Baby babyAlias ......", babyAlias );
        if(fullString.contains("female")) {

            i=i+8;
            childGender = "female";
            Log.d("Modify Baby index i  ......", fullString + "...." + String.valueOf(i));
        }
        else {
            childGender = "male";
        i=i+6;
            Log.d("Modify Baby index i  ......", fullString + "...." + String.valueOf(i));
        }
        Log.d("Modify Baby gender  ......", childGender);
        Log.d("Modify Baby index i outside if  ......", fullString + "...." + String.valueOf(i));
        int t = fullString.length();
        childBD = fullString.substring(i,t);
        modifyBaby.putExtra("babyAlias",babyAlias );
        modifyBaby.putExtra("babyGender",childGender);
        modifyBaby.putExtra("birthDay", childBD);
        Log.d("Modify Baby  ......", babyAlias + childGender+childBD);
        //modifyBaby.putExtra("childArray",child);
        startActivity(modifyBaby);
    }
    public void navigatetoWelcome(View view) {

        Intent i = new Intent(RemoveModBaby.this,SelectBaby.class);
        i.putExtra("userName",myEtText);
        startActivity(i);

        /*Intent selectBaby = new Intent(getApplicationContext(),SelectBaby.class);
        selectBaby.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(selectBaby);*/
    }

    public void removeBaby(View view) {
        enableBabyRemovalDialog(true);
        babyAlias = spinnerBaby.getSelectedItem().toString();

        RegisterAPI.getInstance(this).deleteBaby(myEtText,babyAlias,new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableBabyRemovalDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    result = jsonResponse.getString("result");
                       if(result.equals("OK"))
                    {
                        //enableBabyRemovalDialog(false);
                       // finish();
                       // startActivity(getIntent());
                        Intent i = new Intent(RemoveModBaby.this,RemoveModBaby.class);
                        i.putExtra("userName",myEtText);
                        startActivity(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(RegistrationResponse.RegistrationError error) {
                enableBabyRemovalDialog(false);
            }
            @Override
            public void onNetworkError() {
                enableBabyRemovalDialog(false);

            }
        });

    }


}

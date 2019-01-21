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

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class SelectChild extends Activity {
    private Spinner spinnerBaby;
    private ListAdapter childAdapter;
    private ListView listChild;
    private Button goFwd, profile;
    private ListView lv;
    ArrayList<HashMap<String, String>> babyList;
    String result, child_id;
    ProgressDialog progressDialog;
    String myEtText, child_alias, child_token;
    JSONObject json = new JSONObject();
    JSONArray babylist = new JSONArray();
    List<String> listComplete = new ArrayList<String>();
    List<String> listMag = new ArrayList<String>();
    String ITEM_KEY = "key";
    Boolean isTeacher;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(SelectChild.this,LoginActivity.class);
        i.putExtra("isTeacher",false);
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_child);
        Fabric.with(this, new Crashlytics());
        listChild = (ListView) findViewById(R.id.childList);
        goFwd = (Button)findViewById(R.id.goFwd);
        profile = (Button)findViewById(R.id.profile);
        Bundle extras = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        if (extras != null) {
            myEtText = extras.getString("userName");
            isTeacher = false;
           // Log.d("User to query in onCreate.....=",myEtText);

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
                //addListenerOnChildClick();
                addItemOnSpinneraddBaby(list);
               // addListenerOnChildClickold();
                //addListenerOnChildSelection();

                //addItemOnSpinneraddBabyCompleted(listCompleted);
                //addListenerOnButtonFwd();
            }
        });
    }


    private void addListenerOnChildClickold() {
        listChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 child_alias = listChild.getItemAtPosition(position).toString();
                Log.d("MILA", "On item selected = " + child_alias);

            }
        });

    }
    private void addListenerOnChildClick() {


        listChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                child_alias = listChild.getItemAtPosition(position).toString();
                //String childGender =
                Log.d("MILA", "On item selected = " + child_alias);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


    private void addItemOnSpinneraddBaby(List<String> selection) {
        int i = selection.size();
        Child [] child = new Child[i];
        for (int j=0; j<i; j++) {
            int child_index = selection.get(j).indexOf("+");
            int token_index = selection.get(j).indexOf("+",child_index +1);
            Log.d("MILA", "index =" + i);
            child[j] = new Child();
            child_alias = selection.get(j).substring(0, child_index);
            child_id = selection.get(j).substring(child_index + 1, token_index);
            child_token = selection.get(j).substring(token_index+1,selection.get(j).length());
            Log.d("MILA", "Loop for babies = " + j + "***" + child_alias + " " + child_token + " " + child_id);
            child[j].setAlias(child_alias);
            child[j].setChild_ID(child_id);
            child[j].setToken(child_token);
        }
        childAdapter = new ListAdapter(this,R.layout.activity_listview,child);
       // childAdapter = new CustomAdapter(this,R.layout.activity_listview,R.id.child_alias,child);
             //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview,R.id.child_alias,selection);
            // listChild.setAdapter(dataAdapter);


        listChild.setAdapter(childAdapter);
        listChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Child child = (Child)childAdapter.getItem(position);
                child_alias = child.getChild_alias();
                child_id = child.getChild_Id();
                child_token = child.getChild_token();
                Log.d("MILA", child_alias + " " + child_token + " " + child_id);
            }


        });

       /* listChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Child child = (Child)childAdapter.getItem(position);
                child_alias = child.getChild_alias();
                String childGender = child.getChild_gender();
                Log.d("MILA", "Child data = " + child_alias + " ++" + childGender);
            }

        }

        );*/

    }

    private void getBabies(String myEtText){
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
                            int j = babylist.length();

                            if(babylist.length()>0) {
                                Log.d("What is returned on get baby query ......", babylist.getString(0) + "-------" + babylist.length());
                                for (int i = 0; i < j; i++) {
                               JSONObject b = babylist.getJSONObject(i);
                                    // Log.d("Inside cycle for ......", b.toString());
                                    String babyAlias = b.getString("Baby_Alias");
                                    String babyID = b.getString("BabyID");
                                    String token = "";
                                    if(b.getString("Token")!= null)
                                     token = b.getString("Token");

                                    //String child_id = b.getString("child_id");//To change to real json field
                                    listMag.add(babyAlias+"+"+babyID+"+"+ token);

                                }
                                updateUISpinner(listMag);
                                Log.d("MILA", "BABIES..........."+listMag);
                            }
                            else
                            {
                                Log.d("MILA", "No BABIES...........");
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

    }


    public void goToProfile(View view) {
        Intent i = new Intent(getApplicationContext(),UserProfile.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("userName",myEtText);
      //  progressDialog.dismiss();
        startActivity(i);
    }


    public void navigatetoHome(View view) {
        Intent welcome = new Intent(getApplicationContext(),Welcome.class);
        welcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        welcome.putExtra("userName",myEtText);

        welcome.putExtra("babyAlias", child_alias);
        welcome.putExtra("childID",child_id);
        welcome.putExtra("isTeacher", false);
        welcome.putExtra("token",child_token);
     //   progressDialog.dismiss();
        Log.d("MILA", "Your baby selected in SelectChild activity = " + child_alias + "child_token = " + child_token);
        startActivity(welcome);
    }

    public void addNewChild(View view) {
        Intent i = new Intent(getApplicationContext(),RegisterBaby.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("isTeacher", false);
        i.putExtra("userName",myEtText);
        //  progressDialog.dismiss();
        startActivity(i);

    }
}
package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
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

    private SpinAdapter newAdapter;
    private ListView childList;
    private Button removeBaby, modifyBaby;
    private ListView lv;
    ArrayList<HashMap<String, String>> babyList;
    String result, result_baby, childGender, childBD;
    String myEtText;
    String babyAlias, BabyAliasLong;
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
    int j;
    Spinner spinnerBaby;
    List<Child> childItems;


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
        childList = (ListView)findViewById(R.id.listChild);

        if (extras != null) {

            myEtText = extras.getString("userName");

        }

        getBabies(myEtText);

    }
    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(RemoveModBaby.this,SelectChild.class);
        i.putExtra("userName",myEtText);
        startActivity(i);
    }

    private void enableProgressDialog(final boolean enable)
    {
        if(!this.isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (enable) {
                        progressDialog.show();
                        //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    } else
                        progressDialog.hide();
                }
            });
        }
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

                //addListenerOnSpinnerBabySelection();
                addItemOnSpinneraddBaby(list);

            }
        });
    }


   /* private void addListenerOnSpinnerBabySelection() {
        Log.d("We are in listener on spinner","....");
        spinnerBaby = (Spinner)findViewById(R.id.spinnerBaby);
        CustomAdapter adapter = new CustomAdapter(RemoveModBaby.this, R.layout.listitems_layout,R.id.child_alias,childItems);
        spinnerBaby.setAdapter(adapter);
        spinnerBaby.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }*/



   private void addItemOnSpinneraddBaby(List<String> selection) {

        int i = selection.size();
        Child [] child = new Child[i];

        for (int j=0; j<i; j++){
            int space = selection.get(j).indexOf(" ");
            int bd_index;
            Log.d("selection ========", selection.toString() + " " + String.valueOf(i) + " " + String.valueOf(space));
            child[j] = new Child();
            babyAlias = selection.get(j).substring(0,space);
          if (selection.get(j).contains("female")){
                childGender = "female";
                bd_index = space + 6;
                childBD = selection.get(j).substring(bd_index,bd_index+12);
                child[j].setGender(childGender);
                child[j].setChild_birthDay(childBD);
                child[j].setAlias(babyAlias);

            }
            else
            {
                childGender = "male";
                bd_index = space + 4;
                childBD = selection.get(j).substring(bd_index,bd_index+12);
                child[j].setGender(childGender);
                child[j].setChild_birthDay(childBD);
                child[j].setAlias(babyAlias);


            }

            //Log.d("selection inizialize   ========", babyAlias + " bbb" + childGender + "childGender" + childBD + "childBD");
        }
       // Log.d(" outside FOR    ========", babyAlias + " bbb" + childGender + "childGender" + childBD + "childBD");


        /*newAdapter = new SpinAdapter(RemoveModBaby.this, android.R.layout.simple_spinner_item,child);
        spinnerBaby = (Spinner) findViewById(R.id.spinnerBaby);
        spinnerBaby.setAdapter(newAdapter);*/
      // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview,R.id.child_alias,selection);

       newAdapter = new SpinAdapter(RemoveModBaby.this,child);
       childList.setAdapter(newAdapter);
       childList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Child child = (Child) newAdapter.getItem(position);
               babyAlias =  child.getChild_alias();
               childGender = child.getChild_gender();
               childBD = child.getChild_birthDay();
           }
       });

      //childList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      //    @Override
      //    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      //        Child child = (Child) newAdapter.getItem(position);
      //        babyAlias =  child.getChild_alias();
      //        childGender = child.getChild_gender();
      //        childBD = child.getChild_birthDay();
      //    }

      //    @Override
      //    public void onNothingSelected(AdapterView<?> parent) {

      //    }
      //});
     /*   spinnerBaby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Child child = (Child) newAdapter.getItem(position);
            //  String cld_al =  newAdapter.getItem(position).getChild_alias();

               Log.d(" Baby  at position =======", String.valueOf(position) + "....  " +child.getChild_alias());

               babyAlias =  child.getChild_alias();
               childGender = child.getChild_gender();
               childBD = child.getChild_birthDay();
          //     Log.d(" ALIAS =======", babyAlias + "  " + "childGender = " + childGender + "childBD = " + childBD);
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });*/

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
                                babylist = result_obj.getJSONArray("BabyList");
                               // Log.d("What is returned on get baby query ......", babylist.getString(0) + "-------" + babylist.length());

                                for (int i = 0; i < babylist.length(); i++) {
                                    JSONObject b = babylist.getJSONObject(i);
                                    Log.d("Inside cycle for ......", b.toString());
                                    String babyAlias = b.getString("Baby_Alias");
                                    String babyGender = b.getString("Baby_Gender");
                                    String babyDB = b.getString("Baby_Birthdate");
                                    listMag.add(babyAlias);
                                    list.add(babyAlias+" "+babyGender+" "+babyDB);

                                }

                                updateUISpinner(list);
                                //updateUISpinner(childItems);
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
        modifyBaby.putExtra("babyAlias",babyAlias );
        modifyBaby.putExtra("babyGender",childGender);
        modifyBaby.putExtra("birthDay", childBD);
        startActivity(modifyBaby);
    }


    public void removeBaby(View view) {
        //enableBabyRemovalDialog(true);
        enableProgressDialog(true);
        //babyAlias = spinnerBaby.getSelectedItem().toString();

        RegisterAPI.getInstance(this).deleteBaby(myEtText,babyAlias,new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                //enableBabyRemovalDialog(false);
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    result = jsonResponse.getString("result");
                       if(result.equals("OK"))
                    {

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
                //enableBabyRemovalDialog(false);
                enableProgressDialog(false);
            }
            @Override
            public void onNetworkError() {
                //enableBabyRemovalDialog(false);
                enableProgressDialog(false);

            }
        });

    }


    public void addBaby(View view) {
        Intent i = new Intent(RemoveModBaby.this,RegisterBaby.class);
        i.putExtra("userName",myEtText);
        startActivity(i);
    }
}

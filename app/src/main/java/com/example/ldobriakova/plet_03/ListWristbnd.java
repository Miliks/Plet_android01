package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ListWristbnd extends Activity {
   // private Spinner spinnerBaby;
    private WrstAdapter wristAdapter;
    private ListView listWrstV;
    String result, wristID, studentID, groupID;
    ProgressDialog progressDialog;
    String myEtText, wristName;
    JSONObject json = new JSONObject();
    JSONArray groupslist = new JSONArray();
    List<String> listGroups = new ArrayList<String>();
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(ListWristbnd.this,ListStudents.class);
         i.putExtra("isTeacher",true);
         i.putExtra("userName",myEtText);
         startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_wristbnd);
        Fabric.with(this, new Crashlytics());
        listWrstV = (ListView) findViewById(R.id.wrstbndName);
        Bundle extras = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
       if (extras != null) {
            myEtText = extras.getString("userName");
            studentID = extras.getString("studentID");
            groupID = extras.getString("groupID");
           }
        getWristList(myEtText);

        /*getGroups(myEtText);*/
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

                addItemOnSpinneraddGroup(list);
                Log.d("MILA", " in wristband =" + list.toString());

            }
        });
    }



       private void addItemOnSpinneraddGroup(List<String> selection) {
        int i = selection.size();
        Wristbnd [] wrstAr= new Wristbnd[i];
        for (int j=0; j<i; j++) {
            int group_index = selection.get(j).indexOf("+");
            Log.d("MILA", "wrist =" + i);
            wrstAr[j] = new Wristbnd();
            wristName = selection.get(j).substring(0, group_index);
            wristID = selection.get(j).substring(group_index + 1, selection.get(j).length());
            //Log.d("MILA", "Loop for gender = " + j + "***" + groupName + "+++++" + groupID);
            wrstAr[j].setAlias(wristName);
            wrstAr[j].setWrst_ID(wristID);
            //Log.d("MILA", "groupName =" + groupAr[j].getGroup_Id());
        }

          wristAdapter = new WrstAdapter(this,R.layout.list_wristbnd,wrstAr);

           listWrstV.setAdapter(wristAdapter);
           listWrstV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Wristbnd wristbnd = (Wristbnd) wristAdapter.getItem(position);
                   Log.d("MILA", "wristname =" + wristbnd.getWrst_alias());
                   wristName = wristbnd.getWrst_alias();
                   wristID = wristbnd.getWrst_Id();
               }
           });
          /* listWrstV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Wristbnd wristbnd = (Wristbnd) wristAdapter.getItem(position);
                Log.d("MILA", "wristname =" + wristbnd.getWrst_alias());
                wristName = wristbnd.getWrst_alias();
                wristID = wristbnd.getWrst_Id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

    }

    private void getWristList(String myEtText){
        enableProgressDialog(true);
        RegisterAPI.getInstance(this).getWristList(new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    //Log.d("Parsing JSON, object  = ",jsonResponse.toString());
                    JSONObject result_obj = jsonResponse.getJSONObject("content");
                    //Log.d("Parsing JSON, object Content = ",result_obj.toString());
                    result = jsonResponse.getString("result");
                    //Log.d("What is returned on get baby query ......", result);
                    if(result.equals("OK"))
                    {
                        try {
                            groupslist = result_obj.getJSONArray("RecogHWList");
                            int j = groupslist.length();

                            if(groupslist.length()>0) {
                            for (int i = 0; i < j; i++) {
                               JSONObject b = groupslist.getJSONObject(i);
                                    // Log.d("Inside cycle for ......", b.toString());
                                    String groupName = b.getString("HWCommonName");
                                    String groupID = b.getString("RecogHWID");
                                     listGroups.add(groupName+"+"+groupID);
                                }
                                updateUISpinner(listGroups);
                                  }
                            else
                            {
                                updateUISpinner(listGroups);
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


    public void addWrstbnd(View view) {
        Intent i = new Intent(getApplicationContext(),CreateWrist.class);
        i.putExtra("userName",myEtText);
        i.putExtra("groupID",groupID);
        i.putExtra("studentId",studentID);
        startActivity(i);
    }

    public void assignToGroup() {
        Log.d("MILA .... groupID =",groupID+" studentID=" + studentID);
        enableProgressDialog(true);

        RegisterAPI.getInstance(this).addStudentToGroup(studentID, groupID,  new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    //Log.d("Parsing JSON, object  = ",jsonResponse.toString());
                    //JSONObject result_obj = jsonResponse.getJSONObject("content");
                    //Log.d("Parsing JSON, object Content = ",result_obj.toString());
                    Log.d("MILA","groupID = " + groupID);
                    result = jsonResponse.getString("result");

                    if(result.equals("OK"))
                    {
                        Intent i = new Intent(getApplicationContext(),ListStudents.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("userName",myEtText);
                        i.putExtra("groupID",groupID);
                        i.putExtra("isTeacher", true);
                        startActivity(i);
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

    private void assignWristToStud(final String studentID, String wristID){
        enableProgressDialog(true);
        RegisterAPI.getInstance(this).assignWrst(studentID,wristID, new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    Log.d("MILA parsing json  = ",jsonResponse.toString());
                   // JSONObject result_obj = jsonResponse.getJSONObject("content");
                    //Log.d("Parsing JSON, object Content = ",result_obj.toString());
                    result = jsonResponse.getString("result");
                    Log.d("MILA assign wristband to student ......",studentID+ "  " +  result);
                    if(result.equals("OK"))
                    {
                        assignToGroup();
                        /*Intent i = new Intent(ListWristbnd.this, ListAllStudents.class);
                        i.putExtra("userName",myEtText);
                        i.putExtra("groupID",groupID);*/
                       // i.putExtra("groupId", groupID);
                       // startActivity(i);
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

    public void assignToStud(View view) {
//assignWrst
        Log.d("MILA show the wrist id = ",wristID + "studentID= " + studentID);
        assignWristToStud(wristID,studentID);
    }

    public void deleteWrstbnd(View view) {
        enableProgressDialog(true);
                RegisterAPI.getInstance(this).deleteWrst(wristID, new RegisterAPI.RegistrationCallback() {
                @Override
                public void onResponse(String str) {
                    try {
                        enableProgressDialog(false);
                        JSONObject jsonResponse = new JSONObject(str);
                        String result = jsonResponse.getString("result");
                        Log.d("What is returned on wrist deletion ......", result);
                        //Log.d("What is returned on activity Login view ......", jsonResponse.toString());
                        if (result.equals("OK")) {
                            finish();
                            startActivity(getIntent());

                        } else {
                            onFailRegistration(jsonResponse.getString("message"));
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
        //}
    }
    private void onFailRegistration(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            }
        });
    }
   /* public void allStudent(View view) {
        Intent i = new Intent(getApplicationContext(),ListStudents.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("userName",myEtText);
        //i.putExtra("groupId", groupID);
        startActivity(i);
    }*/
}
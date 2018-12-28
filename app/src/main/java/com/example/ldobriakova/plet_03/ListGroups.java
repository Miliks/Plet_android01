package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ListGroups extends Activity {
   // private Spinner spinnerBaby;
    private GroupAdapter groupAdapter;
    private ListView listGroupsV;
    String result, groupID;
    ProgressDialog progressDialog;
    String myEtText, groupName;
    JSONObject json = new JSONObject();
    JSONArray groupslist = new JSONArray();
    List<String> listGroups = new ArrayList<String>();
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(ListGroups.this,LoginActivity.class);
         i.putExtra("isTeacher",true);
         startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_groups);
        Fabric.with(this, new Crashlytics());
        listGroupsV = (ListView) findViewById(R.id.groupList);
        Bundle extras = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        if (extras != null) {
            myEtText = extras.getString("userName");
            Log.d("MILA", " in on Create =" + myEtText);

           }

        getGroups(myEtText);
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
                Log.d("MILA", " in Group update =" + list.toString());

            }
        });
    }
       private void addItemOnSpinneraddGroup(List<String> selection) {
        int i = selection.size();
        Group [] groupAr= new Group[i];
        for (int j=0; j<i; j++) {
            int group_index = selection.get(j).indexOf("+");
            Log.d("MILA", "index =" + i);
            groupAr[j] = new Group();
            groupName = selection.get(j).substring(0, group_index);
            groupID = selection.get(j).substring(group_index + 1, selection.get(j).length());
            Log.d("MILA", "Loop for group = " + j + "***" + groupName + "+++++" + groupID);
            groupAr[j].setGroupName(groupName);
            groupAr[j].setGroupId(groupID);
            Log.d("MILA", "groupName =" + groupAr[j].getGroup_Id() + groupAr[j].getGroup_name());
        }
          groupAdapter = new GroupAdapter(this,R.layout.group_listview,groupAr);
           listGroupsV.setAdapter(groupAdapter);
           listGroupsV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Group group = (Group)groupAdapter.getItem(position);
                   groupName = group.getGroup_name();
                   groupID = group.getGroup_Id();
                   Log.d("MILA", "groupID =" + group.getGroup_Id());
               }

           });
           /*listGroupsV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Group group = (Group)groupAdapter.getItem(position);
                groupName = group.getGroup_name();
                groupID = group.getGroup_Id();
                Log.d("MILA", "groupID =" + group.getGroup_Id());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    private void getGroups(String myEtText){
        enableProgressDialog(true);

        RegisterAPI.getInstance(this).queryGroups(myEtText,  new RegisterAPI.RegistrationCallback() {
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
                            //TODO
                            //Need to handle empty list returned in case there is no babies registered on the user
                            //json = new JSONObject(result);
                            groupslist = result_obj.getJSONArray("GroupsList");
                            int j = groupslist.length();

                            if(groupslist.length()>0) {
                                //Log.d("What is returned on get baby query ......", babylist.getString(0) + "-------" + babylist.length());
                                for (int i = 0; i < j; i++) {
                               JSONObject b = groupslist.getJSONObject(i);
                                    // Log.d("Inside cycle for ......", b.toString());
                                    String groupName = b.getString("GroupName");
                                    String groupID = b.getString("GroupID");
                                    //String child_id = b.getString("child_id");//To change to real json field
                                    listGroups.add(groupName+"+"+groupID);

                                }
                                updateUISpinner(listGroups);
                                Log.d("MILA", "GROUPS..........."+listGroups);
                            }
                            else
                            {
                                Log.d("MILA", "No BABIES...........");
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


    public void addGroup(View view) {
        Intent i = new Intent(getApplicationContext(),CreateGroup.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("userName",myEtText);
      //  progressDialog.dismiss();
        startActivity(i);
    }

    public void studentsbyGroup(View view) {
        Intent welcome = new Intent(getApplicationContext(),ListStudents.class);
        welcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        welcome.putExtra("userName",myEtText);
        welcome.putExtra("groupId", groupID);
        startActivity(welcome);
    }

    public void deleteGroup(View view) {
        enableProgressDialog(true);
        RegisterAPI.getInstance(this).deleteGroup(myEtText,groupID, new RegisterAPI.RegistrationCallback() {
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

    public void modifyTeacher(View view) {
        //TODO create new screen to modify teacher profile
    }

    public void assignToy(View view) {
        Intent welcome = new Intent(getApplicationContext(),Welcome.class);
        welcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        welcome.putExtra("userName",myEtText);
        welcome.putExtra("groupId", groupID);
        startActivity(welcome);
        //TODO create new screen to assign toy to the teacher, the screen for personal usage could be used?
    }
}
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

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ListStudents extends Activity {
    private Spinner spinnerBaby;
    private StudentAdapter studentAdapter;
    private ListView listChild;
    private Button goFwd, profile;
    private ListView lv;
    ArrayList<HashMap<String, String>> babyList;
    String result, Student_id;
    ProgressDialog progressDialog;
    String myEtText, stud_alias, stud_name, stud_surname, stud_id, groupId;
    JSONObject json = new JSONObject();
    JSONArray babylist = new JSONArray();
    List<String> listComplete = new ArrayList<String>();
    List<String> listMag = new ArrayList<String>();
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(ListStudents.this,ListGroups.class);
        i.putExtra("userName",myEtText);
        i.putExtra("groupID",groupId);
        i.putExtra("isTeacher", true);
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_students);
        Fabric.with(this, new Crashlytics());
        listChild = (ListView) findViewById(R.id.listStudents);
        Bundle extras = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        if (extras != null) {
            groupId = extras.getString("groupID");
            myEtText = extras.getString("userName");
            //if (groupId != null){
                getStudentsByGroup(myEtText, groupId);
        Log.d("MILA", "On create List students filtered by groupID= " + groupId);
        }
           /* }


            }

        getAllStudents(myEtText, groupId);*/
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
                 addItemOnSpinneraddBaby(list);

            }
        });
    }



    private void addItemOnSpinneraddBaby(List<String> selection) {
        int i = selection.size();
        Student [] stud = new Student[i];
        for (int j=0; j<i; j++) {
            int stud_index = selection.get(j).indexOf("+");
            Log.d("MILA", "index =" + i);
            stud[j] = new Student();
           // stud_name = selection.get(j).substring(0, stud_index);
           // stud_surname = selection.get(j).substring(0, stud_index);
            stud_alias = selection.get(j).substring(0, stud_index);
            stud_id = selection.get(j).substring(stud_index + 1, selection.get(j).length());
            //Log.d("MILA", "Loop for gender = " + j + "***" + child_gender);
            stud[j].setSt_ID(stud_id);
            stud[j].setSTAlias(stud_alias);
        }
        studentAdapter = new StudentAdapter(this,R.layout.students_listview,stud);
       // childAdapter = new CustomAdapter(this,R.layout.activity_listview,R.id.child_alias,child);
             //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview,R.id.child_alias,selection);
            // listChild.setAdapter(dataAdapter);
        listChild.setAdapter(studentAdapter);
        listChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) studentAdapter.getItem(position);
                stud_alias = student.getSt_alias();
                stud_id = student.getSt_Id();
                Log.d("MILA", "studID =" + stud_id);
            }
        });

        Student_id = stud_id;
        Log.d("MILA", "Student_id =" + Student_id);

    }
   /* private void getAllStudents(String myEtText, String groupId){
        enableProgressDialog(true);

        RegisterAPI.getInstance(this).queryStudentsByGroup(myEtText, groupId,  new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    //Log.d("Parsing JSON, object  = ",jsonResponse.toString());
                    JSONObject result_obj = jsonResponse.getJSONObject("content");
                    //Log.d("Parsing JSON, object Content = ",result_obj.toString());
                    result = jsonResponse.getString("result");

                    if(result.equals("OK"))
                    {

                        try {
                            //TODO
                            //Need to handle empty list returned in case there is no babies registered on the user
                            babylist = result_obj.getJSONArray("StudentsList");
                            int j = babylist.length();

                            if(babylist.length()>0) {
                                Log.d("What is returned on get baby query ......", babylist.getString(0) + "-------" + babylist.length());
                                for (int i = 0; i < j; i++) {
                                    JSONObject b = babylist.getJSONObject(i);
                                    // Log.d("Inside cycle for ......", b.toString());
                                    String stName = b.getString("StudentName");
                                    String stSurname = b.getString("StudentSurname");
                                    String stID = b.getString("StudentID");
                                    //String child_id = b.getString("child_id");//To change to real json field
                                    listMag.add(stName + " " + stSurname +"+"+stID);

                                }
                                updateUISpinner(listMag);
                                Log.d("MILA", "Students..........."+listMag);
                            }
                            else
                            {
                                Log.d("MILA", "No Students...........");
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
*/
    private void getStudentsByGroup(String myEtText, String groupID){
        enableProgressDialog(true);
        groupId = groupID;
        Log.d("MILA","getStudentByGroup, where groupid = " + groupId);
        RegisterAPI.getInstance(this).queryStudentsByGroup(myEtText, groupID,  new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    //Log.d("Parsing JSON, object  = ",jsonResponse.toString());
                    JSONObject result_obj = jsonResponse.getJSONObject("content");
                    //Log.d("Parsing JSON, object Content = ",result_obj.toString());
                    result = jsonResponse.getString("result");

                    if(result.equals("OK"))
                    {

                        try {
                            //TODO
                            //Need to handle empty list returned in case there is no babies registered on the user
                            babylist = result_obj.getJSONArray("StudentsList");
                            int j = babylist.length();

                            if(babylist.length()>0) {
                                Log.d("What is returned on group list students query ......j= ", j + babylist.getString(0) + "-------" + babylist.length());
                                for (int i = 0; i < j; i++) {
                               JSONObject b = babylist.getJSONObject(i);
                                    // Log.d("Inside cycle for ......", b.toString());
                                    String stName = b.getString("StudentName");
                                    String stSurname = b.getString("StudentSurname");
                                    String stID = b.getString("StudentID");
                                    String token = b.getString("Token");
                                    //String child_id = b.getString("child_id");//To change to real json field
                                    listMag.add(stName + " " + stSurname + " T ("+ token + ")" + "+"+stID);

                                }
                                updateUISpinner(listMag);
                                Log.d("MILA", "Students..........."+listMag);
                            }
                            else
                            {
                                Log.d("MILA", "No Students...........");
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
    public void createStudent(View view) {
        Intent i = new Intent(getApplicationContext(),ListAllStudents.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("userName",myEtText);
        i.putExtra("groupID",groupId);
        i.putExtra("isTeacher", true);
       Log.d("Add students to groupID= ",groupId);
        startActivity(i);
    }

    public void removeFromGroup(View view) {
        enableProgressDialog(true);
        Log.d("MILA  ","Add students group=" + groupId);
        RegisterAPI.getInstance(this).deleteStudFromGroup(stud_id, groupId,  new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    //JSONObject result_obj = jsonResponse.getJSONObject("content");
                    result = jsonResponse.getString("result");

                    if(result.equals("OK"))
                    {
                        Intent i = new Intent(getApplicationContext(),ListStudents.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("userName",myEtText);
                        i.putExtra("groupID",groupId);
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

    /*public void assignWristband(View view) {
        Intent i = new Intent(getApplicationContext(),ListWristbnd.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("studentID",stud_id);
        i.putExtra("userName",myEtText);
        i.putExtra("groupID",groupId);
        startActivity(i);
    }*/
}
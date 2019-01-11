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

public class ListAllStudents extends Activity {
    private Spinner spinnerBaby;
    private StudentAdapter studentAdapter;
    private ListView listChild;
    private Button goFwd, profile;
    private ListView lv;
    ArrayList<HashMap<String, String>> babyList;
    String result, Student_id;
    ProgressDialog progressDialog;
    String myEtText, stud_alias, stud_name, stud_surname, stud_id, groupId,stud_hw;
    JSONObject json = new JSONObject();
    JSONArray babylist = new JSONArray();
    List<String> listComplete = new ArrayList<String>();
    List<String> listMag = new ArrayList<String>();
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(ListAllStudents.this,ListStudents.class);
        i.putExtra("userName",myEtText);
        i.putExtra("groupID",groupId);
        i.putExtra("isTeacher", true);
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_all_students);
        Fabric.with(this, new Crashlytics());
        listChild = (ListView) findViewById(R.id.listStudents);
        Bundle extras = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        if (extras != null) {
           groupId = extras.getString("groupID");
           myEtText = extras.getString("userName");
           getAllStudents(myEtText, groupId);
            }
            Log.d("MILA groupId = ",groupId);
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
            int second_occurance = selection.get(j).indexOf("+",stud_index+1);
            //stud_hw = selection.get(j).substring(second_occurance+1,selection.get(j).length());
            //Log.d("MILA", "stud_hw = " + stud_hw);
            //if(stud_hw.isEmpty()||stud_hw.equals(null)||stud_hw.equals("null")) {
                stud_id = selection.get(j).substring(stud_index + 1, second_occurance);

                //stud_hw = selection.get(j).substring(second_occurance + 1, selection.get(j).length());
                Log.d("MILA", "there is nothing associated...........");
                stud[j].setSt_ID(stud_id);
                stud[j].setSTAlias(stud_alias);
               // stud[j].sethwLabel(stud_hw);
           // }
            Log.d("MILA", "stud_id = " + stud_id);
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
                //stud_hw = student.gethwLabel();
                Log.d("MILA", "On item click stud_id =" + stud_id /* " hwLabel" + stud_hw*/ );
            }
        });

        Student_id = stud_id;
        Log.d("MILA", "Student_id =" + Student_id);

    }
    private void getAllStudents(String myEtText, String groupId){
        enableProgressDialog(true);

        RegisterAPI.getInstance(this).queryStudentsByGroup(myEtText, null,  new RegisterAPI.RegistrationCallback() {
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
                                    String hwLabel = b.getString("HwLabel");
                                    //String token = b.getString("Token");
                                    if(hwLabel.equals("null"))
                                    //String child_id = b.getString("child_id");//To change to real json field
                                    listMag.add(stName + " " + stSurname + "+"+stID + "+" + hwLabel);

                                }
                                updateUISpinner(listMag);
                                Log.d("MILA", "All Students..........."+listMag);
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

    private void getStudentsByGroup(String myEtText, String groupID){
        enableProgressDialog(true);

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


    public void createStudent(View view) {
        Intent i = new Intent(getApplicationContext(),RegisterStudent.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("userName",myEtText);
        i.putExtra("groupID",groupId);
        startActivity(i);
    }

   /*     public void assignGroup(View view) {
            Log.d("MILA .... groupID =",groupId+" studentID=" + stud_id);
           enableProgressDialog(true);

            RegisterAPI.getInstance(this).addStudentToGroup(stud_id, groupId,  new RegisterAPI.RegistrationCallback() {
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
*/
    public void assignWrstbnd(View view) {
        Intent i = new Intent(getApplicationContext(),ListWristbnd.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("studentID",stud_id);
        i.putExtra("userName",myEtText);
        i.putExtra("groupID",groupId);
        startActivity(i);
    }
}
package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 *
 * Start Activity Class
 *
 */
public class ChoiceActivity extends Activity {
    String myEtText, babyAlias, productID, serialNumb, childID, child_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myEtText = extras.getString("userName");
            babyAlias = extras.getString("babyAlias");
            if(extras.getString("token")!= null)
                child_token = extras.getString("token");
           if(extras.getString("productid")!=null)
            productID = extras.getString("productid");
           if(extras.getString("serialNumber")!=null)
            serialNumb = extras.getString("serialNumber");
            childID = extras.getString("childID");
            /*babyAlias = extras.getString("babyAlias");
            productID = extras.getString("productid");
            serialNumb = extras.getString("serialNumber");
            childID = extras.getString("childID");*/
            // Log.d("User to query in onCreate.....=",myEtText);

        }
        Log.d("MILA.....=",myEtText + "token = " + child_token);


    }
    @Override
    public void onBackPressed()
    {
               Intent i = new Intent(ChoiceActivity.this,SelectChild.class);
               i.putExtra("userName", myEtText);
               startActivity(i);

    }



    public void personalPlay(View view) {
        Intent i = new Intent(ChoiceActivity.this, Statistic.class);
       // String userName = myEtText;
        i.putExtra("userName", myEtText);
        i.putExtra("isTeacher", false);
        i.putExtra("personalPlay",true);
        i.putExtra("babyAlias", babyAlias);
        i.putExtra("productid", productID);
        i.putExtra("serialNumber", serialNumb);
        i.putExtra("childID", childID);
        Log.d("What is returned on activity Login view ......", myEtText);
        startActivity(i);
    }

    public void groupPlay(View view) {
        if(child_token!=null&&!(child_token.contains("null"))) {
            Intent i = new Intent(ChoiceActivity.this, Group_Statistic.class);
            i.putExtra("userName", myEtText);
            i.putExtra("isTeacher", false);
            i.putExtra("token", child_token);
            i.putExtra("babyAlias",babyAlias);
            Log.d("MILA", myEtText + " " + child_token + " " + "baby_alias = " + babyAlias);
            startActivity(i);
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "NO GROUP ACTIVITY AVAILABLE", Toast.LENGTH_LONG).show();

                }
            });}
    }

}

package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 *
 * Start Activity Class
 *
 */
public class ChoiceActivity extends Activity {
    String myEtText, babyAlias, productID, serialNumb, childID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myEtText = extras.getString("userName");
            babyAlias = extras.getString("babyAlias");
            productID = extras.getString("productid");
            serialNumb = extras.getString("serialNumber");
            childID = extras.getString("childID");
            // Log.d("User to query in onCreate.....=",myEtText);

        }


    }
    @Override
    public void onBackPressed()
    {
               Intent i = new Intent(ChoiceActivity.this,GetProduct.class);

        startActivity(i);

    }



    public void personalPlay(View view) {
        Intent i = new Intent(ChoiceActivity.this, Statistic.class);
        String userName = myEtText;
        i.putExtra("userName", userName);
        i.putExtra("babyAlias", babyAlias);
        i.putExtra("productid", productID);
        i.putExtra("serialNumber", serialNumb);
        i.putExtra("childID", childID);
        Log.d("What is returned on activity Login view ......", userName);
        startActivity(i);
    }

    public void groupPlay(View view) {

    }
}

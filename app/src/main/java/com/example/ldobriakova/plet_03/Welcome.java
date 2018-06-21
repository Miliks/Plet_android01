package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Welcome extends Activity  {
    private Spinner spinnerProduct, spinnerActivity;
    private Button btnNetwork;
    String userName, babyAlias, pruductAndID;
    String result;
    ProgressDialog progressDialog;
    JSONArray productList = new JSONArray();
    List<String> listMag = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
       // addItemOnSpinnerProduct();
        addItemOnSpinnerActivity();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        Bundle extras = getIntent().getExtras();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);

        if (extras != null) {

            userName = extras.getString("userName");
            babyAlias = extras.getString("babyAlias");

        }

        getProducts(userName);
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
                addListenerOnSpinnerItemSelection();
                addItemOnSpinnerProductList(list);//change the function

            }
        });
    }

    public void getProducts(String myEtText){
        enableProgressDialog(true);

        RegisterAPI.getInstance(this).queryProductList(myEtText,  new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                enableProgressDialog(false);
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    JSONObject result_obj = jsonResponse.getJSONObject("content");
                    result = jsonResponse.getString("result");
                    if(result.equals("OK"))
                    {

                        try {
                            //TODO
                            //Need to handle empty list returned in case there is no babies registered on the user
                            productList = result_obj.getJSONArray("UserInstanceList");
                            if(productList.length()>0) {
                                for (int i = 0; i < productList.length(); i++) {
                                    JSONObject b = productList.getJSONObject(i);
                                    String productAlias = b.getString("productName");
                                    String serialNumber = b.getString("serialNumber");
                                    pruductAndID = productAlias + "_" + serialNumber;
                                    listMag.add(pruductAndID);
                                    int j = pruductAndID.indexOf("_");
                                    Log.d("Index of _ ========",String.valueOf(j));
                                   }
                                updateUISpinner(listMag);
                            }
                            else
                            {
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

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(Welcome.this,SelectChild.class);
        i.putExtra("userName",userName);
        startActivity(i);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addListenerOnSpinnerItemSelection() {
        spinnerProduct = (Spinner)findViewById(R.id.spinnerProduct);
        spinnerProduct.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    private void addItemOnSpinnerActivity() {
        spinnerActivity = (Spinner) findViewById(R.id.spinnerActivity);
        List<String> list = new ArrayList<String>();
        list.add("DefaultActivity");
        /*list.add("Activity_002");
        list.add("Activity_003");*/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(dataAdapter);
    }

    /**********************
     * private void addItemOnSpinneraddBaby(List<String> selection) {
     spinnerBaby = (Spinner) findViewById(R.id.spinnerBaby);

     Log.d("LArray returned ========", selection.toString());
     ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,selection);
     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     spinnerBaby.setAdapter(dataAdapter);

     }
     *
     */
    private void addItemOnSpinnerProductList(List<String> selection) {
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);

        Log.d("LArray of products returned ========", selection.toString());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,selection);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(dataAdapter);

    }

    /*private void addItemOnSpinnerProduct_old() {
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        List<String> list = new ArrayList<String>();
        list.add("DOLPHINE_001");
        list.add("DOLPHINE_002");
        list.add("DOLPHINE_003");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(dataAdapter);
    }*/
    // get the selected dropdown list value
    private void addListenerOnButton() {
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        spinnerActivity = (Spinner) findViewById(R.id.spinnerActivity);
        btnNetwork = (Button)findViewById(R.id.btnNetwork);

        btnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(Welcome.this,
                        "OnClickListener : " +
                                "\nSpinner 1 : "+ String.valueOf(spinnerProduct.getSelectedItem()) +
                                "\nSpinner 2 : "+ String.valueOf(spinnerActivity.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();*/
                navigatetoWiFiSearch();
            }
        });
    }


    private void navigatetoWiFiSearch() {
        Intent scanner = new Intent(getApplicationContext(), WiFiDemo.class);
        scanner.putExtra("userName",userName);
        scanner.putExtra("babyAlias", babyAlias);
        Log.d("Product selected ========", spinnerProduct.toString());
        scanner.putExtra("productID",spinnerProduct.toString());
        scanner.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(scanner);
    }

    public void navigateStat(View view) {

        Intent i = new Intent(Welcome.this,Statistic.class);
        i.putExtra("userName",userName);
        i.putExtra("babyAlias", babyAlias);
        startActivity(i);


    }
    public void navigateLogin(View view) {
        Intent navLogin = new Intent(getApplicationContext(),LoginActivity.class);
        navLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(navLogin);
    }
    public void quitApp(View view) {
        //finishAndRemoveTask();
        finishAffinity();
        System.exit(0);
        onBackPressed();
    }

    public void startLogging(View view) {
        //TODO
        //Create a method to activate a data logging on the smart toy
    }
}

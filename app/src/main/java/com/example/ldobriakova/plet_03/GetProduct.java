package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetProduct extends Activity  {
    private Spinner spinnerProduct;
    EditText serialNumber;
    ProgressDialog prgDialog;
    private Button btnNetwork;
    String userName, babyAlias, productID, productName, combined, combined1;
    String result, toyAlias;
    JSONArray productList = new JSONArray();
    List<String> listMag = new ArrayList<String>();
    List<String> listName = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_product);
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        //addListenerOnButton();
        //addListenerOnSpinnerItemSelection();
        //addItemOnSpinnerProduct();
        Bundle extras = getIntent().getExtras();
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setIndeterminate(true);
        if (extras != null) {

            userName = extras.getString("userName");
            babyAlias = extras.getString("babyAlias");
        }
        getProductList();
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(GetProduct.this,SelectBaby.class);
        i.putExtra("userName",userName);
        i.putExtra("babyAlias",babyAlias);
        startActivity(i);

    }

    private void addListenerOnSpinnerItemSelection() {
        spinnerProduct = (Spinner)findViewById(R.id.spinnerProduct);
        spinnerProduct.setOnItemSelectedListener(new CustomOnItemSelectedListener());

    }

   /* private void addItemOnSpinnerProduct() {
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        List<String> list = new ArrayList<String>();
        list.add("Smart Elephant");
        list.add("SmartDolphin");
        list.add("SmartPeluche");
        list.add("TABLET-0000");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(dataAdapter);
    }*/
    // get the selected dropdown list value
    private void addListenerOnButton() {
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        btnNetwork = (Button)findViewById(R.id.btnNetwork);


    }


     public void quitApp(View view) {
         Intent i = new Intent(GetProduct.this, Welcome.class);
         //Intent scanner = new Intent(getApplicationContext(), WiFiDemo.class);
         i.putExtra("userName",userName);
         i.putExtra("babyAlias",babyAlias);
         startActivity(i);
    }
    private void enableProgressDialog(final boolean enable)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enable)
                    prgDialog.show();
                else
                    prgDialog.hide();
            }
        });
    }
    private void cleanText(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            }
        });
    }
    private void updateUISpinner( final List<String> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addListenerOnSpinnerProductSelection();
                addItemOnSpinnerProductList(list);
                //addListenerOnButtonFwd();
            }
        });
    }
    private void addListenerOnSpinnerProductSelection() {
        Log.d("We are in listener on spinner","....");
        spinnerProduct = (Spinner)findViewById(R.id.spinnerProduct);
        spinnerProduct.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    private void addItemOnSpinnerProductList(List<String> selection) {
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
         ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,selection);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(dataAdapter);

    }

    //Get product list from ARAS
    public void getProductList()
    {
        enableProgressDialog(true);

        RegisterAPI.getInstance(this).getProductList(new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                try {
                    enableProgressDialog(false);
                    JSONObject jsonResponse = new JSONObject(str);
                    String result = jsonResponse.getString("result");
                    JSONObject result_obj = jsonResponse.getJSONObject("content");

                    if (result.equals("OK")) {
                        productList = result_obj.getJSONArray("ProductList");
                        if(productList.length()>0) {
                              for (int i = 0; i < productList.length(); i++) {
                                JSONObject b = productList.getJSONObject(i);
                                combined = b.getString("ProductId") + "_" + b.getString("CommercialName");
                                combined1 = b.getString("CommercialName")+ "_" +b.getString("ProductId");
                                productID = b.getString("ProductId");
                                productName=b.getString("CommercialName");
                                String temp = combined1.substring(0,combined1.indexOf("_"));
                                  //String temp1 = combined.substring(0,combined.indexOf("_"));
                                  Log.d("product selected ======= ",combined1);
                                  Log.d("product selected ======= ",temp);
                                 // Log.d("combined truncated = ",combined.substring(combined.indexOf("__"),combined.indexOf("__")+combined.lastIndexOf(combined)));
                                listMag.add(productID);
                               // listName.add(combined.substring(0,combined.indexOf("_")));
                                  listName.add(temp);

                                  //listName.add(combined.substring(combined.indexOf("__"),combined.indexOf("__")+combined.lastIndexOf(combined)));

                            }
                            updateUISpinner(listName);
                        }
                        else
                        {
                            Log.d("Array returned1111 ========", "No BABIES...........");
                            updateUISpinner(listMag);
                        }


                        prgDialog.dismiss();

                    } else {

                        cleanText(jsonResponse.getString("message"));

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
    public void backTo_(View view) {
        spinnerProduct = (Spinner)findViewById(R.id.spinnerProduct);

        serialNumber = (EditText)findViewById(R.id.serialNumber);
        String serialNumberString = serialNumber.getText().toString();
        productName = spinnerProduct.getSelectedItem().toString();
        //TODO
        //This data have to be retrived from the toy
        Log.d("product selected ======= ",productName);
        /*if(productName.contains("Smart Elephant")){
            productID = "512B28C8A41CE065F1493686D93717FD";
            serialNumber = "00000001";
        }
        else if (productName.contains("SmartPeluche")){
            productID = "E08C5D9D672C098BF605FD04B06396FB";
            serialNumber = "00000001";
        }
        else if (productName.contains("SmartDolphin")){
            productID = "D20BEAE6BD1D23B0D6569611B5BF45B3";
            serialNumber = "00000001";
        }
        else
        {
            productID = "5543DB569905454A99EA2BE5402DF0BA";
            serialNumber = "00000001";
        }
*/
        //After we get the product serial number we need to map the userID and product uniqui identifier to strt session
        enableProgressDialog(true);
        String toyAl = "My Toy";

        RegisterAPI.getInstance(this).registerProdInstance(userName, productID, serialNumberString, toyAl, new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                try {
                    enableProgressDialog(false);
                    JSONObject jsonResponse = new JSONObject(str);
                    String result = jsonResponse.getString("result");

                    if (result.equals("OK")) {
                        Log.d("attemptToRegister", "SUCCESSSSSSSS!!!!!..");
                        Intent i = new Intent(GetProduct.this, Welcome.class);
                        //Intent scanner = new Intent(getApplicationContext(), WiFiDemo.class);
                        i.putExtra("userName",userName);
                        i.putExtra("babyAlias",babyAlias);
                        i.putExtra("productID", productID);
                       // i.putExtra("serialNumber", serialNumber);
                        //startActivity(scanner);
                        i.putExtra("productName", productName);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        prgDialog.dismiss();
                        startActivity(i);
                    } else {

                        cleanText(jsonResponse.getString("message"));

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
}

package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Welcome extends Activity  {
    private Spinner spinnerProduct, spinnerActivity;
    private ProductSpinAdapter newAdapter;
    private Button btnNetwork;
    String userName, babyAlias, productID, serialNumb, productAlias;
    String result;
    ProgressDialog progressDialog;
    JSONArray productList = new JSONArray();
    List<String> listMag = new ArrayList<String>();
    NsdServiceInfo serviceInfo = new NsdServiceInfo();
    final String SERVICE_TYPE = "_smartobject._tcp.";
    //final String SERVICE_NAME = "smartobject";
    final String SERVICE_NAME = "smartkitchen_35461_5461";
    //final String SERVICE_NAME = "smartdolphin_ae30";
    Button collectData;
    public static final String TAG = "MILA";
    Boolean listenerFlag = false;
    Boolean succcess = false;
   // Boolean succcess;
    private InetAddress hostAddress;
    private int hostPort;
    private NsdManager mNsdManager;
    private Boolean listenerButton = false;
    Boolean valueInternal = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        addItemOnSpinnerActivity();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        Bundle extras = getIntent().getExtras();
        collectData = (Button)findViewById(R.id.start_logging) ;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        if (extras != null) {

            userName = extras.getString("userName");
            babyAlias = extras.getString("babyAlias");

        }

        getProducts(userName);
        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
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
               // addListenerOnSpinnerItemSelection();
                addItemOnSpinnerProductList(list);//change the function

            }
        });
    }
//Select toys registered on this user account
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

                            productList = result_obj.getJSONArray("UserInstanceList");
                            if(productList.length()>0) {
                                for (int i = 0; i < productList.length(); i++) {
                                    JSONObject b = productList.getJSONObject(i);
                                    String productID = b.getString("productID");
                                    String serialNumber = b.getString("serialNumber");
                                    String toyAlias = b.getString("toyAlias");
                                    listMag.add(productID + "+" + serialNumber + "++" + toyAlias);
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

    private void addItemOnSpinnerProductList(List<String> selection) {
        int i = selection.size();
        Product [] product = new Product[i];

        for (int j=0; j<i; j++){
            int product_index = selection.get(j).indexOf("+");
            product[j] = new Product();
            productID = selection.get(j).substring(0, product_index);
            int serial_index = selection.get(j).indexOf("++");
            serialNumb = selection.get(j).substring(product_index+1,serial_index);
            productAlias = selection.get(j).substring(serial_index+2,selection.get(j).length());
            product[j].setProd_serial(serialNumb);
            product[j].setProdAlias(productAlias);
            product[j].setProdID(productID);
        }
        newAdapter = new ProductSpinAdapter(Welcome.this, android.R.layout.simple_spinner_item,product);
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        spinnerProduct.setAdapter(newAdapter);
        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Product product = (Product) newAdapter.getItem(position);
               productAlias =  product.getProd_alias();
               productID = product.getProd_id();
               serialNumb = product.getSerial_number();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });


    }

     private void addListenerOnButton() {
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        spinnerActivity = (Spinner) findViewById(R.id.spinnerActivity);
        btnNetwork = (Button)findViewById(R.id.btnNetwork);

        btnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigatetoWiFiSearch();
                navigateToRegisterNewToy();
            }
        });
    }

    private void navigateToRegisterNewToy() {
        Intent registerNewToy = new Intent(getApplicationContext(), GetProduct.class);
        registerNewToy.putExtra("userName",userName);
        registerNewToy.putExtra("babyAlias", babyAlias);
        //Log.d("Product selected ========", spinnerProduct.toString());
        //scanner.putExtra("productID",spinnerProduct.toString());
        registerNewToy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerNewToy);
    }

    public void navigateStat(View view) {
        Intent i = new Intent(Welcome.this,Statistic.class);
        i.putExtra("userName",userName);
        i.putExtra("babyAlias", babyAlias);
        startActivity(i);


    }

   /* private class MyResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(TAG, "Resolve failed " + errorCode);
            Log.e(TAG, "service = " + serviceInfo);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d("MILA", "Resolve Succeeded. " + serviceInfo);

            if (serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                Log.d(TAG, "Same IP.");
                return;
        }
    }}*/

        private final NsdManager.ResolveListener mResolveListener = new NsdManager.ResolveListener() {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(TAG, "Resolve failed " + errorCode);
            Log.e(TAG, "service = " + serviceInfo);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d("MILA", "Resolve Succeeded. " + serviceInfo);

            if (serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                 // Obtain port and IP
             hostAddress = serviceInfo.getHost();
             controlDataCollectionUI(hostAddress);
             listenerFlag = true;

            }
        }
    };

    private final NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {
        // Called as soon as service discovery begins.
        @Override
        public void onDiscoveryStarted(String regType) {

            Log.d(TAG, "Service discovery started............");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {

            // A service was found! Do something with it.
            Log.d(TAG, "Service discovery success : " + service);
            Log.d(TAG, "Host = "+ service.getServiceName());
            Log.d(TAG, "Host port = " + service.getHost());
            Log.d(TAG, "port = " + String.valueOf(service.getPort()));
            if (!service.getServiceType().equals(SERVICE_TYPE)) {
                // Service type is the string containing the protocol and
                // transport layer for this service.
                Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
            } else if (service.getServiceName().equals(SERVICE_NAME)) {
                // The name of the service tells the user what they'd be
                // connecting to. It could be "Bob's Chat App".
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
                mNsdManager.resolveService(service, mResolveListener);
                listenerFlag=true;

            } else {
                Log.d(TAG, "Diff Machine : " + service.getServiceName());
                // connect to the service and obtain serviceInfo
                mNsdManager.resolveService(service, mResolveListener);
            }
              }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost" + service);
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i(TAG, "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }
    };

    private void setIPUI( final InetAddress hostAddress){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startDataCollectionCall(hostAddress);
                //setIP(hostAddress);
            }
        });
    }

    private void controlDataCollectionUI( final InetAddress hostAddress){
               if(listenerButton)
                        startDataCollectionCall(hostAddress);

                            else
                    stopDataCollectionCall(hostAddress);
     }

     private void startDataCollectionCall(InetAddress hostAddress) {

        if(!hostAddress.equals(null)){
            enableProgressDialog(true);
            String internalHost = hostAddress.toString().replace("/","");

            RegisterAPI.getInstance(this).controllerDataCollection("1",internalHost,new RegisterAPI.RegistrationCallback(){
                @Override
                public void onResponse(String str) {
                    JSONObject jsonResponse = null;
                    try {
                        enableProgressDialog(false);
                        jsonResponse = new JSONObject(str);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(jsonResponse.toString().equals("{}")){
                        succcess=true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Data logging is enabled", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"value succes in startDataCollection IF  = " + succcess);}

                    });
                    }
                                          else{
                        succcess=false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"value succes in startDataCollection ELSE  = " + succcess);}

                    });}
                }


                @Override
                public void onError(RegistrationResponse.RegistrationError error) {
                    enableProgressDialog(false);
                    succcess=false;
                    Log.d(TAG, "OnError FAILED!!!!!..");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"value succes in startDataCollection ELSE  = " + succcess);}

                    });
                }

                @Override
                public void onNetworkError() {
                    succcess=false;
                    enableProgressDialog(false);
                    Log.d("onNetworkError", "FAILED!!!!!..");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"value succes in startDataCollection ELSE  = " + succcess);}

                    });
                }
            });}
   }

    private void stopDataCollectionCall(InetAddress hostAddress) {
        enableProgressDialog(true);
        if(!hostAddress.equals(null)){
             String internalHost = hostAddress.toString().replace("/","");
            Log.d(TAG,"HOST = " + internalHost);
            RegisterAPI.getInstance(this).controllerDataCollection("0",internalHost,new RegisterAPI.RegistrationCallback(){
                @Override
                public void onResponse(String str) {
                    try {
                        enableProgressDialog(false);

                        JSONObject jsonResponse = new JSONObject(str);
                        Log.d(TAG, "Responsefrom toy=" + jsonResponse.toString());
                        //TODO
                        //POLIMI have to confirm the response from the start stop data logging and i'll adapt it to
                        if(jsonResponse.toString().equals("{}")){
                            succcess=true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Data logging is disabled", Toast.LENGTH_LONG).show();}

                            });
                        }


                        else {
                            succcess = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();}

                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(RegistrationResponse.RegistrationError error) {
                    succcess=false;
                    enableProgressDialog(false);
                    Log.d(TAG, "OnError FAILED!!!!!..");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"value succes in startDataCollection ELSE  = " + succcess);}

                    });
                }

                @Override
                public void onNetworkError() {
                    succcess=false;
                    enableProgressDialog(false);
                    Log.d("onNetworkError SetIP", "FAILED!!!!!..");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"value succes in startDataCollection ELSE  = " + succcess);}

                    });
                }
            });}
    }
    private void setIP(InetAddress hostAddress) {
        enableProgressDialog(true);
        if(!hostAddress.equals(null)){
            //g.d("Host adress in SetIP","HOST = " + hostAddress);
            String internalHost = hostAddress.toString().replace("/","");
            Log.d(TAG,"HOST = " + internalHost);
            RegisterAPI.getInstance(this).setAdress(internalHost,new RegisterAPI.RegistrationCallback(){
                @Override
                public void onResponse(String str) {
                    try {
                        enableProgressDialog(false);

                        JSONObject jsonResponse = new JSONObject(str);
                        String result = jsonResponse.getString("status");
                         if (result.equals("ok")) {
                            Log.d(TAG, "SUCCESSSSSSSS!!!!!..");

                        } else {

                            Log.d(TAG, "FAILED!!!!!..");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(RegistrationResponse.RegistrationError error) {
                    enableProgressDialog(false);
                    Log.d(TAG, "OnError FAILED!!!!!..");
                }

                @Override
                public void onNetworkError() {

                    enableProgressDialog(false);
                    Log.d("onNetworkError SetIP", "FAILED!!!!!..");
                }
            });}


    }

    public void discoveryToy(){
        Log.d(TAG, "Discovery started ...........");
        listenerFlag = true;
        mNsdManager.discoverServices(SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

    }

    public void startLogging(View view)
    {
        String prodID_selected = productID;
        if(!listenerButton) {
            listenerButton = true;
            discoveryToy();
            Log.d(TAG, "Start logging SUCCESS value = " + succcess);
            collectData.setText("   STOP DATA COLLECTION   ");


        }
        else
        {
            collectData.setText("   START DATA COLLECTION   ");
            listenerButton = false;
            discoveryToy();
            Log.d(TAG, "Start logging SUCCESS value = " + succcess);
        }

   }


}

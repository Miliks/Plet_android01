package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
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
    final String SERVICE_NAME = "smartobject";
    //final String SERVICE_NAME = "smartdolphin_ae30";
    Button collectData;
    public static final String TAG = "MILA";
    Boolean listenerFlag = false;
    private InetAddress hostAddress;
    private int hostPort;
    private NsdManager mNsdManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
       // addItemOnSpinnerProduct();
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
        int i = selection.size();
        Product [] product = new Product[i];

        for (int j=0; j<i; j++){
            Log.d("Array of products returned ========", selection.get(j));
            int product_index = selection.get(j).indexOf("+");
            Log.d("Index of last simbol of productID ========", String.valueOf(product_index));
            product[j] = new Product();
            productID = selection.get(j).substring(0, product_index);
            Log.d("productID ========", productID);
            int serial_index = selection.get(j).indexOf("++");
            Log.d("Index of serial number ========", String.valueOf(serial_index));
            serialNumb = selection.get(j).substring(product_index+1,serial_index);
            Log.d("serial number ========", serialNumb);
            Log.d("Index of alias ========", String.valueOf(selection.get(j).length()));

            productAlias = selection.get(j).substring(serial_index+2,selection.get(j).length());
            Log.d("alias ========", productAlias);
            product[j].setProd_serial(serialNumb);
            product[j].setProdAlias(productAlias);
            product[j].setProdID(productID);
        }
        newAdapter = new ProductSpinAdapter(Welcome.this, android.R.layout.simple_spinner_item,product);
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        spinnerProduct.setAdapter(newAdapter);
       /* Log.d("LArray of products returned ========", selection.toString());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,selection);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
       spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Product product = (Product) newAdapter.getItem(position);
               //  String cld_al =  newAdapter.getItem(position).getChild_alias();

               Log.d(" Baby  at position =======", String.valueOf(position) + "....  " +product.getProd_alias());

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


    private void navigatetoWiFiSearch() {
        Intent scanner = new Intent(getApplicationContext(), WiFiDemo.class);
        scanner.putExtra("userName",userName);
        scanner.putExtra("babyAlias", babyAlias);
        Log.d("Product selected ========", spinnerProduct.toString());
        //scanner.putExtra("productID",spinnerProduct.toString());
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

    private class MyResolveListener implements NsdManager.ResolveListener {
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
    }}
    //Susbstituted by class implementing the same functionality
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
                Log.d(TAG, "Same IP.");
                return;
            }

            // Obtain port and IP
            //hostPort = serviceInfo.getPort();
            //hostAddress = serviceInfo.getHost();
           // Log.d("MILA", "Resolve listener ..........." + hostAddress);
            // setIPUI(hostAddress);
            // mNsdManager.unregisterService(mRegistrationListener);
            // mNsdManager.stopServiceDiscovery(mDiscoveryListener);
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
            Log.d("MILA FLAG  = ",listenerFlag.toString());
            //if(!listenerFlag){
            if (!service.getServiceType().equals(SERVICE_TYPE)) {
                // Service type is the string containing the protocol and
                // transport layer for this service.
                Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
            } else if (service.getServiceName().equals(SERVICE_NAME)&&(!listenerFlag)) {
                // The name of the service tells the user what they'd be
                // connecting to. It could be "Bob's Chat App".
                Log.d("MILA","BEFORE RESOLVE LISTENER");
                mNsdManager.resolveService(service, new MyResolveListener());
                //mNsdManager.resolveService(service, mResolveListener);
                Log.d("MILA","AFTER RESOLVE LISTENER");
                //Log.d(TAG, "Same machine: " + SERVICE_NAME);
                //Log.d("MILA",service.getHost().getHostAddress());
                //hostAddress = service.getHost();
                Log.d(TAG, "NOT working hostadress: " + hostAddress);
                //TODO
                //Since the simulator do not work properly and do not return the IP adress of the service i'll set it up manually
                try {
                    hostAddress = InetAddress.getByName("192.168.0.184");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Same machine hostadress: " + hostAddress);
                //mNsdManager.resolveService(service,mResolveListener);
                //Let's call the service to set up the IP to send the sensors datac

                // setAdress
                //mNsdManager.resolveService(service,mResolveListener);
                listenerFlag=true;
                setIPUI(hostAddress);


                mNsdManager.stopServiceDiscovery(this);
                // mNsdManager.unregisterService(this);

            } else {
                Log.d(TAG, "Diff Machine : " + service.getServiceName());
                // connect to the service and obtain serviceInfo
                mNsdManager.resolveService(service, mResolveListener);
            }
            //  }
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
                setIP(hostAddress);
            }
        });
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
                        Log.d(TAG, "SUCCESSSSSSSS!!!!!..");
                        JSONObject jsonResponse = new JSONObject(str);
                        String result = jsonResponse.getString("result");
                        //Log.d("What is returned on activity Login view ......", result);
                        if (result.equals("OK")) {
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

    public void startLogging(View view) {
        //TODO
        //Create a method to activate a data logging on the smart toy
        //Verify what the productID of selected toy is the same returned by Toy firware
        //After several click on button app crash
        String prodID_selected = productID;
        listenerFlag = false;
        Log.d("MILA","start discovery and logging");
        collectData.setTag(1);
        collectData.setText("   START DATA COLLECTION   ");
        collectData.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final int status =(Integer) v.getTag();
                if(status == 1) {
                    mNsdManager.discoverServices(SERVICE_TYPE,
                            NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
                    collectData.setText("   STOP DATA COLLECTION   ");
                    //collectData.setBackgroundColor(1);
                    v.setTag(0); //started
                } else {
                    collectData.setText("   START DATA COLLECTION   ");
                    //Not sure about this, but to avoi crash want to try
                    mNsdManager.stopServiceDiscovery(mDiscoveryListener);
                    v.setTag(1); //stopped
                }
            }
        });
        //mNsdManager.discoverServices(SERVICE_TYPE,
          //      NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }
}

package com.example.ldobriakova.plet_03;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WiFiDemo extends Activity implements View.OnClickListener {
    WifiManager wifi;
    private Spinner spinnerWiFi;

    ListView lv;
    TextView textStatus;
    Button buttonScan;
    int size = 0;
    List<ScanResult> results;
    String ipString, result;
    String wifiSpinnerValue = null;
    Boolean listenerFlag = false;
    Button collectData;
    String mServiceName;
    //NsdServiceInfo mServiceInfo;
    NsdServiceInfo serviceInfo = new NsdServiceInfo();
    final String SERVICE_TYPE = "_smartobject._tcp.";

    final String SERVICE_NAME = "smartobject";
    int mLocalPort;

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    String userName, babyAlias;

    List<String> list = new ArrayList<String>();
   ProgressDialog progressDialog;
    private InetAddress hostAddress;
    private int hostPort;
    private NsdManager mNsdManager;
    public static final String TAG = "MILA";

    /* Called when the activity is first created. */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifidemo);
        addListenerOnSpinnerWifi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        Bundle extras = getIntent().getExtras();
        userName = extras.getString("userName");
        babyAlias = extras.getString("babyAlias");
        buttonScan = (Button) findViewById(R.id.buttonScan);
        collectData = (Button)findViewById(R.id.collectData);
        buttonScan.setOnClickListener(this);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        registerService();

        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        this.adapter = new SimpleAdapter(WiFiDemo.this, arraylist, R.layout.row, new String[] { ITEM_KEY }, new int[] { R.id.list_value });

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();
                unregisterReceiver(this);

            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    //Let's register dummy service to discover
    public void registerService(int port) {

        serviceInfo.setServiceName(SERVICE_NAME);
        Log.d(TAG, "REGISTER SERVICE started......");
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);
        InetAddress s = null;
        try {
            s = InetAddress.getByName("192.168.0.164");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "BEFORE setHost");
        serviceInfo.setHost(s);
        Log.d(TAG, "Registered service address..... =" + serviceInfo.getHost());
       // mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(serviceInfo,
                NsdManager.PROTOCOL_DNS_SD,
                mRegistrationListener);
    }

    private final NsdManager.RegistrationListener mRegistrationListener = new NsdManager.RegistrationListener() {

        @Override
        public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
            Log.d(TAG, "onServiceRegistered............." );
            String mServiceName = nsdServiceInfo.getServiceName()+nsdServiceInfo.getPort();

            Log.d(TAG, "Registered name : " + mServiceName);

           // InetAddress addressI = nsdServiceInfo.getHost();
            Log.d(TAG, "Registered IP : " + nsdServiceInfo.getHost());


        }

        @Override
        public void onRegistrationFailed(NsdServiceInfo serviceInfo,
                                         int errorCode) {
            // Registration failed! Put debugging code here to determine
            // why.
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
            // Service has been unregistered. This only happens when you
            // call
            // NsdManager.unregisterService() and pass in this listener.
            Log.d(TAG,
                    "Service Unregistered : " + serviceInfo.getServiceName());
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo,
                                           int errorCode) {
            // Unregistration failed. Put debugging code here to determine
            // why.
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
            //if(!listenerFlag){
            if (!service.getServiceType().equals(SERVICE_TYPE)) {
                // Service type is the string containing the protocol and
                // transport layer for this service.
                Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
            } else if (service.getServiceName().equals(SERVICE_NAME)&&(!listenerFlag)) {
                // The name of the service tells the user what they'd be
                // connecting to. It could be "Bob's Chat App".
              //mNsdManager.resolveService(service, mResolveListener);
                Log.d(TAG, "Same machine: " + SERVICE_NAME);

                hostAddress = service.getHost();
                Log.d(TAG, "NOT working hostadress: " + hostAddress);
                //TODO
                //Since the simulator do not work properly and do not return the IP adress of the service i'll set it up manually
                try {
                    hostAddress = InetAddress.getByName("192.168.0.164");
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

    private void registerService(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registerService(80);
            }
        });
    }

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

   private final NsdManager.ResolveListener mResolveListener = new NsdManager.ResolveListener() {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(TAG, "Resolve failed " + errorCode);
            Log.e(TAG, "service = " + serviceInfo);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "Resolve Succeeded. " + serviceInfo);

            if (serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                Log.d(TAG, "Same IP.");
                return;
            }

            // Obtain port and IP
            hostPort = serviceInfo.getPort();
            hostAddress = serviceInfo.getHost();
            Log.d(TAG, "Resolve listener ..........." + hostAddress);
           // setIPUI(hostAddress);
           // mNsdManager.unregisterService(mRegistrationListener);
           // mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
    };

    public void discoveryToy(){
        Log.d(TAG, "Discovery started ...........");

        mNsdManager.discoverServices(SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

   /* @Override
    protected void onPause() {
        if (mNsdHelper != null) {
            mNsdHelper.tearDown();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNsdHelper != null) {
//            mNsdHelper.registerService(mConnection.getLocalPort());
//            mNsdHelper.discoverServices();
        }
    }

    @Override
    protected void onDestroy() {
        mNsdHelper.tearDown();
//        mConnection.tearDown();
        super.onDestroy();
    }*/

    private void updateUISpinner( final List<String> list){
               runOnUiThread(new Runnable() {
            @Override
            public void run() {
               //addListenerOnSpinnerWifi();
                addOneItemOnSpinner(list);
            }
        });
    }

    private void addListenerOnSpinnerWifi() {
        spinnerWiFi = (Spinner)findViewById(R.id.spinnerWifi);
        spinnerWiFi.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

     private void addOneItemOnSpinner(List<String> selection){
        spinnerWiFi = (Spinner) findViewById(R.id.spinnerWifi);
        Log.d("list ........................=",selection.toString());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,selection);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWiFi.setAdapter(dataAdapter);
        wifiSpinnerValue = spinnerWiFi.getSelectedItem().toString();
        Log.d("wifiSpinnerValue =  ........................=",wifiSpinnerValue);

    }
    public void connectToToy_old(View view)
    {

        if(spinnerWiFi != null && spinnerWiFi.getSelectedItem() !=null ) {
            String wifi_ssid = spinnerWiFi.getSelectedItem().toString();
            Log.d("WIFI........... =", wifi_ssid);
            String networkPass = "12345678";
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = String.format("\"%s\"", wifi_ssid);
            conf.preSharedKey = String.format("\"%s\"", networkPass);
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            int netId = wifiManager.addNetwork(conf);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(netId, true);
                    wifiManager.reconnect();

            String url = "http://192.168.4.1/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        } else  {

            Toast.makeText(getApplicationContext(), "No wifi network have been selected please click scan button", Toast.LENGTH_LONG).show();
        }

    }


    public void onClick(View view)
    {
        arraylist.clear();
            ActivityCompat.requestPermissions(WiFiDemo.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.CHANGE_WIFI_STATE},
                    123);

        wifi.setWifiEnabled(true);
        if(!wifi.startScan())
            Toast.makeText(this, "Not scanning...." + size, Toast.LENGTH_SHORT).show();
       try
        {
            size = size - 1;
            while (size >= 0)
            {
                HashMap<String, String> item = new HashMap<String, String>();
                ScanResult sr = results.get(size);
                item.put(ITEM_KEY, sr.SSID);
                arraylist.add(item);
                //list.add(sr.SSID + "  " + sr.capabilities);
                list.add(sr.SSID);
                size--;
                adapter.notifyDataSetChanged();
            }
            //addOneItemOnSpinner(list);
            updateUISpinner(list);

        } catch (Exception e)
        {
            Log.d("Error Exception = ",e.getMessage());
        }
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


    public void assignToy(View view) {
        listenerFlag=false;
        collectData.setTag(1);
        collectData.setText("START DATA COLLECTION");
        collectData.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final int status =(Integer) v.getTag();
                if(status == 1) {
                    discoveryToy();
                    collectData.setText("STOP DATA COLLECTION");
                    collectData.setBackgroundColor(1);
                    v.setTag(0); //started
                } else {
                    collectData.setText("START DATA COLLECTION");
                    v.setTag(1); //stopped
                }
            }
        });
        Log.d(TAG,"Assigning the toy to the network........");
       /* mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

                registerService();*/

               // discoveryToy();
    }


}

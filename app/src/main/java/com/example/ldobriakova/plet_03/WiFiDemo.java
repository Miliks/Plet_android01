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
import android.os.Handler;
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

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class WiFiDemo extends Activity  {
    WifiManager wifi;
    private Spinner spinnerWiFi;
    Boolean listenerButton = false;
    ListView lv;
    TextView textStatus;
    Button buttonScan;
    int size = 0;
    List<ScanResult> results;
    String ipString, result;
    String wifiSpinnerValue = null;
    Boolean listenerFlag = false;
    Boolean buttonListener = false;
    Boolean connected = false;
    Boolean wificonnected = false;
    Button collectData;
    String mServiceName;
    //NsdServiceInfo mServiceInfo;
    NsdServiceInfo serviceInfo = new NsdServiceInfo();
    final String SERVICE_TYPE = "_smartobject._tcp.";
    //final String SERVICE_NAME = "smartobject";
    final String SERVICE_NAME = "smartkitchen_35461_5461";
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
        Fabric.with(this, new Crashlytics());
        addListenerOnSpinnerWifi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        Bundle extras = getIntent().getExtras();
        userName = extras.getString("userName");
        babyAlias = extras.getString("babyAlias");
        buttonScan = (Button) findViewById(R.id.buttonScan);
        collectData = (Button)findViewById(R.id.collectData);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        //registerService();

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

            if (!service.getServiceType().equals(SERVICE_TYPE)) {
                // Service type is the string containing the protocol and
                // transport layer for this service.
                Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
            } else if (service.getServiceName().equals(SERVICE_NAME)) {
                // The name of the service tells the user what they'd be
                // connecting to. It could be "Bob's Chat App".
                try{
                    mNsdManager.stopServiceDiscovery(mDiscoveryListener);}
                catch (IllegalArgumentException ex){
                    Log.d(TAG,"Listener already stopped ");
                }
                mNsdManager.resolveService(service, mResolveListener);
                listenerFlag=true;

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
            try{
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);}
            catch (IllegalArgumentException ex){
                Log.d(TAG,"Listener already stopped ");
            }
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            try{
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);}
            catch (IllegalArgumentException ex){
                Log.d(TAG,"Listener already stopped ");
            }
        }

    };

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
                Log.d(TAG, "Same IP = " + serviceInfo.getHost());

                hostAddress = serviceInfo.getHost();
                startDataCollectionCall(hostAddress);
               return;
            }

        }
    };

    private void startDataCollectionCall(InetAddress hostAddress) {
        enableProgressDialog(true);
        Log.d(TAG,"Start data collection = ");
        if(!hostAddress.equals(null)){
            String internalHost = hostAddress.toString().replace("/","");
            Log.d(TAG,"HOST = " + internalHost);
            RegisterAPI.getInstance(this).controllerDataCollection("1",internalHost,new RegisterAPI.RegistrationCallback(){
                @Override
                public void onResponse(String str) {
                    try {
                        enableProgressDialog(false);

                        JSONObject jsonResponse = new JSONObject(str);
                        //TODO
                        //When the real service will be available if have to be substituted by the commented lines
                        //String result = jsonResponse.getString("status");
                        //if (result.equals("ok")) {
                        if(jsonResponse.toString().equals("{}")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Data logging is enabled", Toast.LENGTH_LONG).show();
                                    connected = true;
                                    returnWelcome();
                                }

                            });

                        }
                        else
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();
                                }

                            });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(RegistrationResponse.RegistrationError error) {
                    enableProgressDialog(false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();
                        }

                    });
                    Log.d(TAG, "OnError FAILED!!!!!..");
                }

                @Override
                public void onNetworkError() {
                    enableProgressDialog(false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No communication with the toy have been established, please try again", Toast.LENGTH_LONG).show();
                        }

                    });
                    Log.d("onNetworkError", "FAILED!!!!!..");
                }
            });}

            }


    private void updateUISpinner( final List<String> list){
               runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
    public void returnWelcome()
    {
        Intent i = new Intent(WiFiDemo.this,Welcome.class);
        i.putExtra("userName",userName);

        startActivity(i);
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
            wificonnected = true;

        } else  {

            Toast.makeText(getApplicationContext(), "No wifi network have been selected please click scan button", Toast.LENGTH_LONG).show();
        }

    }


    public void onWiFiSearch(View view)
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
                list.add(sr.SSID);
                size--;
                adapter.notifyDataSetChanged();
            }
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

public void discovery(){
        if(buttonListener){

    mNsdManager.discoverServices(SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    try{
                        mNsdManager.stopServiceDiscovery(mDiscoveryListener);}
                    catch (IllegalArgumentException ex){
                        Log.d(TAG,"Listener already stopped ");
                    }
                }
            }, 5 * 1000);

        }
            else {
            mNsdManager.discoverServices(SERVICE_TYPE,
                    NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
try{
                    mNsdManager.stopServiceDiscovery(mDiscoveryListener);}
                    catch (IllegalArgumentException ex){
                        Log.d(TAG,"Listener already stopped ");
                    }
                }
            }, 5 * 1000);

        }
}

    public void assignToy(View view)
    {

       if(wificonnected){
           discovery();
        buttonListener = true;}
       // mNsdManager.discoverServices(SERVICE_TYPE,
           //     NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
       else
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(getApplicationContext(), "No new Wi-Fi hotspot have been added, please try again or go back to the Welcome page", Toast.LENGTH_LONG).show();
               }

           });

    }



}

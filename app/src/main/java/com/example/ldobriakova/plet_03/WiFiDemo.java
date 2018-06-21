package com.example.ldobriakova.plet_03;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WiFiDemo extends Activity implements View.OnClickListener
{
    WifiManager wifi;
    private Spinner spinnerWiFi;

    ListView lv;
    TextView textStatus;
    Button buttonScan;
    int size = 0;
    List<ScanResult> results;
    String ipString;
    String wifiSpinnerValue = null;

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    String userName, babyAlias;

    List<String> list = new ArrayList<String>();

    NsdHelper mNsdHelper;

    /* Called when the activity is first created. */

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifidemo);
        addListenerOnSpinnerWifi();
        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();
        mNsdHelper.discoverServices();

        Bundle extras = getIntent().getExtras();
        userName = extras.getString("userName");
        babyAlias = extras.getString("babyAlias");

        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

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
    @Override
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
    }

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
        // Log.d("WIFI =", "We r in addOneItemOnSpinner  .............");
        //wifiSpinnerValue = null;
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

    /*public void connectToToy(View view)
    {
        connectToToy_old(view);
        *//*Intent i = new Intent(WiFiDemo.this, GetProduct.class);
        i.putExtra("userName", userName);
        startActivity(i);
*//*

    }*/

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


    public void goToWelcome(View view) {
        Intent gotoWelcome = new Intent(getApplicationContext(),Welcome.class);
        gotoWelcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        gotoWelcome.putExtra("userName", userName);
        gotoWelcome.putExtra("babyAlias",babyAlias);
        startActivity(gotoWelcome);
    }
}

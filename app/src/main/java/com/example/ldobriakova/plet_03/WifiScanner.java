package com.example.ldobriakova.plet_03;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiScanner extends Activity implements View.OnClickListener{


    WifiManager wifi;
    ListView lv;
    Button buttonScan;
    int size = 0;
    List<ScanResult> results;

    String ITEM_KEY = "key";
    ArrayList<String> arraylist = new ArrayList<>();
    ArrayAdapter adapter;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scanner);
        //getActionBar().setTitle("Widhwan Setup Wizard");

        buttonScan = (Button) findViewById(R.id.scan);
        buttonScan.setOnClickListener(this);
        lv = (ListView)findViewById(R.id.wifilist);


        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        this.adapter =  new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arraylist);
        lv.setAdapter(this.adapter);

        scanWifiNetworks();
    }

    public void onClick(View view)
    {
        scanWifiNetworks();
    }

    private void scanWifiNetworks(){

        arraylist.clear();
        ActivityCompat.requestPermissions(WifiScanner.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION/*, Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.CHANGE_WIFI_STATE*/},
                123);

        registerReceiver(wifi_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifi.startScan();

        Log.d("WifScanner", "scanWifiNetworks");

        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();

    }

    BroadcastReceiver wifi_receiver= new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context c, Intent intent)
        {
            Log.d("WifScanner", "onReceive");
            results = wifi.getScanResults();
            size = results.size();
            unregisterReceiver(this);

            try
            {
                while (size > 0)
                {
                    size--;
                    arraylist.add(results.get(size).SSID);
                    adapter.notifyDataSetChanged();
                }
            }
            catch (Exception e)
            {
                Log.w("WifScanner", "Exception: "+e);

            }


        }
    };

}

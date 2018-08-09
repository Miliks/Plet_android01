package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

public class Statistic extends Activity {
    Button btnDatePicker;
    private int mYear, mMonth, mDay;
    TextView babyAliasView, title, averageTime;
    String productID, serialNumber;
    EditText txtDate;
    ProgressDialog prgDialog;
   // private BarChart barcrt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.statistic_view);
        title = (TextView) findViewById(R.id.title2_1);
        averageTime = (TextView)findViewById(R.id.title2_2);
        babyAliasView = (TextView)findViewById(R.id.title1_1);
        //String babyName = title.getText().toString();
        String babyName = extras.getString("babyAlias");
        productID = extras.getString("productid");
        serialNumber = extras.getString("serialNumber");
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setIndeterminate(true);

        Log.d("MILA","Your baby name =" + babyName);
        babyAliasView.setText(babyName);
        title.setText(babyName);
        btnDatePicker=(Button)findViewById(R.id.btn_date);

        txtDate=(EditText)findViewById(R.id.in_date);
        //title.setText(babyName);

       // barcrt chart =  findViewById(R.id.chart);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"punch", "RFID", "hug"});
        staticLabelsFormatter.setVerticalLabels(new String[] {"1", "2", "3"});

        /*GraphView graph1 = (GraphView) findViewById(R.id.graph1);
        StaticLabelsFormatter staticLabelsFormatter1 = new StaticLabelsFormatter(graph1);
        staticLabelsFormatter1.setHorizontalLabels(new String[] {"1", "2", "3"});
        staticLabelsFormatter1.setVerticalLabels(new String[] {"hug", "RFID", "punch"});*//*
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
               // new DataPoint(3, 2),
                //new DataPoint(4, 6)
        });
        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[] {
               // new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
               // new DataPoint(4, 6)
        });
        graph.addSeries(series);
        //graph1.addSeries(series1);

// styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });
        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });
        series.setSpacing(50);
        series1.setSpacing(30);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        series1.setDrawValuesOnTop(true);
        series1.setValuesOnTopColor(Color.BLUE);*/
//series.setValuesOnTopSize(50);

    }

  /* private void navigatetoLoginActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Statistic.this, LoginActivity.class));
            }
        });
    }*/

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(Statistic.this,Welcome.class);
       // i.putExtra("userName",userName);
        //i.putExtra("babyAlias",babyAlias);
        startActivity(i);

    }
public void selectDate(View v) {


           // Get Current Date
           final Calendar c = Calendar.getInstance();
           mYear = c.get(Calendar.YEAR);
           mMonth = c.get(Calendar.MONTH);
           mDay = c.get(Calendar.DAY_OF_MONTH);


           DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                   new DatePickerDialog.OnDateSetListener() {

                       @Override
                       public void onDateSet(DatePicker view, int year,
                                             int monthOfYear, int dayOfMonth) {

                           txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                       }
                   }, mYear, mMonth, mDay);
           datePickerDialog.show();
       }
   private void enableProgressDialog(final boolean enable) {
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               if (enable)
                   prgDialog.show();
               else
                   prgDialog.hide();
           }
       });
   }
public void averageTime(View v)

    {
        //enableProgressDialog(true);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        Date d_now = calendar.getTime();
        String formattedDate = df.format(d_now);
      //  Date today = Calendar.getInstance().getTime();
        Log.d("MILA","Current date ="+formattedDate);

        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        String formattedYesterday = df.format(yesterday);
        Log.d("MILA","Yesterday date ="+formattedYesterday);



      /*  RegisterAPI.getInstance(this).getAverageTime(productID, serialNumber,babyID, startTime, stopTime, new RegisterAPI.RegistrationCallback() {
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
                                listName.add(temp);


                            }
                            updateUISpinner(listName);
                        }
                        else
                        {
                            Log.d("Array returned1111 ========", "No BABIES...........");
                            updateUISpinner(listName);
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

*/

    }

    public void navigateBackLogin(View v) {
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);

}
}
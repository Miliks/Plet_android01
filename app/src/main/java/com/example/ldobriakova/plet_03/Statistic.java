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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

import static java.lang.String.valueOf;

public class Statistic extends Activity {
   // Button btnDatePicker;
    private int mYear, mMonth, mDay;
    TextView babyAliasView, title, averageTime, sessions;
    Date parsedStartDate, parsedStopDate, parsedAver;
    String productID, serialNumber, childID, userName, sessionsValue;
    EditText txtDate;
    String startTime, stopTime, totalTime,parsedAverString;
    ProgressDialog prgDialog;
    JSONArray sessionArray = new JSONArray();
    ArrayList<String> valuesForGraphTS = new ArrayList<String>();
    ArrayList<Double> valuesForGraph = new ArrayList<Double>();
    LineGraphSeries<DataPoint> series;
    Double d;
    // private BarChart barcrt;
   SimpleDateFormat dfsf = new SimpleDateFormat("MM-dd");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.statistic_view);
        title = (TextView) findViewById(R.id.title2_1);
        String babyName = extras.getString("babyAlias");
        userName = extras.getString("userName");
        productID = extras.getString("productid");
        serialNumber = extras.getString("serialNumber");
        childID = extras.getString("childID");
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setIndeterminate(true);
        title.setText(babyName);
        playTime();
        averageTimeWeek();
            // barcrt chart =  findViewById(R.id.chart);



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
       i.putExtra("userName",userName);
       i.putExtra("childID",childID);
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
    //averageTime();
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

   public void playTime()
   {
       enableProgressDialog(true);
       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
       Calendar calendar = Calendar.getInstance();
       Date d_now = calendar.getTime();
       String formattedDate = df.format(d_now);
       calendar.add(Calendar.DATE, -1);
       Date yesterday = calendar.getTime();
       calendar.add(Calendar.DATE, 2);
       Date tomorrow = calendar.getTime();
       String formattedYesterday = df.format(yesterday);
       String formattedTomorrow = df.format(tomorrow);

       RegisterAPI.getInstance(this).getAverageTime(productID, serialNumber,childID, formattedYesterday, formattedTomorrow, new RegisterAPI.RegistrationCallback() {
           @Override
           public void onResponse(String str) {
               try {
                   enableProgressDialog(false);
                   JSONObject jsonResponse = new JSONObject(str);
                   String result = jsonResponse.getString("result");
                   JSONObject result_obj = jsonResponse.getJSONObject("content");
                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                       if (result.equals("OK")) {
                       Log.d("MILA","Get average time.......resul OK for child " + childID);
                       sessionArray = result_obj.getJSONArray("SessionList");
                       sessionsValue = valueOf(sessionArray.length()).toString();
                          if(sessionArray.length()>0) {
                           for (int i = 0; i < sessionArray.length(); i++) {
                               JSONObject b = sessionArray.getJSONObject(i);
                               startTime = b.getString("startTime") ;
                               stopTime  = b.getString("endTime");
                               try {
                                   parsedStartDate = dateFormat.parse(startTime);
                                   parsedStopDate = dateFormat.parse(stopTime);

                                   Log.d("MILA","Time elapsed =" + parsedAver);
                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }
                               Long tmp = (parsedStopDate.getTime() - parsedStartDate.getTime())/60000;
                               Log.d("MILA", "PLAYTIME = " + tmp + "rounded = " + Math.round(tmp));
                               parsedAverString = valueOf((parsedStopDate.getTime() - parsedStartDate.getTime())/60000).toString();
                           }
                       }
                       else
                       {
                            parsedAverString = "00";
                            sessionsValue = "0";

                           Log.d("MILA", "No playtime...........");
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(getApplicationContext(), "Your child havn't been playing in this timeframe", Toast.LENGTH_LONG).show();
                               }

                           });
                       }
                       prgDialog.dismiss();
                       averageTime = (TextView)findViewById(R.id.title2_2);
                       averageTime.setText(parsedAverString);
                       averageTime.append(" min");
                       sessions = (TextView) findViewById(R.id.sessionsValue);
                       sessions.setText(sessionsValue);
                       Log.d("MILA","Sessions played OUT...... " + sessionArray.length());

                   } else {

                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(getApplicationContext(), "Something went wrong, no data have been recieved", Toast.LENGTH_LONG).show();
                           }

                       });
                       parsedAverString = "NO DATA";
                       Log.d("MILA", "PLAYTIME = " + parsedAverString);
                       averageTime.setText(parsedAverString);

                   }
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
                       Toast.makeText(getApplicationContext(), "Something went wrong, no data have been recieved", Toast.LENGTH_LONG).show();
                   }

               });
               parsedAverString = "NO DATA";
               Log.d("MILA", "PLAYTIME = " + parsedAverString);
               averageTime.setText(parsedAverString);
           }

           @Override
           public void onNetworkError() {
               enableProgressDialog(false);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(getApplicationContext(), "No connection with server have been detected, please try again later!", Toast.LENGTH_LONG).show();
                   }

               });
               parsedAverString = "NO DATA";
               Log.d("MILA", "PLAYTIME = " + parsedAverString);
               averageTime.setText(parsedAverString);
           }
       });


   }
public void averageTimeWeek()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat dfny = new SimpleDateFormat("MM-dd");

        Calendar calendar = Calendar.getInstance();
        final Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d4 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d5 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d6 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        final Date d7 = calendar.getTime();
        //final String formattedWeekAgo = df.format(d7);
        final String formattedWeekAgo = df.format(d7);
        final String formattedDate = df.format(d1);
       Log.d("MILA", "dddddd"+d1 + d2 + d3 + d4 + d5 + d6 +d7);
//get data for one week
       RegisterAPI.getInstance(this).getAverageTime(productID, serialNumber,childID, formattedWeekAgo, formattedDate, new RegisterAPI.RegistrationCallback() {
            @Override
            public void onResponse(String str) {
                try {
                    enableProgressDialog(false);
                   JSONObject jsonResponse = new JSONObject(str);
                    String result = jsonResponse.getString("result");
                    JSONObject result_obj = jsonResponse.getJSONObject("content");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    if (result.equals("OK")) {
                        sessionArray = result_obj.getJSONArray("SessionList");
                        if(sessionArray.length()>0) {
                            for (int i = 0; i < sessionArray.length(); i++) {
                               JSONObject b = sessionArray.getJSONObject(i);
                               startTime = b.getString("startTime") ;//String
                               stopTime  = b.getString("endTime");
                                try {
                                    parsedStartDate = dateFormat.parse(startTime); //Date
                                    parsedStopDate = dateFormat.parse(stopTime);
                                    String parsedAver = valueOf(parsedStopDate.getTime() - parsedStartDate.getTime()).toString();
                                    Log.d("MILA","Time elapsed in try =" + parsedAver);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                      if(parsedStartDate.after(d7))
                                    {

                                        d = 0.0;
                                        valuesForGraph.add(d);
                                        Log.d("MILA","Start data timestamp = " + parsedStartDate + "d1 = " + dateFormat.format(d1) + "value = "+ valuesForGraph.get(0));
                                    }
                                 else
                                    {
                                        valuesForGraph.add(7.0);

                                    }
                                    if(parsedStartDate.after(d7)&&parsedStartDate.before(d5))
                                    {
                                        //add calculation for the time
                                        valuesForGraph.add(6.0);
                                    }
                                    else
                                        valuesForGraph.add(0.0);
                                if(parsedStartDate.after(d6)&&parsedStartDate.before(d4))
                                {
                                    //add calculation for the time
                                    valuesForGraph.add(5.0);
                                }
                                else
                                    valuesForGraph.add(0.0);
                                if(parsedStartDate.after(d5)&&parsedStartDate.before(d3))
                                {
                                    //add calculation for the time
                                    valuesForGraph.add(4.0);
                                }
                                else
                                    valuesForGraph.add(0.0);
                                if(parsedStartDate.after(d4)&&parsedStartDate.before(d2))
                                {
                                    //add calculation for the time
                                    valuesForGraph.add(3.0);
                                }
                                else
                                    valuesForGraph.add(0.0);
                                if(parsedStartDate.after(d3)&&parsedStartDate.before(d1))
                                {
                                    //add calculation for the time
                                    valuesForGraph.add(2.0);
                                }
                                else
                                    valuesForGraph.add(0.0);
                                if(parsedStartDate.after(d2))
                                {
                                    //add calculation for the time
                                    valuesForGraph.add(1.0);
                                }
                                else
                                    valuesForGraph.add(0.0);
                               // }
                                Log.d("MILA","Start data timestamp = value1 = "+ valuesForGraph.get(0));
                               // valuesForGraph.add(0.0);
                            }
                            Log.d("MILA","Start data timestamp = value2 = "+ valuesForGraph.get(0));
                        }
                        else
                        {
                            for(int i= 0;i<7; i++) {
                                valuesForGraph.add(i,0.0);
                            }
                            Log.d("MILA", "No playtime...........");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Your child havn't been playing in this timeframe", Toast.LENGTH_LONG).show();
                                   }

                            });
                        }


                        prgDialog.dismiss();

                    } else {

                            for(int i= 0;i<7; i++) {
                                valuesForGraph.add(i,0.0);
                            }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Your child havn't been playing in this timeframe", Toast.LENGTH_LONG).show();
                            }

                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // only 7 because week is 7 days
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
        GraphView graph = (GraphView) findViewById(R.id.graph);
        Log.d("MILA", "dddddd"+d7 );
        final LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(1,0),
                new DataPoint(2, 5),
                new DataPoint(3, 2),
                new DataPoint(4,1),
                new DataPoint(5, 0),
                new DataPoint(6, 2),
                new DataPoint(7, 0)
        });
        Log.d("MILA", "dddddd5 =" + d5);
        Log.d("MILA", "dddddd6 =" + d6);
        graph.addSeries(series);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"mon", "tue", "wed","thu","fri","sat","sun"});
        //staticLabelsFormatter.setVerticalLabels(new String[] {"minutes"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        //series.
       // graph.getGridLabelRenderer().setNumHorizontalLabels(4);


      /*graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    Log.d("MILA", "Not formatted =" + value);
                    Log.d("MILA", "formatted =" + dfsf.format(value));
                    return dfsf.format(value);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });*/

       // graph.getGridLabelRenderer().setNumHorizontalLabels(7);

   }


    public void refresh(View view) {
        finish();
        startActivity(getIntent());
    }
}
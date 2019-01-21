package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

import static java.lang.String.valueOf;

public class Group_Statistic extends Activity {
   // Button btnDatePicker;
    private int mYear, mMonth, mDay;
    TextView babyAliasView, title, averageTime, sessions, day_title, week_title;
    Date parsedStartDate, parsedStopDate, parsedAver;
    String productID, serialNumber, childID, userName, sessionsValue, child_token;
    EditText txtDate;
    String startTime, stopTime, totalTime,parsedAverString;
    ProgressDialog prgDialog;
    JSONArray sessionArray = new JSONArray();
    ArrayList<String> valuesForGraphTS = new ArrayList<String>();
    ArrayList<Double> valuesForGraph = new ArrayList<Double>();
    LineGraphSeries<DataPoint> series;
    ImageView single_emoji_happy,single_emoji_sad ;
    Double d;
    // private BarChart barcrt;
   SimpleDateFormat dfsf = new SimpleDateFormat("MM-dd");
   LinearLayout single_emoji, multiple_emoji;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.group_statistic);
        title = (TextView) findViewById(R.id.title2_1);
        day_title = (TextView) findViewById(R.id.title2);
        week_title = (TextView) findViewById(R.id.title_week2);
        String babyName = extras.getString("babyAlias");
        Log.d("MILA", "babyName = " + babyName);
        userName = extras.getString("userName");
        productID = extras.getString("productid");
        serialNumber = extras.getString("serialNumber");
        childID = extras.getString("childID");
        prgDialog = new ProgressDialog(this);
        child_token = extras.getString("token");
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setIndeterminate(true);

        LinearLayout.LayoutParams lp_l = new LinearLayout.LayoutParams(
                (ViewGroup.LayoutParams.WRAP_CONTENT), (ViewGroup.LayoutParams.WRAP_CONTENT));

        single_emoji = (LinearLayout) findViewById(R.id.single_emoji_layout);
        multiple_emoji = (LinearLayout) findViewById(R.id.weekly_emoji_layout);
        single_emoji_happy = (ImageView)findViewById(R.id.emoji);
        single_emoji.setLayoutParams(lp_l);
        multiple_emoji.setLayoutParams(lp_l);

        //LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams((ViewGroup.LayoutParams.WRAP_CONTENT), (ViewGroup.LayoutParams.WRAP_CONTENT));
       // LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams)day_title.getLayoutParams();
       // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)day_title.getLayoutParams();

       // layoutParams.addRule(RelativeLayout.ALIGN_LEFT);
        day_title.setLayoutParams(lp_l);
        week_title.setLayoutParams(lp_l);
        day_title.setEnabled(true);
        day_title.setVisibility(View.VISIBLE);

        week_title.setEnabled(false);
        week_title.setVisibility(View.INVISIBLE);
        single_emoji.setEnabled(true);
        single_emoji_happy.setImageResource(R.drawable.sad);
        //single_emoji_happy.setImageURI(happyUri);
        multiple_emoji.setEnabled(false);
        multiple_emoji.setVisibility(View.INVISIBLE);
        title.setText(babyName);
       // playExperience();
        //averageTimeWeek();
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
        Intent i = new Intent(Group_Statistic.this,SelectChild.class);
       i.putExtra("userName",userName);
       //i.putExtra("childID",childID);
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

   public void child_playExperience()
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
//getGroupInteractionStats?startDate=2018-10-11&endDate=2019-01-15&token=FEAC4E893D
       RegisterAPI.getInstance(this).queryPlayBehaviour(formattedYesterday, formattedTomorrow,child_token, new RegisterAPI.RegistrationCallback() {
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
                       sessionArray = result_obj.getJSONArray("GroupInteractionList");
                       sessionsValue = valueOf(sessionArray.length()).toString();
                           int distant = 0;
                           int interact = 0;
                           int indif = 0;
                           int play = 0;
                           if(sessionArray.length()>0) {
                           for (int i = 0; i < sessionArray.length(); i++) {
                               JSONObject b = sessionArray.getJSONObject(i);
                               startTime = b.getString("Timestamp") ;
                               String actionType = b.getString("ActionType");
                               if(actionType.contains("DISTANT"))
                                   distant= distant++;
                               else if(actionType.contains("INTERACTING"))
                                   interact=interact++;
                               else play = play++;

                               //stopTime  = b.getString("endTime");
                               try {
                                   parsedStartDate = dateFormat.parse(startTime);
                                   //parsedStopDate = dateFormat.parse(stopTime);

                                   Log.d("MILA","Time elapsed =" + parsedAver);
                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }
                               Long tmp = (parsedStopDate.getTime() - parsedStartDate.getTime())/60000;
                               Log.d("MILA", "PLAYTIME = " + tmp + "rounded = " + Math.round(tmp));
                              // parsedAverString = valueOf((parsedStopDate.getTime() - parsedStartDate.getTime())/60000).toString();
                           }
                       }
                       else
                       {
                            indif=indif++;
                       }
                       prgDialog.dismiss();
                      /* averageTime = (TextView)findViewById(R.id.title2_2);
                       averageTime.setText(parsedAverString);
                       averageTime.append(" min");
                       sessions = (TextView) findViewById(R.id.sessionsValue);
                       sessions.setText(sessionsValue);*/
                       Log.d("MILA","Sessions played OUT...... " + sessionArray.length() +"inter= " + interact + " indif =" + indif + " play = " + play + " distant ="  + distant);

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
                new DataPoint(d7,0),
                new DataPoint(d6, 5),
                new DataPoint(d5, 8),
                new DataPoint(d4,1)
//                new DataPoint(d3, 7),
//                new DataPoint(d2, 0),
//                new DataPoint(d1, 7)
        });
        Log.d("MILA", "dddddd5 =" + d5);
        Log.d("MILA", "dddddd6 =" + d6);
        graph.addSeries(series);
        //series.
       // graph.getGridLabelRenderer().setNumHorizontalLabels(4);


      graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
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
        });

       // graph.getGridLabelRenderer().setNumHorizontalLabels(7);

   }


    public void refresh(View view) {
        finish();
        startActivity(getIntent());
    }
//change screen on clicking the week view button
    public void weekView(View view) {
        single_emoji.setEnabled(false);
        single_emoji.setVisibility(View.INVISIBLE);
       // single_emoji_happy.setImageURI(happyUri);
        multiple_emoji.setEnabled(true);
        multiple_emoji.setVisibility(View.VISIBLE);
        day_title.setEnabled(false);
        day_title.setVisibility(View.INVISIBLE);
        week_title.setEnabled(true);
        week_title.setVisibility(View.VISIBLE);
    }
}
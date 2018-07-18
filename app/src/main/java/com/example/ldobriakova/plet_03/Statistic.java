package com.example.ldobriakova.plet_03;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

public class Statistic extends Activity {
    Button btnDatePicker;
    private int mYear, mMonth, mDay;
    EditText txtDate;
    private BarChart barcrt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.statistic_view);
        TextView title = (TextView) findViewById(R.id.title1);
        String babyName = title.getText().toString();
        btnDatePicker=(Button)findViewById(R.id.btn_date);
        txtDate=(EditText)findViewById(R.id.in_date);
        //btnDatePicker.setOnClickListener(this);


        title.setText("Mary" + babyName);

       // barcrt chart =  findViewById(R.id.chart);

        /*GraphView graph = (GraphView) findViewById(R.id.graph);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"punch", "RFID", "hug"});
        staticLabelsFormatter.setVerticalLabels(new String[] {"1", "2", "3"});

        *//*GraphView graph1 = (GraphView) findViewById(R.id.graph1);
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

   /*public void selectDate(View v) {


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
       }*/

    public void navigateBackLogin(View v) {
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);

}
}
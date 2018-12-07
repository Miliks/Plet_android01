package com.example.ldobriakova.plet_03;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StudentAdapter extends ArrayAdapter<Student> {
    public StudentAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public StudentAdapter(Context context, int resource, Student [] items) {
        super(context, resource, items);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.students_listview, null);

        }

        Student p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.listStudents);

            Log.d("MILA", " in updateUISpinner =" + p);
            if (tt1 != null) {
                tt1.setText(p.getSt_alias());
            }

                   }

        return v;
    }

}

package com.example.ldobriakova.plet_03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Child> {
    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, Child [] items) {
        super(context, resource, items);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_listview, null);
        }

        Child p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.child_alias);


            if (tt1 != null) {
                tt1.setText(p.getChild_alias());
            }

                   }

        return v;
    }

}

package com.example.ldobriakova.plet_03;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WrstAdapter extends ArrayAdapter<Wristbnd> {
    public WrstAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public WrstAdapter(Context context, int resource, Wristbnd [] items) {
        super(context, resource, items);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.wrstbnd_listview, null);

        }

        Wristbnd p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.wrstbndList);

            Log.d("MILA", " in updateUISpinner =" + p);
            if (tt1 != null) {
                tt1.setText(p.getWrst_alias() + p.getWrst_mac());

            }

                   }

        return v;
    }

}

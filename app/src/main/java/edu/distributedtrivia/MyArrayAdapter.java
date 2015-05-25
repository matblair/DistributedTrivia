package edu.distributedtrivia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devneetsinghvirdi on 25/05/15.
 */
public class MyArrayAdapter extends ArrayAdapter<String> {
    private final Context context;

    List<String> userNames = new ArrayList<String>();

    public MyArrayAdapter(Context context, List<String> userNames) {
        super(context, R.layout.rowlayout, userNames);
        this.context = context;
        this.userNames = userNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);


        TextView textView = (TextView) rowView.findViewById(R.id.txtUserName);
        textView.setText(userNames.get(position));


        return rowView;
    }
}

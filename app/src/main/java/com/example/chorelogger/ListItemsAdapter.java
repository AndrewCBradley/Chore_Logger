package com.example.chorelogger;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListItemsAdapter extends BaseAdapter {

    private List<String> mChoreNames = new ArrayList<>();
    private List<String> mCompletedByNames = new ArrayList<>();
    private LayoutInflater mInflator;
    private Context mContext;

    public ListItemsAdapter(Context context, List<String> choreNames, List<String> completedByNames) {
        mChoreNames = choreNames;
        mCompletedByNames = completedByNames;
        mContext = context;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }




    @Override
    public int getCount() {
        return mChoreNames.size();
    }

    @Override
    public Object getItem(int position) {
        return mChoreNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflator.inflate(R.layout.chore_list, null);

        TextView choreNameTextView = v.findViewById(R.id.titleTextView);
        TextView completedByTextView = v.findViewById(R.id.completedByTextView);

        String choreName = mChoreNames.get(position);

        String completedByName = mCompletedByNames.get(position);

        if(completedByName.equals("INCOMPLETE")) {
            completedByTextView.setTextColor(mContext.getResources().getColor(R.color.colorRed));
        } else {
            completedByName = "Completed by: " + completedByName;
            completedByTextView.setTextColor(mContext.getResources().getColor(R.color.colorSeaGreen));
        }

        choreNameTextView.setText(choreName);
        completedByTextView.setText(completedByName);

        return v;
    }
}

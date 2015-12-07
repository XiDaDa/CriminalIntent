package com.app.criminalintent;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CrimeAdapter extends ArrayAdapter<Crime> {

	private LayoutInflater mInflater = null;  
	public CrimeAdapter(Context context, int resource, List<Crime> objects) {
		super(context, resource, objects);
		mInflater = (LayoutInflater) context  
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int postion, View convertView, ViewGroup patent){
		if(null == convertView){
			convertView = mInflater.inflate(R.layout.list_item_crime, null);
		}
		Crime c = getItem(postion);
		TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
		titleTextView.setText(c.getTitle());
		TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
		dateTextView.setText(c.getDate().toString());
		CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
		solvedCheckBox.setChecked(c.isSolved());
		return convertView;
	}
}

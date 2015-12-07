package com.app.criminalintent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

public class CriminalIntentJSONSerializer {
	private Context mContext;
	private String mFileName;

	public CriminalIntentJSONSerializer(Context c, String f) {
		mContext = c;
		mFileName = f;
	}

	//保存json数据
	public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException { 
			JSONArray array = new JSONArray();
			for(Crime c : crimes) {
				array.put(c.toJSON());
			} 
			Writer writer = null;
			try {
				OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
				writer = new OutputStreamWriter(out);
				writer.write(array.toString());
			}finally {
				if(writer != null) {
					writer.close();
				}
			}
	}
	
	//读取json数据
	public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
		ArrayList<Crime>  crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			InputStream in = mContext.openFileInput(mFileName);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while(null != (line = reader.readLine())) {
				jsonString.append(line);
			}
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			for(int i = 0; i < array.length(); i++) {
				crimes.add(new Crime(array.getJSONObject(i)));	
			}
		}
		catch (FileNotFoundException e) {}
		finally{
			if(null != reader) {
				reader.close();
			}
		}
		return crimes;
	}
	
}

package com.app.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class CrimeLab {
	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	private static CrimeLab sCrimeLab;
	
	private CriminalIntentJSONSerializer mSerializer;
	private Context mAppContext;
	private ArrayList<Crime> mCrimes;
	
	private CrimeLab(Context appContext){
		mAppContext = appContext;
		mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
		try{
			mCrimes = mSerializer.loadCrimes();
		} catch(Exception e) {
			mCrimes = new ArrayList<Crime>();
			Log.e(TAG, "TTT: " + e);
		}
	}

	public static CrimeLab get(Context c){
		if(sCrimeLab == null){
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;	
	}
	
	public void addCrime(Crime c){
		mCrimes.add(c);
	}
	
	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}


	public void setCrimes(ArrayList<Crime> crimes) {
		mCrimes = crimes;
	}

	public Crime getCrime(UUID id){
		for(Crime c : mCrimes){
			if(c.getId().equals(id))
				return c;
		}
		return null;
	}
	
	public boolean saveCrimes() {
		try{
			mSerializer.saveCrimes(mCrimes);
			Log.e(TAG, "TTTT:" + "save to file");
			return true;
		}
		catch (Exception e) {
			Log.e(TAG, "TTTT:" + e);
			return false;	
		}
	}
	
	public void deleteCrime(Crime c) {
		mCrimes.remove(c);
	}
	
}

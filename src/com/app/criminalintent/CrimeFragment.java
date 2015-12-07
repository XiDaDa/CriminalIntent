package com.app.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class CrimeFragment extends Fragment {
	private static final String TAG = "CrimeFragment";
	public static final String EXTRA_CRIME_ID = "com.app.criminalintent.crime_id";
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_IMAGE = "image";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_PHOTO = 1;
	private static final int REQUEST_CONTACT = 2;
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private Button mSuspectButton;
	private ImageButton mImageButton;
	private CheckBox mSolvedCheckBox;
	private ImageView mPhotoView;
	
	public static CrimeFragment newInstance(UUID crimeId){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;	
	}
	
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mCrime = new Crime();
		UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		Log.d("TTT", "TTT" + " $$ "+NavUtils.getParentActivityName(getActivity()));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(null != NavUtils.getParentActivityName(getActivity())) {				
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				mCrime.setTitle(arg0.toString());
			}
			
		});
		mDateButton = (Button) v.findViewById(R.id.crime_date);
		mDateButton.setText(mCrime.getDate().toString());
		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FragmentManager fm = getActivity().getSupportFragmentManager();
//				DatePickerFragment dialog = new DatePickerFragment();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm,DIALOG_DATE);
			}
		});
		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				mCrime.setSolved(arg1);
			}
			
		});
		mImageButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
		PackageManager pm = getActivity().getPackageManager();
		boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) 
				|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) 
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
				|| Camera.getNumberOfCameras() > 0;
		if(!hasCamera) {
			mImageButton.setEnabled(false);
		}
		mImageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
//				startActivity(i);
			}
		});
		mPhotoView = (ImageView) v.findViewById(R.id.crime_imageview);
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					// TODO Auto-generated method stub
					Photo p = mCrime.getPhoto();
					if(p == null) {
						return;
					}
					FragmentManager fm = getActivity().getSupportFragmentManager();
					String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
					ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
				}
		});
		Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
		reportButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
			}
		});
		mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACT);			
			}
		});
		if(mCrime.getSuspect() != null ) {
			mSuspectButton.setText(mCrime.getSuspect());
		}
		return v;
	}
	
	private void showPhoto() {
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if(p != null) {
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaleDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}
	
	private String getCrimeReport() {
		String solvedString = null;
		if(mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		}else {
			solvedString = getString(R.string.crime_report_unsolved);
		}
		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
		
		String suspect = mCrime.getSuspect();
		if(suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		}else {
			suspect = getString(R.string.crime_report_suspect, suspect);
		}
		String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
		
		return report;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			if(null != NavUtils.getParentActivityName(getActivity())) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		if(requestCode == REQUEST_DATE){
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			mDateButton.setText(mCrime.getDate().toString());
		}else if(requestCode == REQUEST_PHOTO) {
			String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if(filename != null) {
				Photo p = new Photo(filename);
				mCrime.setPhoto(p);
				Log.i(TAG,"Filename: " + filename);
				showPhoto();
			}
		}else if(requestCode == REQUEST_CONTACT) {
			Uri contactUri = data.getData();
			String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
			Cursor c = getActivity().getContentResolver().query(contactUri,queryFields, null, null, null);
			if(c.getCount() == 0) {
				c.close();
				return;
			}
			c.moveToFirst();
			String suspect = c.getString(0);
			mCrime.setSuspect(suspect);
			mSuspectButton.setText(suspect);
			c.close();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	} 
	
	@Override
	public void onPause() {
			super.onPause();
			CrimeLab.get(getActivity()).saveCrimes();
	}
	
	@Override 
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	
}

package com.app.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {
	public static final String TAG = "CrimeCameraFragment";
	public static final String EXTRA_PHOTO_FILENAME = "com.app.criminalintent.photo_filename"; 
	private Camera mCamera;
	private View mProgressContainer;
	private SurfaceView mSurfaceView;
	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};
	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			String filename = UUID.randomUUID().toString() + ".jpg";
			FileOutputStream os = null;
			boolean success = true;
			try{
				os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				os.write(data);
			} catch (Exception e){
				Log.e(TAG, "Error writing to file " + filename, e);
				success = false;
			} finally {
				try{
					if(os != null) {
						os.close();
					}
					} catch (Exception e) {
						Log.e(TAG, "Error writing to file " + filename, e);
						success = false;
					}
			}
			if(success){				
				if(success) {
					Intent i = new Intent();
					i.putExtra(EXTRA_PHOTO_FILENAME, filename);
					getActivity().setResult(Activity.RESULT_OK,i);
//				Log.i(TAG, "JPEG saved at " + filename);
				}else {
					getActivity().setResult(Activity.RESULT_CANCELED);
				}
			}
			getActivity().finish();
			Log.d(TAG, "TTT" + success);
		}
	};  
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
		mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
		mProgressContainer.setVisibility(View.INVISIBLE);
		Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mCamera != null) {
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}
			}
		});
		mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				try{
					if(mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch(IOException exception) {
					Log.e(TAG, "Error setting up preview display", exception);
				}
				
			}
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				if(mCamera != null) {
					mCamera.stopPreview();
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				// TODO Auto-generated method stub
				if(mCamera == null) {
					return;
				}	
				Camera.Parameters parameters = mCamera.getParameters();
//				Size  s = null;
				Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
//				Log.d(TAG, "TTT:" + s.height + s.width);
				parameters.setPreviewSize(s.width, s.height);
				s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
				parameters.setPictureSize(s.width, s.height);
				mCamera.setParameters(parameters);
				try{
					mCamera.startPreview();
				}catch(Exception e) {
					mCamera.release();
					mCamera = null;
				} 
			}
			
		});
		return v;
	}

	@TargetApi(9)
	@Override
	public void onResume() {
		super.onResume();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		}else{
			mCamera = Camera.open();	
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	//找出设备支持的最佳支持拍照预览尺寸
	private Size getBestSupportedSize(List<Size> size, int width, int height) {
		
		Size  bestSize = size.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for(Size s : size) {
			int area = s.height * s.width;
			if(area > largestArea) {				
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}
}

package com.voyagegames.janus;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.View;

public class TakePictureListener implements View.OnClickListener, ILogger {
	
	private static final String TAG = "TakePictureListener";
	
	private final Camera mCamera;
	private final PictureCallback mCallback;
	
	public TakePictureListener(final Camera camera, final PictureCallback callback) {
		mCamera = camera;
		mCallback = callback;
	}
	
    @Override
    public void onClick(final View view) {
    	try {
    		mCamera.takePicture(null, null, mCallback);
    	} catch (final Exception e) {
    		log(TAG, "Exception thrown in TakePictureListener.onClick()", e);
    	}
    }

	@Override
	public void log(final String tag, final String msg) {
		Log.e(TAG, msg);
	}

	@Override
	public void log(final String tag, final String msg, final Exception e) {
		Log.e(TAG, msg, e);
	}
}

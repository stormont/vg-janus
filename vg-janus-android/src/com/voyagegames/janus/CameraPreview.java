package com.voyagegames.janus;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final String TAG = "CameraPreview";
	
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Activity mContext;

	public CameraPreview(Activity context, Camera camera) {
        super(context);
        mContext = context;
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		RelativeLayout preview = (RelativeLayout) mContext.findViewById(R.id.camera_layout);
        View guide = (View) mContext.findViewById(R.id.camera_guide);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(3, preview.getHeight());
        relativeParams.setMargins(preview.getWidth() / 2 - 1, 0, 0, 0);
        guide.setLayoutParams(relativeParams);
        
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        mCamera.setDisplayOrientation(90);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}

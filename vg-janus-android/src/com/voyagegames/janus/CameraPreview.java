package com.voyagegames.janus;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, ILogger {
	
	private static final String TAG = "CameraPreview";
	
	public Runnable onSurfaceChanged;
    private Camera mCamera;
	
    private final SurfaceHolder mHolder;

	public CameraPreview(final Context context) {
		super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
	}

	public CameraPreview(final Context context, final Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }
	
	public void setCamera(final Camera camera) {
		mCamera = camera;
	}
	
	public void stopPreview() {
		if (mCamera == null) return;
		
		try {
            mCamera.stopPreview();
        } catch (final Exception e) {
            // ignore: tried to stop a non-existent preview
        }
	}

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
    	if (mCamera == null) return;
    	
        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            log(TAG, "Error setting camera preview in CameraPreview.surfaceCreated()", e);
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        // no-op. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int w, final int h) {
    	if (mCamera == null) return;
    	
    	if (onSurfaceChanged != null) onSurfaceChanged.run();
        
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            log(TAG, "Preview surface does not exist in CameraPreview.surfaceChanged()");
            return;
        }

        // stop preview before making changes
        stopPreview();

        // set preview size and make any resize, rotate or
        // reformatting changes here
        mCamera.setDisplayOrientation(90);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (final Exception e){
            log(TAG, "Error starting camera preview in CameraPreview.surfaceChanged()", e);
        }
    }

	@Override
	public void log(final String tag, final String msg) {
		ApplicationLogger.log(tag, msg);
	}

	@Override
	public void log(final String tag, final String msg, final Exception e) {
		ApplicationLogger.log(tag, msg, e);
	}
	
}

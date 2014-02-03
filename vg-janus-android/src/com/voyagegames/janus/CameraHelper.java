package com.voyagegames.janus;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

public class CameraHelper {
	
	private static final String TAG = "CameraHelper";

	public static boolean deviceHasCamera(final PackageManager packageManager) {
	    if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
	        return true;
	    }

	    return false;
	}
	
	public static Camera getCameraInstance() {
	    try {
	        return Camera.open();
	    } catch (Exception e) {
	    	ApplicationLogger.log(TAG, "Exception in CameraActivity.getCameraInstance()", e);
	    }
	    
	    return null;
	}
	
	public static Camera getCameraInstance(final int id) {
	    try {
	        return Camera.open(id);
	    } catch (Exception e) {
	    	ApplicationLogger.log(TAG, "Exception in CameraActivity.getCameraInstance()", e);
	    }
	    
	    return null;
	}
	
	public static void releaseCamera(Camera camera) {
		if (camera == null) return;
		camera.release();
		camera = null;
	}
	
	public static int firstFrontFacingCameraId() {
		return firstArgFacingCameraId(CameraInfo.CAMERA_FACING_FRONT);
	}
	
	public static int firstBackFacingCameraId() {
		return firstArgFacingCameraId(CameraInfo.CAMERA_FACING_BACK);
	}
	
	private static int firstArgFacingCameraId(final int cameraFacing) {
	    final int numberOfCameras = Camera.getNumberOfCameras();
	    
	    int cameraId = 0;

	    for (int i = 0; i < numberOfCameras; i++) {
	    	final CameraInfo info = new CameraInfo();
	    	Camera.getCameraInfo(i, info);
	    	
	    	if (info.facing == cameraFacing) {
	    		return i;
	    	}
	    }
	    
	    return cameraId;
	}
	
}

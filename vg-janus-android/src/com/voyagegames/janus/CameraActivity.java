package com.voyagegames.janus;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CameraActivity extends Activity {
	
	private static final String TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (!checkCameraHardware(this)) return;
        
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    mCamera.takePicture(null, null, mPicture);
                }
            }
        );
    }

	@Override
	protected void onPause() {
		super.onPause();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public static Camera getCameraInstance() {
	    Camera c = null;
	    try {
	        c = Camera.open(findFrontFacingCamera()); // attempt to get a Camera instance
	    }
	    catch (Exception e) {
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private static int findFrontFacingCamera() {
	    int cameraId = -1;
	    // Search for the front facing camera
	    int numberOfCameras = Camera.getNumberOfCameras();
	    for (int i = 0; i < numberOfCameras; i++) {
	      CameraInfo info = new CameraInfo();
	      Camera.getCameraInfo(i, info);
	      if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	        Log.d(TAG, "Camera found");
	        cameraId = i;
	        break;
	      }
	    }
	    return cameraId;
	  }
	
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
	    	
	    	if (bitmap == null) {
	    		Log.e(TAG, "Bitmap was null");
	    		camera.startPreview();
	    		return;
	    	}

	    	Matrix matrix = new Matrix();
	    	matrix.postRotate(-90);
	    	bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	    	
	    	int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
	    	int[] pixels1 = new int[pixels.length];
	    	int[] pixels2 = new int[pixels.length];
	    	bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
	    	int w = bitmap.getWidth();
	    	for (int i=0; i<bitmap.getHeight(); i++) {
	    	    for (int j = 0; j < w / 2; j++) {
	    	    	pixels1[(i * w) + j] = pixels[(i * w) + j];
	    	    }
	    	    for (int j = w / 2; j < w; j++) {
	    	    	pixels1[(i * w) + j] = pixels[(i * w) + (w - j - 1)];
	    	    }
	    	}
	    	for (int i=0; i<bitmap.getHeight(); i++) {
	    	    for (int j = 0; j < w / 2; j++) {
	    	    	pixels2[(i * w) + j] = pixels[(i * w) + (w - j - 1)];
	    	    }
	    	    for (int j = w / 2; j < w; j++) {
	    	    	pixels2[(i * w) + j] = pixels[(i * w) + j];
	    	    }
	    	}
	    	Bitmap bitmap1 = Bitmap.createBitmap(pixels1, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	    	Bitmap bitmap2 = Bitmap.createBitmap(pixels2, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	    	
	    	RelativeLayout preview = (RelativeLayout) findViewById(R.id.camera_layout);
	        preview.setVisibility(View.GONE);
	        
	        ImageView image1 = (ImageView) findViewById(R.id.image_result_right);
	        image1.setVisibility(View.VISIBLE);
	        image1.setImageBitmap(bitmap1);
	        
	        ImageView image2 = (ImageView) findViewById(R.id.image_result_left);
	        image2.setVisibility(View.VISIBLE);
	        image2.setImageBitmap(bitmap2);
	    }
	};

}

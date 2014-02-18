package com.voyagegames.janus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CameraActivity extends Activity implements ILogger {
	
	private static final String TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallbackHelper mPictureCallback;
	private boolean mFrontCamera = true;
	private boolean mLeftImage = true;
	
	private OnClickListener resetCaptureListener = new OnClickListener() {

		@Override
		public void onClick(final View view) {
			prepareView(false);
			initCamera();
		}

	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initCamera();
    }

	@Override
	protected void onPause() {
		super.onPause();
		
		try {
			CameraHelper.releaseCamera(mCamera);
        } catch (final Exception e) {
        	log(TAG, "Exception in CameraActivity.onPause()", e);
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
	
	private void initCamera() {
		try {
        	setupCamera();
        	setupPictureCallback();
        	setupPreview();
        } catch (final Exception e) {
        	log(TAG, "Exception in CameraActivity.initCamera()", e);
        }
	}
	
	private void setupCamera() {
        if (!CameraHelper.deviceHasCamera(getPackageManager())) return;
		if (mCamera != null) mCamera.release();
        if (mFrontCamera) mCamera = CameraHelper.getCameraInstance(CameraHelper.firstFrontFacingCameraId());
		else mCamera = CameraHelper.getCameraInstance(CameraHelper.firstBackFacingCameraId());
		
		Camera.Parameters params = mCamera.getParameters();
		if (params.getMaxNumDetectedFaces() < 1) return;
		mCamera.setFaceDetectionListener(new MyFaceDetectionListener());
		mCamera.startFaceDetection();
	}
	
	private void onSurfaceChanged() {
		final RelativeLayout preview = (RelativeLayout)findViewById(R.id.camera_layout);
		final View guide = findViewById(R.id.camera_guide);
		final RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(3, preview.getHeight());
        relativeParams.setMargins(preview.getWidth() / 2 - 1, 0, 0, 0);
        guide.setLayoutParams(relativeParams);
	}
	
	private void setupPictureCallback() {
        mPictureCallback = new PictureCallbackHelper();
        mPictureCallback.onComplete = new GenericRunnable<Bitmap>() {

			@Override
			public void run(final Bitmap input) {
				pictureCallbackComplete(input);
			}
        	
        };
	}
	
	private void setupPreview() {
		if (mCamera == null) return;
		
        mPreview = new CameraPreview(this, mCamera);
        mPreview.onSurfaceChanged = new Runnable() {

			@Override
			public void run() {
				onSurfaceChanged();
			}
        	
        };
        
        final FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
		preview.removeAllViews();
        preview.addView(mPreview);
        
        final Button captureButton = (Button)findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new TakePictureListener(mCamera, mPictureCallback));
		
		final Button switchButton = (Button)findViewById(R.id.button_switch);
		switchButton.setVisibility(View.VISIBLE);
		switchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View view) {
				mFrontCamera = !mFrontCamera;
				initCamera();
			}
			
		});
	}
	
	private void pictureCallbackComplete(final Bitmap bitmap) {
		if (bitmap == null) {
			log(TAG, "Bitmap result was null in CameraActivity.pictureCallbackComplete()");
			return;
		}
		
		final JanusBitmaps janusBitmaps = new JanusBitmaps(bitmap);
		prepareView(true);
        
        final ImageView imageResult = (ImageView)findViewById(R.id.image_result);
        imageResult.setImageBitmap(janusBitmaps.bitmapLeft);
		
		final Button captureButton = (Button)findViewById(R.id.button_capture);
		captureButton.setOnClickListener(resetCaptureListener);
        
        final Button flip = (Button)findViewById(R.id.button_flip);
        flip.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(final View view) {
				if (mLeftImage) imageResult.setImageBitmap(janusBitmaps.bitmapRight);
				else imageResult.setImageBitmap(janusBitmaps.bitmapLeft);
				mLeftImage = !mLeftImage;
			}
			
        });
	}
	
	private void prepareView(final boolean showResult) {
		final RelativeLayout preview = (RelativeLayout)findViewById(R.id.camera_layout);
		final Button switchButton = (Button)findViewById(R.id.button_switch);
		final LinearLayout result = (LinearLayout)findViewById(R.id.layout_result);
		
		if (showResult) {
			mPreview.stopPreview();
			preview.setVisibility(View.GONE);
			switchButton.setVisibility(View.GONE);
			result.setVisibility(View.VISIBLE);
		} else {
			preview.setVisibility(View.VISIBLE);
			switchButton.setVisibility(View.VISIBLE);
			result.setVisibility(View.GONE);
		}
	}
	
	class MyFaceDetectionListener implements Camera.FaceDetectionListener {

		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			if (faces.length < 1) return;
		}
	}

}

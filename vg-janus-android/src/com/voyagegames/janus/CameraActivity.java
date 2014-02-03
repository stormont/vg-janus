package com.voyagegames.janus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        try {
        	setupCamera();
        	setupPictureCallback();
        	setupPreview();
        } catch (final Exception e) {
        	log(TAG, "Exception in CameraActivity.onCreate()", e);
        }
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
	
	private void setupCamera() {
        if (!CameraHelper.deviceHasCamera(getPackageManager())) return;
        mCamera = CameraHelper.getCameraInstance(CameraHelper.firstFrontFacingCameraId());
	}
	
	private void onSurfaceChanged() {
		final RelativeLayout preview = (RelativeLayout)findViewById(R.id.camera_layout);
		final View guide = (View)findViewById(R.id.camera_guide);
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
        preview.addView(mPreview);
        
        final Button captureButton = (Button)findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new TakePictureListener(mCamera, mPictureCallback));
	}
	
	private void pictureCallbackComplete(final Bitmap bitmap) {
		if (bitmap == null) {
			log(TAG, "Bitmap result was null in CameraActivity.pictureCallbackComplete()");
			return;
		}
		
		final JanusBitmaps janusBitmaps = new JanusBitmaps(bitmap);
    	
    	final RelativeLayout preview = (RelativeLayout)findViewById(R.id.camera_layout);
        preview.setVisibility(View.GONE);
        
        final LinearLayout result = (LinearLayout)findViewById(R.id.layout_result);
        result.setVisibility(View.VISIBLE);
        
        final ImageView imageLeft = (ImageView)findViewById(R.id.image_result_left);
        imageLeft.setImageBitmap(janusBitmaps.bitmapLeft);
        
        final ImageView imageRight = (ImageView)findViewById(R.id.image_result_right);
        imageRight.setImageBitmap(janusBitmaps.bitmapRight);
        imageRight.setVisibility(View.GONE);
        
        final Button flip = (Button)findViewById(R.id.button_flip);
        flip.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(final View view) {
				flipJanusViews(imageLeft, imageRight);
			}
			
        });
	}

	@Override
	public void log(final String tag, final String msg) {
		ApplicationLogger.log(tag, msg);
	}

	@Override
	public void log(final String tag, final String msg, final Exception e) {
		ApplicationLogger.log(tag, msg, e);
	}
	
	private void flipJanusViews(final View imageLeft, final View imageRight) {
		if (imageRight.getVisibility() == View.GONE) {
			imageLeft.setVisibility(View.GONE);
			imageRight.setVisibility(View.VISIBLE);
		} else {
			imageLeft.setVisibility(View.VISIBLE);
			imageRight.setVisibility(View.GONE);
		}
	}

}

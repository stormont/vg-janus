package com.voyagegames.janus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.voyagegames.janus.bitmaptransformers.AlphaBlendBitmapTransformer;
import com.voyagegames.janus.bitmaptransformers.DualBitmapTransformer;
import com.voyagegames.janus.bitmaptransformers.FaceDetectBitmapTransformer;
import com.voyagegames.janus.bitmaptransformers.JanusBitmapTransformer;
import com.voyagegames.janus.bitmaptransformers.MonkeyBitmapTransformer;

public class CameraActivity extends Activity implements ILogger {
	
	private static final String TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallbackHelper mPictureCallback;
	private int mCurImageIndex = 0;
	
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
        mCamera = CameraHelper.getCameraInstance(CameraHelper.firstFrontFacingCameraId());
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
				final ITransformer<Bitmap, Bitmap> faceTransform = new FaceDetectBitmapTransformer();
				final Bitmap face = faceTransform.transform(input);
				
				/*
				final ITransformer<Bitmap, Bitmap> monkeyTransform = new MonkeyBitmapTransformer(CameraActivity.this);
				final Bitmap monkey = monkeyTransform.transform(face);

				final ITransformer<Bitmap[], Bitmap> blendTransform = new AlphaBlendBitmapTransformer();
				final Bitmap blend = blendTransform.transform(new Bitmap[] { face, monkey });
				*/
				
				pictureCallbackComplete(face);
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
	}
	
	private boolean mShowMonkey = false;
	private boolean mShowJanus = false;
	private Bitmap[] mBitmaps;
	
	private void pictureCallbackComplete(final Bitmap origBitmap) {
		if (origBitmap == null) {
			log(TAG, "Bitmap result was null in CameraActivity.pictureCallbackComplete()");
			return;
		}
		
		final ITransformer<Bitmap, Bitmap[]> dualTransform = new DualBitmapTransformer();
		mBitmaps = dualTransform.transform(origBitmap);
		prepareView(true);
        
        final ImageView imageResult = (ImageView)findViewById(R.id.image_result);
        imageResult.setImageBitmap(mBitmaps[0]);
		
		final Button captureButton = (Button)findViewById(R.id.button_capture);
		captureButton.setOnClickListener(resetCaptureListener);
        
        final Button flip = (Button)findViewById(R.id.button_flip);
        flip.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(final View view) {
				mCurImageIndex = (mCurImageIndex + 1) % mBitmaps.length;
				imageResult.setImageBitmap(mBitmaps[mCurImageIndex]);
			}
			
        });
        
        final Button monkey = (Button)findViewById(R.id.button_monkey);
        monkey.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(final View view) {
				mShowMonkey = !mShowMonkey;
				if (!mShowMonkey) monkey.setText("Monkey");
				else monkey.setText("No Monkey");
				process(origBitmap, imageResult);
			}
        	
        });
        
        final Button janus = (Button)findViewById(R.id.button_janus);
        janus.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(final View view) {
				mShowJanus = !mShowJanus;
				if (!mShowJanus) janus.setText("Janus");
				else janus.setText("No Janus");
				process(origBitmap, imageResult);
			}
        	
        });
        
        final Button save = (Button)findViewById(R.id.button_save);
        save.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(final View view) {
				saveImage();
			}
        	
        });
	}
	
	private void process(final Bitmap origBitmap, final ImageView imageResult) {
		final ITransformer<Bitmap, Bitmap[]> janus = mShowJanus ? new JanusBitmapTransformer() : new DualBitmapTransformer();
		
		if (!mShowMonkey) {
			mBitmaps = janus.transform(origBitmap);
			imageResult.setImageBitmap(mBitmaps[mCurImageIndex]);
			return;
		}
		
		final ITransformer<Bitmap, Bitmap> monkeyTransform = new MonkeyBitmapTransformer(CameraActivity.this);
		final Bitmap monkeyBitmap = monkeyTransform.transform(origBitmap);

		final ITransformer<Bitmap[], Bitmap> blendTransform = new AlphaBlendBitmapTransformer();
		final Bitmap blend = blendTransform.transform(new Bitmap[] { origBitmap, monkeyBitmap });
		
		mBitmaps = janus.transform(blend);
		imageResult.setImageBitmap(mBitmaps[mCurImageIndex]);
	}
	
	private void saveImage() {
		final long time = System.currentTimeMillis();
		MediaStore.Images.Media.insertImage(
			getContentResolver(),
			mBitmaps[mCurImageIndex],
		    "JANUS_" + time + ".jpg",
		    "JANUS_" + time + ".jpg");
	}
	
	private void prepareView(final boolean showResult) {
		final RelativeLayout preview = (RelativeLayout)findViewById(R.id.camera_layout);
		final LinearLayout result = (LinearLayout)findViewById(R.id.layout_result);
		
		if (showResult) {
			mPreview.stopPreview();
			preview.setVisibility(View.GONE);
			result.setVisibility(View.VISIBLE);
		} else {
			preview.setVisibility(View.VISIBLE);
			result.setVisibility(View.GONE);
		}
	}

}

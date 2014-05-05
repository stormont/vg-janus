package com.voyagegames.janus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class PictureCallbackHelper implements PictureCallback {
	
	public GenericRunnable<Bitmap> onComplete;

    @Override
    public void onPictureTaken(final byte[] data, final Camera camera) {
    	if (onComplete == null) return;

    	final Options opts = new Options();
    	opts.inPreferredConfig = Bitmap.Config.RGB_565;
    	final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
    	
    	if (bitmap == null) {
    		onComplete.run(null);
    		return;
    	}
    	
    	final Matrix matrix = new Matrix();
    	matrix.postRotate(-90);
    	
    	final Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    	onComplete.run(result);
    }
    
}

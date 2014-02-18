package com.voyagegames.janus;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class JanusBitmaps {
	
	public final Bitmap bitmapLeft;
	public final Bitmap bitmapRight;
	
	public JanusBitmaps(final Bitmap bitmap) {
		if (bitmap == null) {
			bitmapLeft = null;
			bitmapRight = null;
			return;
		}
		
    	final int[] pixels = new int[bitmap.getHeight() * bitmap.getWidth()];
    	final int w = bitmap.getWidth();
    	final int halfW = w / 2;
    	
    	bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    	
    	for (int i = 0; i < bitmap.getHeight(); i++) {
    	    for (int j = 0; j < halfW; j++) {
    	    	pixels[(i * w) + j] = pixels[(i * w) + (w - j - 1)];
    	    }
    	    
    	    for (int j = halfW; j < w; j++) {
    	    	pixels[(i * w) + j] = pixels[(i * w) + j];
    	    }
    	}
		
		bitmapLeft = Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    	
    	for (int i = 0; i < bitmap.getHeight(); i++) {
    	    for (int j = 0; j < halfW; j++) {
    	    	pixels[(i * w) + j] = pixels[(i * w) + j];
    	    }
    	    
    	    for (int j = halfW; j < w; j++) {
    	    	pixels[(i * w) + j] = pixels[(i * w) + (w - j - 1)];
    	    }
    	}
    	
    	bitmapRight = Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	}

}

package com.voyagegames.janus.bitmaptransformers;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.voyagegames.janus.ITransformer;

public class JanusBitmapTransformer implements ITransformer<Bitmap, Bitmap[]> {
	
	@Override
	public Bitmap[] transform(final Bitmap bitmap) {
		final Bitmap[] bitmaps = new Bitmap[2];

		if (bitmap == null) {
			bitmaps[0] = null;
			bitmaps[1] = null;
			return bitmaps;
		}
		
    	final int[] pixels = new int[bitmap.getHeight() * bitmap.getWidth()];
    	final int w = bitmap.getWidth();
    	final int halfW = w / 2;
    	
    	// Get the 'left face' bitmap
    	bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    	
    	for (int i = 0; i < bitmap.getHeight(); i++) {
    	    for (int j = 0; j < halfW; j++) {
    	    	pixels[(i * w) + j] = pixels[(i * w) + (w - j - 1)];
    	    }
    	    
    	    for (int j = halfW; j < w; j++) {
    	    	pixels[(i * w) + j] = pixels[(i * w) + j];
    	    }
    	}
		
    	bitmaps[0] = Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
    	
    	// Get the 'right face' bitmap
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    	
    	for (int i = 0; i < bitmap.getHeight(); i++) {
    	    for (int j = 0; j < halfW; j++) {
    	    	pixels[(i * w) + j] = pixels[(i * w) + j];
    	    }
    	    
    	    for (int j = halfW; j < w; j++) {
    	    	pixels[(i * w) + j] = pixels[(i * w) + (w - j - 1)];
    	    }
    	}
    	
    	bitmaps[1] = Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
    	return bitmaps;
	}

}

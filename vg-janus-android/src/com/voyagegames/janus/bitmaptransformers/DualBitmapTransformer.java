package com.voyagegames.janus.bitmaptransformers;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.voyagegames.janus.ITransformer;

public class DualBitmapTransformer implements ITransformer<Bitmap, Bitmap[]> {
	
	@Override
	public Bitmap[] transform(final Bitmap bitmap) {
		final Bitmap[] bitmaps = new Bitmap[2];

		if (bitmap == null) {
			bitmaps[0] = null;
			bitmaps[1] = null;
			return bitmaps;
		}
		
		bitmaps[0] = bitmap.copy(Config.ARGB_8888, true);
		bitmaps[1] = bitmap.copy(Config.ARGB_8888, true);
		return bitmaps;
	}

}

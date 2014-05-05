package com.voyagegames.janus.bitmaptransformers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.voyagegames.janus.ITransformer;
import com.voyagegames.janus.R;

public class MonkeyBitmapTransformer implements ITransformer<Bitmap, Bitmap> {
	
	final Context mContext;
	
	public MonkeyBitmapTransformer(final Context context) {
		mContext = context;
	}
    
    public Bitmap transform(final Bitmap bitmap) {
    	final int size = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
    	final Bitmap monkey = Bitmap.createScaledBitmap(
    			BitmapFactory.decodeResource(mContext.getResources(), R.drawable.monkey),
    			size, size, false);
    	return monkey;
    }
    
}

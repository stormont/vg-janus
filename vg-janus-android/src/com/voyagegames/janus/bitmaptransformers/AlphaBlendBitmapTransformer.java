package com.voyagegames.janus.bitmaptransformers;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;

import com.voyagegames.janus.ITransformer;

public class AlphaBlendBitmapTransformer implements ITransformer<Bitmap[], Bitmap> {
    
    public Bitmap transform(final Bitmap[] bitmaps) {
    	if (bitmaps == null || bitmaps.length == 0) return null;

    	Bitmap result = bitmaps[0].copy(Config.ARGB_8888, true);
    	
    	for (int i = 1; i < bitmaps.length; i++) {
    		result = blend(result, bitmaps[i].copy(Config.ARGB_8888, true));
    	}

    	return result;
    }
    
    private Bitmap blend(final Bitmap base, final Bitmap blend) {
    	final Bitmap result = base.copy(Config.ARGB_8888, true);
    	
    	final Paint p = new Paint();
    	p.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
    	p.setShader(new BitmapShader(blend, TileMode.CLAMP, TileMode.CLAMP));

    	final Canvas c = new Canvas();
    	c.setBitmap(result);
    	c.drawBitmap(base, 0, 0, null);
    	c.drawRect(0, 0, base.getWidth(), base.getHeight(), p);
    	
    	return result;
    }
    
}

package com.voyagegames.janus.bitmaptransformers;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

import com.voyagegames.janus.ITransformer;

public class FaceDetectBitmapTransformer implements ITransformer<Bitmap, Bitmap> {
    
    public Bitmap transform(final Bitmap bitmap) {
    	final Face[] faces = new FaceDetector.Face[1];
    	final FaceDetector faceDetector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 1);
    	final int facesDetected = faceDetector.findFaces(bitmap, faces);
    	
    	if (facesDetected == 0 || faces[0].confidence() < Face.CONFIDENCE_THRESHOLD) return bitmap;
    	
    	final float eyeDist = faces[0].eyesDistance();
    	final PointF midpoint = new PointF();
        faces[0].getMidPoint(midpoint);
        
        // Resize bitmap to be centered on detected face
        final float distX = eyeDist * 1.5f;  // Heads are oval, not circular, so different scaling
        final float distY = eyeDist * 2f;
        final float offsetY = eyeDist * 0.5f;  // Eyes aren't centered in the head, so offset Y-axis
        final int startX = (int)Math.max(0, Math.floor(midpoint.x - distX));
        final int startY = (int)Math.max(0, Math.floor(midpoint.y + offsetY - distY));
        final int endX = (int)Math.min(bitmap.getWidth(), Math.floor(midpoint.x + distX));
        final int endY = (int)Math.min(bitmap.getHeight(), Math.floor(midpoint.y + offsetY + distY));

    	final Bitmap result = Bitmap.createBitmap(bitmap, startX, startY, endX - startX, endY - startY);
    	return result;
    }
    
}

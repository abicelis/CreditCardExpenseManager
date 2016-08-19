package ve.com.abicelis.creditcardexpensemanager.app.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alex on 19/8/2016.
 */
public class ImageUtils {

    /**
     * Return a rounded bitmap version of the image contained in the drawable resource
     *
     * @param res        the context resources
     * @param drawableId the id of the drawable resource
     * @return the RoundedBitmapDrawable with the rounded bitmap
     */
    public static Bitmap getBitmap(byte[] imgInBytes) {
            return BitmapFactory.decodeByteArray(imgInBytes, 0, imgInBytes.length);
    }


    /**
     * Return a rounded bitmap version of the image contained in the drawable resource
     *
     * @param res        the context resources
     * @param drawableId the id of the drawable resource
     * @return the RoundedBitmapDrawable with the rounded bitmap
     */
    public static RoundedBitmapDrawable getRoundedBitmap(Resources res, int drawableId) {
        try {
            Bitmap srcBitmap = BitmapFactory.decodeResource(res, drawableId);
            return getRoundedBitmap(res, srcBitmap);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return a rounded bitmap version of the given bitmap
     *
     * @param res       the context resources
     * @param srcBitmap the bitmap with the image
     * @return the RoundedBitmapDrawable with the rounded bitmap
     */
    public static RoundedBitmapDrawable getRoundedBitmap(Resources res, Bitmap srcBitmap) {
        RoundedBitmapDrawable bitmapDrawable = RoundedBitmapDrawableFactory.create(res, srcBitmap);

        int radius = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());
        bitmapDrawable.setCornerRadius(radius);
        bitmapDrawable.setAntiAlias(true);

        return bitmapDrawable;
    }

    /**
     * Return a rounded bitmap version of the image contained in the byte array
     *
     * @param res        the context resources
     * @param imgInBytes the byte array with the image
     * @return the RoundedBitmapDrawable with the rounded bitmap
     */
    public static RoundedBitmapDrawable getRoundedBitmap(Resources res, byte[] imgInBytes) {
        Bitmap srcBitmap = BitmapFactory.decodeByteArray(imgInBytes, 0, imgInBytes.length);
        return getRoundedBitmap(res, srcBitmap);
    }



    /**
     * Bitmap to byte[]
     *
     * @param bitmap Bitmap
     * @return byte array
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }


    /**
     * Bitmap to compressed byte[]
     *
     * @param bitmap  Bitmap
     * @param quality int
     * @return byte array
     */
    public static byte[] toCompressedByteArray(Bitmap bitmap, int quality) {
        return toCompressedByteArray(bitmap, quality, Bitmap.CompressFormat.JPEG);
    }


    /**
     * Bitmap to compressed byte[]
     *
     * @param bitmap  Bitmap
     * @param quality int
     * @param format  Bitmap.CompressFormat
     * @return byte array
     */
    public static byte[] toCompressedByteArray(Bitmap bitmap, int quality, Bitmap.CompressFormat format) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(format, quality, stream);
        return stream.toByteArray();
    }
}

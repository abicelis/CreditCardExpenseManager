package ve.com.abicelis.creditcardexpensemanager.app.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alex on 19/8/2016.
 */
public class ImageUtils {

    /**
     * Return a bitmap of the image contained in the byte array
     * @param imgInBytes The image in a byte[]
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
     * Returns a scaled Bitmap with:
     *  - Its larger dimension = largerScaledDimension in px
     *  - Its smaller dimension scaled, according to the bitmap's original aspect ratio
     *
     *  Note: if the bitmap's dimensions are already smaller than largerScaledDimension
     *  then nothing will be done to the bitmap
     */
    public static Bitmap scaleBitmap(Bitmap image, int largerScaledDimension) {

        if (image == null || image.getWidth() == 0 || image.getHeight() == 0)
            return image;

        //if the image is already small, leave as is
        if (image.getHeight() <= largerScaledDimension && image.getWidth() <= largerScaledDimension)
            return image;

        // Resize the larger dimension of the image to largerScaledDimension and calculate other size
        // respecting the image's aspect ratio
        boolean heightLargerThanWidth = (image.getHeight() > image.getWidth());
        float aspectRatio = (heightLargerThanWidth ? (float)image.getHeight() / (float)image.getWidth() : (float)image.getWidth() / (float)image.getHeight());
        int smallerScaledDimension = (int) (largerScaledDimension / aspectRatio);
        int scaledWidth = (heightLargerThanWidth ? smallerScaledDimension : largerScaledDimension);
        int scaledHeight = (heightLargerThanWidth ? largerScaledDimension : smallerScaledDimension);

        return Bitmap.createScaledBitmap(image, scaledWidth, scaledHeight, true);
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

    /**
     * Bitmap to compressed Bitmap
     *
     * @param bitmap  Bitmap
     * @param quality int
     * @return Bitmap
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int quality) {
        byte[] arr = toCompressedByteArray(bitmap, quality, Bitmap.CompressFormat.JPEG);
        return BitmapFactory.decodeByteArray(arr, 0, arr.length);
    }




    /**
     * Size in pixels to size in dp
     *
     * @param pixels double
     * @return double
     */
    public static double getDPFromPixels(WindowManager wm, double pixels) {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                pixels = pixels * 0.75;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                //pixels = pixels * 1;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                pixels = pixels * 1.5;
                break;
        }
        return pixels;
    }

}

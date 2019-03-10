package de.danoeh.antennapod.core.glide;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import java.lang.reflect.Array;
import java.security.MessageDigest;

public class FastBlurTransformation extends BitmapTransformation {
    private static final int BLUR_IMAGE_WIDTH = 150;
    private static final int STACK_BLUR_RADIUS = 1;
    private static final String TAG = FastBlurTransformation.class.getSimpleName();

    protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        double d = (double) outHeight;
        Double.isNaN(d);
        d *= 1.0d;
        double d2 = (double) BLUR_IMAGE_WIDTH;
        Double.isNaN(d2);
        d *= d2;
        d2 = (double) outWidth;
        Double.isNaN(d2);
        Bitmap result = fastBlur(ThumbnailUtils.extractThumbnail(source, BLUR_IMAGE_WIDTH, (int) (d / d2)), 1);
        if (result != null) {
            return result;
        }
        Log.w(TAG, "result was null");
        return source;
    }

    private static Bitmap fastBlur(Bitmap bitmap, int radius) {
        int i = radius;
        if (i < 1) {
            return null;
        }
        int divsum;
        int h;
        int wh;
        int wm;
        int w = bitmap.getWidth();
        int h2 = bitmap.getHeight();
        int[] pix = new int[(w * h2)];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h2);
        int wm2 = w - 1;
        int hm = h2 - 1;
        int wh2 = w * h2;
        int div = (i + i) + 1;
        int[] r = new int[wh2];
        int[] g = new int[wh2];
        int[] b = new int[wh2];
        int[] vmin = new int[Math.max(w, h2)];
        int divsum2 = (div + 1) >> 1;
        int divsum3 = divsum2 * divsum2;
        int[] dv = new int[(divsum3 * 256)];
        divsum2 = 0;
        while (divsum2 < divsum3 * 256) {
            dv[divsum2] = divsum2 / divsum3;
            divsum2++;
        }
        int yi = 0;
        int yw = 0;
        int[][] stack = (int[][]) Array.newInstance(int.class, new int[]{div, 3});
        int r1 = i + 1;
        divsum2 = 0;
        while (divsum2 < h2) {
            int i2;
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            divsum = divsum3;
            divsum3 = -i;
            int bsum = 0;
            while (divsum3 <= i) {
                h = h2;
                wh = wh2;
                h2 = pix[yi + Math.min(wm2, Math.max(divsum3, 0))];
                int[] sir = stack[divsum3 + i];
                sir[0] = (h2 & 16711680) >> 16;
                sir[1] = (h2 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                sir[2] = h2 & 255;
                wh2 = r1 - Math.abs(divsum3);
                rsum += sir[0] * wh2;
                gsum += sir[1] * wh2;
                bsum += sir[2] * wh2;
                if (divsum3 > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                divsum3++;
                h2 = h;
                wh2 = wh;
            }
            h = h2;
            wh = wh2;
            h2 = radius;
            wh2 = 0;
            while (wh2 < w) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                int[] sir2 = stack[((h2 - i) + div) % div];
                routsum -= sir2[0];
                goutsum -= sir2[1];
                boutsum -= sir2[2];
                if (divsum2 == 0) {
                    i2 = divsum3;
                    vmin[wh2] = Math.min((wh2 + i) + 1, wm2);
                } else {
                    i2 = divsum3;
                }
                divsum3 = pix[yw + vmin[wh2]];
                sir2[0] = (divsum3 & 16711680) >> 16;
                sir2[1] = (divsum3 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                wm = wm2;
                sir2[2] = divsum3 & 255;
                rinsum += sir2[0];
                ginsum += sir2[1];
                binsum += sir2[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                h2 = (h2 + 1) % div;
                int[] sir3 = stack[h2 % div];
                routsum += sir3[0];
                goutsum += sir3[1];
                boutsum += sir3[2];
                rinsum -= sir3[0];
                ginsum -= sir3[1];
                binsum -= sir3[2];
                yi++;
                wh2++;
                wm2 = wm;
                divsum3 = i2;
            }
            i2 = divsum3;
            wm = wm2;
            yw += w;
            divsum2++;
            h2 = h;
            divsum3 = divsum;
            wh2 = wh;
        }
        divsum = divsum3;
        h = h2;
        wm = wm2;
        wh = wh2;
        wm2 = divsum2;
        h2 = 0;
        while (h2 < w) {
            rsum = 0;
            boutsum = 0;
            routsum = 0;
            binsum = 0;
            divsum2 = -i;
            bsum = 0;
            wh2 = 0;
            gsum = 0;
            divsum3 = (-i) * w;
            rinsum = 0;
            goutsum = 0;
            while (divsum2 <= i) {
                int y = wm2;
                yi = Math.max(0, divsum3) + h2;
                int[] sir4 = stack[divsum2 + i];
                sir4[0] = r[yi];
                sir4[1] = g[yi];
                sir4[2] = b[yi];
                wm2 = r1 - Math.abs(divsum2);
                wh2 += r[yi] * wm2;
                bsum += g[yi] * wm2;
                gsum += b[yi] * wm2;
                if (divsum2 > 0) {
                    binsum += sir4[0];
                    routsum += sir4[1];
                    rinsum += sir4[2];
                } else {
                    boutsum += sir4[0];
                    rsum += sir4[1];
                    goutsum += sir4[2];
                }
                if (divsum2 < hm) {
                    divsum3 += w;
                }
                divsum2++;
                wm2 = y;
            }
            yi = radius;
            ginsum = h2;
            wm2 = 0;
            while (true) {
                int yp = divsum3;
                divsum3 = h;
                if (wm2 >= divsum3) {
                    break;
                }
                pix[ginsum] = (((pix[ginsum] & ViewCompat.MEASURED_STATE_MASK) | (dv[wh2] << 16)) | (dv[bsum] << 8)) | dv[gsum];
                wh2 -= boutsum;
                bsum -= rsum;
                gsum -= goutsum;
                int[] sir5 = stack[((yi - i) + div) % div];
                boutsum -= sir5[0];
                rsum -= sir5[1];
                goutsum -= sir5[2];
                if (h2 == 0) {
                    vmin[wm2] = Math.min(wm2 + r1, hm) * w;
                }
                i = vmin[wm2] + h2;
                sir5[0] = r[i];
                sir5[1] = g[i];
                sir5[2] = b[i];
                binsum += sir5[0];
                routsum += sir5[1];
                rinsum += sir5[2];
                wh2 += binsum;
                bsum += routsum;
                gsum += rinsum;
                yi = (yi + 1) % div;
                sir5 = stack[yi];
                boutsum += sir5[0];
                rsum += sir5[1];
                goutsum += sir5[2];
                binsum -= sir5[0];
                routsum -= sir5[1];
                rinsum -= sir5[2];
                ginsum += w;
                wm2++;
                h = divsum3;
                divsum3 = yp;
                i = radius;
            }
            h2++;
            h = divsum3;
            i = radius;
        }
        int h3 = h;
        bitmap.setPixels(pix, 0, w, 0, 0, w, h3);
        return bitmap;
    }

    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(TAG.getBytes());
    }
}

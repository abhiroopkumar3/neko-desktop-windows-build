package android.graphics;

import java.io.InputStream;

/** Java facade so call sites see platform types (null on failure) like on Android. */
public final class BitmapFactory {
    private BitmapFactory() {}

    public static class Options {
        public boolean inJustDecodeBounds = false;
        public int inSampleSize = 1;
        public Bitmap.Config inPreferredConfig = null;
        public boolean inMutable = false;
        public int outWidth = 0;
        public int outHeight = 0;
        public String outMimeType = null;
    }

    public static Bitmap decodeStream(InputStream stream) { return decodeStream(stream, null, null); }

    public static Bitmap decodeStream(InputStream stream, Rect outPadding, Options opts) {
        return BitmapDecoding.decodeStream(stream, opts);
    }

    public static Bitmap decodeResource(android.content.res.Resources res, int id) { return BitmapDecoding.decodeResource(res, id); }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) { return decodeByteArray(data, offset, length, null); }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts) {
        return BitmapDecoding.decodeStream(new java.io.ByteArrayInputStream(data, offset, length), opts);
    }
}

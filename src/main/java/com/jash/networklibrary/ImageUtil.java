//package com.jash.networklibrary;
//
//import android.graphics.Bitmap;
//import android.support.v4.util.LruCache;
//import android.widget.ImageView;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//
//public class ImageUtil {
//    private static final Executor executor = new ScheduledThreadPoolExecutor(3);
//    static LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20 << 20) {
//        @Override
//        protected int sizeOf(String key, Bitmap value) {
////            int count = value.getByteCount();
//            int bytes = value.getRowBytes() * value.getHeight()/1024;
//            return bytes;
//        }
//    };
//    public static void loadImage(ImageView image, String url) {
//        Bitmap bitmap = cache.get(url);
//        ImageLoader loader = (ImageLoader) image.getTag();
//        if (loader != null) {
//            loader.cancel(false);
//            //AsynchTask.cancel——————》iscancel=false  or  true
//        }
//        if (bitmap != null) {
//            image.setImageBitmap(bitmap);
//        } else {
//            new ImageLoader(image).executeOnExecutor(executor, url);
//        }
//    }
//}

package com.jash.networklibrary;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/9/17 0017.
 */
public class ImageLoader {
    static Context context;
    static LruCache<String ,Bitmap> mLruCache;
    static int loadImage;
    static int errorImage;

    public ImageLoader(Context context,int loadImage,int errorImage){
        this.context = context;
        this.loadImage = loadImage;
        this.errorImage = errorImage;
        long maxMemory = Runtime.getRuntime().maxMemory();
        mLruCache = new LruCache<String,Bitmap>((int) (maxMemory/8)){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getRowBytes() * value.getHeight()/1024;
                return byteCount;
            }
        };
    }
    private Bitmap getFromCache(String url){
        return mLruCache.get(url);
    }
    private void setToCache(String url,Bitmap bitmap){
        mLruCache.put(url,bitmap);
    }

    public static void loadImage(String url, ImageView imgView) {
        Log.d("bitl", "loadImage: " + mLruCache.get(url));
        Bitmap bitmap = mLruCache.get(url);
        if (bitmap !=null){
            Log.d("Lru", "loadImage: ");
            imgView.setImageBitmap(bitmap);
            return;
        }
        bitmap = getFromFiles(url);
        Log.d("bitf", "loadImage: " + bitmap);
        if (bitmap != null){
            Log.d("File", "loadImage: ");
            imgView.setImageBitmap(bitmap);
            mLruCache.put(url,bitmap);
            return;
        }
        getFromIntent(url,imgView);
    }

    private static void getFromIntent(final String url,final ImageView imageView) {
        Log.d("Int", "getFromIntent: ");
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                imageView.setImageResource(loadImage);
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream is = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        if (bitmap != null){
                            mLruCache.put(url,bitmap);
                            String filesPath = context.getExternalFilesDir(null).getAbsolutePath();
                            String fileName = url.substring(url.lastIndexOf("/") + 1);
                            String filePath = filesPath + "/" + fileName;
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(filePath));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap == null) {
                    imageView.setImageResource(errorImage);
                }else {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    private static Bitmap getFromFiles(String url) {
        //通过Context.getExternalFilesDir()方法可以获取到 SDCard/Android/data/你的应用的包名/files/ 目录，
        // 一般放一些长时间保存的数据
        String filesPath = context.getExternalFilesDir(null).getAbsolutePath();
        Log.d("Image", "getFromFiles: " + filesPath);
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String filePath = filesPath + "/" + fileName;
        //如果没有返回null
        return BitmapFactory.decodeFile(filePath);
    }


}

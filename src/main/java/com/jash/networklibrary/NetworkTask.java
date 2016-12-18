package com.jash.networklibrary;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkTask<T> extends AsyncTask<NetworkTask.Callback<T>, Void, Object> {

    private Callback<T> callback;
    private String url;
    private Class<T> t;
    public NetworkTask(String url, Class<T> t) {
        this.url = url;
        this.t = t;
    }

    @Override
    protected Object doInBackground(Callback<T>... params) {
        callback = params[0];
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int length;
                byte[] buffer = new byte[102400];
                while ((length = is.read(buffer)) != -1){
                    bos.write(buffer, 0, length);
                }
                Gson gson = new Gson();
                return gson.fromJson(bos.toString("UTF-8"), t);
            } else {
                return new RuntimeException("ResponseCode: " + code);
            }
        } catch (IOException e) {
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (t.isInstance(o)) {
            callback.onSuccess((T) o);
        } else if (o instanceof Exception) {
            callback.onFail((Exception) o);
        }
    }

    public interface Callback<S> {
        void onSuccess(S text);
        void onFail(Exception e);
    }
}

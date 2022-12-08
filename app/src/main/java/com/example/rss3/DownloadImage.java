package com.example.rss3;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImage extends AsyncTask<String, Void, Drawable> {
    CustomViewHolder holder = null;

    public DownloadImage(CustomViewHolder holder) {
    this.holder = holder;
    }
    @Override
    protected Drawable doInBackground(String... strings) {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(strings[0]).openConnection();


            connection.connect();
            Log.d("TRY:", "doInBackground: " + strings[0]);
            InputStream input = connection.getInputStream();
            Drawable d = Drawable.createFromStream(input, "src name");
            return d;
        } catch (IOException e) {
            Log.d("MESSAGE", "doInBackground: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        holder.image.setImageDrawable(drawable);
    }
}
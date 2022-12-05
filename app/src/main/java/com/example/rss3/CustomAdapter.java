package com.example.rss3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> descriptions;
    ArrayList<String> pubDates;
    ArrayList<String> images;
    ArrayList<String> categories;
    Context context;
    CustomViewHolder holder = null;

    public CustomAdapter(Context context, ArrayList<String> titles, ArrayList<String> links, ArrayList<String> descriptions, ArrayList<String> pubDates, ArrayList<String> images, ArrayList<String> categories) {
        super(context, R.layout.single_item, R.id.tv1, titles);
        this.context = context;
        this.titles = titles;
        this.links = links;
        this.descriptions = descriptions;
        this.pubDates = pubDates;
        this.images = images;
        this.categories = categories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View item = convertView;

        if (item == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.single_item, parent, false);
            holder = new CustomViewHolder(item);
            item.setTag(holder);
        }
        else{
            holder = (CustomViewHolder) item.getTag();
        }
        String[] image = {images.get(position)};
        new DownloadImage().execute(image);
        //holder.image.setImageDrawable();
        holder.title.setText(titles.get(position));
        holder.description.setText(descriptions.get(position));
        holder.pubDate.setText(pubDates.get(position));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        return item;
    }

    private InputStream getInputStream(String s) throws IOException {
        InputStream is = (InputStream) new URL(s).openConnection().getInputStream();
        return is;
    }
    private static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Drawable> {
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

}

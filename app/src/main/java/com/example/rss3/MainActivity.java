package com.example.rss3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lvRSS;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> descriptions;
    ArrayList<String> pubDates;
    ArrayList<String> images;
    ArrayList<String> categories;
    CustomAdapter adapter;
    BackgroundProcess backgroundProcess;
    Bundle extras;
    JobScheduler jobScheduler;
    String mainurl = "";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchRSS:
                Intent intent = new Intent(this, SearchRss.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jobScheduler = (JobScheduler) getSystemService(this.JOB_SCHEDULER_SERVICE);
        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        pubDates = new ArrayList<String>();
        images = new ArrayList<String>();
        categories = new ArrayList<String>();

        lvRSS = (ListView) findViewById(R.id.lvRSS);
        lvRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Uri uri = Uri.parse(links.get(i));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Log.d("Item:", "Item" + i + "\n Title: " + titles.get(i) + "\n Link: " + links.get(i) + "\n Description: " + descriptions.get(i) + "\n PubDate: " + pubDates.get(i) + "\n Image: " + images.get(i) + "\n Category: " + categories.get(i));
            }
        });
        String intentinfo;
        try {
            extras = getIntent().getExtras();
            intentinfo = extras.getString("url");
            Log.d("Intent", intentinfo);
        } catch (NullPointerException e) {
            intentinfo = "";
            Log.d("Errormessage", e.getMessage());
        }

        backgroundProcess = (BackgroundProcess) new BackgroundProcess(intentinfo).execute();

        adapter = new CustomAdapter(this, titles, links, descriptions, pubDates, images, categories);
        Log.d("TIMESTAMP MESSAGE", titles.toString());

    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public void checkNewFeed(String timestamp) {
        ComponentName component = new ComponentName(this, MyJobService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("timestamp", timestamp);
        bundle.putString("url", mainurl);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            JobInfo info = new JobInfo.Builder(3696, component)
                    .setRequiresBatteryNotLow(true).
                    setPersisted(true)
                    .setPeriodic(15 * 60 * 1000).setExtras(bundle)
                    .build();
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int result = scheduler.schedule(info);
            if (result == JobScheduler.RESULT_SUCCESS) {
                Log.d("Job", "Job scheduled");
            } else {
                Log.d("Job", "Job scheduling failed");
            }
        }
    }

    public void stopFeedCheck() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(3696);
    }

    public class BackgroundProcess extends AsyncTask<Integer, Void, Exception> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        Exception e = null;

        BackgroundProcess(String website) {
            if (website.equals("")) {
                mainurl = "https://www.independent.ie/breaking-news/rss/";
            } else {
                mainurl = website;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading RSS feed");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {

            try {
                URL url = new URL(mainurl);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "utf-8");
                boolean item = false;

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            item = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (item) titles.add(xpp.nextText());
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (item) links.add(xpp.nextText());
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (item) descriptions.add(xpp.nextText());
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (item) {pubDates.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("media:content")) {
                            if (item) images.add(xpp.getAttributeValue(null, "url"));
                        } else if (xpp.getName().equalsIgnoreCase("enclosure")) {
                            if (item) images.add(xpp.getAttributeValue(null, "url"));
                        } else if (xpp.getName().equalsIgnoreCase("category")) {
                            if (item) categories.add(xpp.nextText());
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        item = false;
                    }
                    eventType = xpp.next();

                }
                checkNewFeed(pubDates.get(0));
             } catch (MalformedURLException e) {
                this.e = e;
            } catch (XmlPullParserException e) {
                this.e = e;
            } catch (IOException e) {
                this.e = e;
            }
            return e;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);
            lvRSS.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }
}
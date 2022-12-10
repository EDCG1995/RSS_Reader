package com.example.rss3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.Toast;
import com.example.rss3.helperClasses.MyJobService;
import com.example.rss3.R;
import com.example.rss3.layoutClasses.CustomAdapter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Main Activity class displays the RSS feed inputted by the user
 */
@SuppressWarnings("ALL")
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
    Intent extras;
    JobScheduler jobScheduler;
    String mainurl = "";
    int number = 10;
    String intentinfo;

    /**
     * Inflates the toolbar implemented
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Sets the actions of the toolbar
     * @param item
     * @return
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /**
             * go back to the SearchRss activity to display another RSS feed
             */
            case R.id.searchRSS:
                Intent intent = new Intent(this, SearchRss.class);
                startActivity(intent);
                break;
            /**
             * By default, when the application excecutes the job scheduler starts working in the background.
             * This option terminates the job scheduler operations
              */
            case R.id.shechuleStop:
                Toast.makeText(this, "Job scheduler has terminated", Toast.LENGTH_SHORT).show();
                stopFeedCheck();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        pubDates = new ArrayList<String>();
        images = new ArrayList<String>();
        categories = new ArrayList<String>();
        /**
         * try/cathch gets the intent passed from the SearchRss activity
         * and extracts the url and the number items that need to be displayed.
         */
        try {
            extras = getIntent();
            intentinfo = extras.getStringExtra("url");
            number = Integer.parseInt(extras.getStringExtra("number"));
            Log.d("Intent", intentinfo);
        } catch (NullPointerException e) {
            intentinfo = "";
            Log.d("Errormessage", e.getMessage());
        }

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

        backgroundProcess = (BackgroundProcess) new BackgroundProcess(intentinfo).execute();
        adapter = new CustomAdapter(this, titles, links, descriptions, pubDates, images, categories);
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * checkNewFeed schedules the job inside MyJobService.
     * it sends the most recent timestamp and the url of the page inside a bundle
     * and sets the activity to be repeated every 15 minutes.
     * @param timestamp most recent timestamp stored
     */
    private void checkNewFeed(String timestamp) {
        ComponentName component = new ComponentName(this, MyJobService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("timestamp", timestamp);
        bundle.putString("url", mainurl);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            JobInfo info = new JobInfo.Builder(3696, component)
                    .setRequiresBatteryNotLow(true).
                    setPersisted(true)
                    .setPeriodic(15 * 60 * 1000)
                    .setExtras(bundle)
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

    /**
     * Background progress is the async task implemented in the MainActivity.
     * It parses the RSS feed and adds the needed elements into ArrayLists.
     */
    @SuppressLint("StaticFieldLeak")
    public class BackgroundProcess extends AsyncTask<Integer, Void, Exception> {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        Exception e = null;

        BackgroundProcess(String website) {
            mainurl = website.equals("") ? "https://www.independent.ie/breaking-news/rss/" : website;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading RSS feed");
            progressDialog.show();
        }

        /**
         * doInBackground parses the RSS feed using XMLPullParser. Extraction sourced from
         * @param integers
         * @return
         */
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
                int guard = 0;
                while (eventType != XmlPullParser.END_DOCUMENT && guard< number  ) {
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
                            if (item) {images.add(xpp.getAttributeValue(null, "url")); guard++;}
                        } else if (xpp.getName().equalsIgnoreCase("enclosure")) {
                            if (item) {images.add(xpp.getAttributeValue(null, "url"));guard++;}
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
            lvRSS.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }
}
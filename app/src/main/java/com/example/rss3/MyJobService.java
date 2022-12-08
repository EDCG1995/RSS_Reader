package com.example.rss3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MyJobService extends JobService {
    private static final String Tag = "MyJobService";
    private boolean job = false;
    String timestamp = "";
    String mainurl = "";
    String checkedTimestamp = "";
    ArrayList<String> timstamps = new ArrayList<>();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(Tag, "Job Has Begun");
        timestamp = jobParameters.getExtras().getString("timestamp").trim();
        mainurl = jobParameters.getExtras().getString("url");
        checkForNewFeed(jobParameters);
        return true;
    }

    private void checkForNewFeed(JobParameters jobParameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("MyNotification", "MyNotification", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                try {
                    URL url = new URL(mainurl);
                    URLConnection conn = url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        if(inputLine.contains("pubDate"))
                            timstamps.add(inputLine.replace("<pubDate>", "").replace("</pubDate>", ""));
                        System.out.println(inputLine);
                    }
                    checkedTimestamp = timstamps.get(0).trim();
                    if(timestamp.equals(checkedTimestamp)){
                        Log.d("TIMESTAMP", "checkForNewFeed: " + timestamp);
                        Log.d("TIMESTAMP", "checkForNewFeed: " + timstamps.get(0));
                        Log.d("TIMESTAMP", "checkForNewFeed: " + "No new feed");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this, "MyNotification")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("No new feed")
                                .setContentText("No new feed")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyJobService.this);
                        notificationManager.notify(1, builder.build());
                    }
                    // <description></description>(\n)<pubDate></pubDate><(\n)<bla>
                    else{
                        Log.d("TIMESTAMP", "checkForNewFeed: " + timestamp);
                        Log.d("TIMESTAMP", "checkForNewFeed: " + timstamps.get(0));
                        Log.d("TIMESTAMP", "checkForNewFeed: " + "New feed");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this, "MyNotification")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("No new feed")
                                .setContentText("No new feed")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyJobService.this);
                        notificationManager.notify(1, builder.build());
                    }
                    br.close();

                    System.out.println("Done");


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (job) {
                    return;
                }

                jobFinished(jobParameters, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        //When job gets cancelled
        job = true;
        return true;
    }

//    public class CheckTimestamp extends AsyncTask<String, Void, String> {
//        String e = null;
//
//
//
//        @Override
//        protected void onPostExecute(String s) {
//            checkedTimestamp = s;
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//        }


    }



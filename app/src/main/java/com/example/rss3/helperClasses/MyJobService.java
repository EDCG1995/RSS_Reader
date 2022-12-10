package com.example.rss3.helperClasses;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.rss3.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * custom JobService class that searches of new timestamps
 *
 *
 *
 * Coding in Flow for JobScheduler https://www.youtube.com/watch?v=3EQWmME-hNA
 */
public class MyJobService extends JobService {
    private static final String Tag = "MyJobService";
    String timestamp = "";
    String mainurl = "";
    String checkedTimestamp = "";
    final ArrayList<String> timstamps = new ArrayList<>();
    private boolean job = false;

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
                    String inputLine = null;
                    String alltext="";

                    while ((inputLine = br.readLine()) != null) {
                        alltext += inputLine;
                    }

                    String[] alltextarray = alltext.split("<pubDate>");
                    String[] pubdate = null;
                    for (int i = 1; i < alltextarray.length; i++) {
                        pubdate = alltextarray[i].split("</pubDate>");
                        timstamps.add(pubdate[0].trim());
                    }
                    checkedTimestamp = timstamps.get(0).trim();
                    /**
                     * Easy Tuto for Notifications in Android
                     * https://www.youtube.com/watch?v=4BuRMScaaI4
                     */
                    if(timestamp.equals(checkedTimestamp)){
                        Log.d("TIMESTAMP", "checkForNewFeed: " + timestamp);
                        Log.d("TIMESTAMP", "checkForNewFeed: " + timstamps.get(0));
                        Log.d("TIMESTAMP", "checkForNewFeed: " + "No new feed");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this, "MyNotification")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("RSS READER")
                                .setContentText("No new feed at the moment")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyJobService.this);
                        notificationManager.notify(1, builder.build());
                    }
                    else{
                        Log.d("TIMESTAMP", "checkForNewFeed: " + timestamp);
                        Log.d("TIMESTAMP", "checkForNewFeed: " + timstamps.get(0));
                        Log.d("TIMESTAMP", "checkForNewFeed: " + "New feed");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyJobService.this, "MyNotification")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("RSS READER")
                                .setContentText("There's a new feed!!!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyJobService.this);
                        notificationManager.notify(1, builder.build());
                    }
                    br.close();
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
}



package com.example.rss3.helperClasses;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * IDBItem is the item sent to the database
 */
public class DBItem {
    private final String userID;
    private final String newsTitle;
    private final String newsDescription;
    private final String timestamp;

    public DBItem(String userID, String newsTitle, String newsDescription){
        this.userID = userID;
        this.newsTitle = newsTitle;
        this.newsDescription = newsDescription;
        Date date = new Date(System.currentTimeMillis());


        // convert Instant to Timestamp
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timestamp = date.toInstant().toString();
        }else timestamp = "timestamp not available";



    }
    public String getTimestamp(){return timestamp;}

    public String getUserID() {return userID;}

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsDescription() {
        return newsDescription;
    }

}

package com.example.rss3.layoutClasses;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.rss3.R;

public class CustomViewHolder  {
    final ImageView image;
    public final TextView title;
    public final TextView description;
    public final TextView pubDate;

    CustomViewHolder(View view){
        image = (ImageView) view.findViewById(R.id.imageView);
        title = (TextView) view.findViewById(R.id.tv1);
        description = (TextView) view.findViewById(R.id.tv2);
        pubDate = (TextView) view.findViewById(R.id.tv3);
        }
}
/**
 * Sandip Bhattacharya tutorial on how to create custom ListViews:
 * https://www.youtube.com/watch?v=zEU7lpAjaGo&ab_channel=SandipBhattacharya
 */

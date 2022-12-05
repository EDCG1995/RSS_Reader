package com.example.rss3;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomViewHolder  {
    ImageView image;
    TextView title;
    TextView description;
    TextView pubDate;
    CustomViewHolder(View view){
        image = (ImageView) view.findViewById(R.id.imageView);
        title = (TextView) view.findViewById(R.id.tv1);
        description = (TextView) view.findViewById(R.id.tv2);
        pubDate = (TextView) view.findViewById(R.id.tv3);
        }


}

package com.example.rss3.layoutClasses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.rss3.helperClasses.DBItem;
import com.example.rss3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends ArrayAdapter<String> {
    final ArrayList<String> titles;
    final ArrayList<String> links;
    final ArrayList<String> descriptions;
    final ArrayList<String> pubDates;
    final ArrayList<String> images;
    final ArrayList<String> categories;
    final Context context;
    CustomViewHolder holder = null;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    final FirebaseAuth mAuth;

    public CustomAdapter(Context context, ArrayList<String> titles, ArrayList<String> links, ArrayList<String> descriptions, ArrayList<String> pubDates, ArrayList<String> images, ArrayList<String> categories) {
        super(context, R.layout.single_item, R.id.tv1, titles);
        this.context = context;
        this.titles = titles;
        this.links = links;
        this.descriptions = descriptions;
        this.pubDates = pubDates;
        this.images = images;
        this.categories = categories;
        mAuth = FirebaseAuth.getInstance();
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
        new DownloadImage(holder).execute(image);
        holder.title.setText(titles.get(position));
        holder.description.setText(descriptions.get(position));
        holder.pubDate.setText(pubDates.get(position));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = mAuth.getUid();
            myRef = database.getReference("DBItem").child("Clicks");
                HashMap<String,DBItem> map = new HashMap<>();
                map.put(userID, new DBItem(userID,holder.title.getText().toString(),holder.description.getText().toString()));
                myRef.push().setValue(map);
                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        return item;
    }
}

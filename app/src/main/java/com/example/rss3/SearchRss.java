package com.example.rss3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchRss extends AppCompatActivity {
    EditText editText;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_rss_activity);
        editText = findViewById(R.id.newURL);
        button = findViewById(R.id.button);
    }

public void searchRSSWebsite(View view){
        String url = editText.getText().toString();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

}


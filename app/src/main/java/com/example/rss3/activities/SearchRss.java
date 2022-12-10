package com.example.rss3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rss3.R;

/**
 * SearchRss is an activity that displays the inputs of the URL of the rss feed and the number of itmes to be displayed
 */
public class SearchRss extends AppCompatActivity {
    EditText editText;
    EditText number;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_rss_activity);
        editText = findViewById(R.id.newURL);
        button = findViewById(R.id.button);
        number = findViewById(R.id.editTextNumber);
    }

public void searchRSSWebsite(View view){
        String url = editText.getText().toString();
        String numberString = number.getText().toString();
        if(!Patterns.WEB_URL.matcher(url).matches()){
            editText.setError("Please enter a valid RSS feed URL");
            editText.requestFocus();
            return;
        }
        if(numberString.isEmpty()){
            number.setError("Please enter a number");
            number.requestFocus();
            return;
        }
        if(Integer.parseInt(numberString)>15){
            number.setError("Please enter a number less than 15");
            number.requestFocus();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("number", numberString);
        startActivity(intent);
    }
}


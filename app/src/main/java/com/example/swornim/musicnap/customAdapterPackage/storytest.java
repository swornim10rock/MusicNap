package com.example.swornim.musicnap.customAdapterPackage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.swornim.musicnap.R;

public class storytest extends AppCompatActivity {

    private TextView storyContainer;
    private MinorDetails minorDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storytest);
        storyContainer=(TextView) findViewById(R.id.storytestid);

        minorDetails=(MinorDetails) getIntent().getSerializableExtra("minorDetails");


        storyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storyContainer.setText(minorDetails.getUserStoryMessage().toLowerCase());
            }
        });

    }
}

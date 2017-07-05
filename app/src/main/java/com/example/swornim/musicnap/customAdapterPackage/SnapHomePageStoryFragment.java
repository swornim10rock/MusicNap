package com.example.swornim.musicnap.customAdapterPackage;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.swornim.musicnap.R;

/**
 * Created by Swornim on 6/8/2017.
 */

public class SnapHomePageStoryFragment extends Fragment {
    private MinorDetails minorDetails;
    private TextView storyContainer;//contains user information holder
    private boolean firstClicked=false;
    private boolean secondClicked=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.snaphomestoryfragment, container, false);
        storyContainer=(TextView) mView.findViewById(R.id.snap_home_page_story_fragment_TextViewId);

//        minorDetails=(MinorDetails) getArguments().getSerializable("minorDetails");

        storyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextStory();
            }
        });

        return mView;

    }


    private void showNextStory(){
        //whenever user clicks on the textview once like snapchat
    //    storyContainer.setText(minorDetails.getUserStoryMessage());
    }

}

package com.example.swornim.musicnap.customAdapterPackage;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.swornim.musicnap.R;

/**
 * Created by Swornim on 7/11/2017.
 */

public class PhotoZoomFragment extends Fragment {

    private UserDatabaseInformation messageObject;


    public static PhotoZoomFragment newInstance(UserDatabaseInformation messageObject) {
        PhotoZoomFragment fragment = new PhotoZoomFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("photoMessageObject", messageObject);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.fullscreenphotofragment,container,false);

        messageObject = (UserDatabaseInformation) getArguments().getSerializable(
                "photoMessageObject");

        ImageView imageView=(ImageView) mView.findViewById(R.id.photozoomid);

        Glide.with(getActivity()).
                load(messageObject.getpUrl()).
                centerCrop().
                into(imageView);

//        Toast.makeText(getActivity(),"fragment called",Toast.LENGTH_LONG).show();

        return mView;
    }
}

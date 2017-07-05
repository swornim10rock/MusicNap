package com.example.videomodule;

/**
 * Created by Swornim on 12/12/2016.
 */
public class videoCapturePart1 {


    //ttry to call the button from the xml
    //onclick="methodName"

    /*

     private final int VIDEO_REQUEST_CODE=100;
    private Button recordButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_main);



    }

    public void captureVideo(View view){
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File video_file= getFilePath();
        Uri video_uri= Uri.fromFile(video_file);

        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(camera_intent,VIDEO_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(requestCode==VIDEO_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(),"Video Recorded Successfully",Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(),"Video Recording Failed",Toast.LENGTH_LONG).show();


        }
    }

    public File getFilePath(){

        File folder = new File("sdcard/video_app/hmm");
        if(!folder.exists()){
            folder.mkdir();
        }

        File video_File= new File(folder,"sampleVideo.mp4");
        return video_File;

    }

}



     */

}

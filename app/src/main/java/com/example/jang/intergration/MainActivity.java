package com.example.jang.intergration;


import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final String sPath = "file://mnt/sdcard/vr/";

    //public static final String sPath = "file:////storage/sdcard1/vr/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        final EditText et = (EditText) findViewById(R.id.edit_text_url);

        SparseArray<String> data = new SparseArray<>();
        int num = 1;
//C:\Users\jang\Desktop\intergration\MD360Player4Android-master\vrlib\src\main\res\values\desktop.ini
        if(num == 0)
        {
            data.put(data.size(), sPath + "sample360_2.mp4");
            data.put(data.size(), sPath + "sample360_4.mp4");
            data.put(data.size(), "http://techslides.com/demos/sample-videos/small.mp4");
            data.put(data.size(), "http://www.opticodec.com/test/ps.3gp");
            data.put(data.size(), "http://210.117.31.104:10002/?action=stream");
            data.put(data.size(), "http://cache.utovr.com/201508270528174780.m3u8");

        }
        data.put(data.size(), "http://www.opticodec.com/test/tropic.3gp");

//        data.put(data.size(), getDrawableUri(R.drawable.bitmap360).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.texture).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.dome_pic).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.stereo).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.multifisheye).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.multifisheye2).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.fish2sphere180sx2).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.fish2sphere180s).toString());

//        data.put(data.size(), "rtsp://mm2.pcslab.com:554/mm/wh200.3gp");
//        data.put(data.size(), "rtsp://masds03.htc.com.tw/h264/H264_20f_384k_AAC_160k_5KF_qcif.3gp");

//        data.put(data.size(), " http://mm2.pcslab.com/mm/7h1000.mp4");

//        data.put(data.size(), "rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp");
//        data.put(data.size(), sPath + "ch0_160701145544.ts");
//        data.put(data.size(), sPath + "videos_s_4.mp4");
//        data.put(data.size(), sPath + "28.mp4");
//        data.put(data.size(), sPath + "haha.mp4");
//        data.put(data.size(), sPath + "halfdome.mp4");
//        data.put(data.size(), sPath + "dome.mp4");
//        data.put(data.size(), sPath + "stereo.mp4");
//        data.put(data.size(), "http://10.240.131.39/vr/570624aae1c52.mp4");
//        data.put(data.size(), "http://192.168.5.106/vr/570624aae1c52.mp4");
//        data.put(data.size(), sPath + "video_31b451b7ca49710719b19d22e19d9e60.mp4");

//        data.put(data.size(), "https://youtu.be/IFqbDmL28OE");


//        data.put(data.size(), sPath + "AGSK6416.jpg");
//        data.put(data.size(), sPath + "IJUN2902.jpg");
//        data.put(data.size(), sPath + "SUYZ2954.jpg");
//        data.put(data.size(), sPath + "TEJD0097.jpg");
//        data.put(data.size(), sPath + "WSGV6301.jpg");

        SpinnerHelper.with(this)
                .setData(data)
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        et.setText(value);
                    }
                })
                .init(R.id.spinner_url);

        findViewById(R.id.video_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = et.getText().toString();
                if (!TextUtils.isEmpty(url)){
                    MD360PlayerActivity.startVideo(MainActivity.this, Uri.parse(url));
                } else {
                    Toast.makeText(MainActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.bitmap_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = et.getText().toString();
                if (!TextUtils.isEmpty(url)){
                    MD360PlayerActivity.startBitmap(MainActivity.this, Uri.parse(url));
                } else {
                    Toast.makeText(MainActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        findViewById(R.id.ijk_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = et.getText().toString();
//                if (!TextUtils.isEmpty(url)){
//                    IjkPlayerDemoActivity.start(MainActivity.this, Uri.parse(url));
//                } else {
//                    Toast.makeText(MainActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private Uri getDrawableUri(@DrawableRes int resId){
        Resources resources = getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId) + '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId) );
    }
}

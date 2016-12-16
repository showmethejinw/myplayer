package com.example.jang.intergration;

/**
 * Created by jang on 2016-11-30.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;


import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class STTActivity extends Activity implements RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";
    private static final String UP = "up";
    private static final String DOWN = "down";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "hi man";
    private static final String KEYPHRASE2 = "hi girl";
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    private String text;
    private boolean test = true;
//    private  boolean test = false;

    /**
     * 비디오 관련 변수
     **/
    public static final String sPath = "file://mnt/sdcard/vr/";
    public static SparseArray<String> data;

    private String youtubeID = "INVtU0LDOMg";
    private String ytInfoUrl;

    private YouTubeExtractor youTubeExtractor;
    private YouTubeExtractor.YouTubeExtractorResult result;

    private Uri uri;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);

        data = new SparseArray<>();
//        boolean test = true;
//        boolean test = false;
        try {
            ytInfoUrl = "http://www.youtube.com/get_video_info?video_id=" + youtubeID + "&eurl="
                    + URLEncoder.encode("https://youtube.googleapis.com/v/" + youtubeID, "UTF-8");

        } catch (Exception e) {

        }

        if (test) {
//            data.put(data.size(), sPath + "before.avi");
//            data.put(data.size(), "https://manifest.googlevideo.com/api/manifest/hls_playlist/id/zogcioMsAAc.1/itag/95/source/yt_live_broadcast/requiressl/yes/ratebypass/yes/live/1/cmbypass/yes/goi/160/sgoap/gir%3Dyes%3Bitag%3D140/sgovp/gir%3Dyes%3Bitag%3D136/hls_chunk_host/r6---sn-3u-bh2e7.googlevideo.com/playlist_type/LIVE/gcr/kr/hcs/yes/mm/32/mn/sn-3u-bh2e7/ms/lv/mv/u/pl/23/shardbypass/yes/smhost/r1---sn-3u-bh2ls.googlevideo.com/dover/6/upn/C2tQskL3dvo/mt/1481866376/ip/125.131.73.149/ipbits/0/expire/1481887987/sparams/ip,ipbits,expire,id,itag,source,requiressl,ratebypass,live,cmbypass,goi,sgoap,sgovp,hls_chunk_host,playlist_type,gcr,hcs,mm,mn,ms,mv,pl,shardbypass,smhost/signature/01C5B9AC55E1571426C459F1671769654D9BEF12.7E735863211AAB7A8DA19E191D483578AC3C7DA5/key/dg_yt0/playlist/index.m3u8");
//            data.put(data.size(), ytInfoUrl);
            youTubeExtractor = new YouTubeExtractor(youtubeID);
            youTubeExtractor.startExtracting(new YouTubeExtractor.YouTubeExtractorListener() {

                @Override
                public void onSuccess(YouTubeExtractor.YouTubeExtractorResult result) {
                    uri = result.getVideoUri();
                    uri.getPath();
                }

                @Override
                public void onFailure(Error error) {
                    error.getMessage();
                }
            });
//            data.put(data.size(),"https://r4---sn-oguelned.googlevideo.com/videoplayback?gcr=kr&ipbits=0&dur=417.030&ratebypass=yes&expire=1481892925&ip=125.131.73.149&requiressl=yes&sparams=dur,expire,gcr,id,initcwndbps,ip,ipbits,itag,lmt,mime,mm,mn,ms,mv,nh,pcm2cms,pl,ratebypass,requiressl,source,upn&id=o-APo6xEClZBVngskAzDcPHEdAoA0DZibxLBjXOCY3ISBI&pl=23&lmt=1481033106249952&upn=nnAdBBU6NT4&source=youtube&itag=18&mime=video%2Fmp4&key=cms1&signature=4673844DFF502C81BF55C84A5337AB12B094DE31.639BE9C2BF9DDB5C1DCE97FCA6A2E234B2930641&req_id=4faa354884b6a3ee&redirect_counter=2&cm2rm=sn-i3b677z&cms_redirect=yes&mm=34&mn=sn-oguelned&ms=ltu&mt=1481871278&mv=u&nh=IgpwcjAzLm5ydDEwKgkxMjcuMC4wLjE");
//            data.put(data.size(), sPath + "videoplayback.mp4");
//            data.put(data.size(), "https://manifest.googlevideo.com/api/manifest/hls_playlist/id/8lLLOnyELak.0/itag/95/source/yt_live_broadcast/requiressl/yes/ratebypass/yes/live/1/cmbypass/yes/goi/160/sgoap/gir%3Dyes%3Bitag%3D140/sgovp/gir%3Dyes%3Bitag%3D136/hls_chunk_host/r2---sn-3u-bh2ll.googlevideo.com/gcr/kr/playlist_type/DVR/mm/32/mn/sn-3u-bh2ll/ms/lv/mv/u/pl/23/dover/6/upn/IlK54lQ_d-0/mt/1481874094/ip/125.131.73.149/ipbits/0/expire/1481895797/sparams/ip,ipbits,expire,id,itag,source,requiressl,ratebypass,live,cmbypass,goi,sgoap,sgovp,hls_chunk_host,gcr,playlist_type,mm,mn,ms,mv,pl/signature/46FAEF2DA1A37107D0671E0A60B17CB01BAB25C4.94592B8CA836DAEAED486838CE94A90821D066BE/key/dg_yt0/playlist/index.m3u8");
//            data.put(data.size(), "https://manifest.googlevideo.com/api/manifest/hls_playlist/id/EfkA3lwB0W0.0/itag/95/source/yt_live_broadcast/requiressl/yes/ratebypass/yes/live/1/cmbypass/yes/goi/160/sgoap/gir%3Dyes%3Bitag%3D140/sgovp/gir%3Dyes%3Bitag%3D136/hls_chunk_host/r4---sn-3u-bh2d.googlevideo.com/playlist_type/DVR/gcr/kr/mm/32/mn/sn-3u-bh2d/ms/lv/mv/m/pcm2cms/yes/pl/23/dover/6/upn/MV4yKHDV1CE/mt/1481874669/ip/125.131.73.149/ipbits/0/expire/1481896313/sparams/ip,ipbits,expire,id,itag,source,requiressl,ratebypass,live,cmbypass,goi,sgoap,sgovp,hls_chunk_host,playlist_type,gcr,mm,mn,ms,mv,pcm2cms,pl/signature/6A9462AC36A13E68D20B006C0C9EB42C5377F64A.0440AB23B9020301BDDFB91241492C0FA4B4D045/key/dg_yt0/playlist/index.m3u8");
            data.put(data.size(), "https://r4---sn-oguelned.googlevideo.com/videoplayback?gcr=kr&ipbits=0&dur=417.030&ratebypass=yes&expire=1481892925&ip=125.131.73.149&requiressl=yes&sparams=dur,expire,gcr,id,initcwndbps,ip,ipbits,itag,lmt,mime,mm,mn,ms,mv,nh,pcm2cms,pl,ratebypass,requiressl,source,upn&id=o-APo6xEClZBVngskAzDcPHEdAoA0DZibxLBjXOCY3ISBI&pl=23&lmt=1481033106249952&upn=nnAdBBU6NT4&source=youtube&itag=18&mime=video%2Fmp4&key=cms1&signature=4673844DFF502C81BF55C84A5337AB12B094DE31.639BE9C2BF9DDB5C1DCE97FCA6A2E234B2930641&req_id=4faa354884b6a3ee&redirect_counter=2&cm2rm=sn-i3b677z&cms_redirect=yes&mm=34&mn=sn-oguelned&ms=ltu&mt=1481871278&mv=u&nh=IgpwcjAzLm5ydDEwKgkxMjcuMC4wLjE");
        } else {
            data.put(data.size(), "http://199.21.149.43:9083/live/CA42-live1/cX/Iu/cXIuLIgTUuG1XxsBCHPF0A==/live.m3u8");
            data.put(data.size(), sPath + "sample360_2.mp4");
            data.put(data.size(), sPath + "sample360_4.mp4");
            data.put(data.size(), "http://techslides.com/demos/sample-videos/small.mp4");
//        data.put(data.size(), "http://www.opticodec.com/test/ps.3gp");
//        data.put(data.size(), "http://210.117.31.104:10002/?action=stream");
            data.put(data.size(), "http://cache.utovr.com/201508270528174780.m3u8");
        }


//        WebView WebView01 = (WebView) findViewById(R.id.webView01);
//        WebView01.setWebViewClient(new WebViewClient());
//
//        WebSettings webSettings = WebView01.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//
//        WebView01.loadUrl("https://www.youtube.com/watch?v=TJpknB-1X5I");

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChannel();
            }
        });


        // Prepare the data for UI
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(PHONE_SEARCH, R.string.phone_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);

//        ((TextView) findViewById(R.id.caption_text))
//                .setText("Preparing the recognizer");

        // Check if user has given permission to record audio           //권한 체크
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
//        runRecognizerSetup();
//        startService(new Intent(STTActivity.this, BackgroundService.class));
    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(STTActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
//                    ((TextView) findViewById(R.id.caption_text))
//                            .setText("Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,                                 //권한 요청
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runRecognizerSetup();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        text = hypothesis.getHypstr();
        if (text.equals(UP))     //KEYPHRASE
            switchSearch(MENU_SEARCH);
        else if (text.equals(DIGITS_SEARCH))
            switchSearch(DIGITS_SEARCH);
        else if (text.equals(PHONE_SEARCH))
            switchSearch(PHONE_SEARCH);
        else if (text.equals(FORECAST_SEARCH))
            switchSearch(FORECAST_SEARCH);
        else if (text.equals(KEYPHRASE)) {
            /**
             recognizer.stop();
             Intent intent = new Intent(PocketSphinxActivity.this, UpActivity.class);
             startActivity(intent);
             finish();
             text = "";
             **/

            recognizer.stop();
            MD360PlayerActivity.startVideo(STTActivity.this, Uri.parse(data.get(MD360PlayerActivity.number++ % data.size())));
            text = "";
//            setContentView(R.layout.up);
//            switchSearch(KEYPHRASE);
//            Toast.makeText(this, "hi man", Toast.LENGTH_SHORT).show();
        } else if (text.equals(KEYPHRASE2)) {
//            Intent intent = new Intent(PocketSphinxActivity.this, DownActivity.class);
//            startActivity(intent);
//            text = "";
//            recognizer.stop();
//            Intent intent = new Intent(PocketSphinxActivity.this, DownActivity.class);
//            startActivity(intent);
//            finish();
//            text = "";
        }
//        else
//            ((TextView) findViewById(R.id.result_text)).setText(text);
        text = "";
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
//        ((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        String caption = getResources().getString(captions.get(searchName));
//        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);       //keyphrase 등록
//        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE2);

//        // Create grammar-based search for selection between demos
//        File menuGrammar = new File(assetsDir, "menu.gram");
//        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
//
//        // Create grammar-based search for digit recognition
//        File digitsGrammar = new File(assetsDir, "digits.gram");
//        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
//
//        // Create language model search
//        File languageModel = new File(assetsDir, "weather.dmp");
//        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
//
//        // Phonetic search
//        File phoneticModel = new File(assetsDir, "en-phone.dmp");
//        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
    }

    @Override
    public void onError(Exception error) {
//        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    public void init() {
        data.put(data.size(), "http://techslides.com/demos/sample-videos/small.mp4");
        data.put(data.size(), "http://www.opticodec.com/test/ps.3gp");
        data.put(data.size(), sPath + "sample360_2.mp4");
        data.put(data.size(), sPath + "sample360_4.mp4");
        data.put(data.size(), "http://210.117.31.104:10002/?action=stream");
        data.put(data.size(), "http://cache.utovr.com/201508270528174780.m3u8");
    }

    public void changeChannel() {
        MD360PlayerActivity.startVideo(STTActivity.this, Uri.parse(data.get(MD360PlayerActivity.number++ % data.size())));
    }
}
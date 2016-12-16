package com.example.jang.intergration;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

/**
 * Created by jang on 2016-11-30.
 */

public class BackgroundService extends Service implements RecognitionListener {

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

    /**
     * 비디오 관련 변수
     **/
    public static final String sPath = "file://mnt/sdcard/vr/";
    private SparseArray<String> data;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
//        runRecognizerSetup();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runRecognizerSetup();
        return super.onStartCommand(intent, flags, startId);

    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(BackgroundService.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
//                    Thread.sleep(1000);
//                    Toast.makeText(BackgroundService.this, "hihi", Toast.LENGTH_SHORT).show();
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

//        while(true)
//        {
//            try {
//                    Assets assets = new Assets(BackgroundService.this);
//                    File assetDir = assets.syncAssets();
//                    setupRecognizer(assetDir);
////                    Thread.sleep(1000);
////                    Toast.makeText(BackgroundService.this, "hihi", Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//
//                }
//        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }


    public void init() {
        data = new SparseArray<>();


        data.put(data.size(), sPath + "sample360_2.mp4");
        data.put(data.size(), sPath + "sample360_4.mp4");
        data.put(data.size(), "http://techslides.com/demos/sample-videos/small.mp4");
        data.put(data.size(), "http://www.opticodec.com/test/ps.3gp");
        data.put(data.size(), "http://210.117.31.104:10002/?action=stream");
        data.put(data.size(), "http://cache.utovr.com/201508270528174780.m3u8");

        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(PHONE_SEARCH, R.string.phone_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);

//        ((TextView) findViewById(R.id.caption_text))
//                .setText("Preparing the recognizer");

        // Check if user has given permission to record audio           //권한 체크

    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(new edu.cmu.pocketsphinx.RecognitionListener() {
            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onEndOfSpeech() {
                if (!recognizer.getSearchName().equals(KWS_SEARCH))
                    switchSearch(KWS_SEARCH);
            }

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
                    MD360PlayerActivity.startVideo(getApplicationContext(), Uri.parse(data.get(MD360PlayerActivity.number++ % data.size())));
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

            @Override
            public void onResult(Hypothesis hypothesis) {
                if (hypothesis != null) {
                    String text = hypothesis.getHypstr();
                    makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onTimeout() {
                switchSearch(KWS_SEARCH);
            }
        });

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


}

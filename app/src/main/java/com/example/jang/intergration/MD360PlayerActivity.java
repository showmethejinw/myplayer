package com.example.jang.intergration;

/**
 * Created by jang on 2016-11-30.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDHotspotBuilder;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDRay;
import com.asha.vrlib.plugins.IMDHotspot;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.plugins.MDHotspotPlugin;
import com.asha.vrlib.plugins.MDWidgetPlugin;
import com.asha.vrlib.texture.MD360BitmapTexture;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public abstract class MD360PlayerActivity extends Activity {

    private static final String TAG = "MD360PlayerActivity";

    private static final SparseArray<String> sDisplayMode = new SparseArray<>();
    private static final SparseArray<String> sInteractiveMode = new SparseArray<>();
    private static final SparseArray<String> sProjectionMode = new SparseArray<>();
    private static final SparseArray<String> sAntiDistortion = new SparseArray<>();

    public static int number = 1;
    public static Activity activity;

    public static SparseArray<String> data;


    static {
//        sDisplayMode.put(MDVRLibrary.DISPLAY_MODE_NORMAL, "NORMAL");
        sDisplayMode.put(MDVRLibrary.DISPLAY_MODE_GLASS, "GLASS");

        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_MOTION,"MOTION");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_TOUCH, "TOUCH");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH, "M & T");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION, "CARDBOARD M");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION_WITH_TOUCH, "CARDBOARD M&T");

        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_SPHERE, "SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME180, "DOME 180");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME230, "DOME 230");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME180_UPPER, "DOME 180 UPPER");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME230_UPPER, "DOME 230 UPPER");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_STEREO_SPHERE_HORIZONTAL, "STEREO H SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_STEREO_SPHERE_VERTICAL, "STEREO V SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_FIT, "PLANE FIT");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_CROP, "PLANE CROP");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_FULL, "PLANE FULL");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_MULTI_FISH_EYE_HORIZONTAL, "MULTI FISH EYE HORIZONTAL");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_MULTI_FISH_EYE_VERTICAL, "MULTI FISH EYE VERTICAL");
        sProjectionMode.put(CustomProjectionFactory.CUSTOM_PROJECTION_FISH_EYE_RADIUS_VERTICAL, "CUSTOM MULTI FISH EYE");

        sAntiDistortion.put(1, "ANTI-ENABLE");
//        sAntiDistortion.put(0, "ANTI-DISABLE");
    }


    public static void startVideo(Context context, Uri uri) {
        start(context, uri, VideoPlayerActivity.class);
    }

    public static void startBitmap(Context context, Uri uri) {
        start(context, uri, BitmapPlayerActivity.class);
    }

    private static void start(Context context, Uri uri, Class<? extends Activity> clz) {
        Intent i = new Intent(context, clz);
        i.setData(uri);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }

    private static void stop() {

    }

    private MDVRLibrary mVRLibrary;

    private List<MDAbsPlugin> plugins = new LinkedList<>();

    private MDPosition logoPosition = MDPosition.newInstance().setY(-8.0f).setYaw(-90.0f);

    private MDPosition[] positions = new MDPosition[]{
            MDPosition.newInstance().setZ(-8.0f).setYaw(-45.0f),
            MDPosition.newInstance().setZ(-18.0f).setYaw(0.0f).setAngleX(15),
            MDPosition.newInstance().setZ(-18.0f).setYaw(15.0f).setAngleX(15),
            MDPosition.newInstance().setZ(-10.0f).setYaw(-10.0f).setAngleX(-15),
            MDPosition.newInstance().setZ(-10.0f).setYaw(30.0f).setAngleX(30),
            MDPosition.newInstance().setZ(-10.0f).setYaw(-30.0f).setAngleX(-30),
            MDPosition.newInstance().setZ(-5.0f).setYaw(30.0f).setAngleX(60),
            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45),
            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45).setAngleY(45),
            MDPosition.newInstance().setZ(-3.0f).setYaw(0.0f).setAngleX(90),
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new SparseArray<>();



        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set content view
        setContentView(R.layout.activity_md_using_surface_view);

        // init VR Library
        mVRLibrary = createVRLibrary();

        addplugin();


        final List<View> hotspotPoints = new LinkedList<>();
        hotspotPoints.add(findViewById(R.id.hotspot_point1));
        hotspotPoints.add(findViewById(R.id.hotspot_point2));

        SpinnerHelper.with(this)
                .setData(sDisplayMode)
                .setDefault(mVRLibrary.getDisplayMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchDisplayMode(MD360PlayerActivity.this, key);
                        int i = 0;
                        int size = key == MDVRLibrary.DISPLAY_MODE_GLASS ? 2 : 1;
                        for (View point : hotspotPoints) {
                            point.setVisibility(i < size ? View.VISIBLE : View.GONE);
                            i++;
                        }
                    }
                })
                .init(R.id.spinner_display);

        SpinnerHelper.with(this)
                .setData(sInteractiveMode)
                .setDefault(mVRLibrary.getInteractiveMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchInteractiveMode(MD360PlayerActivity.this, key);
                    }
                })
                .init(R.id.spinner_interactive);

        SpinnerHelper.with(this)
                .setData(sProjectionMode)
                .setDefault(mVRLibrary.getProjectionMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchProjectionMode(MD360PlayerActivity.this, key);
                    }
                })
                .init(R.id.spinner_projection);

        SpinnerHelper.with(this)
                .setData(sAntiDistortion)
                .setDefault(mVRLibrary.isAntiDistortionEnabled() ? 1 : 0)
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.setAntiDistortionEnabled(key != 0);
                    }
                })
                .init(R.id.spinner_distortion);

        findViewById(R.id.button_add_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int index = (int) (Math.random() * 100) % positions.length;
                MDPosition position = positions[index];
                MDHotspotBuilder builder = MDHotspotBuilder.create()
                        .size(4f, 4f)
                        .provider(0, new AndroidDrawableProvider(android.R.drawable.star_off))
                        .provider(1, new AndroidDrawableProvider(android.R.drawable.star_on))
                        .provider(10, new AndroidDrawableProvider(android.R.drawable.checkbox_off_background))
                        .provider(11, new AndroidDrawableProvider(android.R.drawable.checkbox_on_background))
                        .listenClick(new MDVRLibrary.ITouchPickListener() {
                            @Override
                            public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                                if (hitHotspot instanceof MDWidgetPlugin) {
                                    MDWidgetPlugin widgetPlugin = (MDWidgetPlugin) hitHotspot;
                                    widgetPlugin.setChecked(!widgetPlugin.getChecked());
                                }
                            }
                        })
                        .title("star" + index)
                        .position(position)
                        .status(0, 1)
                        .checkedStatus(10, 11);

                MDWidgetPlugin plugin = new MDWidgetPlugin(builder);

                plugins.add(plugin);
                getVRLibrary().addPlugin(plugin);
                Toast.makeText(MD360PlayerActivity.this, "add plugin position:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_add_plugin_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDHotspotBuilder builder = MDHotspotBuilder.create()
                        .size(4f, 4f)
                        .provider(new MDVRLibrary.IBitmapProvider() {
                            @Override
                            public void onProvideBitmap(MD360BitmapTexture.Callback callback) {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moredoo_logo);
                                callback.texture(bitmap);
                            }
                        })
                        .title("logo")
                        .position(logoPosition)
                        .listenClick(new MDVRLibrary.ITouchPickListener() {
                            @Override
                            public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                                Toast.makeText(MD360PlayerActivity.this, "click logo", Toast.LENGTH_SHORT).show();
                            }
                        });
                MDHotspotPlugin plugin = new MDHotspotPlugin(builder);
                plugins.add(plugin);
                getVRLibrary().addPlugin(plugin);
                Toast.makeText(MD360PlayerActivity.this, "add plugin logo", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_remove_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plugins.size() > 0) {
                    MDAbsPlugin plugin = plugins.remove(plugins.size() - 1);
                    getVRLibrary().removePlugin(plugin);
                }
            }
        });

        findViewById(R.id.button_remove_plugins).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plugins.clear();
                getVRLibrary().removePlugins();
            }
        });

        final TextView hotspotText = (TextView) findViewById(R.id.hotspot_text);
        getVRLibrary().setEyePickChangedListener(new MDVRLibrary.IEyePickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                String text = hotspot == null ? "1" : String.format(Locale.CHINESE, "%s  %fs", hotspot.getTitle(), (System.currentTimeMillis() - hitTimestamp) / 1000.0f);
                hotspotText.setText(text);

//                hotspotText.setText(hotspot.getTitle());

                if ((System.currentTimeMillis() - hitTimestamp) / 1000.0f < 3) {


                    Random random = new Random();

                    int rannum = random.nextInt(5);
//                    finish();
                    if(rannum == 0)
                        setContentView(R.layout.introduce_program_layout1);
                    else if(rannum == 1)
                        setContentView(R.layout.introduce_program_layout2);
                    else if(rannum == 2)
                        setContentView(R.layout.introduce_program_layout3);
                    else if(rannum == 3)
                        setContentView(R.layout.introduce_program_layout4);
                    else if(rannum == 4)
                        setContentView(R.layout.introduce_program_layout5);

                    try{
                        Thread.sleep(3000);
                    }catch (Exception e)
                    {

                    }
                    finish();
                    MD360PlayerActivity.startVideo(MD360PlayerActivity.this, Uri.parse(STTActivity.data.get(MD360PlayerActivity.number++ % STTActivity.data.size())));


//                    Toast.makeText(MD360PlayerActivity.this, "abcd", Toast.LENGTH_SHORT).show();
//                    startVideo(MD360PlayerActivity.this, Uri.parse("http://techslides.com/demos/sample-videos/small.mp4"));
//                    getVRLibrary().resetEyePick();
//                    startVideo(MD360PlayerActivity.this, Uri.parse("http://techslides.com/demos/sample-videos/small.mp4"));
                }
            }
        });
    }

    abstract protected MDVRLibrary createVRLibrary();

    public MDVRLibrary getVRLibrary() {
        return mVRLibrary;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVRLibrary.onOrientationChanged(this);
    }

    protected Uri getUri() {
        Intent i = getIntent();
        if (i == null || i.getData() == null) {
            return null;
        }
        return i.getData();
    }

    public void cancelBusy() {
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    public void busy() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }

    private class AndroidDrawableProvider implements MDVRLibrary.IBitmapProvider {

        private int res;

        public AndroidDrawableProvider(int res) {
            this.res = res;
        }

        @Override
        public void onProvideBitmap(MD360BitmapTexture.Callback callback) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), this.res);
            callback.texture(bitmap);
        }
    }

    public void addplugin() {
        final int index = (int) (Math.random() * 100) % positions.length;
//        MDPosition position = positions[index];
        MDPosition position = positions[1];
        MDHotspotBuilder builder = MDHotspotBuilder.create()
                .size(4f, 4f)
                .provider(0, new AndroidDrawableProvider(R.drawable.lion_off1))
                .provider(1, new AndroidDrawableProvider(R.drawable.lion_on1))
                .provider(10, new AndroidDrawableProvider(android.R.drawable.checkbox_off_background))
                .provider(11, new AndroidDrawableProvider(android.R.drawable.checkbox_on_background))
                .listenClick(new MDVRLibrary.ITouchPickListener() {
                    @Override
                    public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                        if (hitHotspot instanceof MDWidgetPlugin) {
                            MDWidgetPlugin widgetPlugin = (MDWidgetPlugin) hitHotspot;
                            widgetPlugin.setChecked(!widgetPlugin.getChecked());
                        }
                    }
                })
                .title("star" + index)
                .position(position)
                .status(0, 1)
                .checkedStatus(10, 11);

        MDWidgetPlugin plugin = new MDWidgetPlugin(builder);

        plugins.add(plugin);
        getVRLibrary().addPlugin(plugin);
//        Toast.makeText(MD360PlayerActivity.this, "add plugin position:" + index, Toast.LENGTH_SHORT).show();


    }
}
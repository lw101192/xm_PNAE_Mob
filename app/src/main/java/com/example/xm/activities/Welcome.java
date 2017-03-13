package com.example.xm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import xm.mina.RequestCallBack;
import com.example.xm.finebiopane.R;
import xm.mina.Client;
import com.example.xm.util.Util;
import com.xm.Bean.MessageBean;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;


public class Welcome extends AppCompatActivity implements View.OnClickListener {

    private Button skip;
    SharedPreferences preference;
    Timer timer;
    int count = 3;
    Intent intent;
    ImageView img;
    String actionurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getMobAPPVersionInfo();
        init();
        loadImage();
        initSplashAction();

    }
    private void getMobAPPVersionInfo() {
        new Thread(new Runnable() {
            public String infoJson;
            String link = null;

            @Override
            public void run() {


                try {
                    MessageBean requestBean = Client.getInstance().getDownloadFileRequestBean("mob/snaeii32","version.txt");
                    Client.getInstance().sendRquestForResponse(requestBean, true, new RequestCallBack<MessageBean>() {
                        @Override
                        public void Response(MessageBean messageBean) {
                            try {
                                infoJson = new String(messageBean.getContent().getBytecontent(),"utf-8");
                                try {
                                    JSONObject jsonObject = new JSONObject(infoJson);
                                    link = jsonObject.getString("welcomelogolink");     //得到欢迎界面图片链接
                                    actionurl = jsonObject.getString("actionurl");      //得到欢迎界面图片点击跳转链接
                                } catch (Exception e) {
                                }
                                final String finalLink = link;
                                String originallink = null;
                                try {
                                    JSONObject jsonObject = new JSONObject(Util.ReadFromFile(Welcome.this, "version.txt"));       //获取本地MobAPPVersionInfo.txt
                                    originallink = jsonObject.getString("welcomelogolink");
                                    System.out.println("originallink>>>"+originallink);
                                } catch (Exception e) {
                                }
                                if (infoJson != null)
                                    if (originallink == null || !originallink.equals(finalLink)) {      //如果本地文件没有MobAPPVersionInfo.txt，或者两个图片链接不一致，则替换旧文件的内容，并下载图片
                                        Util.SaveToFile(Welcome.this, infoJson.getBytes(), "version.txt");
                                        downloadImage(finalLink);
                                    }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                    }
//                });
            }
        }).start();
    }

    /**
     * 下载图片
     *
     * @param url 图片链接
     */
    void downloadImage(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                ByteArrayOutputStream bais = null;
                try {
                    byte[] buf = new byte[1024];
                    URL ur = new URL(url);
                    URLConnection conn = ur.openConnection();
                    conn.connect();
                    is = conn.getInputStream();
                    bais = new ByteArrayOutputStream();
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        bais.write(buf, 0, len);
                    }
                    final byte[] btImg = bais.toByteArray();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Util.SaveToFile(Welcome.this, btImg, "welcomlogo" + url.substring(url.lastIndexOf(".")));
                        }
                    });

                } catch (Exception e) {
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (bais != null)
                            bais.close();
                    } catch (Exception e) {
                    }
                }

            }
        }).start();
    }


    /**
     * 从本地加载图片并显示
     */
    private void loadImage() {
        FileInputStream is = null;
        try {
            is = openFileInput("welcomlogo.jpg");
            Drawable drawable = Drawable.createFromStream(is, "welcomlogo.jpg");
            img.setImageDrawable(drawable);

        } catch (FileNotFoundException e) {
//            String strJson = Util.ReadFromFile(this, "MobAPPVersionInfo.txt");
//            try {
//                JSONObject jsonObject = new JSONObject(strJson);
//                downloadImage(jsonObject.getString("welcomelogolink"));
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {
            }

        }
    }

    /**
     *
     */
    private void initSplashAction() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        skip.setText(count + "");
                        count--;
                    }
                });

                if (count <= 0) {       //倒计时结束，跳转页面
                    startActivity();
                }

            }
        }, 0, 1000);
    }

    private void init() {

        timer = new Timer();
        preference = getSharedPreferences("config",
                MODE_PRIVATE);
        img = (ImageView) findViewById(R.id.welcomeLogo);
        skip = (Button) findViewById(R.id.skip);
        skip.setOnClickListener(this);
        img.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip:     //跳过欢迎界面
                count = 3;
                if (timer != null)
                    timer.cancel();
                startActivity();
                break;
            case R.id.welcomeLogo:      //图片点击跳转
                if (!TextUtils.isEmpty(actionurl)) {
                    skip.performClick();
                    intent = new Intent(Welcome.this, WebView_Activity.class);
                    intent.putExtra("url", actionurl);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_to_left);
                }
                break;
        }
    }

    /**
     * 页面跳转
     */
    private void startActivity() {
        if (preference.getBoolean("user_autologin", false)) {
            intent = new Intent(Welcome.this, MainActivity.class);
            intent.setAction("autoLogin");
            startActivity(intent);
        } else {
            startActivity(new Intent(Welcome.this, Login_Activity.class));
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}

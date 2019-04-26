package com.meng.bilibilihelper;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.meng.bilibilihelper.activity.MainActivity;
import com.meng.bilibilihelper.javaBean.LoginInfoPeople;
import com.meng.bilibilihelper.javaBean.getAward.GetAward;
import com.meng.bilibilihelper.javaBean.liveCaptcha.LiveCaptcha;
import com.meng.bilibilihelper.javaBean.liveTimeStamp.LiveTimeStamp;

import java.io.IOException;

import Decoder.BASE64Decoder;

public class GuaJiService extends Service {
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onHandleIntent((Intent) msg.obj);
        }
    }

    public GuaJiService(String name) {
        super();
        mName = name;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {

    }

    protected void onHandleIntent(Intent intent) {
        LoginInfoPeople loginInfoPeople = MainActivity.instence.loginInfo.loginInfoPeople.get(intent.getIntExtra("pos", 0));
        String cookie = loginInfoPeople.cookie;
        String refer = intent.getStringExtra("refer");
        LiveTimeStamp liveTimeStamp = getLiveTimeStamp(refer);
        LiveCaptcha liveCaptcha = getLiveCaptcha(refer);
        sendHeartBeat(true);
        while (true) {
            if (System.currentTimeMillis() / 1000 > liveTimeStamp.data.time_end) {
                showDialog(liveTimeStamp, liveCaptcha);

            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendHeartBeat(false);
        }

    }

    public LiveTimeStamp getLiveTimeStamp(String refer) {
        return new Gson().fromJson(
                MainActivity.instence.getSourceCode(
                        "https://api.live.bilibili.com/lottery/v1/SilverBox/getCurrentTask",
                        null, refer),
                LiveTimeStamp.class);
    }

    public LiveCaptcha getLiveCaptcha(String refer) {
        return new Gson().fromJson(
                MainActivity.instence.getSourceCode(
                        "https://api.live.bilibili.com/lottery/v1/SilverBox/getCaptcha?ts=" + System.currentTimeMillis(),
                        null, refer),
                LiveCaptcha.class);
    }

    public GetAward getGetAward(long time_start, long time_end, int captcha) {
        return new Gson().fromJson(
                MainActivity.instence.getSourceCode(
                        "https://api.live.bilibili.com/lottery/v1/SilverBox/getAward?time_start=" + time_start +
                                "&end_time=" + time_end +
                                "&captcha=" + captcha),
                GetAward.class);
    }

    public void sendHeartBeat(boolean isFirst) {
        if (isFirst) {
      /*      return new Gson().fromJson(
                    MainActivity.instence.getSourceCode(
                            "https://api.live.bilibili.com/relation/v1/feed/heartBeat?_=" + System.currentTimeMillis()),
                    GetAward.class);*/
            MainActivity.instence.getSourceCode("https://api.live.bilibili.com/relation/v1/feed/heartBeat?_=" + System.currentTimeMillis());
        } else {
        /*    return new Gson().fromJson(
                    MainActivity.instence.getSourceCode(
                            "https://api.live.bilibili.com/relation/v1/Feed/heartBeat"),
                    GetAward.class);*/
            MainActivity.instence.getSourceCode("https://api.live.bilibili.com/relation/v1/Feed/heartBeat");
        }
    }

    public Bitmap getCaptcha(String base64String) {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] result = new byte[0];
        try {
            result = decoder.decodeBuffer(base64String);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeByteArray(result, 0, result.length);
    }

    public boolean showDialog(final LiveTimeStamp liveTimeStamp, LiveCaptcha liveCaptcha) {
        boolean succeess = false;

        Bitmap bitmap = getCaptcha(liveCaptcha.data.img);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
        dialogBuilder.setTitle("提示");
        dialogBuilder.setMessage("xxxxxxxxxxxxx");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GetAward award = getGetAward(liveTimeStamp.data.time_start, liveTimeStamp.data.time_end, 0);
                if (award.code != 0) {
                    MainActivity.instence.showToast(award.message);
                } else {
                    MainActivity.instence.showToast("成功");
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY - 1);
        } else {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        }
        alertDialog.show();
        return succeess;
    }

}

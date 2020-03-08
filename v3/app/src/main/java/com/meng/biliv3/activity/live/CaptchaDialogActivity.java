package com.meng.biliv3.activity.live;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.biliv3.*;
import com.meng.biliv3.activity.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;

public class CaptchaDialogActivity extends Activity {
    private EditText etResult;
    private ImageView imPicture;
    private String picBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.captcha);
        etResult = (EditText) findViewById(R.id.autoCompleteTextview);
        imPicture = (ImageView) findViewById(R.id.captchaImageView);
        final LiveGuajiJavaBean guaji = new Gson().fromJson(getIntent().getStringExtra("data"), LiveGuajiJavaBean.class);
        Button btn = (Button) findViewById(R.id.naiAll);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(getLiveCaptcha(guaji.referer, guaji.cookie)).getAsJsonObject();
                final String capImg = obj.get("data").getAsJsonObject().get("img").getAsString();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap b = getCaptcha(capImg.substring(capImg.indexOf(",") + 1));
                        imPicture.setImageBitmap(b);
                        picBase64 = capImg;
                        final String result = DataBaseHelper.searchResult(picBase64);
                        if (result != null) {
                            etResult.setText(result);
                        }
                        imPicture.setImageBitmap(getCaptcha(picBase64.substring(picBase64.indexOf(",") + 1)));
                    }
                });
            }
        }).start();
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String ge = getGetAward(guaji.liveTimeStamp.data.time_start,
                                guaji.liveTimeStamp.data.time_end,
                                etResult.getText().toString(),
                                guaji.cookie,
                                guaji.referer);
                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(ge).getAsJsonObject();
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        switch (obj.get("code").getAsInt()) {
                            case 0:
                                showToast(guaji.name + "成功");
                                DataBaseHelper.insertData(picBase64, etResult.getText().toString());
                                GuaJiService.guajijavabean.get(guaji.id).isNeedRefresh = true;
                                if (SharedPreferenceHelper.getBoolean("notifi", false)) {
                                    manager.cancel(guaji.id);
                                }
                                finish();
                                break;
                            case -500:  //时间未到
                                showToast(obj.get("msg").getAsString());
                                if (SharedPreferenceHelper.getBoolean("notifi", false)) {
                                    manager.cancel(guaji.id);
                                }
                                GuaJiService.guajijavabean.get(guaji.id).isNeedRefresh = true;
                                finish();
                                break;
                            case -901:    //过期
                            case -902:    //错误
                                showToast(obj.get("msg").getAsString());
                                JsonObject obj2 = parser.parse(getLiveCaptcha(guaji.referer, guaji.cookie)).getAsJsonObject();
                                final String capImg = obj2.get("data").getAsJsonObject().get("img").getAsString();
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Bitmap b = getCaptcha(capImg.substring(capImg.indexOf(",") + 1));
                                        imPicture.setImageBitmap(b);
                                    }
                                });
                                break;
                            case -903:
                                MainActivity.instance.showToast("已经领取过了");
                                if (SharedPreferenceHelper.getBoolean("notifi", false)) {
                                    manager.cancel(guaji.id);
                                }
                                GuaJiService.guajijavabean.get(guaji.id).isNeedRefresh = true;
                                finish();
                                break;
                        }
                    }
                }).start();

            }
        });
    }

    public Bitmap getCaptcha(String base64String) {
        byte[] result = Tools.Base64.decode(base64String);
        return BitmapFactory.decodeByteArray(result, 0, result.length);
    }

    public String getLiveCaptcha(String refer, String cookie) {
        return Tools.Network.getSourceCode(
                "https://api.live.bilibili.com/lottery/v1/SilverBox/getCaptcha?ts=" + System.currentTimeMillis(),
                cookie, refer);
    }

    public String getGetAward(long time_start, long time_end, String captcha, String cookie, String refer) {
        return
                Tools.Network.getSourceCode(
                        "https://api.live.bilibili.com/lottery/v1/SilverBox/getAward?time_start=" + time_start +
                                "&end_time=" + time_end +
                                "&captcha=" + captcha, cookie, refer);
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(CaptchaDialogActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}

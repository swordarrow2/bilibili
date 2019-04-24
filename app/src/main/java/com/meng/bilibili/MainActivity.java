package com.meng.bilibili;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibili.javaBean.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.jsoup.*;
import android.view.View.*;

public class MainActivity extends Activity{
    public static MainActivity instence;
    public final String POST_URL = "http://api.live.bilibili.com/msg/send";
    public static final String UA = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
    public static Gson gson = new Gson();
    private static final String exDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String mainDic = exDir+"/meng/myBilibili/";
    public HashMap<String, LoginInfoPeople> hashMap = new HashMap<>();
    public static LoginInfo loginInfo;
    private Button btn;
	private Button btn2;
    public ListView listView;
    private EditText et;
	private String[] strs=new String[]{
		"发发发",
		"你稳了",
		"不会糟的",
		"稳的很",
		"今天,也是发气满满的一天",
		"你这把全关稳了"};

	public ArrayAdapter<String> adapter;

	public ArrayList<String> arrayList;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence=this;
        File f = new File(mainDic);
        if(!f.exists()){
            f.mkdirs();
            loginInfo=new LoginInfo();
            saveConfig();
		  }
        arrayList=new ArrayList<>();
        try{
            loginInfo=gson.fromJson(readFileToString(),LoginInfo.class);
		  }catch(IOException e){
            e.printStackTrace();
		  }
        if(loginInfo!=null){
            for(LoginInfoPeople loginInfoPeople : loginInfo.loginInfoPeople){
                hashMap.put(loginInfoPeople.name,loginInfoPeople);
                arrayList.add(loginInfoPeople.name);
			  }
		  }
        btn=(Button) findViewById(R.id.btn);
		btn2=(Button) findViewById(R.id.btn2);
        listView=(ListView) findViewById(R.id.lv);
        et=(EditText) findViewById(R.id.et);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(final AdapterView<?> parent,View view,final int position,long id){
				  new Thread(new Runnable(){

						@Override
						public void run(){
							try{
								String key=(String) parent.getItemAtPosition(position);
								sendDanmakuData(strs[new Random().nextInt(strs.length)],hashMap.get(key).cookie,Long.parseLong(et.getText().toString()));
							  }catch(IOException e){
								e.printStackTrace();
							  }
						  }
					  }).start();
				}
			});
        btn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v){
				  Intent intent = new Intent(MainActivity.this,Login.class);
				  startActivity(intent);
				}
			});
		btn2.setOnClickListener(new OnClickListener(){

			  @Override
			  public void onClick(View p1){
				  new Thread(new Runnable(){

						@Override
						public void run(){
							for(LoginInfoPeople l:hashMap.values()){
								try{
									sendDanmakuData(strs[new Random().nextInt(strs.length)],l.cookie,Long.parseLong(et.getText().toString()));
								  }catch(IOException e){
									e.printStackTrace();
								  }
							  }
						  }
					  }).start();
				}
			});
	  }

    public void doVibrate(long time){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
	  }

    public static String getSourceCode(String url){
        return getSourceCode(url,null);
	  }

    public static String getSourceCode(String url,String cookie){
        Connection.Response response = null;
        Connection connection = null;
        try{
            connection=Jsoup.connect(url);
            if(cookie!=null){
                connection.cookies(cookieToMap(cookie));
			  }
            connection.ignoreContentType(true).method(Connection.Method.GET);
            response=connection.execute();
            if(response.statusCode()!=200){
			  }
		  }catch(IOException e){
            e.printStackTrace();
		  }
        return response.body();
	  }

    public static Map<String, String> cookieToMap(String value){
        Map<String, String> map = new HashMap<String, String>();
        String values[] = value.split("; ");
        for(String val : values){
            String vals[] = val.split("=");
            if(vals.length==2){
                map.put(vals[0],vals[1]);
			  }else if(vals.length==1){
                map.put(vals[0],"");
			  }
		  }
        return map;
	  }

    public static String readFileToString() throws IOException, UnsupportedEncodingException{
        File file = new File(mainDic+"info.json");
        if(!file.exists()){
            file.createNewFile();
		  }
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
        return new String(filecontent,"UTF-8");
	  }

    public static void saveConfig(){
        try{
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File(mainDic+"info.json");
            fos=new FileOutputStream(file);
            writer=new OutputStreamWriter(fos,"utf-8");
            writer.write(gson.toJson(loginInfo));
            writer.flush();
            if(fos!=null){
                fos.close();
			  }
		  }catch(IOException e){
            e.printStackTrace();
		  }
	  }

    public void sendDanmakuData(String msg,String cookie,final long roomId) throws IOException{
        URL postUrl = new URL(POST_URL);
        String content = "";//要发出的数据
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        // 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        //	 Post请求不能使用缓存
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Host","api.live.bilibili.com");
        connection.setRequestProperty("Connection","keep-alive");
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin","https://live.bilibili.com");
        connection.setRequestProperty("User-Agent",UA);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer","https://live.bilibili.com/"+roomId);
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie",cookie);
        content="color=16777215"+
		  "&fontsize=25"+
		  "&mode=1"+
		  "&msg="+encode(msg)+
		  "&rnd="+(System.currentTimeMillis()/1000)+
		  "&roomid="+roomId+
		  "&bubble=0"+
		  "&csrf_token="+cookieToMap(cookie).get("bili_jct")+
		  "&csrf="+cookieToMap(cookie).get("bili_jct");
        connection.setRequestProperty("Content-Length",String.valueOf(content.length()));
        // 连接，从postUrl.openConnection()至此的配置必须要在 connect之前完成，
        // 要注意的是connection.getOutputStream会隐含的进行 connect。
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(content);
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
		String s="";
        while((line=reader.readLine())!=null){
            s+=line;
		  }
		final String ss=s;
        reader.close();
        connection.disconnect();
        final ReturnData returnData=gson.fromJson(ss,ReturnData.class);
        switch (returnData.code){
            case 0:
                if( !returnData.message.equals("")){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run(){
                            Toast.makeText(MainActivity.this,
                                    //  roomId+"已奶,返回"+
                                    returnData.message,Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,roomId+"已奶", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            case 1990000:
                if( returnData.message.equals("risk")){
                    Intent intent = new Intent(MainActivity.this,RiskActivity.class);
                    intent.putExtra("url",returnData.data.verify_url);
                    startActivity(intent);
                }
                break;
            default:
                runOnUiThread(new Runnable() {

                    @Override
                    public void run(){
                        Toast.makeText(MainActivity.this,
                                //  roomId+"已奶,返回"+
                                ss,Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
	  }

    public String encode(String url){
        try{
            String encodeURL = URLEncoder.encode(url,"UTF-8");
            return encodeURL;
		  }catch(UnsupportedEncodingException e){
            return "Issue while encoding"+e.getMessage();
		  }
	  }
  }

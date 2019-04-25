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
import android.widget.AdapterView.*;

public class MainActivity extends Activity{
    public static MainActivity instence;
    public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
    public Gson gson = new Gson();
    public HashMap<String, LoginInfoPeople> loginInfoPeopleHashMap = new HashMap<>();
    public LoginInfo loginInfo;
    public ListView listView;
    private EditText editText;
    private String[] strings = new String[]{
		"发发发",
		"你稳了",
		"不会糟的",
		"稳的很",
		"今天,也是发气满满的一天",
		"你这把全关稳了",
		"点歌 信仰は儚き人間の為に",
		"点歌 星条旗のピエロ",
		"点歌 春の湊に-上海アリス幻樂団",
		"点歌 the last crusade",
		"点歌 ピュアヒューリーズ~心の在処",
		"点歌 忘れがたき、よすがの緑",
		"点歌 遥か38万キロのボヤージュ",
		"点歌 プレイヤーズスコア"
	  };

    public ArrayAdapter<String> adapter;
    public ArrayList<String> arrayList;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence=this;
		copyFile();
        File f = new File("info.json");
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
                loginInfoPeopleHashMap.put(loginInfoPeople.name,loginInfoPeople);
                arrayList.add(loginInfoPeople.name);
			  }
		  }
        Button btn = (Button) findViewById(R.id.btn);
        Button btn2 = (Button) findViewById(R.id.btn2);
		Button btn3 = (Button) findViewById(R.id.btn3);
        listView=(ListView) findViewById(R.id.lv);
        editText=(EditText) findViewById(R.id.et);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(final AdapterView<?> parent,View view,final int position,long id){
				  ListView l = new ListView(MainActivity.this);
				  l.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,strings));
				  l.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(final AdapterView<?> p1,View p2,final int p3,long p4){
							new Thread(new Runnable() {

								  @Override
								  public void run(){
									  try{
										  String key = (String) parent.getItemAtPosition(position);
										  sendDanmakuData((String) p1.getItemAtPosition(p3),loginInfoPeopleHashMap.get(key).cookie,editText.getText().toString());
										}catch(IOException e){
										  e.printStackTrace();
										}
									}
								}).start();
						  }
					  });
				  new AlertDialog.Builder(MainActivity.this)
					.setView(l)
					.setTitle("奶")
					.setNegativeButton("我好了",null).show();
				}
			});
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			  @Override
			  public boolean onItemLongClick(final AdapterView<?> parent,View view,final int position,long id){
				  new Thread(new Runnable() {

						@Override
						public void run(){
							try{
								sendSignData(loginInfoPeopleHashMap.get(parent.getItemAtPosition(position)).cookie,editText.getText().toString());
							  }catch(Exception e){
								e.printStackTrace();
							  }
						  }
					  }).start();             
				  return true;
				}
			});
        btn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v){
				  Intent intent = new Intent(MainActivity.this,Login.class);
				  startActivity(intent);
				}
			});
        btn2.setOnClickListener(new OnClickListener() {

			  @Override
			  public void onClick(View p1){
				  new Thread(new Runnable() {

						@Override
						public void run(){
							for(LoginInfoPeople l : loginInfoPeopleHashMap.values()){
								try{
									Thread.sleep(1000);
									sendDanmakuData(strings[new Random().nextInt(strings.length)],l.cookie,editText.getText().toString());
								  }catch(Exception e){
									e.printStackTrace();
								  }
							  }
						  }
					  }).start();
				}
			});
		btn3.setOnClickListener(new OnClickListener() {

			  @Override
			  public void onClick(View p1){
				  new Thread(new Runnable() {

						@Override
						public void run(){
							for(LoginInfoPeople l : loginInfoPeopleHashMap.values()){
								try{
									Thread.sleep(1000);
									sendSignData(l.cookie,editText.getText().toString());
								  }catch(Exception e){
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

    public String getSourceCode(String url){
        return getSourceCode(url,null);
	  }

    public String getSourceCode(String url,String cookie){
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
			  showToast(String.valueOf(response.statusCode()));
			  }
		  }catch(IOException e){
            e.printStackTrace();
		  }
        return response.body();
	  }

    public Map<String, String> cookieToMap(String value){
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

    public String readFileToString() throws IOException, UnsupportedEncodingException{
        File file = new File(getApplicationContext().getFilesDir()+"/info.json");
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

    public void saveConfig(){
        try{
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File("info.json");
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

    public void sendDanmakuData(String msg,String cookie,final String roomId) throws IOException{
        URL postUrl = new URL("http://api.live.bilibili.com/msg/send");
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
        connection.setRequestProperty("User-Agent",userAgent);
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
        // 连接,从postUrl.openConnection()至此的配置必须要在 connect之前完成
        // 要注意的是connection.getOutputStream会隐含的进行 connect
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(content);
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder s = new StringBuilder();
        while((line=reader.readLine())!=null){
            s.append(line);
		  }
        final String ss = s.toString();
        reader.close();
        connection.disconnect();
        try{
            final ReturnData returnData = gson.fromJson(ss,ReturnData.class);
            switch(returnData.code){
                case 0:
				  if(!returnData.message.equals("")){
					  showToast(returnData.message);
                    }else{
						showToast(roomId+"已奶");
                    }
				  break;
                case 1990000:
				  if(returnData.message.equals("risk")){
					  showToast("需要在官方客户端进行账号风险验证");
                    }
				  break;
                default:
				  showToast(ss);
				  break;
			  }
		  }catch(Exception e){
            showToast(ss);
		  }
	  }

    public String encode(String url){
        try{
            return URLEncoder.encode(url,"UTF-8");
		  }catch(UnsupportedEncodingException e){
            return "Issue while encoding"+e.getMessage();
		  }
	  }

	public boolean copyFile(){
		File f1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/meng/myBilibili/"+"info.json") ;
		File f2 = new File(getApplicationContext().getFilesDir()+"/info.json") ;
		if(!f1.exists()){
			return false;
		  }
		  if(!f2.exists()){
			  try{
				  f2.createNewFile();
				}catch(IOException e){
				  showToast(e.toString());
				}
			}else{
			  showToast("f2Exists");
			}
		InputStream in = null ;
		OutputStream out = null ;
		try{
			in=new FileInputStream(f1) ;
		  }catch(FileNotFoundException e){
			showToast("f1NotFound");
			return false;
		  }
		try{
			out=new FileOutputStream(f2) ;
		  }catch(FileNotFoundException e){
			showToast("f2NotFound");
			return false;
		  }
		if(in!=null&&out!=null){
			int temp ;
			try{
				while((temp=in.read())!=-1){
					out.write(temp) ;
				  }				 
			  }catch(IOException e){
				showToast(e.toString());
				return false;
			  }
			try{
				in.close() ;
				out.close() ;
			  }catch(IOException e){
				showToast(e.toString());
				return false;
			  }
		  }
		return true;
	  }

    public void sendSignData(String cookie,String roomId) throws IOException{
        URL postUrl = new URL("https://api.live.bilibili.com/sign/doSign");
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Host","api.live.bilibili.com");
        connection.setRequestProperty("Connection","keep-alive");
        connection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Origin","https://live.bilibili.com");
        connection.setRequestProperty("User-Agent",userAgent);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.setRequestProperty("Referer","https://live.bilibili.com/"+roomId);
        connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
        connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
        connection.setRequestProperty("cookie",cookie);
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder s = new StringBuilder();
        while((line=reader.readLine())!=null){
            s.append(line);
		  }
        final String ss = s.toString();
        reader.close();
        connection.disconnect();
        showToast("结果"+ss);
	  }
	  
	  public void showToast(final String msg){
		  runOnUiThread(new Runnable(){

				@Override
				public void run(){
					Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
				  }
			  });
	  }
  }

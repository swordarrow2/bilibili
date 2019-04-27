package com.meng.bilibilihelper.fragment;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.javaBean.*;
import java.io.*;
import java.net.*;

public class NaiFragment extends Fragment{

	public CustomSentence cs;	  
	File cusCen;
    AlertDialog ab;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.list_fragment,container,false);	
	  }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
		cusCen=new File(Environment.getExternalStorageDirectory()+"/sjf.json");

        if(!cusCen.exists()){
            try{
				cusCen.createNewFile();
			  }catch(IOException e){}
            cs=new CustomSentence();
			String[] strings = new String[]{
				"发发发","你稳了","不会糟的","稳的很",
				"今天,也是发气满满的一天",
				"你这把全关稳了",
				"点歌 信仰は儚き人間の為に",
				"点歌 星条旗のピエロ",
				"点歌 春の湊に-上海アリス幻樂団",
				"点歌 the last crusade",
				"点歌 ピュアヒューリーズ~心の在処",
				"点歌 忘れがたき、よすがの緑",
				"点歌 遥か38万キロのボヤージュ",
				"点歌 プレイヤーズスコア" };
			for(String s:strings){
				cs.sent.add(s);
			  }			
            saveConfig();
		  }else{
			String s="{}";
			try{
				s=readFileToString();
			  }catch(IOException e){}
			cs=new Gson().fromJson(s,CustomSentence.class);		
		  }

        ListView list=(ListView)view.findViewById(R.id.list);
		list.setAdapter(MainActivity.instence.adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(final AdapterView<?> parent,View view,final int position,long id){
				  ListView l = new ListView(getActivity());
				  l.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,cs.sent));
				  l.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(final AdapterView<?> p1,View p2,final int p3,long p4){
							new Thread(new Runnable() {

								  @Override
								  public void run(){
									  try{
										  String key = (String) parent.getItemAtPosition(position);
										  String co="";
										  if(MainActivity.instence.mainFrgment.editText.getText().toString().equals("")){
											  co=MainActivity.instence.mainFrgment.editText.getHint().toString();
											}else{
											  co=MainActivity.instence.mainFrgment.editText.getText().toString();
											}
										  sendDanmakuData(
											(String) p1.getItemAtPosition(p3),
											MainActivity.instence.loginInfoPeopleHashMap.get(key).cookie,
											co);
										}catch(IOException e){
										  e.printStackTrace();
										}
									}
								}).start();
							ab.dismiss();
						  }
					  });
				  ab=new AlertDialog.Builder(getActivity())
					.setView(l)
					.setTitle("奶")
					.setNegativeButton("我好了",null).show();
				}
			});
	  }

	public String readFileToString() throws IOException, UnsupportedEncodingException{
        Long filelength = cusCen.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(cusCen);
        in.read(filecontent);
        in.close();
        return new String(filecontent,"UTF-8");
	  }

	public void saveConfig(){
        try{
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            fos=new FileOutputStream(cusCen);
            writer=new OutputStreamWriter(fos,"utf-8");
            writer.write(new Gson().toJson(cs));
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
        connection.setRequestProperty("User-Agent",MainActivity.instence. userAgent);
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
		  "&csrf_token="+MainActivity.instence.cookieToMap(cookie).get("bili_jct")+
		  "&csrf="+MainActivity.instence.cookieToMap(cookie).get("bili_jct");
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
            final ReturnData returnData = new Gson().fromJson(ss,ReturnData.class);
            switch(returnData.code){
                case 0:
				  if(!returnData.message.equals("")){
					  MainActivity.instence.showToast(returnData.message);
                    }else{
					  MainActivity.instence.showToast(roomId+"已奶");
                    }
				  break;
                case 1990000:
				  if(returnData.message.equals("risk")){
					  MainActivity.instence.showToast("需要在官方客户端进行账号风险验证");
                    }
				  break;
                default:
				  MainActivity.instence. showToast(ss);
				  break;
			  }
		  }catch(Exception e){
			MainActivity.instence. showToast(ss);
		  }
	  }

    public String encode(String url){
        try{
            return URLEncoder.encode(url,"UTF-8");
		  }catch(UnsupportedEncodingException e){
            return "Issue while encoding"+e.getMessage();
		  }
	  }

  }

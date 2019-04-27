package com.meng.bilibilihelper.activity;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.activity.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.materialDesign.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.meng.bilibilihelper.R;
import com.meng.bilibilihelper.materialDesign.ActionBarDrawerToggle;


public class MainActivity extends Activity{
    public static MainActivity instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    public MainFrgment mainFrgment;
    public NaiFragment naiFragment;
    public SignFragment signFragment;
    public ManagerFragment managerFragment;
    public LoginCoinFragment loginCoinFragment;
	public GuaJiFragment guaJiFragment;

    public FragmentManager manager;
    public RelativeLayout rt;
    public TextView rightText;

    public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
    public Gson gson = new Gson();
    public HashMap<String, LoginInfoPeople> loginInfoPeopleHashMap = new HashMap<>();
    public LoginInfo loginInfo;

    public ArrayAdapter<String> adapter;
    public ArrayList<String> arrayList;

    public String jsonPath;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence=this;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
				new AlertDialog.Builder(this)
				  .setTitle("权限申请")
				  .setMessage("本软件需要存储权限用于部分数据存储")
				  .setPositiveButton("我知道了",new DialogInterface.OnClickListener() {
					  @Override
					  public void onClick(DialogInterface dialog,int which){
						  ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },321);
						}
					}).setCancelable(false).show();
			  }
		  }
		findViews();
		initFragment();
		setActionBar();
		setListener();
        jsonPath=getApplicationContext().getFilesDir()+"/info.json";
        File f = new File(jsonPath);
        if(!f.exists()){
            try{
                f.createNewFile();
			  }catch(IOException e){
			  }
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
                loginInfoPeopleHashMap.put(loginInfoPeople.personInfo.data.name,loginInfoPeople);
                arrayList.add(loginInfoPeople.personInfo.data.name);
			  }
		  }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
	  }

    private void setActionBar(){
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
	  }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==321){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    showToast("缺失权限会使应用工作不正常");
				  }else{
					  initNaiFragment(false);
					  initNaiFragment(true);
				  }
			  }
		  }
	  }

    private void setListener(){
        drawerArrow=new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl(){
                return false;
			  }
		  };
        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,drawerArrow,R.string.open,R.string.close) {

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
			  }

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
			  }

            @Override
            public void onDrawerSlide(View drawerView,float slideOffset){
                super.onDrawerSlide(drawerView,slideOffset);
			  }
		  };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,new String[]{
															"首页(大概)", "添加账号", "管理账号", "签到(测试)", "奶","挂机", "签到-直播间", "退出"
														  }));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent,View view,int position,long id){
				  switch(((TextView) view).getText().toString()){
					  case "首页(大概)":
                        initMainFragment(true);
                        break;
					  case "添加账号":
                        startActivity(new Intent(MainActivity.this,Login.class));
                        break;
					  case "管理账号":
                        initManagerFragment(true);
                        break;
					  case "奶":
                        initNaiFragment(true);
                        break;
					  case "挂机":
                        initGuajiFragment(true);
                        break;
					  case "签到-直播间":
                        initSignFragment(true);
                        break;
					  case "签到(测试)":
                        initLoginCoinFragment(true);
                        break;
					  case "退出":
                        if(true){
                            System.exit(0);
						  }else{
                            finish();
						  }
                        break;
					}
				  mDrawerToggle.syncState();
				  mDrawerLayout.closeDrawer(mDrawerList);
				}
			});
	  }

    private void findViews(){
        rt=(RelativeLayout) findViewById(R.id.right_drawer);
        rightText=(TextView) findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList=(ListView) findViewById(R.id.navdrawer);
	  }

    private void initFragment(){
        manager=getFragmentManager();
        initSignFragment(false);
        initManagerFragment(false);
        initLoginCoinFragment(false);
		initGuajiFragment(false);
        initMainFragment(true);
	  }

    private void initMainFragment(boolean showNow){
        FragmentTransaction transactionWelcome = manager.beginTransaction();
        if(mainFrgment==null){
            mainFrgment=new MainFrgment();
            transactionWelcome.add(R.id.main_activityLinearLayout,mainFrgment);
		  }
        hideFragment(transactionWelcome);
        if(showNow){
            transactionWelcome.show(mainFrgment);
		  }
        transactionWelcome.commit();
	  }

    private void initNaiFragment(boolean showNow){
        FragmentTransaction transactionsettings = manager.beginTransaction();
        if(naiFragment==null){
            naiFragment=new NaiFragment();
            transactionsettings.add(R.id.main_activityLinearLayout,naiFragment);
		  }
        hideFragment(transactionsettings);
        if(showNow){
            transactionsettings.show(naiFragment);
		  }
        transactionsettings.commit();
	  }

    private void initSignFragment(boolean showNow){
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if(signFragment==null){
            signFragment=new SignFragment();
            transactionBusR.add(R.id.main_activityLinearLayout,signFragment);
		  }
        hideFragment(transactionBusR);
        if(showNow){
            transactionBusR.show(signFragment);
		  }
        transactionBusR.commit();
	  }

    private void initManagerFragment(boolean showNow){
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if(managerFragment==null){
            managerFragment=new ManagerFragment();
            transactionBusR.add(R.id.main_activityLinearLayout,managerFragment);
		  }
        hideFragment(transactionBusR);
        if(showNow){
            transactionBusR.show(managerFragment);
		  }
        transactionBusR.commit();
	  }

    private void initLoginCoinFragment(boolean showNow){
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if(loginCoinFragment==null){
            loginCoinFragment=new LoginCoinFragment();
            transactionBusR.add(R.id.main_activityLinearLayout,loginCoinFragment);
		  }
        hideFragment(transactionBusR);
        if(showNow){
            transactionBusR.show(loginCoinFragment);
		  }
        transactionBusR.commit();
	  }
	  
	private void initGuajiFragment(boolean showNow){
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if(guaJiFragment==null){
            guaJiFragment=new GuaJiFragment();
            transactionBusR.add(R.id.main_activityLinearLayout,guaJiFragment);
		  }
        hideFragment(transactionBusR);
        if(showNow){
            transactionBusR.show(guaJiFragment);
		  }
        transactionBusR.commit();
	  }
	
    public void hideFragment(FragmentTransaction transaction){
        Fragment fs[] = {
			mainFrgment,
			naiFragment,
			signFragment,
			managerFragment,
			loginCoinFragment,
			guaJiFragment
		  };
        for(Fragment f : fs){
            if(f!=null){
                transaction.hide(f);
			  }
		  }
	  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            if(mDrawerLayout.isDrawerOpen(mDrawerList)){
                mDrawerLayout.closeDrawer(mDrawerList);
			  }else{
                mDrawerLayout.openDrawer(mDrawerList);
			  }
		  }
        return super.onOptionsItemSelected(item);
	  }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
	  }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
	  }

    public void doVibrate(long time){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
	  }

    public String getSourceCode(String url){
        return getSourceCode(url,null,null);
	  }

    public String getSourceCode(String url,String cookie){
        return getSourceCode(url,cookie,null);
    }

    public String getSourceCode(String url,String cookie,String refer){
        Connection.Response response = null;
        Connection connection = null;
        try{
            connection=Jsoup.connect(url);
            if(cookie!=null){
                connection.cookies(cookieToMap(cookie));
			  }
			  if(refer!=null){
                connection.referrer(refer);
              }
			  connection.userAgent(userAgent);
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
        File file = new File(jsonPath);
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
            File file = new File(jsonPath);
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

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_MENU){
            if(mDrawerLayout.isDrawerOpen(mDrawerList)){
                mDrawerLayout.closeDrawer(mDrawerList);
			  }else{
                mDrawerLayout.openDrawer(mDrawerList);
			  }
            return true;
		  }
        return super.onKeyDown(keyCode,event);
	  }

    public void showToast(final String msg){
        runOnUiThread(new Runnable() {

			  @Override
			  public void run(){
				  Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
				}
			});
	  }
  }

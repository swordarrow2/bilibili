package com.meng.bilibili;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;

import com.meng.bilibili.fragments.SettingsFragment;
import com.meng.bilibili.lib.ExceptionCatcher;
import com.meng.bilibili.lib.SharedPreferenceHelper;
import com.meng.bilibili.lib.materialDesign.ActionBarDrawerToggle;
import com.meng.bilibili.lib.materialDesign.DrawerArrowDrawable;
import com.meng.qrtools.*;
import com.meng.qrtools.creator.*;
import com.meng.qrtools.lib.materialDesign.*;
import com.meng.qrtools.reader.*;

public class MainActivity2 extends Activity{
    public static MainActivity2 instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout rt;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    private SettingsFragment settingsFragment;
    public TextView rightText;

    public FragmentManager manager;

    public static final int SELECT_FILE_REQUEST_CODE = 822;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ExceptionCatcher.getInstance().init(this);
        setContentView(R.layout.main_activity);
        instence=this;
        setActionBar();
        findViews();
        initFragment();
        setListener();
        changeTheme();

	  }
    public void doVibrate(long time) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }
    public static void selectImage(Fragment f){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        f.startActivityForResult(intent,SELECT_FILE_REQUEST_CODE);
	  }

    private void changeTheme(){
        if(SharedPreferenceHelper.getBoolean("useLightTheme",true)){
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_light));
		  }else{
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
		  }
        if(getIntent().getBooleanExtra("setTheme",false)){
            initSettingsFragment(true);
		  }else{
            initWelcome(true);
            if(SharedPreferenceHelper.getBoolean("opendraw",true)){
                mDrawerLayout.openDrawer(mDrawerList);
			  }
		  }
	  }

    @Override
    public void setTheme(int resid){
        if(MainActivity.lightTheme){
            super.setTheme(R.style.AppThemeLight);
		  }else{
            super.setTheme(R.style.AppThemeDark);
		  }
	  }

    private void setActionBar(){
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
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
															"首页(大概)" ,"退出"
														  }));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent,View view,int position,long id){
				  switch(((TextView) view).getText().toString()){
					  case "首页(大概)":
                        initWelcome(true);
                        break;
					  case "退出":
                        if(SharedPreferenceHelper.getBoolean("exitsettings")){
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

        initAwesomeFragment(false);
        if(MainActivity.instence.sharedPreference.getBoolean("ldgif")){
            initGifAwesomeFragment(false);
		  }

	  }


    private void initSettingsFragment(boolean showNow){
        FragmentTransaction transactionGifAwesomeCreatorFragment = manager.beginTransaction();
        if(gifAwesomeFragment==null){
            gifAwesomeFragment=new SettingsFragment();
            transactionGifAwesomeCreatorFragment.add(R.id.main_activityLinearLayout,gifAwesomeFragment);
		  }
        hideFragment(transactionGifAwesomeCreatorFragment);
        if(showNow){
            transactionGifAwesomeCreatorFragment.show(gifAwesomeFragment);
		  }
        transactionGifAwesomeCreatorFragment.commit();
	  }

    public void hideFragment(FragmentTransaction transaction){
        Fragment fs[] = {
			settingsFragment
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
        if(arbAwesomeFragment!=null&&arbAwesomeFragment.isVisible()){
            arbAwesomeFragment.onKeyDown(keyCode,event);
            return true;
		  }
        if(gifArbAwesomeFragment!=null&&gifArbAwesomeFragment.isVisible()){
            gifArbAwesomeFragment.onKeyDown(keyCode,event);
            return true;
		  }
        return super.onKeyDown(keyCode,event);
	  }


  }


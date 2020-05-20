package com.meng.mbili;

import android.app.*;
import android.content.res.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.util.concurrent.*;

import android.support.v7.widget.Toolbar;


public class MainActivity2 extends AppCompatActivity {

    public static MainActivity2 instence;
	private DrawerLayout mDrawerLayout;
    public LinearLayout mainLinearLayout;

    public boolean onWifi = false;

   	private HashMap<String,Fragment> fragments=new HashMap<>();

    private ActionBarDrawerToggle toggle;


	public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";

    public String jsonPath ;
    public String mainDic = mainDic = Environment.getExternalStorageDirectory() + "/Pictures/grzx/";


	public ExecutorService threadPool = Executors.newCachedThreadPool();

	public static final String regAv = "[Aa][Vv](\\d{1,})\\D{0,}";
	public static final String regBv = ".{0,}([Bb][Vv]1.{2}4.{1}1.{1}7.{2}).{0,}";
	public static final String regLive = "\\D{0,}live\\D{0,}(\\d{1,})\\D{0,}";
	public static final String regCv = "[Cc][Vv](\\d{1,})";
	public static final String regUid = "space\\D{0,}(\\d{1,})";
	public static final String regUid2 = "UID\\D{0,}(\\d{1,})";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
		jsonPath = getFilesDir() + "/account.json";



		
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
		mDrawerLayout.openDrawer(GravityCompat.START);
        navigationView.setCheckedItem(R.id.first_page);
	}

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
			switch (item.getItemId()) {
				case R.id.exit:
					//exit();
					System.exit(0);
					break;
				case R.id.settings:
					showFragment(SettingsPreference.class);
					break;
				case R.id.first_page:
					Snackbar snackbar = Snackbar.make(MainActivity2.instence.mainLinearLayout, "snackBar test", Snackbar.LENGTH_LONG).setAction("Action", null);
					//new View.OnClickListener() {
					//     @Override
					//     public void onClick(View view) {
					//      }
					//   });
					//   snackbar.setText("动态文本");//动态设置文本显示内容
					//    snackbar.setActionTextColor(Color.RED);//动态设置Action文本的颜色
					snackbar.setDuration(4000);//动态设置显示时间

					snackbar.show();
					break;
			}
            return true;
		}
	};

	public <T extends Fragment> T getFragment(Class<T> c) {
		return (T)fragments.get(c.getName());
	}

	public <T extends Fragment> void showFragment(Class<T> c) {
		FragmentTransaction transactionWelcome = getFragmentManager().beginTransaction();
		Fragment frag=fragments.get(c.getName());
		if (frag == null) {
			try {
				Class<?> cls = Class.forName(c.getName());
				frag = (Fragment) cls.newInstance();
				fragments.put(c.getName(), frag);
				transactionWelcome.add(R.id.fragment, frag);
			} catch (Exception e) {
				throw new RuntimeException("反射爆炸");
			}
		}
        hideFragment(transactionWelcome);
		transactionWelcome.show(frag);
        transactionWelcome.commit();
	}

    public void hideFragment(FragmentTransaction transaction) {
        for (Fragment f : fragments.values()) {
			transaction.hide(f);
		}
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
				//  exit();
				finish();
			} else {
                mDrawerLayout.openDrawer(GravityCompat.START);
			}
            return true;
		}
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
			} else {
                mDrawerLayout.openDrawer(GravityCompat.START);
			}
            return true;
		}
        return super.onKeyDown(keyCode, event);
	}

    public void doVibrate(long time) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
	}
}


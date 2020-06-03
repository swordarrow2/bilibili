package com.meng.biliv3.activity;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.reflect.*;
import com.meng.biliv3.*;
import com.meng.biliv3.update.*;
import com.meng.sjfmd.adapters.*;
import com.meng.sjfmd.customView.*;
import com.meng.sjfmd.enums.*;
import com.meng.sjfmd.fragment.*;
import com.meng.sjfmd.javabean.*;
import com.meng.sjfmd.libs.*;
import com.meng.sjfmd.tasks.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.java_websocket.client.*;

import android.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import com.meng.biliv3.R;


public class MainActivity extends AppCompatActivity {
	public static MainActivity instance;

	private LinearLayout mainLinearLayout;
    private DrawerLayout mDrawerLayout;
	private RelativeLayout rightDrawer;
    public ListView lvRecent;
	public HashMap<String,Fragment> fragments = new HashMap<>();
    public TextView tvMemory;
    public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
    public ArrayList<AccountInfo> loginAccounts;

    public AccountAdapter mainAccountAdapter;

    public String jsonPath ;
    public String mainDic = mainDic = Environment.getExternalStorageDirectory() + "/Pictures/grzx/";

    public static boolean onWifi = false;
	private RecentAdapter recentAdapter;

	public ExecutorService threadPool = Executors.newCachedThreadPool();
	public SanaeConnect sanaeConnect;

	public SJFSettings sjfSettings;
	public ColorManager colorManager;
	private boolean autoDrawerOperated=false;
	public static final String regAv = ".{0,}[Aa][Vv](\\d{1,})\\D{0,}";
	public static final String regBv = ".{0,}([Bb][Vv]1.{2}4.{1}1.{1}7.{2}).{0,}";
	public static final String regLive = ".{0,}live\\D{0,}(\\d{1,})\\D{0,}";
	public static final String regCv = ".{0,}[Cc][Vv](\\d{1,})";
	public static final String regUid = ".{0,}space\\D{0,}(\\d{1,})";
	public static final String regUid2 = ".{0,}UID\\D{0,}(\\d{1,})";

	private NavigationView navigationView;

	private int themeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 321);
        } else {
			init();
		}
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (!autoDrawerOperated && hasFocus && sjfSettings.getOpenDrawer() && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.openDrawer(GravityCompat.START);
			autoDrawerOperated = true;
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("缺失权限会使应用工作不正常");
                } else {
					init();
				}
            }
        }
    }

	public void init() {
		instance = this;
		colorManager = new ColorManager();
		colorManager.setColor(themeId);
		setContentView(R.layout.main_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		colorManager.addView(toolbar, ColorType.ToolBar);
		tvMemory = new TextView(this);
        rightDrawer = (RelativeLayout) findViewById(R.id.right_drawer);
		lvRecent = (ListView) findViewById(R.id.right_list);
		mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
		navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

		ColorStateList csl = getResources().getColorStateList(R.color.navigation_menu_item_color_blue);
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);

		jsonPath = getFilesDir() + "/account.json";
		ExceptionCatcher.getInstance().init(getApplicationContext());
		//  DataBaseHelper.init(getBaseContext());
		colorManager.addView(new TextView(this), ColorType.StatusBar);
		colorManager.addView(rightDrawer, ColorType.RightDrawer);
        setListener();
		File f = new File(jsonPath);
		if (!f.exists()) {
			saveConfig();
		}
		loginAccounts = GSON.fromJson(Tools.FileTool.readString(jsonPath), new TypeToken<ArrayList<AccountInfo>>(){}.getType());
		if (loginAccounts == null) {
			loginAccounts = new ArrayList<>();
		}
		mainAccountAdapter = new AccountAdapter(this);
		navigationView.addHeaderView(new UserInfoHeaderView(this));
		for (String s:new String[]{"group/","user/","bilibili/","cache/"}) {
			File ff = new File(mainDic + s);
			if (!ff.exists()) {
				ff.mkdirs();
			}
		}
		File f4 = new File(mainDic + ".nomedia");
		if (!f4.exists()) {
			try {
				f4.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//mDrawerList.addHeaderView(new UserInfoHeaderView(this));
		onWifi = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		threadPool.execute(new AutoSign());
		try {
			sanaeConnect = new SanaeConnect();
			sanaeConnect.addOnOpenAction(new WebSocketOnOpenAction(){

					@Override
					public void action(WebSocketClient wsc) {
						try {
							PackageInfo packageInfo = MainActivity.instance.getPackageManager().getPackageInfo(MainActivity.instance.getPackageName(), 0);
							wsc.send(GSON.toJson(new CheckNewBean(packageInfo.packageName, packageInfo.versionCode)));
						} catch (PackageManager.NameNotFoundException e) {
							MainActivity.instance.showToast(e.toString());
						}
					}
				});
			sanaeConnect.connect();
		} catch (Exception e) {
			showToast(e.toString());
		}
	}

	private void setListener() {
        recentAdapter = new RecentAdapter();
        lvRecent.setAdapter(recentAdapter);
		lvRecent.addHeaderView(tvMemory);
		threadPool.execute(new Runnable(){

				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
						runOnUiThread(new Runnable(){

								@Override
								public void run() {
									//ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
									//最大分配内存
									//	int memory = activityManager.getMemoryClass();
									//System.out.println("memory:" + memory);
									//最大分配内存获取方法2
									float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
									//当前分配的总内存
									float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
									//剩余内存
									//float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
									/*	System.out.println("maxMemory: " + maxMemory);
									 System.out.println("totalMemory: " + totalMemory);
									 System.out.println("freeMemory: " + freeMemory);*/
									tvMemory.setText("最大内存:" + maxMemory + "M\n当前分配:" + totalMemory + "M");
								}
							});
					}
				}
			});
		lvRecent.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
					String s=(String)p1.getAdapter().getItem(p3);
					showFragment(s);
					showToast(s);
				}
			});
	}
    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
			switch (item.getItemId()) {
					/*  case R.id.home:
					 showFragment(HomeFragment.class, IDType.egHome);
					 break;
					 case R.id.menus:
					 showFragment(MenusFragment.class, IDType.egMenu);
					 break;
					 case R.id.progress:
					 showFragment(ProgressFragment.class, IDType.egProgress);
					 break;*/
     			case R.id.input_id:
					final View seView = getLayoutInflater().inflate(R.layout.input_id_selecter, null);
					final EditText et = (EditText) seView.findViewById(R.id.input_id_selecterEditText_id);
					final RadioButton uid,av,live,cv;
					uid = (RadioButton) seView.findViewById(R.id.input_id_selecterRadioButton_uid);
					av = (RadioButton)seView.findViewById(R.id.input_id_selecterRadioButton_av);
					live = (RadioButton) seView.findViewById(R.id.input_id_selecterRadioButton_live);
					cv = (RadioButton) seView.findViewById(R.id.input_id_selecterRadioButton_cv);
					et.addTextChangedListener(new TextWatcher(){

							@Override
							public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
								// TODO: Implement this method
							}

							@Override
							public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
								// TODO: Implement this method
							}

							@Override
							public void afterTextChanged(Editable p1) {
								String typeReg=getIdType(p1.toString());
								if (typeReg == null) {
									return;
								}
								switch (typeReg) {
									case regAv:
									case regBv:
										av.setChecked(true);
										break;
									case regCv:
										cv.setChecked(true);
										break;
									case regLive:
										live.setChecked(true);
										break;
									case regUid:
									case regUid2:
										uid.setChecked(true);
										break;
								}
							}
						});
					new AlertDialog.Builder(MainActivity.this)
						.setTitle("输入ID")
						.setView(seView)
						.setNegativeButton("取消", null)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String content = et.getText().toString();
								String typeReg=getIdType(content);
								long conId=getId(content, typeReg);
								if (uid.isChecked()) {
									showFragment(UidFragment.class, IDType.UID , conId);
								} else if (av.isChecked()) {
									showFragment(AvFragment.class, IDType.Video , conId);
								} else if (live.isChecked()) {
									showFragment(LiveFragment.class, IDType.Live , conId);
								} else if (cv.isChecked()) {
									showFragment(CvFragment.class, IDType.Article , conId);
								}
							}
						}).show();
					break;
				case R.id.accounts:
					showFragment(ManagerFragment.class, IDType.Accounts);
					break;
				case R.id.medal:
					String items[] = new String[MainActivity.instance.loginAccounts.size()];
					for (int i=0,j=MainActivity.instance.loginAccounts.size();i < j;++i) {
						items[i] = MainActivity.instance.loginAccounts.get(i).name;
					}
					new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.ic_launcher).setTitle("选择账号").setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								showFragment(MedalFragment.class, IDType.Medal, MainActivity.instance.loginAccounts.get(which).uid);
							}
						}).show();
					break;
				case R.id.avbv:
					showFragment(AvBvConvertFragment.class, IDType.AVBV);
					break;
				case R.id.settings:
					showFragment(SettingsFragment.class, IDType.Settings);
					break;
				case R.id.dynamic:
					showFragment(DynamicFragment.class, IDType.Dynamic);
					break;
				case R.id.exit:
					if (sjfSettings.getExit0()) {
						System.exit(0);
					} else {
						finish();
					}
					break;
			}
            return true;
        }
    };
	@Override
	public void setTheme(int resid) {
		themeId = resid;
		sjfSettings = new SJFSettings(this);
		NetworkCacher.setJsonCacheMode(NetworkCacher.Mode.valueOf(sjfSettings.getJsonCacheMode()));
		NetworkCacher.setPicCacheMode(NetworkCacher.Mode.valueOf(sjfSettings.getPicCacheMode()));
//		switch (sjfSettings.getTheme()) {
//            case "Holo":
//				super.setTheme(themeId = R.style.AppThemeHolo);
//				break;
//			case "Holo Wallpaper":
//				super.setTheme(themeId = R.style.AppThemeHoloWallpaper);
//				break;
//            case "MD":
//				super.setTheme(themeId = R.style.AppThemeLight);
//				break;
//			case "MD dark":
//				super.setTheme(themeId = R.style.AppThemeDark);
//				break;
//			case "Holo light":
//				super.setTheme(themeId = R.style.AppThemeHoloL);
//				break;
//            default:
//				super.setTheme(themeId = R.style.AppThemeHolo);
//				break;
//		}
		super.setTheme(themeId);
	}


	private String getIdType(String s) {
		if (s.matches(regAv)) {
			return regAv;
		}
		if (s.matches(regBv)) {
			return regBv;
		}
		if (s.matches(regCv)) {
			return regCv;
		}
		if (s.matches(regLive)) {
			return regLive;
		}
		if (s.matches(regUid)) {
			return regUid;
		}
		if (s.matches(regUid2)) {
			return regUid2;
		}
		return null;
	}

	private long getId(String link, String regex) {
		if (regex == null) {
			if (!link.matches("\\d{0,}")) {
				return -1;
			}
			return Long.parseLong(link);
		}
		Matcher m2 = Pattern.compile(regex).matcher(link);  
		if (m2.find()) {
			if (regex.equals(regBv)) {
				return AvBvConverter.getInstance().decode(m2.group(1));
			} else {
				return Long.parseLong(m2.group(1));
			} 
		}
		return -1;
	}

	public void showFragment(String id) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment frag = fragments.get(id);
		if (frag == null) 	{
			throw new RuntimeException("获取不存在的碎片");
		}
		hideFragment();
		transaction.show(frag);
		transaction.commit();
	}

	public <T extends Fragment> void showFragment(Class<T> c, IDType type) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment frag = fragments.get(type.toString());
		if (frag == null) {
			try {
				Class<?> cls = Class.forName(c.getName());
				frag = (Fragment) cls.newInstance();
				fragments.put(type.toString(), frag);
				recentAdapter.add(type, 0, type.toString());
				transaction.add(R.id.fragment, frag);	
			} catch (Exception e) {
				throw new RuntimeException("反射爆炸:" + e.toString());
			}
		}
		hideFragment();
		transaction.show(frag);
        transaction.commit();
	}

	public <T extends Fragment> void showFragment(Class<T> c, IDType type, long id) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment frag = fragments.get(type.toString() + id);
		if (frag == null) {
			try {
				Class<?> cls = Class.forName(c.getName());
				Constructor con = cls.getConstructor(IDType.class, long.class);
				frag = (Fragment) con.newInstance(type, id);
				fragments.put(type.toString() + id, frag);
				recentAdapter.add(type , id, type.toString() + id);
				transaction.add(R.id.fragment, frag);	
			} catch (Exception e) {
				throw new RuntimeException("反射爆炸:" + e.toString());
			}
		}
		hideFragment();
		transaction.show(frag);
        transaction.commit();
	}

	public void renameFragment(String origin, String newName) {
		Fragment f=fragments.get(origin);
		fragments.put(newName, f);
		recentAdapter.rename(origin, newName);
	}

    public void hideFragment() {
		FragmentTransaction ft=getFragmentManager().beginTransaction();
        for (Fragment f : fragments.values()) {
			ft.hide(f);
        }
		ft.commit();
    }

	public void removeFragment(String id) {
		if (!fragments.containsKey(id)) {
			recentAdapter.remove(id);
			return;
		}
		Fragment f = fragments.get(id);
		Iterator<Map.Entry<String,Fragment>> iterator = fragments.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String,Fragment> entry = iterator.next();
			if (entry.getValue() == f) {
				recentAdapter.remove(entry.getKey());
				iterator.remove();
			}
		}
		getFragmentManager().beginTransaction().remove(f).commit();
		fragments.remove(id);
	}

	public String getCookie(long bid) {
        for (AccountInfo l : loginAccounts) {
            if (bid == l.uid) {
                return l.cookie;
            }
        }
        return null;
    }

	public AccountInfo getAccount(long id) {
		for (AccountInfo ai:loginAccounts) {
			if (ai.uid == id) {
				return ai;
			}
		}
		return null;
	}

	public AccountInfo getAccount(String name) {
		for (AccountInfo ai:loginAccounts) {
			if (ai.name.equals(name)) {
				return ai;
			}
		}
		return null;
	}

	public int getAccountIndex(long uid) {
		for (int i=0;i < loginAccounts.size();++i) {
			if (loginAccounts.get(i).uid == uid) {
				return i;
			}
		}
		return -1;
	}

	/*public int getAccountIndex(String name) {
	 for (int i=0;i < loginAccounts.size();++i) {
	 if (loginAccounts.get(i).name.equals(name)) {
	 return i;
	 }
	 }
	 return -1;
	 }
	 */
    public void saveConfig() {
        try {
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File(jsonPath);
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(GSON.toJson(loginAccounts));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		saveConfig2();
    }

	public void saveConfig2() {
        try {
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File(Environment.getExternalStorageDirectory() + "/account.json");
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(GSON.toJson(loginAccounts));
            writer.flush();
            fos.close();
		} catch (IOException e) {
            e.printStackTrace();
		}
	}

	public void showToast(final String msgAbbr, final String msgOrigin) {
        runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Snackbar.make(mainLinearLayout, msgAbbr, 5000)
						.setAction("查看全文", msgOrigin.trim().length() == 0 ?null: new View.OnClickListener(){

									   @Override
									   public void onClick(View v) {
										   new AlertDialog.Builder(MainActivity.this)
											   .setIcon(R.drawable.ic_launcher)
											   .setTitle("全文")
											   .setMessage(msgOrigin)
											   .setPositiveButton("确定", null).show();
									   }
								   }).show();
				}
			});
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Snackbar.make(mainLinearLayout, msg, 5000)
						.setAction("查看全文", getLines(msg) < 2 && msg.length() < 40 ?null: new View.OnClickListener(){

									   @Override
									   public void onClick(View v) {
										   new AlertDialog.Builder(MainActivity.this)
											   .setIcon(R.drawable.ic_launcher)
											   .setTitle("全文")
											   .setMessage(msg)
											   .setPositiveButton("确定", null).show();
									   }
								   }).show();
				}
			});
    }

	public int getLines(String s) {
		int l=0;
		for (char c:s.toCharArray()) {
			if (c == '\n') {
				++l;
			}
		}
		return l;
	}

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

package com.meng.biliv3.activity;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import com.meng.biliv3.*;
import com.meng.biliv3.adapters.*;
import com.meng.biliv3.fragment.*;
import com.meng.biliv3.javaBean.*;
import com.meng.biliv3.libs.*;
import com.meng.biliv3.materialDesign.*;
import com.meng.biliv3.update.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.java_websocket.client.*;

import com.meng.biliv3.R;


public class MainActivity extends Activity {

    public static MainActivity instance;
    private DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    private RelativeLayout rightDrawer;
    public ListView lvRecent;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

	public HashMap<String,Fragment> fragments=new HashMap<>();
    public TextView tvMemory;
    public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
    public Gson gson = new Gson();
    public ArrayList<AccountInfo> loginAccounts;

    public MainListAdapter mainAccountAdapter;

    public String jsonPath;
    public String mainDic = "";

    public static boolean onWifi = false;
	private RecentAdapter recentAdapter;

	public static final String AccountManager = "管理账号";
	public static final String Settings = "设置";

	public ExecutorService threadPool = Executors.newCachedThreadPool();

	public SanaeConnect sanaeConnect;
	public SJFSettings sjfSettings;
	public ColorManager colorManager;

	private int themeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instance = this;
		ExceptionCatcher.getInstance().init(getApplicationContext());
		//  DataBaseHelper.init(getBaseContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 321);
        }
        findViews();
		colorManager = new ColorManager(themeId);
		colorManager.doRun(this);
		mDrawerList.setBackgroundColor(colorManager.getColorBackground());
		rightDrawer.setBackgroundColor(colorManager.getColorBackground());

        setListener();
        jsonPath = getApplicationContext().getFilesDir() + "/account.json";
        File f = new File(jsonPath);
        if (!f.exists()) {
            saveConfig();
		}
		Type tt=new TypeToken<ArrayList<AccountInfo>>(){}.getType();
		loginAccounts = gson.fromJson(Tools.FileTool.readString(jsonPath), tt);
		if (loginAccounts == null) {
			loginAccounts = new ArrayList<>();
		}
        mainAccountAdapter = new MainListAdapter(this);
        if (sjfSettings.getOpenDrawer()) {
            mDrawerLayout.openDrawer(mDrawerList);
        } else {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        mainDic = Environment.getExternalStorageDirectory() + "/Pictures/grzx/";

		for (String s:new String[]{"group/","user/","bilibili/"}) {
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
		try {
			sanaeConnect = new SanaeConnect();
			sanaeConnect.addOnOpenAction(new WebSocketOnOpenAction(){

					@Override
					public int useTimes() {
						return 1;
					}

					@Override
					public void action(WebSocketClient wsc) {
						try {
							PackageInfo packageInfo = MainActivity.instance.getPackageManager().getPackageInfo(MainActivity.instance.getPackageName(), 0);
							wsc.send(new Gson().toJson(new CheckNewBean(packageInfo.packageName, packageInfo.versionCode)));
						} catch (PackageManager.NameNotFoundException e) {
							MainActivity.instance.showToast(e.toString());
						}
					}
				});
			sanaeConnect.connect();
		} catch (Exception e) {
			showToast(e.toString());
		}

		mDrawerList.addHeaderView(new UserInfoHeaderView(this));
        onWifi = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		threadPool.execute(new Runnable(){

				@Override
				public void run() {
					StringBuilder sb= new StringBuilder();
					for (int i=0;i < loginAccounts.size();++i) {
						AccountInfo ai=loginAccounts.get(i);
						if (!Tools.Time.getDate().equals(Tools.Time.getDate(ai.lastSign))) {
							ai.setSigned(false);
						}
						if (!ai.isSigned() && !ai.isCookieExceed()) {
							int rc = Tools.BilibiliTool.sendLiveSign(ai.cookie);
							switch (rc) {
								case -101:
									ai.setCookieExceed(true);
									sb.append("\n").append(ai.name).append(":cookie过期");
									break;
								case 0:
									ai.lastSign = System.currentTimeMillis();
									sb.append("\n").append(ai.name).append(":成功");
									ai.setSigned(true);
									break;
								case 1011040:
									sb.append("\n").append(ai.name).append(":今日已签到");
									ai.setSigned(true);
									break;
							}
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {}
						} else if (ai.isSigned()) {
							sb.append("\n").append(ai.name).append(":今日已签到");
						} else if (ai.isCookieExceed()) {
							sb.append("\n").append(ai.name).append(":cookie过期");
						} else {
							sb.append("\n").append(ai.name).append(":未知错误");
						}
					}
					showToast("自动签到:" + sb.toString());
					saveConfig();
				}
			});
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("缺失权限会使应用工作不正常");
                } else {
					//      showFragment(NaiFragment.class);
                }
            }
        }
    }

    private void setListener() {
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.open, R.string.close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerList.setAdapter(
			new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{
										 "输入ID", "管理账号","AVBV转换", "设置", "退出"
									 }));
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
        mDrawerList.setOnItemClickListener(itemClickListener);
        lvRecent.setOnItemClickListener(itemClickListener);
	}

	@Override
	public void setTheme(int resid) {
		themeId = resid;
		sjfSettings = new SJFSettings(this);
		switch (sjfSettings.getTheme()) {
            case "Holo":
				super.setTheme(themeId = R.style.AppThemeHolo);
				break;
            case "MD":
				super.setTheme(themeId = R.style.AppThemeLight);
				break;
			case "MD dark":
				super.setTheme(themeId = R.style.AppThemeDark);
				break;
			case "Holo light":
				super.setTheme(themeId = R.style.AppThemeHoloL);
				break;
            default:
				super.setTheme(themeId = R.style.AppThemeHolo);
				break;
		}
	}

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (view instanceof TextView) {
                switch (((TextView) view).getText().toString()) {
					case "输入ID":
						final View seView = getLayoutInflater().inflate(R.layout.input_id_selecter, null);
						final EditText et = (EditText) seView.findViewById(R.id.input_id_selecterEditText_id);
						new AlertDialog.Builder(MainActivity.this)
							.setTitle("输入ID")
							.setView(seView)
							.setNegativeButton("取消", null)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String content = et.getText().toString();
									RadioButton uid,av,live,cv;
									uid = (RadioButton) seView.findViewById(R.id.input_id_selecterRadioButton_uid);
									av = (RadioButton)seView.findViewById(R.id.input_id_selecterRadioButton_av);
									live = (RadioButton) seView.findViewById(R.id.input_id_selecterRadioButton_live);
									cv = (RadioButton) seView.findViewById(R.id.input_id_selecterRadioButton_cv);
									if (uid.isChecked()) {
										showFragment(UidFragment.class, BaseIdFragment.typeUID , getId(content));
									} else if (av.isChecked()) {
										if (content.startsWith("BV")) {
											showFragment(AvFragment.class, BaseIdFragment.typeAv , AvBvConverter.decode(content));
										} else {
											showFragment(AvFragment.class, BaseIdFragment.typeAv , getId(content));
										}
									} else if (live.isChecked()) {
										showFragment(LiveFragment.class, BaseIdFragment.typeLive , getId(content));
									} else if (cv.isChecked()) {
										showFragment(CvFragment.class, BaseIdFragment.typeCv , getId(content));
									}
								}
							}).show();
						break;
					case "管理账号":
						showFragment(ManagerFragment.class, AccountManager);
						break;
					case "AVBV转换":
						showFragment(AvBvConvertFragment.class, "AVBV转换");
						break;
                    case "设置":
                        showFragment(SettingsFragment.class, Settings);
                        break;
                    case "退出":
                        if (sjfSettings.getExit0()) {
                            System.exit(0);
                        } else {
                            finish();
                        }
                        break;
                }
            }
            mDrawerToggle.syncState();
            mDrawerLayout.closeDrawer(mDrawerList);
            mDrawerLayout.closeDrawer(rightDrawer);
        }
    };

	private int getId(String s) {
		String reg = "\\D{0,}(\\d{3,})\\D{0,}";
		Pattern p2 = Pattern.compile(reg);  
		Matcher m2 = p2.matcher(s);  
		int historyHighestLevel = -1;
		if (m2.find()) {  
			historyHighestLevel = Integer.parseInt(m2.group(1));
		}
		return historyHighestLevel;
	}

    private void findViews() {
        tvMemory = new TextView(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        rightDrawer = (RelativeLayout) findViewById(R.id.right_drawer);
        lvRecent = (ListView) findViewById(R.id.right_list);
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

	public <T extends Fragment> void showFragment(Class<T> c, String type) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment frag = fragments.get(type);
		if (frag == null) {
			try {
				Class<?> cls = Class.forName(c.getName());
				frag = (Fragment) cls.newInstance();
				fragments.put(type, frag);
				recentAdapter.add(type, 0, type);
				transaction.add(R.id.main_activityLinearLayout, frag);	
			} catch (Exception e) {
				throw new RuntimeException("反射爆炸:" + e.toString());
			}
		}
		hideFragment();
		transaction.show(frag);
        transaction.commit();
	}

	public <T extends Fragment> void showFragment(Class<T> c, String type, long id) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment frag = fragments.get(type + id);
		if (frag == null) {
			try {
				Class<?> cls = Class.forName(c.getName());
				Constructor con = cls.getConstructor(String.class, long.class);
				frag = (Fragment) con.newInstance(type, id);
				fragments.put(type + id, frag);
				recentAdapter.add(type , id, type + id);
				transaction.add(R.id.main_activityLinearLayout, frag);	
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
			//throw new RuntimeException("no such key:"+id);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	/*  public void doVibrate(long time) {
	 Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	 if (vibrator != null) {
	 vibrator.vibrate(time);
	 }
	 }*/

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
            writer.write(gson.toJson(loginAccounts));
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
            writer.write(gson.toJson(loginAccounts));
            writer.flush();
            fos.close();
		} catch (IOException e) {
            e.printStackTrace();
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
				}
			});
    }
}

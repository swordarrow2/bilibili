package com.meng.bilibilihelper.activity;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.bilibilihelper.*;
import com.meng.bilibilihelper.adapters.*;
import com.meng.bilibilihelper.fragment.*;
import com.meng.bilibilihelper.fragment.live.*;
import com.meng.bilibilihelper.fragment.main.*;
import com.meng.bilibilihelper.javaBean.*;
import com.meng.bilibilihelper.libAndHelper.*;
import com.meng.bilibilihelper.materialDesign.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.jsoup.*;

import com.meng.bilibilihelper.R;


public class MainActivity extends android.support.v7.app.AppCompatActivity {

    public static MainActivity instence;
    private DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    private RelativeLayout rightDrawer;
    public ListView lvRecent;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

	public HashMap<String,Fragment> fragments=new HashMap<>();
    public TextView rightText;
    public MengInfoHeaderView infoHeaderLeft;
    public MengLiveControl mengLiveControl;

    public final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0";
    public Gson gson = new Gson();
    public LoginInfo loginInfo;

    public MainListAdapter loginInfoPeopleAdapter;
    public ArrayList<String> arrayList;

    public String jsonPath;
    public String mainDic = "";

    public static boolean onWifi = false;
	private RecentAdapter recentAdapter;

	public static final String UID="uid";
	public static final String AV="av";
	public static final String Live="lv";

	public ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
		//  ExceptionCatcher.getInstance().init(getApplicationContext());
        SharedPreferenceHelper.init(getApplicationContext(), "settings");
        DataBaseHelper.init(getBaseContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 321);
        }
        infoHeaderLeft = new MengInfoHeaderView(this);

        mengLiveControl = new MengLiveControl(this);
        findViews();
        //   setActionBar();
        setListener();
        jsonPath = getApplicationContext().getFilesDir() + "/info.json";
        File f = new File(jsonPath);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
            }
            loginInfo = new LoginInfo();
            saveConfig();
		}
		//   methodsManager.fileCopy(jsonPath, Environment.getExternalStorageDirectory() + "/fafafa.json");
        arrayList = new ArrayList<>();
        try {
            loginInfo = gson.fromJson(readFileToString(), LoginInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
		}
		saveConfig2();
        if (loginInfo != null) {
            for (LoginInfoPeople loginInfoPeople : loginInfo.loginInfoPeople) {
                arrayList.add(loginInfoPeople.personInfo.data.name);
            }
        }
        loginInfoPeopleAdapter = new MainListAdapter(this, loginInfo.loginInfoPeople);
        if (SharedPreferenceHelper.getBoolean("opendraw", true)) {
            mDrawerLayout.openDrawer(mDrawerList);
        } else {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        mainDic = Environment.getExternalStorageDirectory() + "/Pictures/grzx/";
        File ff = new File(mainDic + "group/");
        if (!ff.exists()) {
            ff.mkdirs();
        }
        File f2 = new File(mainDic + "user/");
        if (!f2.exists()) {
            f2.mkdirs();
        }
        File f3 = new File(mainDic + "bilibili/");
        if (!f3.exists()) {
            f3.mkdirs();
        }
        File f4 = new File(mainDic + ".nomedia");
        if (!f4.exists()) {
            try {
                f4.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        onWifi = wifiNetworkInfo.isConnected();
        final String mainUID = SharedPreferenceHelper.getValue("mainAccount", "");
        if (!mainUID.equals("")) {
            mDrawerList.addHeaderView(infoHeaderLeft);
            mDrawerList.addHeaderView(mengLiveControl);
            File imf = new File(mainDic + "bilibili/" + mainUID + ".jpg");
            if (imf.exists()) {
                Bitmap b = BitmapFactory.decodeFile(imf.getAbsolutePath());
                MainActivity.instence.infoHeaderLeft.setImage(b);
            } else {
                MainActivity.instence.getFragment("人员信息", PersonInfoFragment.class).threadPool.execute(new DownloadImageRunnable(this, infoHeaderLeft.getImageView(), mainUID, DownloadImageRunnable.BilibiliUser));
			}
            new Thread(new Runnable() {
					@Override
					public void run() {
						final BilibiliUserInfo info = gson.fromJson(getSourceCode("https://api.bilibili.com/x/space/acc/info?mid=" + mainUID + "&jsonp=jsonp"), BilibiliUserInfo.class);
						UserSpaceToLive sjb = gson.fromJson(MainActivity.instence.getSourceCode("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + info.data.mid), UserSpaceToLive.class);
						String json = MainActivity.instence.getSourceCode("https://api.live.bilibili.com/live_user/v1/UserInfo/get_anchor_in_room?roomid=" + sjb.data.roomid);
						JsonParser parser = new JsonParser();
						JsonObject obj = parser.parse(json).getAsJsonObject();
						final JsonObject obj2 = obj.get("data").getAsJsonObject().get("level").getAsJsonObject().get("master_level").getAsJsonObject();
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									JsonArray ja = obj2.get("next").getAsJsonArray();
									infoHeaderLeft.setTitle(info.data.name);
									infoHeaderLeft.setSummry("主站 Lv." + info.data.level + "\n主播 Lv." + obj2.get("level").getAsInt() + "\n" + obj2.get("anchor_score").getAsInt() + "/" + ja.get(1));
								}
							});
					}
				}).start();
        }
		showFragment("Main");
    }

    private void setActionBar() {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
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

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{
															"首页(大概)","输入ID", "挂机", "信息", "管理账号", "签到", "设置", "退出"
														}));
		recentAdapter = new RecentAdapter();
        lvRecent.setAdapter(recentAdapter);
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

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (view instanceof TextView) {
                switch (((TextView) view).getText().toString()) {
                    case "首页(大概)":
                        showFragment("Main");
                        break;
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
									RadioButton uid,av,live;
									uid = (RadioButton) seView.findViewById(R.id.input_id_selecterRadioButton_uid);
									av = (RadioButton)seView.findViewById(R.id.input_id_selecterRadioButton_av);
									live = (RadioButton) seView.findViewById(R.id.input_id_selecterRadioButton_live);
									if (uid.isChecked()) {
										newIDFragment(UidFragment.class, UID , getId(content));
									} else if (av.isChecked()) {
										newIDFragment(AvFragment.class, AV , getId(content));
									} else if (live.isChecked()) {
										newIDFragment(LiveFragment.class, Live , getId(content));
									}
								}
							}).show();
						break;
                    case "管理账号":
                        showFragment("管理账号");
                        break;
					case "信息":
                        showFragment("人员信息");
                        break;
                    case "挂机":
                        showFragment("挂机");
                        break;
					case "签到":
                        showFragment("签到");
                        break;
                    case "设置":
                        showFragment("设置");
                        break;
                    case "退出":
                        if (SharedPreferenceHelper.getBoolean("exit", false)) {
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
		String reg = "\\D{0,}(\\d{1,})\\D{0,}";
		Pattern p2 = Pattern.compile(reg);  
		Matcher m2 = p2.matcher(s);  
		int historyHighestLevel = -1;
		if (m2.find()) {  
			historyHighestLevel = Integer.parseInt(m2.group(1));
		}
		return historyHighestLevel;
	}

    private void findViews() {
        rightText = (TextView) findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        rightDrawer = (RelativeLayout) findViewById(R.id.right_drawer);
        lvRecent = (ListView) findViewById(R.id.right_list);
    }

	public <T extends Fragment> T getFragment(String id, Class<T> c) {
		return (T)fragments.get(id);
	}

	public void showFragment(String id) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment frag=fragments.get(id);
		if (frag == null) {
			switch (id) {
				case "Main":
					frag = new MainFragment();
					fragments.put(id, frag);
					break;
				case "管理账号":
					frag = new ManagerFragment();
					fragments.put(id, frag);
					break;
				case "人员信息":
					frag = new PersonInfoFragment();
					fragments.put(id, frag);
					break;
				case "挂机":
					frag = new GuaJiFragment();
					fragments.put(id, frag);
					break;
				case "签到":
					frag = new SignFragment();
					fragments.put(id, frag);
					break;
				case "设置":
					frag = new SettingsFragment();
					fragments.put(id, frag);
					break;
				default:
					throw new RuntimeException("获取不存在的碎片");
			}
			transaction.add(R.id.main_activityLinearLayout, frag);
		}
        hideFragment();
		transaction.show(frag);
        transaction.commit();
	}

	public <T extends Fragment> void newIDFragment(Class<T> c, String type, int id) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment frag=null;
		try {
			Class<?> cls = Class.forName(c.getName());
			Constructor con=cls.getConstructor(int.class);
			frag = (Fragment) con.newInstance(id);
			fragments.put(type + id, frag);
			recentAdapter.add(type + id);
			transaction.add(R.id.main_activityLinearLayout, frag);	
		} catch (Exception e) {
			throw new RuntimeException("反射爆炸:" + e.toString());
		}
		hideFragment();
		transaction.show(frag);
        transaction.commit();
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
			throw new RuntimeException("no such key");
		}
		getFragmentManager().beginTransaction().remove(fragments.get(id)).commit();
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

    public void doVibrate(long time) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(time);
        }
    }

    public String getSourceCode(String url) {
        return getSourceCode(url, null, null);
    }

    public String getSourceCode(String url, String cookie) {
        return getSourceCode(url, cookie, null);
    }

    public String getSourceCode(String url, String cookie, String refer) {
        Connection.Response response = null;
        Connection connection;
        try {
            connection = Jsoup.connect(url);
            if (cookie != null) {
                connection.cookies(cookieToMap(cookie));
            }
            if (refer != null) {
                connection.referrer(refer);
            }
            connection.userAgent(userAgent);
            connection.ignoreContentType(true).method(Connection.Method.GET);
            response = connection.execute();
            if (response.statusCode() != 200) {
                showToast(String.valueOf(response.statusCode()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            return response.body();
        }
        return "";
    }

    public Map<String, String> cookieToMap(String value) {
        Map<String, String> map = new HashMap<String, String>();
        String values[] = value.split("; ");
        for (String val : values) {
            String vals[] = val.split("=");
            if (vals.length == 2) {
                map.put(vals[0], vals[1]);
            } else if (vals.length == 1) {
                map.put(vals[0], "");
            }
        }
        return map;
    }

    public String getCookie(long bid) {
        for (LoginInfoPeople l : loginInfo.loginInfoPeople) {
            if (bid == l.personInfo.data.mid) {
                return l.cookie;
            }
        }
        return null;
    }

    public String readFileToString() throws IOException, UnsupportedEncodingException {
        File file = new File(jsonPath);
        if (!file.exists()) {
            file.createNewFile();
        }
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
        return new String(filecontent, "UTF-8");
    }

    public void saveConfig() {
        try {
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File(jsonPath);
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(gson.toJson(loginInfo));
            writer.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void saveConfig2() {
        try {
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            File file = new File(Environment.getExternalStorageDirectory() + "/info.json");
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(gson.toJson(loginInfo));
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


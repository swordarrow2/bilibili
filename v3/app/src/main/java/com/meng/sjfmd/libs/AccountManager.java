package com.meng.sjfmd.libs;

import android.os.*;
import com.google.gson.reflect.*;
import com.meng.sjfmd.javabean.*;
import com.meng.sjfmd.libs.*;
import java.io.*;
import java.util.*;
import android.content.*;

public class AccountManager {
	private ArrayList<AccountInfo> loginAccounts;
	private String jsonPath;

	public AccountManager(Context c) {
		jsonPath = c.getFilesDir() + "/account.json";
		File f = new File(jsonPath);
		if (!f.exists()) {
			saveConfig();
		}
		loginAccounts = GSON.fromJson(FileTool.readString(jsonPath), new TypeToken<ArrayList<AccountInfo>>(){}.getType());
		if (loginAccounts == null) {
			loginAccounts = new ArrayList<>();
		}
	}

	public boolean contains(long uid) {
		for (AccountInfo a:loginAccounts) {
			if (a.uid == uid) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(AccountInfo aci) {
		return contains(aci.uid);
	}

	public void add(AccountInfo aci) {
		loginAccounts.add(aci);
		saveConfig();
	}

	public void remove(int index) {
		loginAccounts.remove(index);
		saveConfig();
	}

	public List<AccountInfo> iterate() {
		return loginAccounts;
	}

	public int size() {
		return loginAccounts.size();
	}

	public boolean moveUp(int index) {
		if (index == 0) {
			return false;
		}
		loginAccounts.add(index - 1, loginAccounts.remove(index)); 
		saveConfig();
		return true;
	}

	public boolean moveDown(int index) {
		if (index == loginAccounts.size() - 1) {
			return false;
		}
		loginAccounts.add(index + 1, loginAccounts.remove(index));
		saveConfig();
		return true;
	}

	public String getCookie(long bid) {
        for (AccountInfo l : loginAccounts) {
            if (bid == l.uid) {
                return l.cookie;
            }
        }
        return null;
    }

	public AccountInfo get(int index) {
		return loginAccounts.get(index);
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
}

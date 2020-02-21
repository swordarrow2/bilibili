import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class Main {
	public static void main(String[] args) {

		ArrayList<File> fs=new ArrayList<>();
		try {
			getFiles("/storage/emulated/0/AppProjects/bilibili/v2/app/src/main/", fs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (File f:fs) {
			String s=readString(f);
			/*
			 getFragment(PersonInfoFragment.class)
			*/
			s = s.replace("getFragment(SettingsFragment.class)", "getFragment(\"设置\",SettingsFragment.class)");
			//System.out.println(s);
			saveString(f, s);
		}
		System.out.println("ok");
	}

	public static void getFiles(String path, ArrayList<File> list) throws Exception {
        //目标集合fileList
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileIndex : files) {
                if (fileIndex.isDirectory()) {
                    getFiles(fileIndex.getPath(), list);
                } else {
					if (fileIndex.getName().endsWith(".java") || fileIndex.getName().endsWith(".xml")) {
						list.add(fileIndex);
					}
                }
            }
        }
    }

	public static void saveString(File f, String str) {
		try {
			FileOutputStream fos = new FileOutputStream(f);
			OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			writer.write(str);
			writer.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public static String readString(File f) {
		String s = null;
		try {      
			long filelength = f.length();
			byte[] filecontent = new byte[(int) filelength];
			FileInputStream in = new FileInputStream(f);
			in.read(filecontent);
			in.close();
			s = new String(filecontent, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;	
	}
}

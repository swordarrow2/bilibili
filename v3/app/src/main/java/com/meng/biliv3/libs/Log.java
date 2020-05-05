package com.meng.biliv3.libs;
import android.os.*;
import java.io.*;
import org.jsoup.*;
import com.meng.biliv3.activity.*;

public class Log {

	public static void network(Connection.Method method, String link, String result, Object... args) {
		File f=new File(Environment.getExternalStorageDirectory() + "/sjfLogNetwork.log");
		try {  
			BufferedWriter writer  = new BufferedWriter(new FileWriter(f, true));  
			writer.write(Tools.Time.getTime());
			writer.write(method == Connection.Method.GET ?" get\n": " post\n");
			writer.write(link);
			if (args != null && args.length > 0) {
				writer.write("\nargs:\n");
				for (int i=0;i < args.length;i += 2) {
					writer.write(String.valueOf(args[i]));
					writer.write("=");
					writer.write(String.valueOf(args[i + 1]));
					writer.write("\n");
				}
			}
			writer.write("\nresult:\n");
			writer.write(result);
			writer.write("\n————————————————————————————————\n");
			writer.flush();  
			writer.close();  
		} catch (Exception e) {  
			MainActivity.instance.showToast(e.toString());
		} 
	}
}

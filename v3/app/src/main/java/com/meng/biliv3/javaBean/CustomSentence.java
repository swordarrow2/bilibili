package com.meng.biliv3.javaBean;

import com.meng.biliv3.activity.*;
import java.util.*;

public class CustomSentence {
	public ArrayList<String> sent = new ArrayList<String>();

	@Override
	public String toString() {
		return MainActivity.instance.gson.toJson(this);
	}
}

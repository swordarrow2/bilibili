package com.meng.biliv3.javabean;

import com.meng.biliv3.libs.*;
import java.util.*;

public class CustomSentence {
	public ArrayList<String> sent = new ArrayList<String>();

	@Override
	public String toString() {
		return GSON.toJson(this);
	}
}

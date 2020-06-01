package com.meng.sjfmd.javabean;

import com.meng.sjfmd.libs.*;
import java.util.*;

public class CustomSentence {
	public ArrayList<String> sent = new ArrayList<String>();

	@Override
	public String toString() {
		return GSON.toJson(this);
	}
}

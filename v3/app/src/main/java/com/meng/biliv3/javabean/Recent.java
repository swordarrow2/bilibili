package com.meng.biliv3.javabean;
import com.meng.biliv3.enums.*;

public class Recent {
	public IDType type;
	public long id;
	public String name;

	public Recent(IDType type, long id, String name) {
		this.type = type;
		this.id = id;
		this.name = name;
	}
}

package com.meng.biliv3.javaBean;

public class AccountInfo {
	public String cookie;
	public String name;
	public long uid;
	public long lastSign;
	public int flag;

	public boolean isSigned() {
		return (flag & 0b1) != 0;
	}

	public void setSigned(boolean b) {
		if (b) {
			flag |= 0b1;
		} else {
			flag &= ~0b1;
		}
	}

	public boolean isCookieExceed() {
		return (flag & 0b10) != 0;
	}

	public void setCookieExceed(boolean b) {
		if (b) {
			flag |= 0b10;
		} else {
			flag &= ~0b10;
		}
	}
}

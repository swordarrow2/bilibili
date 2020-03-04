package com.meng.biliv3.update;
import org.java_websocket.client.*;

public interface WebSocketOnOpenAction {
	public int useTimes();
	public void action(WebSocketClient wsc);
}

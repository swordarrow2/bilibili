package com.meng.biliv3.update;

public interface WebSocketMessageAction {
	public int useTimes();
	public int forOpCode();
	public BotDataPack onMessage(BotDataPack rec);
}

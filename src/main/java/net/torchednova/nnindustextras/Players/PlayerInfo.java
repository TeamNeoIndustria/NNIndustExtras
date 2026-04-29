package net.torchednova.nnindustextras.Players;

import java.util.ArrayList;
import java.util.List;

public class PlayerInfo {
	public PlayerInfo(String uuid, String name)
	{
		this.name = name;
		this.uuid = uuid;
		lastSeen = System.currentTimeMillis() / 100;
		otherStores = new ArrayList<>();
	}

	public String uuid;
	public String name;
	public long lastSeen;
	public int ownStore = -1;
	public List<Integer> otherStores;
}

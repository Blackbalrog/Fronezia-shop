package fr.frozenia.shop.managers;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerManager
{
	
	private static HashMap<Player, PlayerManager> manager = new HashMap<>();
	private HashMap<Player, String> inventorys = new HashMap<Player, String>();
	
	private static Player PLAYER;
	private String key;
	
	public PlayerManager(Player player)
	{
		if (!manager.containsKey(player)) manager.put(player, this);
		PLAYER = player;
	}
	
	public void setMenuPrevious(String menu)
	{
		this.inventorys.put(PLAYER, menu);
	}
	
	public String getMenuPrevious()
	{
		return "Menus/" + inventorys.get(PLAYER) + ".yml";
	}
	
	public void setKey(String itemKey)
	{
		this.key = itemKey;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public static PlayerManager getInstance()
	{
		return manager.get(PLAYER);
	}
}

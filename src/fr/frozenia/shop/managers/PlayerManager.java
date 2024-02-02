package fr.frozenia.shop.managers;

import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class PlayerManager
{
	
	private static HashMap<Player, PlayerManager> manager = new HashMap<>();
	private HashMap<Player, String> inventorys = new HashMap<Player, String>();
	
	private static Player PLAYER;
	private String key;
	private String menu;
	private ConfigurationSection sectionItem;
	
	public PlayerManager(Player player)
	{
		if (!manager.containsKey(player)) manager.put(player, this);
		PLAYER = player;
	}
	
	public void setMenu(String menu)
	{
		this.menu = menu;
	}
	
	public String getMenu()
	{
		return this.menu;
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
	
	public void clearManager(Player player)
	{
		PlayerManager.manager.remove(player);
	}
	
	public static PlayerManager getInstance()
	{
		return manager.get(PLAYER);
	}

	public void setSectionConfiguration(ConfigurationSection sectionItem)
	{
		this.sectionItem = sectionItem;
	}
	
	public ConfigurationSection getSectionItem()
	{
		return this.sectionItem;
	}

	public void removeInventory(HumanEntity player)
	{
		if (player instanceof Player)
		{
			//Definir le menu lorsque le joueur clique sur l'item
			
			
			//inventorys.remove(player);
			//setKey(null);
			//setMenuPrevious(null);
			//setSectionConfiguration(null);
		}
	}
}

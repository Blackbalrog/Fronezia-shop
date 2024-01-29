package fr.frozenia.shop.managers;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerManager
{
	public HashMap<Player, String> players = new HashMap<Player, String>();
	
	public String getMenuPrevious(Player player)
	{
		return players.get(player);
	}
}

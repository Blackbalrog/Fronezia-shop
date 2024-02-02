package fr.frozenia.shop.inventorys;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.frozenia.shop.Shop;
import fr.frozenia.shop.configurtation.ConfigurationManager;
import fr.frozenia.shop.managers.InventoryMenu;
import fr.frozenia.shop.managers.PlayerManager;

public class InventoryMain implements Listener
{

	private Inventory inventaire;
	private ConfigurationManager configurationManager;
	private Shop instance;
	
	public InventoryMain(Shop main, String file_name)
	{
		this.instance = main;
		this.configurationManager = new ConfigurationManager(main, file_name);
	}

	public void openInventory(Player player)
	{
		String title = this.configurationManager.getString("title");
		
		int size = this.configurationManager.getInt("size");
		this.inventaire = Bukkit.createInventory(null, size, title);

		ConfigurationSection section = this.configurationManager.getConfigurationSection("Items");
		for (String key : section.getKeys(false))
		{
			Material material = Material.valueOf(section.getString(key + ".material"));
			
			short nbt = (short) section.getInt(key + ".nbt");
			if (!section.contains(key + ".nbt")) nbt = 0;
			
			ItemStack item = new ItemStack(material, 1, nbt);
			ItemMeta itemMeta = item.getItemMeta();
			
			itemMeta.setDisplayName(section.getString(key + ".name").replaceAll("&", "§"));
			
			if (!section.contains(key + ".lore")) itemMeta.setLore(null);
			itemMeta.setLore(section.getStringList(key + ".lore"));
			item.setItemMeta(itemMeta);
			
			this.inventaire.setItem(section.getInt(key + ".slot"), item);
		}
		player.openInventory(this.inventaire);
	}

	@EventHandler
	public void onInteractInventory(InventoryClickEvent event)
	{
		ItemStack clickedItem = event.getCurrentItem();
		
		HashMap<Integer, String> map_menus = new HashMap<>();
		
		if (event.getView().getTitle().equals(this.configurationManager.getString("title")))
		{
			event.setCancelled(true);
			if (event.getInventory() == null || clickedItem == null) return;
			
			Player player = (Player) event.getWhoClicked();
			
			ConfigurationSection section = this.configurationManager.getConfigurationSection("Items");
			for (String key : section.getKeys(false))
			{
				map_menus.put(section.getInt(key + ".slot"), key);
			}
			
			if (map_menus != null && section.getBoolean(map_menus.get(event.getSlot()) + ".actived") == true)
			{
				InventoryMenu inventoryManager = new InventoryMenu(this.instance, "Menus/" + map_menus.get(event.getSlot()) + ".yml");
				inventoryManager.openInventory(player);
				return;
			}
			
			if (map_menus.get(event.getSlot()) == null) return;
			
			PlayerManager.getInstance().setMenu(map_menus.get(event.getSlot()));
			
			player.sendMessage(Shop.prefix + "§7Le shop §b" + map_menus.get(event.getSlot()) + " §7est désactiver");
		}
	}
}

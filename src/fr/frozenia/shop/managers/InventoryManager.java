package fr.frozenia.shop.managers;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.frozenia.shop.Shop;
import fr.frozenia.shop.configurtation.ConfigurationManager;
import fr.frozenia.shop.inventorys.InventoryVendor;
import fr.frozenia.shop.utils.Calcul;
import fr.frozenia.shop.utils.Utils;

public class InventoryManager implements Listener
{

	private Inventory inventaire;
	private ConfigurationManager configurationManager;
	
	private Shop instance;
	
	private String file_menu;
	
	
	
	public InventoryManager(Shop main, String file_name)
	{
		this.instance = main;
		this.configurationManager = new ConfigurationManager(main, file_name);
	}
	
	public InventoryManager getInventoryFile(String file_name)
	{
		this.configurationManager = new ConfigurationManager(this.instance, file_name);
		return this;
	}
	
	public void openInventory(Player player)
	{
		String title = this.configurationManager.getString("title");
		if (configurationManager.getFileName().startsWith("Menus/"))
			title = configurationManager.getFileName().replace("Menus/", "").replace(".yml", "");
		
		int size = this.configurationManager.getInt("size");
		this.inventaire = Bukkit.createInventory(null, size, title);
		
		if (configurationManager.getFileName().startsWith("Menus/"))
		{
			this.inventaire.setItem(45, new Utils().Retour());
			Shop.getPlayerManager().players.put(player, "Menus/" + title + ".yml");
		}
		
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
			
			if (configurationManager.getFileName().startsWith("Menus/"))
			{
				String[] lores = null;
				
				lores = new String[] {
						"",
						"§7Achat: §6" + section.getDouble(key + ".buy") + "$",
						"§7Vente: §6" + section.getDouble(key + ".sell") + "$" };
				
				File fileData = new File(instance.getDataFolder(), "Data/itemData.dat");
				FileConfiguration data = YamlConfiguration.loadConfiguration(fileData);
				
				if (section.contains(key + ".dynamique") && section.getBoolean(key + ".dynamique.actived") == true)
				{
					double buy = 0D;
					double sell = 0D;
					double max_prix = section.getDouble(key + ".dynamique.max_prix");
					double min_prix = section.getDouble(key + ".dynamique.min_prix");
					
					buy = Calcul.setNewPrix(section.getDouble(key + ".buy"), data.getInt(key + ".today"), data.getInt(key + ".before"), min_prix, max_prix);
					sell = Calcul.setNewPrix(section.getDouble(key + ".sell"), data.getInt(key + ".today"), data.getInt(key + ".before"), min_prix, max_prix);
					
					lores = new String[] {
							"",
							"§7§nPrix dynamique:",
							"",
							"§7Achat: §6" + buy + "$",
							"§7Vente: §6" + sell + "$" };
				}
				itemMeta.setLore(Arrays.asList(lores));
			}
			
			item.setItemMeta(itemMeta);
			
			this.inventaire.setItem(section.getInt(key + ".slot"), item);
		}
		player.openInventory(this.inventaire);
	}

	@EventHandler
	public void onInteractInventory(InventoryClickEvent event)
	{
		ItemStack clickedItem = event.getCurrentItem();
		InventoryManager inventoryManager = null;
		
		HashMap<Integer, String> map_menus = new HashMap<>();
		HashMap<Integer, String> map_Items = new HashMap<>();
		
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
			
			this.file_menu = map_menus.get(event.getSlot());
			
			if (map_menus != null && section.getBoolean(map_menus.get(event.getSlot()) + ".actived") == true)
			{
				inventoryManager = new InventoryManager(this.instance, "Menus/" + map_menus.get(event.getSlot()) + ".yml");
				inventoryManager.openInventory(player);
				return;
			}
			if (map_menus.get(event.getSlot()) == null) return;
			
			player.sendMessage(Shop.prefix + "§7Le shop §b" + map_menus.get(event.getSlot()) + " §7est désactiver");

		}
		
		else if (event.getView().getTitle().equals(this.file_menu))
		{
			event.setCancelled(true);
			if (event.getInventory() == null || clickedItem == null) return;
			
			Player player = (Player) event.getWhoClicked();
			
			ConfigurationManager configurationItem = new ConfigurationManager(instance, "Menus/" + this.file_menu + ".yml");
			ConfigurationSection sectionItem = configurationItem.getConfigurationSection("Items");
			for (String key : sectionItem.getKeys(false))
			{
				map_Items.put(sectionItem.getInt(key + ".slot"), key);
			}
			
			if (event.getSlot() == 45)
			{
				inventoryManager = new InventoryManager(this.instance, "InventoryRoot.yml");
				inventoryManager.openInventory(player);
			}
			
			if (map_Items.get(event.getSlot()) == null) return;
			
			InventoryVendor vendor = new InventoryVendor(instance);
			vendor.openInventory(player, clickedItem, this.file_menu, map_Items.get(event.getSlot()));
			
			
		}
	}
}

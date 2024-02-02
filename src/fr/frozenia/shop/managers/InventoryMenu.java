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
import fr.frozenia.shop.inventorys.InventoryMain;
import fr.frozenia.shop.inventorys.InventoryVendor;
import fr.frozenia.shop.utils.Calcul;
import fr.frozenia.shop.utils.Utils;

@SuppressWarnings("unused")
public class InventoryMenu implements Listener
{

	private Shop instance;
	private ConfigurationManager configurationManager;

	private Inventory inventaire;
	private int page = 1;
	private int pages = 0;
	
	private PlayerManager playerManager;

	public InventoryMenu(Shop main,String file_name)
	{
		this.instance = main;
		this.configurationManager = new ConfigurationManager(main, file_name);
	}

	public void openInventory(Player player)
	{
		String title = this.configurationManager.getString("title");
		if (configurationManager.getFileName().startsWith("Menus/")) title = configurationManager.getFileName().replace("Menus/", "").replace(".yml", "");

		int size = this.configurationManager.getInt("size");
		this.inventaire = Bukkit.createInventory(null, size, title);

		// Create PlayerManager
		this.playerManager = new PlayerManager(player);
		this.playerManager.setMenuPrevious(title);
		this.playerManager.setMenu(title);

		ConfigurationSection section = this.configurationManager.getConfigurationSection("Items");
		for (String page : section.getKeys(false)) pages++;
		
		ConfigurationSection section_page = this.configurationManager.getConfigurationSection("Items.page_" + this.page);
		for (String key : section_page.getKeys(false))
		{
			Material material = Material.valueOf(section_page.getString(key + ".material"));

			short nbt = (short) section_page.getInt(key + ".nbt");
			if (!section_page.contains(key + ".nbt")) nbt = 0;

			ItemStack item = new ItemStack(material, 1, nbt);
			ItemMeta itemMeta = item.getItemMeta();

			itemMeta.setDisplayName(section_page.getString(key + ".name").replaceAll("&", "§"));

			if (!section_page.contains(key + ".lore")) itemMeta.setLore(null);
			itemMeta.setLore(section_page.getStringList(key + ".lore"));

			String[] lores = null;

			lores = new String[] { "", "§7Achat: §6" + section_page.getDouble(key + ".buy") + "$", "§7Vente: §6" + section_page.getDouble(key + ".sell") + "$" };

			File fileData = new File(instance.getDataFolder(), "Data/itemData.dat");
			FileConfiguration data = YamlConfiguration.loadConfiguration(fileData);

			if (section_page.contains(key + ".dynamique"))
			{
				double buy = 0D;
				double sell = 0D;
				double max_prix = section_page.getDouble(key + ".dynamique.max_prix");
				double min_prix = section_page.getDouble(key + ".dynamique.min_prix");

				buy = Calcul.setNewPrix(section_page.getDouble(key + ".buy"), data.getInt(key + ".today"), data.getInt(key + ".before"), min_prix, max_prix);
				sell = Calcul.setNewPrix(section_page.getDouble(key + ".sell"), data.getInt(key + ".today"), data.getInt(key + ".before"), min_prix, max_prix);

				lores = new String[] { "", "§7§nPrix dynamique:", "", "§7Achat: §6" + buy + "$", "§7Vente: §6" + sell + "$" };

			}
			itemMeta.setLore(Arrays.asList(lores));
			item.setItemMeta(itemMeta);
			this.inventaire.setItem(section_page.getInt(key + ".slot"), item);
		}
		
		if (this.page == 1)
		{
			this.inventaire.setItem(46, null);
		}
		else
		{
			this.inventaire.setItem(46, new Utils().PageBefore(this.page-1));
		}

		this.inventaire.setItem(49, new Utils().Retour());
		
		if (this.page == this.pages)
		{
			this.inventaire.setItem(52, null);
		}
		else
		{
			this.inventaire.setItem(52, new Utils().PageAfter(this.page+1));
		}

		player.openInventory(this.inventaire);
	}

	@EventHandler
	public void onInteractInventory(InventoryClickEvent event)
	{
		ItemStack clickedItem = event.getCurrentItem();

		if (event.getView().getTitle().equals(PlayerManager.getInstance().getMenu()))
		{
			HashMap<Integer, String> map_Items = new HashMap<>();

			event.setCancelled(true);
			if (event.getInventory() == null || clickedItem == null) return;

			Player player = (Player) event.getWhoClicked();

			ConfigurationManager configurationItem = new ConfigurationManager(instance, "Menus/" + PlayerManager.getInstance().getMenu() + ".yml");
			ConfigurationSection sectionItem = configurationItem.getConfigurationSection("Items.page_" + this.page);
			for (String key : sectionItem.getKeys(false))
			{
				map_Items.put(sectionItem.getInt(key + ".slot"), key);
			}

			if (event.getSlot() == 49)
			{
				InventoryMain inventoryManager = new InventoryMain(this.instance, "InventoryRoot.yml");
				inventoryManager.openInventory(player);
			}

			if (event.getSlot() == 46)
			{
				if (this.page == 1) return;
				this.page = this.page - 1;
				this.openInventory(player);
			}

			if (event.getSlot() == 52)
			{
				if (this.page == this.pages) return;
				this.page = this.page + 1;
				this.openInventory(player);
			}
			
			if (map_Items.get(event.getSlot()) == null || map_Items.get(event.getSlot()).equals("Page " + page)) return;

			PlayerManager.getInstance().setKey(map_Items.get(event.getSlot()));
			PlayerManager.getInstance().setSectionConfiguration(sectionItem.getConfigurationSection(map_Items.get(event.getSlot())));
			
			InventoryVendor vendor = new InventoryVendor(instance);
			vendor.openInventory(player, clickedItem);
		}
	}
}

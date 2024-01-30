package fr.frozenia.shop.inventorys;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.frozenia.shop.Shop;
import fr.frozenia.shop.data.SaveData;
import fr.frozenia.shop.managers.InventoryManager;
import fr.frozenia.shop.managers.PlayerManager;
import fr.frozenia.shop.utils.Calcul;
import fr.frozenia.shop.utils.Utils;
import net.milkbowl.vault.economy.Economy;

public class InventoryVendor implements Listener
{

	private static Shop instance;
	private static String FILE_MENU;
	private static ItemStack itemRegistered;
	private static int nombreItem = 1;
	private String KEY;
	
	public InventoryVendor(Shop main)
	{
		instance = main;
	}

	public void openInventory(Player player, ItemStack clickedItem, String file_menu)
	{
		KEY = PlayerManager.getInstance().getKey();
		
		SaveData saveData = new SaveData(instance);
		saveData.createData();
		
		File file = new File(instance.getDataFolder(), "Menus/" + file_menu + ".yml");
		FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

		FILE_MENU = file_menu;
		itemRegistered = clickedItem;
		
		File fileData = new File(instance.getDataFolder(), "Data/itemData.dat");
		FileConfiguration data = YamlConfiguration.loadConfiguration(fileData);
		
		Inventory inventaire = Bukkit.createInventory(null, 54, "Vendeur");

		ItemMeta itemMeta = clickedItem.getItemMeta();
		itemMeta.setDisplayName(clickedItem.getItemMeta().getDisplayName());

		double buy = calculTotalPrix(configuration, data, KEY, ".buy");
		double sell = calculTotalPrix(configuration, data, KEY, ".sell");
		
		itemMeta.setLore(Arrays.asList(new String[] { "§7nombre: §6" + nombreItem, "", "§7Achat: §6" + buy + "$", "§7Vente: §6" + sell + "$" }));
	
		itemRegistered.setItemMeta(itemMeta);

		inventaire.setItem(13, itemRegistered);
		inventaire.setItem(28, new Utils().remove(64));
		inventaire.setItem(29, new Utils().remove(32));
		inventaire.setItem(30, new Utils().remove(1));
		inventaire.setItem(31, new Utils().clear());
		inventaire.setItem(32, new Utils().add(1));
		inventaire.setItem(33, new Utils().add(32));
		inventaire.setItem(34, new Utils().add(64));
		inventaire.setItem(40, new Utils().Vendeur());
		inventaire.setItem(45, new Utils().Retour());

		player.openInventory(inventaire);
	}

	@EventHandler
	public void onInteractInventory(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();

		File file = new File(instance.getDataFolder(), "Menus/" + FILE_MENU + ".yml");
		if (!file.exists()) return;
		FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

		File fileData = new File(instance.getDataFolder(), "Data/itemData.dat");
		FileConfiguration data = YamlConfiguration.loadConfiguration(fileData);
		
		if (event.getView().getTitle().equals("Vendeur"))
		{
			ItemStack clickedItem = event.getCurrentItem();
			event.setCancelled(true);
			if (event.getInventory() == null || clickedItem == null) return;
			switch (event.getSlot())
			{
				case 28:
					if (nombreItem <= 0) return;
					nombreItem = nombreItem - 64;
					if (nombreItem <= 0) nombreItem = 1;
					break;

				case 29:
					if (nombreItem <= 0) return;
					nombreItem = nombreItem - 32;
					if (nombreItem <= 0) nombreItem = 1;
					break;

				case 30:
					if (nombreItem <= 0) return;
					nombreItem = nombreItem - 1;
					if (nombreItem <= 0) nombreItem = 1;
					break;

				case 31:
					nombreItem = 1;
					break;

				case 32:
					if (nombreItem >= 2304) return;
					nombreItem = nombreItem + 1;
					if (nombreItem >= 2304) nombreItem = 2304;
					break;

				case 33:
					if (nombreItem >= 2304) return;
					nombreItem = nombreItem + 32;
					if (nombreItem >= 2304) nombreItem = 2304;
					break;

				case 34:
					if (nombreItem >= 2304) return;
					nombreItem = nombreItem + 64;
					if (nombreItem >= 2304) nombreItem = 2304;
					break;
				
				case 40:
					Economy economy = Shop.getEconomy();
					if (economy != null)
					{
						if (event.getClick() == ClickType.LEFT)
						{
							if (isInventoryFull(player) == true)
							{
								player.sendMessage(Shop.prefix + "§7Vôtre inventaire est full. Veuillez le vider.");
								return;
							}

							double calculTotalPrix = calculTotalPrix(configuration, data, KEY, ".buy") * nombreItem;
							
							if (economy.getBalance(player) < calculTotalPrix || economy.getBalance(player) == 0)
						    {
						        player.sendMessage(Shop.prefix + "§7Vous n'avez pas assez d'argent pour acheter §bx" + nombreItem + " " + itemRegistered.getItemMeta().getDisplayName());
						        return;
						    }
							
							economy.withdrawPlayer(player, calculTotalPrix);
						    player.getInventory().addItem(new ItemStack(itemRegistered.getType(), nombreItem));
						    player.sendMessage(Shop.prefix + "§7Vous avez acheter §bx" + nombreItem + " " + itemRegistered.getItemMeta().getDisplayName() + " §7> §b" + calculTotalPrix + "$");
							
						    if (KEY == null) return;
							/* Save Data */
							data.set(KEY + ".today", data.getInt(KEY + ".today") + nombreItem);
							
							System.out.println(KEY);
							
							try
							{
								data.save(fileData);
							}
							catch (IOException exeption)
							{
								exeption.printStackTrace();
							}
							
						}
						else if (event.getClick() == ClickType.RIGHT)
						{
							HashMap<Integer, ? extends ItemStack> itemMap = player.getInventory().all(itemRegistered.getType());
							int totalQuantity = itemMap.values().stream().mapToInt(ItemStack::getAmount).sum();

							if (totalQuantity < nombreItem)
							{
								player.sendMessage(Shop.prefix + "§7Vous n'avez pas §cx" + nombreItem + " " + itemRegistered.getItemMeta().getDisplayName());
								return;
							}
							
							double calculTotalPrix = calculTotalPrix(configuration, data, KEY, ".sell") * nombreItem;

							economy.depositPlayer(player, calculTotalPrix);
							player.getInventory().removeItem(new ItemStack(itemRegistered.getType(), nombreItem));
							player.sendMessage(Shop.prefix + "§7Vous avez vendu §bx" + nombreItem + " " + itemRegistered.getItemMeta().getDisplayName() + " §7> §b" + calculTotalPrix + "$");
							
							if (KEY == null) return;
							/* Save Data */
							data.set(KEY + ".today", data.getInt(KEY + ".today") + nombreItem);
							
							try
							{
								data.save(fileData);
							}
							catch (IOException exeption)
							{
								exeption.printStackTrace();
							}
						}
						
						if (event.isShiftClick())
						{
							if (player.hasPermission("shop.sellall"))
							{
								HashMap<Integer, ? extends ItemStack> itemMap = player.getInventory().all(itemRegistered.getType());
								int totalQuantity = itemMap.values().stream().mapToInt(ItemStack::getAmount).sum();

								if (totalQuantity == 0)
								{
									player.sendMessage(Shop.prefix + "§7Vous n'avez pas §c" + itemRegistered.getItemMeta().getDisplayName());
									return;
								}
								
								double calculTotalPrix = calculTotalPrix(configuration, data, KEY, ".sell") * nombreItem;

								economy.depositPlayer(player, calculTotalPrix);
								player.getInventory().removeItem(new ItemStack(itemRegistered.getType(), totalQuantity));
								player.sendMessage(Shop.prefix + "§7Vous avez vendu §bx" + totalQuantity + " " + itemRegistered.getItemMeta().getDisplayName() + " §7> §b" + calculTotalPrix + "$");
								
								if (KEY == null) return;
								/* Save Data */
								data.set(KEY + ".today", data.getInt(KEY + ".today") + totalQuantity);
								
								try
								{
									data.save(fileData);
								}
								catch (IOException exeption)
								{
									exeption.printStackTrace();
								}
								return;
							}
							player.sendMessage(Shop.prefix + "§7Tu n'as pas la permission");
						}
					}
					break;

				case 45:
					InventoryManager inventoryManager = new InventoryManager(instance, PlayerManager.getInstance().getMenuPrevious());
					inventoryManager.openInventory(player);
					break;
			}
			
			ItemMeta meta = clickedItem.getItemMeta();
			if (meta == null) return;
			meta.setDisplayName(itemRegistered.getItemMeta().getDisplayName());
			
			double buy = calculTotalPrix(configuration, data, KEY, ".buy");
			double sell = calculTotalPrix(configuration, data, KEY, ".sell");

			meta.setLore(Arrays.asList(new String[] { "§7nombre: §6" + nombreItem, "", "§7Achat: §6" + (buy * nombreItem) + "$", "§7Vente: §6" + (sell * nombreItem) + "$" }));
			
			itemRegistered.setItemMeta(meta);
			event.getView().setItem(13, itemRegistered);
		}
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event)
	{
		if (event.getView().getTitle().equals("Vendeur"))
		{
			nombreItem = 1;
		}
	}

	public boolean isInventoryFull(Player player)
	{
		return player.getInventory().firstEmpty() == -1 ? true : false;
	}
	
	public double calculTotalPrix(FileConfiguration configuration, FileConfiguration data, String key, String vendor)
	{
		ConfigurationSection section = configuration.getConfigurationSection("Items");
		double max_prix = section.getDouble(key + ".dynamique.max_prix");
		double min_prix = section.getDouble(key + ".dynamique.min_prix");
		
		if (section.contains(key + ".dynamique") && section.getBoolean(key + ".dynamique.actived") == true)
		{
			return Calcul.setNewPrix(section.getDouble(key + vendor) * nombreItem, data.getInt(key + ".today"), data.getInt(key + ".before"), min_prix, max_prix);
		}
		else
		{
			return section.getDouble(key + vendor) * nombreItem;
		}
	}
}

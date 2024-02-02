package fr.frozenia.shop.inventorys;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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
import fr.frozenia.shop.managers.InventoryMenu;
import fr.frozenia.shop.managers.PlayerManager;
import fr.frozenia.shop.utils.Calcul;
import fr.frozenia.shop.utils.Utils;
import net.milkbowl.vault.economy.Economy;

public class InventoryVendor implements Listener
{

	private Shop instance;
	private static ItemStack itemRegistered;
	private static int nombreItem = 1;
	//update price
	private volatile String KEY;
	private volatile ConfigurationSection sectionItem;
	
	private File fileData;
	private FileConfiguration data;
	
	
	public InventoryVendor(Shop main)
	{
		this.instance = main;
		this.fileData = new File(main.getDataFolder(), "Data/itemData.dat");
		this.data = YamlConfiguration.loadConfiguration(this.fileData);
	}

	public void openInventory(Player player, ItemStack clickedItem)
	{
		SaveData saveData = new SaveData(this.instance);
		saveData.createData();
		
		itemRegistered = clickedItem;

		Inventory inventaire = Bukkit.createInventory(null, 54, "Vendeur");

		ItemMeta itemMeta = clickedItem.getItemMeta();
		itemMeta.setDisplayName(clickedItem.getItemMeta().getDisplayName());

		KEY = PlayerManager.getInstance().getKey();
		
		double buy = calculTotalPrix("buy");
		double sell = calculTotalPrix("sell");
		
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
		
		if (event.getView().getTitle().equals("Vendeur"))
		{
			File file = new File(instance.getDataFolder(), "Menus/" + PlayerManager.getInstance().getMenu() + ".yml");
			if (!file.exists()) return;
			
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

							KEY = PlayerManager.getInstance().getKey();
							double calculTotalPrix = calculTotalPrix(".buy") * nombreItem;
							
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
							data.set(KEY + ".Today", data.getInt(KEY + ".Today") + nombreItem);
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
							
							KEY = PlayerManager.getInstance().getKey();
							double calculTotalPrix = calculTotalPrix("sell") * nombreItem;

							economy.depositPlayer(player, calculTotalPrix);
							player.getInventory().removeItem(new ItemStack(itemRegistered.getType(), nombreItem));
							player.sendMessage(Shop.prefix + "§7Vous avez vendu §bx" + nombreItem + " " + itemRegistered.getItemMeta().getDisplayName() + " §7> §b" + calculTotalPrix + "$");
							
							if (KEY == null) return;
							
							/* Save Data */
							data.set(KEY + ".Today", data.getInt(KEY + ".Today") + nombreItem);
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
								
								KEY = PlayerManager.getInstance().getKey();
								double calculTotalPrix = calculTotalPrix("sell") * totalQuantity;

								economy.depositPlayer(player, calculTotalPrix);
								player.getInventory().removeItem(new ItemStack(itemRegistered.getType(), totalQuantity));
								player.sendMessage(Shop.prefix + "§7Vous avez vendu §bx" + totalQuantity + " " + itemRegistered.getItemMeta().getDisplayName() + " §7> §b" + calculTotalPrix + "$");
								
								if (KEY == null) return;
								
								/* Save Data */
								data.set(KEY + ".Today", data.getInt(KEY + ".Today") + totalQuantity);
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
					if (event.isShiftClick())
					{
						PlayerManager.getInstance().clearManager(player);
						player.closeInventory();
						return;
					}
					InventoryMenu inventoryManager = new InventoryMenu(instance, PlayerManager.getInstance().getMenuPrevious());
					inventoryManager.openInventory(player);
					break;
			}
			
			ItemMeta meta = clickedItem.getItemMeta();
			if (meta == null) return;
			meta.setDisplayName(itemRegistered.getItemMeta().getDisplayName());
			
			KEY = PlayerManager.getInstance().getKey();
			double buy = calculTotalPrix("buy");
			double sell = calculTotalPrix("sell");

			meta.setLore(Arrays.asList(new String[] { "§7nombre: §6" + nombreItem, "", "§7Achat: §6" + (buy * nombreItem) + "$", "§7Vente: §6" + (sell * nombreItem) + "$" }));
			
			itemRegistered.setItemMeta(meta);
			event.getView().setItem(13, itemRegistered);
		}
	}

	public boolean isInventoryFull(Player player)
	{
		return player.getInventory().firstEmpty() == -1 ? true : false;
	}
	
	public double calculTotalPrix(String vendor)
	{
		this.KEY = PlayerManager.getInstance().getKey();
		this.sectionItem = PlayerManager.getInstance().getSectionItem();
		
		double vendorToConfig = this.sectionItem.getDouble(vendor);
		
		double max_prix = this.sectionItem.getDouble("dynamique.max_prix");
		double min_prix = this.sectionItem.getDouble("dynamique.min_prix");
		
		int dataToday 		= this.data.getInt(KEY + ".Today");
		int dataYesterday 	= this.data.getInt(KEY + ".Yesterday");
		int dataTwoDays 	= this.data.getInt(KEY + ".TwoDays");
		
		double value = new Calcul(vendorToConfig, dataToday, dataYesterday, dataTwoDays, min_prix, max_prix).calculePrice();
		
		DecimalFormat decimalFormat = new DecimalFormat("##.##");
	    String formattedValue = decimalFormat.format(value);
	    return Double.parseDouble(formattedValue);
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event)
	{
		if (event.getView().getTitle().equals("Vendeur"))
		{
			nombreItem = 1;
			//PlayerManager.getInstance().removeInventory(event.getPlayer());
		}
	}
}

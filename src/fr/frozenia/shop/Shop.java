package fr.frozenia.shop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.frozenia.shop.commands.CommandShop;
import fr.frozenia.shop.configurtation.ConfigurationManager;
import fr.frozenia.shop.data.SaveData;
import fr.frozenia.shop.inventorys.InventoryVendor;
import fr.frozenia.shop.managers.InventoryManager;
import net.milkbowl.vault.economy.Economy;

public class Shop extends JavaPlugin
{
	public static String prefix;
	private static Economy economy = null;

	@Override
	public void onEnable()
	{
		ConfigurationManager configuration = new ConfigurationManager(this, "config.yml");
		prefix = configuration.getString("prefix").replaceAll("&", "§");
		
		if (!setupEconomy())
		{
			this.onDisable();
			return;
		}
		
		onListeners();
		onCommands();
		
		/* Section Prix Dynamique*/
		SaveData data = new SaveData(this);
		if (configuration.getBoolean("save-auto.enable") == true) data.saveRessources();
		
		Bukkit.getServer().getConsoleSender().sendMessage(prefix + "§adémarrer");
	}

	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(prefix + "§cÉteint");
	}

	private void onCommands()
	{
		this.getCommand("shop").setExecutor(new CommandShop(this));
	}

	private void onListeners()
	{
		PluginManager pluginManager = Bukkit.getPluginManager();

		pluginManager.registerEvents(new InventoryManager(this, "InventoryRoot.yml"), this);
		pluginManager.registerEvents(new InventoryVendor(this), this);
	}

	private boolean setupEconomy()
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
		{
			Bukkit.getServer().getConsoleSender().sendMessage(prefix + "§cVault n'est pas trouver dans vôtre dossier plugins");
			return false;
		}
		RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (registeredServiceProvider == null)
		{
			Bukkit.getServer().getConsoleSender().sendMessage(prefix + "§cAucun plugin d'économie trouver pour être utiliser avec Vault");
			return false;
		}
		economy = registeredServiceProvider.getProvider();
		return economy != null;
	}
	
	public static Economy getEconomy()
	{
		return economy;
	}
	
}

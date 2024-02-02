package fr.frozenia.shop.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import fr.frozenia.shop.Shop;
import fr.frozenia.shop.configurtation.ConfigurationManager;

public class SaveData
{

	private Shop instance;

	public SaveData(Shop main)
	{
		this.instance = main;
	}

	public void saveRessources()
	{
		ConfigurationManager configuration = new ConfigurationManager(instance, "config.yml");

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				File dataFile = new File(instance.getDataFolder(), "Data/itemData.dat");
				FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

				for (String key : data.getKeys(false))
				{
					data.set(key + ".TwoDays", data.getInt(key + ".Yesterday"));
					data.set(key + ".Yesterday", data.getInt(key + ".Today"));
					data.set(key + ".Today", 0);

					try
					{
						data.save(dataFile);
						if (configuration.getBoolean("debug") == true) Bukkit.getServer().getConsoleSender().sendMessage(Shop.prefix + "§7Sauvegarde de la clée: §b" + key + " §7dans le fichier Data/dataItems.dat");
					}
					catch (IOException exeption)
					{
						Bukkit.getServer().getConsoleSender().sendMessage(Shop.prefix + "§7Une erreur est survenu lors de l'enregistrment de la clée: §c" + key + " §7dans le fichier Data/dataItem.dat");
					}
				}
				Bukkit.getServer().getConsoleSender().sendMessage(Shop.prefix + "§7Sauvegarde des données du fichier Data/dataItems.dat");
			}
		}.runTaskTimer(this.instance, 0, 24 * 60 * 60 * 20);
	}

	public void createData()
	{
		File dir = new File(this.instance.getDataFolder(), "Menus");

		for (File file : dir.listFiles())
		{
			FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

			File dataFile = new File(this.instance.getDataFolder(), "Data/itemData.dat");
			FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

			ConfigurationSection section = configuration.getConfigurationSection("Items");
			for (String key : section.getKeys(false))
			{
				if (!section.contains(key))
				{
					ConfigurationSection itemDataSection = data.createSection(key);
					itemDataSection.set("TwoDays", 0);
					itemDataSection.set("Yesterday", 0);
					itemDataSection.set("Today", 0);

					try
					{
						data.save(dataFile);
						Bukkit.getServer().getConsoleSender().sendMessage(Shop.prefix + "§7Création de la clée: §b" + key + " §7dans le fichier Data/dataItem.dat");
					}
					catch (IOException exeption)
					{
						Bukkit.getServer().getConsoleSender().sendMessage(Shop.prefix + "§7Une erreur est survenu lors de la création de la clée: §c" + key + " §7dans le fichier Data/dataItem.dat");
					}
				}
			}
		}
	}
}

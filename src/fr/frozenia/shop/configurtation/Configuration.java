package fr.frozenia.shop.configurtation;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration extends YamlConfiguration
{
	private File file;
	
	public Configuration(File file)
	{
		this.file = file;
	}
	
	public FileConfiguration configuration()
	{
		return YamlConfiguration.loadConfiguration(this.file);
	}
	
	public void saveFile()
	{
		try
		{
			this.configuration().save(this.file);
		}
		catch (IOException exeption)
		{
			exeption.printStackTrace();
		}
	}
}

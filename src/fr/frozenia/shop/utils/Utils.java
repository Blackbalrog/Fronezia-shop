package fr.frozenia.shop.utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils
{
	public ItemStack add(int nombre)
	{
		ItemStack add = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
		ItemMeta metaAdd = add.getItemMeta();
		metaAdd.setDisplayName("§a+" + nombre);
		add.setItemMeta(metaAdd);
		return add;
	}
	
	public ItemStack remove(int nombre)
	{
		ItemStack remove = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemMeta metaRemove = remove.getItemMeta();
		metaRemove.setDisplayName("§c-" + nombre);
		remove.setItemMeta(metaRemove);
		return remove;
	}

	public ItemStack clear()
	{
		ItemStack clear = new ItemStack(Material.PAPER);
		ItemMeta metaClear = clear.getItemMeta();
		metaClear.setDisplayName("§6Reset");
		clear.setItemMeta(metaClear);
		return clear;
	}
	
	public ItemStack Vendeur()
	{
		ItemStack vendeur = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
		ItemMeta metaVendeur = vendeur.getItemMeta();
		metaVendeur.setDisplayName("§aVendeur");
		metaVendeur.setLore(Arrays.asList(new String[] {"", "§7Clique gauche: §aAcheter", "§7Clique droit: §bVendre", "", "§7Shift + Click: §bVendre tous"}));
		vendeur.setItemMeta(metaVendeur);
		return vendeur;
	}
	
	public ItemStack Retour()
	{
		ItemStack retour = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta metaRetour = retour.getItemMeta();
		metaRetour.setDisplayName("§cRetour");
		retour.setItemMeta(metaRetour);
		return retour;
	}
}

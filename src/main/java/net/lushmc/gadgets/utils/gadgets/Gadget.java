package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import net.lushmc.core.utils.items.CustomItem;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;

public class Gadget {

	CustomItem item;
	String id;
	BossBar cooldownbar;

	public Gadget(String id) {
		this.id = id;
		init();
	}

	public void init() {

		/*
		 * Create BossBar
		 */
		cooldownbar = Bukkit.createBossBar("Cooldown", BarColor.BLUE, BarStyle.SOLID);

		/*
		 * Create Item
		 */
		item = new CustomItem(Material.GRASS_BLOCK);
		item.setDisplayName("&cError");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		item.setLore(lore);
	}

	public String getID() {
		return id;
	}

	public void activate(Player player, GadgetAction action) {

	}

	public CustomItem getCustomItem() {
		return item;
	}

}

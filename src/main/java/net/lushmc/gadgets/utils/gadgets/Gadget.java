package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import net.lushmc.core.utils.items.CustomItem;

public class Gadget {

	CustomItem item;
	String id;

	public Gadget(String id) {
		this.id = id;
		init();
	}

	public void init() {

		/*
		 * Create Item
		 */
		item = new CustomItem(Material.GRASS_BLOCK);
		item.setDisplayName("&cError");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		item.setLore(lore);
	}

	public CustomItem getCustomItem() {
		return item;
	}

}

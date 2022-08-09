package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import net.lushmc.core.utils.items.CustomItem;

public class BoomBoomGadget extends Gadget {

	public BoomBoomGadget(String id) {
		super(id);
	}

	@Override
	public void init() {
		/*
		 * Create Item
		 */
		item = new CustomItem(Material.TNT);
		item.setDisplayName("&cBoom Boom");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		item.setLore(lore);
	}

}

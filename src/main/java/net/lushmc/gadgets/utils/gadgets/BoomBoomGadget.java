package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.lushmc.core.utils.items.CustomItem;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;

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
		item.setDisplayName("&c&lBoom Boom");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		item.setLore(lore);
	}

	@Override
	public void activate(Player player, GadgetAction action) {
		player.sendMessage("boom boom");
	}

}

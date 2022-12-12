package net.lushmc.gadgets.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.lushmc.gadgets.utils.gadgets.BoomBoomGadget;
import net.lushmc.gadgets.utils.gadgets.DecoyGadget;
import net.lushmc.gadgets.utils.gadgets.Gadget;
import net.lushmc.gadgets.utils.gadgets.SmokeBombGadget;

public class GadgetUtils {

	private static Map<String, Gadget> gadgets = new HashMap<>();

	public static void registerGadgets() {
		if (!gadgets.isEmpty())
			gadgets.clear();
		addGadget(new BoomBoomGadget());
		addGadget(new SmokeBombGadget());
		addGadget(new DecoyGadget());
	}

	public static void addGadget(String id, Gadget gadget) {
		gadgets.put(id, gadget);
	}

	public static void addGadget(Gadget gadget) {
		if (gadget.getID().equals(""))
			return;
		gadgets.put(gadget.getID(), gadget);
	}

	public static Gadget getGadget(String gadgetId) {
		return gadgets.containsKey(gadgetId) ? gadgets.get(gadgetId) : null;
	}

	public static Gadget getGadget(Player player, ItemStack item) {
		for (Entry<String, Gadget> e : gadgets.entrySet()) {
			if (e.getValue().getCustomItem().getItem(player).equals(item)) {
				return e.getValue();
			}
		}
		return null;
	}

	public static Gadget getGadget(Player player) {
		return getGadget(player, player.getInventory().getItemInMainHand());
	}

	public static Map<String, Gadget> getGadgets() {
		return gadgets;
	}

	public enum GadgetAction {

		SHIFT_LEFT_CLICK, LEFT_CLICK, SHIFT_RIGHT_CLICK, RIGHT_CLICK;

		public static GadgetAction fromEvent(PlayerInteractEvent e) {
			switch (e.getAction()) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				return e.getPlayer().isSneaking() ? SHIFT_LEFT_CLICK : LEFT_CLICK;
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				return e.getPlayer().isSneaking() ? SHIFT_RIGHT_CLICK : RIGHT_CLICK;
			default:
				return null;
			}
		}
	}

}

package net.lushmc.gadgets.utils;

import net.lushmc.core.utils.CoreUtils;
import net.lushmc.gadgets.GadgetPlugin;

public class Utils {

	private static GadgetPlugin plugin;

	public static void init(GadgetPlugin main) {
		plugin = main;
		CoreUtils.addPrefix("gadgets", "&c&lGadgets &7> &f");
	}

	public static GadgetPlugin getPlugin() {
		return plugin;
	}

}

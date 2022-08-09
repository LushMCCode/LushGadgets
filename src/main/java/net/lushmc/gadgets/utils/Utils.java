package net.lushmc.gadgets.utils;

import net.lushmc.core.utils.CoreUtils;
import net.lushmc.core.utils.placeholders.Emoticons;
import net.lushmc.gadgets.GadgetPlugin;
import net.lushmc.gadgets.utils.gadgets.GadgetUtils;

public class Utils {

	private static GadgetPlugin plugin;

	public static void init(GadgetPlugin main) {
		plugin = main;
		CoreUtils.addPrefix("gadgets", "&e&lGadgets &7" + Emoticons.RIGHT_ARROW + " &f");
		GadgetUtils.registerGadgets();
	}

	public static GadgetPlugin getPlugin() {
		return plugin;
	}

}

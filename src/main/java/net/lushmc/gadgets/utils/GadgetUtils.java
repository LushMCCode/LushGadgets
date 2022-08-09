package net.lushmc.gadgets.utils;

import java.util.HashMap;
import java.util.Map;

import net.lushmc.gadgets.utils.gadgets.BoomBoomGadget;
import net.lushmc.gadgets.utils.gadgets.Gadget;

public class GadgetUtils {

	private static Map<String, Gadget> gadgets = new HashMap<>();

	public static void registerGadgets() {
		if (!gadgets.isEmpty())
			gadgets.clear();
		gadgets.put("boomboom", new BoomBoomGadget("boomboom"));
	}

	public static Gadget getGadget(String gadgetId) {
		return gadgets.containsKey(gadgetId) ? gadgets.get(gadgetId) : null;
	}

}

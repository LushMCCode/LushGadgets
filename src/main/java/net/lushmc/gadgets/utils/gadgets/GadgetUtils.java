package net.lushmc.gadgets.utils.gadgets;

import java.util.HashMap;
import java.util.Map;

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

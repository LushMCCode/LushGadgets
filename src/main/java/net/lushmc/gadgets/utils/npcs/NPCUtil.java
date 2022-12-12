package net.lushmc.gadgets.utils.npcs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import net.lushmc.core.utils.UID;

public class NPCUtil {

	private static Map<UID, NPC> npcs = new HashMap<>();

	public void init() {

	}

	public static NPC createNPC(String displayname, String skin, Location loc) {
		NPC npc = new NPC(displayname, loc, skin);
		npcs.put(npc.getUID(), npc);
		return npc;

	}

	public static Object getNPC(UID uid) {
		return npcs.containsKey(uid) ? npcs.get(uid) : null;
	}
}

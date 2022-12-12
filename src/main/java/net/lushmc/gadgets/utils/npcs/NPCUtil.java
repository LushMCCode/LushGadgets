package net.lushmc.gadgets.utils.npcs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class NPCUtil {

	private static Map<Integer, NPC> npcs = new HashMap<>();

	public void init() {

	}

	public static NPC createNPC(String displayname, String skin, Location loc) {
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, skin);
		npc.setName(displayname);
		npc.spawn(loc);
		npcs.put(npc.getId(), npc);
		return npc;

	}

	public static Object getNPC(int id) {
		return npcs.containsKey(id) ? npcs.get(id) : null;
	}
}

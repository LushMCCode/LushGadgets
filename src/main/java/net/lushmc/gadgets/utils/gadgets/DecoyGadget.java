package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.citizensnpcs.api.npc.NPC;
import net.lushmc.core.utils.CoreUtils;
import net.lushmc.core.utils.CosmeticUtils;
import net.lushmc.core.utils.items.CustomItem;
import net.lushmc.core.utils.particles.formats.DotFormat;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;
import net.lushmc.gadgets.utils.Utils;
import net.lushmc.gadgets.utils.npcs.NPCUtil;

public class DecoyGadget extends Gadget {

	DotFormat format = new DotFormat();

	public DecoyGadget() {
		super("decoy");
	}

	@Override
	public void init() {

		/*
		 * Create Item
		 */
		item = new CustomItem(Material.GOLD_INGOT);
//		item.setCustomModelData(10002);
		item.setDisplayName("&aDecoy");
//		item.setDisplayName("&F&LBO&E&LOM &6&LBO&C&LOM");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		lore.add("&8-------------");
		lore.add("&c&lRIGHT CLICK&7 to deploy.");
		item.setLore(lore);

		/*
		 * Create BossBar
		 */
		cooldownbar = Bukkit.createBossBar(CoreUtils.colorize(item.getDisplayName() + " &c&lCooldown"), BarColor.GREEN,
				BarStyle.SOLID);

	}

	@Override
	public void activate(Player player, GadgetAction action) {
		Bukkit.broadcastMessage("test2");
		if (CosmeticUtils.getGenericCooldown("decoygadget").contains(player.getUniqueId())) {
			return;
		}
		Bukkit.broadcastMessage("test3");
		CosmeticUtils.getGenericCooldown("decoygadget").add(player.getUniqueId());

		NPC npc = NPCUtil.createNPC(player.getName(), player.getName(), player.getLocation());
		Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), new DecoyRunnable(npc, player), 0);
	}

	private class DecoyRunnable implements Runnable {

		NPC npc;
		Player player;
		long started;

		public DecoyRunnable(NPC npc, Player player) {
			this.npc = npc;
			this.player = player;
			started = new Date().getTime();
		}

		@Override
		public void run() {
			Bukkit.broadcastMessage("Health: " + ((LivingEntity) npc.getEntity()).getHealth());
			if (TimeUnit.SECONDS.convert(new Date().getTime() - started, TimeUnit.MILLISECONDS) < 5) {
				Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
					player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1, false, true));
				}, 0);
				return;
			}
			npc.destroy();

		}

	}

}

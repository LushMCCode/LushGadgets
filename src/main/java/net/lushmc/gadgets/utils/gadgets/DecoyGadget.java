package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.versioned.AxolotlTrait;
import net.lushmc.core.utils.CoreUtils;
import net.lushmc.core.utils.CosmeticUtils;
import net.lushmc.core.utils.DebugUtils;
import net.lushmc.core.utils.CosmeticUtils.GenericCooldownRunnable;
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
		item.setDisplayName("&a&lDecoy");
//		item.setDisplayName("&F&LBO&E&LOM &6&LBO&C&LOM");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		lore.add("&8-------------");
		lore.add("&a&lRIGHT CLICK&7 to deploy.");
		item.setLore(lore);

		/*
		 * Create BossBar
		 */
		cooldownbar = Bukkit.createBossBar(CoreUtils.colorize(item.getDisplayName() + " &a&lCooldown"), BarColor.GREEN,
				BarStyle.SOLID);

	}

	@Override
	public void activate(Player player, GadgetAction action) {
		if (CosmeticUtils.getGenericCooldown("decoygadget").contains(player.getUniqueId())) {
			return;
		}
		CosmeticUtils.getGenericCooldown("decoygadget").add(player.getUniqueId());
		Bukkit.getScheduler().runTaskLater(Utils.getPlugin(),
				new GenericCooldownRunnable(cooldownbar, "decoygadget", player.getUniqueId(), new Date().getTime(),
						DebugUtils.isDebugger(player.getUniqueId()) ? -1 : 10, () -> {
						}),
				1);

		NPC npc = NPCUtil.createNPC(player.getName(), player.getName(), player.getLocation());
		npc.setProtected(false);
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
			if (TimeUnit.SECONDS.convert(new Date().getTime() - started, TimeUnit.MILLISECONDS) < 10
					&& npc.isSpawned()) {
				Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
					player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1, false, true, false));
				}, 0);

				Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), this, 0);
				return;
			}
			Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
				for (int i = 0; i < 100; i++) {
					player.getWorld().spawnParticle(Particle.TOTEM, npc.getStoredLocation(), 1, 0, 0, 0, 1);
				}
				npc.destroy();
			}, 0);

		}

	}

}

package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import net.lushmc.core.utils.CoreUtils;
import net.lushmc.core.utils.CosmeticUtils;
import net.lushmc.core.utils.CosmeticUtils.GenericCooldownRunnable;
import net.lushmc.core.utils.DebugUtils;
import net.lushmc.core.utils.chat.CoreChatUtils;
import net.lushmc.core.utils.items.CustomItem;
import net.lushmc.core.utils.particles.formats.DotFormat;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;
import net.lushmc.gadgets.utils.Utils;

public class BoomBoomGadget extends Gadget {

	DotFormat format = new DotFormat();

	public BoomBoomGadget() {
		super("boomboom");
	}

	@Override
	public void init() {

		String dn = CoreChatUtils.fade("FFFFFF", "FFFF55", "BOOM", true, false, false, false, false)
				+ CoreChatUtils.fade("FFFF55", "FF5555", " BOOM", true, false, false, false, false);

		/*
		 * Create BossBar
		 */
		cooldownbar = Bukkit.createBossBar(CoreUtils.colorize(dn + " &c&lCooldown"), BarColor.RED, BarStyle.SOLID);

		/*
		 * Create Item
		 */
		item = new CustomItem(Material.GOLD_INGOT);
		item.setCustomModelData(10000);
		item.setDisplayName(dn);
//		item.setDisplayName("&F&LBO&E&LOM &6&LBO&C&LOM");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		lore.add("&8-------------");
		lore.add("&c&lRIGHT CLICK&7 to throw.");
		item.setLore(lore);
	}

	@Override
	public void activate(Player player, GadgetAction action) {

		if (CosmeticUtils.getGenericCooldown("boomboom").contains(player.getUniqueId())) {
			return;
		}
		CosmeticUtils.getGenericCooldown("boomboom").add(player.getUniqueId());

		Item bomb = player.getWorld().dropItem(player.getEyeLocation(), item.getItem(player));
		bomb.setPickupDelay(Integer.MAX_VALUE);
		bomb.setVelocity(player.getEyeLocation().getDirection());
		Bukkit.getScheduler().runTaskLater(Utils.getPlugin(),
				new GenericCooldownRunnable(cooldownbar, "boomboom", player.getUniqueId(), new Date().getTime(),
						DebugUtils.isDebugger(player.getUniqueId()) ? -1 : 10, () -> {
						}),
				1);
		Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), new ExplosionRunnable(bomb, player, this),
				0);

	}

	private class ExplosionRunnable implements Runnable {

		Item item;
		Player player;
		Gadget gadget;

		public ExplosionRunnable(Item item, Player player, Gadget gadget) {
			this.item = item;
			this.player = player;
			this.gadget = gadget;
		}

		@Override
		public void run() {
			if (!item.getLocation().add(item.getVelocity()).getBlock().getType().equals(Material.AIR)) {
				Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
					item.setMetadata("thrower", new FixedMetadataValue(Utils.getPlugin(), player));
					item.setMetadata("gadget", new FixedMetadataValue(Utils.getPlugin(), gadget));
					item.getWorld().createExplosion(item.getLocation(), 5f, false, true, item);
				}, 0);
				return;
			}
			item.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, item.getLocation(), 1, 0, 0, 0, 0);
			item.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, item.getLocation(), 1, 0, 0, 0, 2);
			Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), this, 0);
		}

	}

}

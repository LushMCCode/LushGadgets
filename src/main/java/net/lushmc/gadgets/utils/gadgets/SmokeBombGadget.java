package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.lushmc.core.utils.CoreUtils;
import net.lushmc.core.utils.CosmeticUtils;
import net.lushmc.core.utils.CosmeticUtils.GenericCooldownRunnable;
import net.lushmc.core.utils.DebugUtils;
import net.lushmc.core.utils.chat.CoreChatUtils;
import net.lushmc.core.utils.items.CustomItem;
import net.lushmc.core.utils.particles.formats.DotFormat;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;
import net.lushmc.gadgets.utils.Utils;

public class SmokeBombGadget extends Gadget {

	DotFormat format = new DotFormat();

	public SmokeBombGadget() {
		super("smokebomb");
	}

	@Override
	public void init() {

		String dn = CoreChatUtils.fade("FFFFFF", "808080", "Smoke", true, false, false, false, false)
				+ CoreChatUtils.fade("7F7F7F", "000000", " Bomb", true, false, false, false, false);

		/*
		 * Create BossBar
		 */
		cooldownbar = Bukkit.createBossBar(CoreUtils.colorize(dn + " &7&lCooldown"), BarColor.WHITE, BarStyle.SOLID);

		/*
		 * Create Item
		 */

		item = new CustomItem(Material.GOLD_INGOT);
		item.setCustomModelData(10001);
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

		if (CosmeticUtils.getGenericCooldown("smokebomb").contains(player.getUniqueId())) {
			return;
		}
		CosmeticUtils.getGenericCooldown("smokebomb").add(player.getUniqueId());
		Bukkit.getScheduler().runTaskLater(Utils.getPlugin(),
				new GenericCooldownRunnable(cooldownbar, "smokebomb", player.getUniqueId(), new Date().getTime(),
						DebugUtils.isDebugger(player.getUniqueId()) ? -1 : 10, () -> {
						}),
				1);
		Item bomb = player.getWorld().dropItem(player.getEyeLocation(), item.getItem(player));
		bomb.setPickupDelay(Integer.MAX_VALUE);
		switch (action) {
		case LEFT_CLICK:
		case RIGHT_CLICK:
			bomb.setVelocity(player.getEyeLocation().getDirection());
			Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), new ThrowRunnable(bomb, player, this),
					0);
			break;
		case SHIFT_LEFT_CLICK:
		case SHIFT_RIGHT_CLICK:
			Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), new SmokeScreenRunnable(bomb), 0);
			break;
		}

	}

	private class ThrowRunnable implements Runnable {

		Item item;
		Player player;
		Gadget gadget;

		public ThrowRunnable(Item item, Player player, Gadget gadget) {
			this.item = item;
			this.player = player;
			this.gadget = gadget;
		}

		@Override
		public void run() {
			if (!item.getLocation().add(item.getVelocity()).getBlock().getType().equals(Material.AIR)) {
				Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
					item.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, item.getLocation(), 1, 0, 0, 0, 0);
					item.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, item.getLocation(), 1, 0, 0, 0, 2);
					Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), this, 0);
				}, 0);

			} else {
				prepareSmokeScreen(item, player, gadget);
				Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), new SmokeScreenRunnable(item), 0);
			}
			return;
		}

	}

	private class SmokeScreenRunnable implements Runnable {

		Item item;
		long started;

		public SmokeScreenRunnable(Item item) {
			this.item = item;
			started = new Date().getTime();

		}

		@Override
		public void run() {
			item.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,
					item.getLocation().clone().add(new Random().nextInt(3) * (new Random().nextBoolean() ? 1 : -1),
							new Random().nextInt(3), new Random().nextInt(3) * (new Random().nextBoolean() ? 1 : -1)),
					1, new Random().nextDouble(), new Random().nextDouble(), new Random().nextDouble(), 0);
			Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
				for (Entity e : item.getNearbyEntities(3, 3, 3)) {
					if (e instanceof Player) {
						Player player = (Player) e;
						if (player.equals(item.getMetadata("thrower").get(0).value()))
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 2, false, false, true));
						else
							player.addPotionEffect(
									new PotionEffect(PotionEffectType.BLINDNESS, 50, 100, false, false, true));
					}
				}
			}, 0);

			if (TimeUnit.SECONDS.convert(new Date().getTime() - started, TimeUnit.MILLISECONDS) < 5)
				Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), this, 0);
			else
				item.remove();

		}

	}

	public void prepareSmokeScreen(Item item, Player player, Gadget gadget) {
		item.setMetadata("thrower", new FixedMetadataValue(Utils.getPlugin(), player));
		item.setMetadata("gadget", new FixedMetadataValue(Utils.getPlugin(), gadget));

		for (int i = 0; i < new Random().nextInt(30) + 70; i++) {
			if (new Random().nextDouble() < 0.33)
				item.getWorld().spawnParticle(Particle.EXPLOSION_LARGE,
						item.getLocation().clone().add(new Random().nextInt(3) * (new Random().nextBoolean() ? 1 : -1),
								new Random().nextInt(3),
								new Random().nextInt(3) * (new Random().nextBoolean() ? 1 : -1)),
						1, 0, 0, 0, 0);

			item.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,
					item.getLocation().clone().add(new Random().nextInt(3) * (new Random().nextBoolean() ? 1 : -1),
							new Random().nextInt(3), new Random().nextInt(3) * (new Random().nextBoolean() ? 1 : -1)),
					1, new Random().nextDouble(), new Random().nextDouble(), new Random().nextDouble(), 0);
		}

	}

}

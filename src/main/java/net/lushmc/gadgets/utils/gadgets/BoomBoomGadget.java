package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.lushmc.core.utils.chat.CoreChatUtils;
import net.lushmc.core.utils.items.CustomItem;
import net.lushmc.core.utils.particles.formats.DotFormat;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;
import net.lushmc.gadgets.utils.Utils;

public class BoomBoomGadget extends Gadget {

	DotFormat format = new DotFormat();

	public BoomBoomGadget(String id) {
		super(id);
	}

	@Override
	public void init() {
		/*
		 * Create Item
		 */
		item = new CustomItem(Material.TNT);
		;
		item.setDisplayName(CoreChatUtils.fade("FFFFFF", "FFFF55", "BOOM", true, false, false, false, false)
				+ CoreChatUtils.fade("FFFF55", "FF5555", " BOOM", true, false, false, false, false));
//		item.setDisplayName("&F&LBO&E&LOM &6&LBO&C&LOM");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		lore.add("&8-------------");
		lore.add("&c&lRIGHT CLICK&7 to throw.");
		item.setLore(lore);
	}

	@Override
	public void activate(Player player, GadgetAction action) {
		Item bomb = player.getWorld().dropItem(player.getEyeLocation(), item.getItem(player));
		bomb.setPickupDelay(Integer.MAX_VALUE);
		bomb.setVelocity(player.getEyeLocation().getDirection());
		Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(),
				new ExplosionRunnable(bomb, new Date().getTime()), 0);

	}

	private class ExplosionRunnable implements Runnable {

		long started;
		Item item;

		public ExplosionRunnable(Item item, long started) {
			this.item = item;
			this.started = started;
		}

		@Override
		public void run() {
			if (item.isOnGround()
					|| !item.getLocation().add(item.getVelocity()).getBlock().getType().equals(Material.AIR)) {
				Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
					item.getWorld().createExplosion(item.getLocation(), 5f, false, false, item);
					item.remove();
				}, 0);
				return;
			}
			format.spawnParticle(Particle.END_ROD, item.getLocation());
//			item.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, item.getLocation(), 1, 0, 0, 0, 2);
			Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), this, 0);
		}

	}

}

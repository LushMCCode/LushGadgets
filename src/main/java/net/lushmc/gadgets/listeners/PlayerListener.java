package net.lushmc.gadgets.listeners;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import net.lushmc.core.utils.CoreUtils;
import net.lushmc.gadgets.utils.GadgetUtils;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;
import net.lushmc.gadgets.utils.Utils;
import net.lushmc.gadgets.utils.gadgets.BoomBoomGadget;
import net.lushmc.gadgets.utils.gadgets.Gadget;

public class PlayerListener implements Listener {

	public PlayerListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onTabComplete(TabCompleteEvent e) {
		if (e.getBuffer().contains("/about")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getPlayer().getInventory().getItemInMainHand() == null)
			return;
		if (e.getPlayer().getInventory().getItemInMainHand().getType() == null
				|| e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR))
			return;
		if (GadgetAction.fromEvent(e) != null) {
			if (GadgetUtils.getGadget(e.getPlayer()) != null) {
				Bukkit.broadcastMessage("test");
				GadgetUtils.getGadget(e.getPlayer()).activate(e.getPlayer(), GadgetAction.fromEvent(e));
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			long time = new Date().getTime();
			e.getEntity().setMetadata("last_damage_cause", new FixedMetadataValue(Utils.getPlugin(), e.getCause()));
			e.getEntity().setMetadata("last_damage_time", new FixedMetadataValue(Utils.getPlugin(), time));

			Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
				if (e.getEntity().hasMetadata("last_damage_time")
						&& e.getEntity().getMetadata("last_damage_time").get(0).value().equals(time)) {
					e.getEntity().removeMetadata("last_damager", Utils.getPlugin());
					e.getEntity().removeMetadata("last_damage_cause", Utils.getPlugin());
					e.getEntity().removeMetadata("last_damage_time", Utils.getPlugin());
				}
			}, 3 * 20);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
			if (e.getEntity() instanceof Player) {

				// If there are any errors with detecting who killed who, check to see if player
				// has metadata, remove it, then add new metadata
				e.getEntity().setMetadata("last_damager", new FixedMetadataValue(Utils.getPlugin(), e.getDamager()));

			}
			if (e.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
				if (!(e.getEntity() instanceof Player))
					e.setCancelled(true);
				else {
					if (e.getDamager().hasMetadata("thrower")
							&& ((Player) e.getDamager().getMetadata("thrower").get(0).value()).equals(e.getEntity())) {
						e.setDamage(0.1);
					}
				}
			}
		}, 1);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getEntity().hasMetadata("last_damage_time")) {
			Entity damager = (Entity) e.getEntity().getMetadata("last_damager").get(0).value();
			DamageCause cause = (DamageCause) e.getEntity().getMetadata("last_damage_cause").get(0).value();
			String p1 = e.getEntity().getName();
			String p2 = damager instanceof Player ? damager.getName() : "";
			String a = "";
			String x = "";
			String c1 = "&a";
			String c2 = "&7";
			if (damager.hasMetadata("gadget")) {
				Gadget g = (Gadget) damager.getMetadata("gadget").get(0).value();
				if (g instanceof BoomBoomGadget)
					p2 = ((Player) damager.getMetadata("thrower").get(0).value()).getName();

			}

			switch (cause) {
			case LAVA:
				a = "tried to swim in lava fighting";
				break;
			case FIRE:
			case FIRE_TICK:
				a = "burned to a crisp fighting";
				break;
			case BLOCK_EXPLOSION:
			case ENTITY_EXPLOSION:
				a = "was blown up by";
				break;
			case FALL:
				a = "fell " + c1 + ((int) e.getEntity().getFallDistance()) + c2 + " blocks to their doom fighting";
				break;
			case DROWNING:
				a = "drowned fighting";
				break;
			case PROJECTILE:
				a = "shot by";
				x = "from " + c1 + e.getEntity().getLocation()
						.distance(((Entity) ((Projectile) damager).getShooter()).getLocation()) + c2 + " blocks away";
				break;
			case VOID:
				a = "fell into the void fighting";
				break;
			case THORNS:
				a = "was pricked to death by";
				break;
			default:
				a = "was killed by";
				break;
			}

			Bukkit.broadcastMessage(CoreUtils.colorize(c1 + p1 + c2 + " " + a + " " + c1 + (p2 == "" ? "something" : p2)
					+ c2 + (x == "" ? "." : " " + x + ".")));
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		e.setDropItems(false);
	}

	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		if (e.getEntity().hasMetadata("gadget")) {
			e.getEntity().remove();
			HashMap<Location, BlockData> blocks = new HashMap<>();
			for (Block block : e.blockList()) {
				blocks.put(block.getLocation(), block.getBlockData());
				block.getLocation().getBlock().setType(Material.BARRIER);
			}
			Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), new ExplosionRepair(blocks), 20);

		}
	}

	public class ExplosionRepair implements Runnable {

		HashMap<Location, BlockData> blocks;

		public ExplosionRepair(HashMap<Location, BlockData> blocks) {
			this.blocks = blocks;
		}

		@Override
		public void run() {
			Location loc = null;
			for (Entry<Location, BlockData> e : blocks.entrySet()) {
				loc = e.getKey();
				loc.getBlock().setBlockData(e.getValue());
				break;
			}

			if (loc != null) {
				blocks.remove(loc);
				Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), this,
						(new Random().nextInt(1) + 1) * (new Random().nextInt(5) + 1));
			}

		}
	}

}

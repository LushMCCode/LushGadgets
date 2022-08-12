package net.lushmc.gadgets.listeners;

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
				GadgetUtils.getGadget(e.getPlayer()).activate(e.getPlayer(), GadgetAction.fromEvent(e));
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player)
			e.getEntity().setMetadata("last_damage_cause", new FixedMetadataValue(Utils.getPlugin(), e.getCause()));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			// If there are any errors with detecting who killed who, check to see if player
			// has metadata, remove it, then add new metadata
			e.getEntity().setMetadata("last_damager", new FixedMetadataValue(Utils.getPlugin(), e.getDamager()));
			e.getEntity().setMetadata("last_damage_cause", new FixedMetadataValue(Utils.getPlugin(), e.getCause()));
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
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getEntity().hasMetadata("last_damager")) {
			Entity damager = (Entity) e.getEntity().getMetadata("last_damager").get(0).value();
			DamageCause cause = (DamageCause) e.getEntity().getMetadata("last_damage_cause").get(0).value();
			String p1 = e.getEntity().getName();
			String p2 = damager instanceof Player ? damager.getName() : "";
			String a = "";
			String x = "";
			if (damager.hasMetadata("gadget")) {
				Gadget g = (Gadget) damager.getMetadata("gadget").get(0).value();
				if (g instanceof BoomBoomGadget)
					p2 = ((Player) damager.getMetadata("thrower").get(0).value()).getName();

			}

			switch (cause) {
			case BLOCK_EXPLOSION:
			case ENTITY_EXPLOSION:
				a = "blown up by";
				break;
			}

			Bukkit.broadcastMessage(CoreUtils.colorize(
					"&a" + p1 + "&7" + " was " + a + " " + "&a" + p2 + "&7" + (x == "" ? "." : " " + x + ".")));
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

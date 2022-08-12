package net.lushmc.gadgets.listeners;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.lushmc.gadgets.utils.GadgetUtils;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;
import net.lushmc.gadgets.utils.Utils;

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
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
			if (!(e.getEntity() instanceof Player))
				e.setCancelled(true);
			else {
				if (e.getDamager().hasMetadata("thrower")
						&& ((Player) e.getDamager().getMetadata("thrower").get(0).value()).equals(e.getEntity())) {
					e.setCancelled(true);
				}
			}
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

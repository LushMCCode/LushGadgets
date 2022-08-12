package net.lushmc.gadgets.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
	public void onExplosion(EntityExplodeEvent e) {
		if (e.getEntity().hasMetadata("gadget")) {
			e.getEntity().remove();
			Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), new ExplosionRepair(e.blockList()), 20);

		}
	}

	public class ExplosionRepair implements Runnable {

		List<Block> blocks;

		public ExplosionRepair(List<Block> blocks) {
			this.blocks = blocks;
		}

		@Override
		public void run() {
			Block block = blocks.get(new Random().nextInt(blocks.size() - 1));
			Block other = block.getLocation().getBlock();
			Bukkit.broadcastMessage("Block: " + block.getType());
			Bukkit.broadcastMessage("Other: " + other.getType());
			other.setType(block.getType());
			other.setBlockData(block.getBlockData());
			blocks.remove(block);
			if (!blocks.isEmpty())
				Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), this, (new Random().nextInt(4) + 1) * 20);

		}
	}

}

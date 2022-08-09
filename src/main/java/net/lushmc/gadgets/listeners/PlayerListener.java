package net.lushmc.gadgets.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.lushmc.gadgets.utils.GadgetUtils;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;

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

}

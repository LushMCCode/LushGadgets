package net.lushmc.gadgets.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.lushmc.core.utils.CoreUtils;
import net.lushmc.gadgets.commands.listeners.AdminCommandTabCompleter;
import net.lushmc.gadgets.utils.GadgetUtils;

public class AdminCommands implements CommandExecutor {

	public AdminCommands(JavaPlugin plugin, String... cmd) {
		for (String s : cmd) {
			PluginCommand com = plugin.getCommand(s);
			com.setExecutor(this);
			com.setTabCompleter(new AdminCommandTabCompleter());
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gadget")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("player only command.");
				return true;
			}

			if (!sender.hasPermission("lush.admin")) {
				sender.sendMessage(
						CoreUtils.prefixes("admin") + CoreUtils.colorize("You don't have permission for that."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(CoreUtils.prefixes("gadgets") + "Try /gadget <gadget-id> [player]");
				return true;
			}

			if (GadgetUtils.getGadget(args[0]) != null) {
				Player player = args.length == 1 ? (Player) sender : Bukkit.getPlayer(args[1]);
				if (player == null) {
					sender.sendMessage(CoreUtils.prefixes("gadgets") + "That player doesn't seem to be online.");
					return true;
				}
				player.getInventory().setItem(0, GadgetUtils.getGadget(args[0]).getCustomItem().getItem(player));
			}

		}
		return true;
	}
}

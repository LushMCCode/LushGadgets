package net.lushmc.gadgets.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import net.lushmc.core.utils.CoreUtils;
import net.lushmc.gadgets.commands.listeners.AdminCommandTabCompleter;

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
			if (!sender.hasPermission("lush.admin")) {
				sender.sendMessage(
						CoreUtils.prefixes("admin") + CoreUtils.colorize("You don't have permission for that."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(CoreUtils.prefixes("gadgets") + "Try /gadget <gadget-id> [player]");
				return true;
			}

		}
		return true;
	}
}

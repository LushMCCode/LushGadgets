package net.lushmc.gadgets.commands.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.lushmc.gadgets.utils.GadgetUtils;

public class AdminCommandTabCompleter implements TabCompleter {

	public AdminCommandTabCompleter() {
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1)
			if (cmd.getName().equalsIgnoreCase("gadget")) {
				StringUtil.copyPartialMatches(args[0], getGadgets(), completions);
			}
		if (args.length == 2) {
			if (cmd.getName().equalsIgnoreCase("gadget")) {
				StringUtil.copyPartialMatches(args[1], getOnlinePlayers(), completions);
			}
		}

		return completions;

	}

	public List<String> getGadgets() {
		List<String> gadgets = new ArrayList<>();
		for (String id : GadgetUtils.getGadgets().keySet()) {
			gadgets.add(id);
		}
		return gadgets;
	}

	public List<String> getOnlinePlayers() {
		List<String> players = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			players.add(player.getName());
		}
		return players;
	}

}

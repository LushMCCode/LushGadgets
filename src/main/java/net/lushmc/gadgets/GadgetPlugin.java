package net.lushmc.gadgets;

import org.bukkit.plugin.java.JavaPlugin;

import net.lushmc.gadgets.commands.AdminCommands;

public class GadgetPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		new AdminCommands(this, "gadget");

	}

}

package net.lushmc.gadgets;

import org.bukkit.plugin.java.JavaPlugin;

import net.lushmc.gadgets.commands.AdminCommands;
import net.lushmc.gadgets.listeners.PlayerListener;
import net.lushmc.gadgets.utils.Utils;

public class GadgetPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		Utils.init(this);

		new AdminCommands(this, "gadget");

		new PlayerListener(this);

	}

}

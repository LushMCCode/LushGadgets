package net.lushmc.gadgets;


import org.bukkit.plugin.java.JavaPlugin;

import net.lushmc.core.commands.AdminCommands;
import net.lushmc.core.commands.PlayerCommands;
import net.lushmc.core.utils.CoreUtils;


public class GadgetPlugin extends JavaPlugin {

	@Override
	public void onEnable() {

		CoreUtils.init(this);

		new AdminCommands(this, "lush");
		new PlayerCommands(this, "console");
	}

}

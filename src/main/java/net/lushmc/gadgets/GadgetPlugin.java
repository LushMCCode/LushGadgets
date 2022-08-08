package net.lushmc.gadgets;

public class GadgetPlugin extends JavaPlugin {

	@Override
	public void onEnable() {

		CoreUtils.init(this);

		new AdminCommands(this, "lush");
		new PlayerCommands(this, "console");
	}

}

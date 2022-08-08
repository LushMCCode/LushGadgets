package net.lushmc.gadgets;

<<<<<<< Updated upstream
public class GadgetPlugin {
=======
import org.bukkit.plugin.java.JavaPlugin;

public class GadgetPlugin extends JavaPlugin {

	@Override
	public void onEnable() {

		CoreUtils.init(this);

		new AdminCommands(this, "lush");
		new PlayerCommands(this, "console");
	}
>>>>>>> Stashed changes

}

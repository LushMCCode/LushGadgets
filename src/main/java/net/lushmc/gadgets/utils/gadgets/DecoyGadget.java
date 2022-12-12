package net.lushmc.gadgets.utils.gadgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.lushmc.core.utils.CoreUtils;
import net.lushmc.core.utils.CosmeticUtils;
import net.lushmc.core.utils.DebugUtils;
import net.lushmc.core.utils.CosmeticUtils.GenericCooldownRunnable;
import net.lushmc.core.utils.items.CustomItem;
import net.lushmc.core.utils.particles.formats.DotFormat;
import net.lushmc.gadgets.utils.GadgetUtils.GadgetAction;
import net.lushmc.gadgets.utils.Utils;
import net.lushmc.gadgets.utils.npcs.NPCUtil;

public class DecoyGadget extends Gadget {

	DotFormat format = new DotFormat();

	public DecoyGadget() {
		super("decoy");
	}

	@Override
	public void init() {

		/*
		 * Create Item
		 */
		item = new CustomItem(Material.GOLD_INGOT);
		item.setCustomModelData(10002);
		item.setDisplayName("&a&lDecoy");
//		item.setDisplayName("&F&LBO&E&LOM &6&LBO&C&LOM");
		List<String> lore = new ArrayList<>();
		lore.add("&7Gadget-ID: " + id);
		lore.add("&8-------------");
		lore.add("&a&lRIGHT CLICK&7 to deploy.");
		item.setLore(lore);

		/*
		 * Create BossBar
		 */
		cooldownbar = Bukkit.createBossBar(CoreUtils.colorize(item.getDisplayName() + " &a&lCooldown"), BarColor.GREEN,
				BarStyle.SOLID);

	}

	@Override
	public void activate(Player player, GadgetAction action) {
		if (CosmeticUtils.getGenericCooldown("decoygadget").contains(player.getUniqueId())) {
			return;
		}
		CosmeticUtils.getGenericCooldown("decoygadget").add(player.getUniqueId());
		Bukkit.getScheduler().runTaskLater(Utils.getPlugin(),
				new GenericCooldownRunnable(cooldownbar, "decoygadget", player.getUniqueId(), new Date().getTime(),
						DebugUtils.isDebugger(player.getUniqueId()) ? -1 : 10, () -> {
						}),
				1);

		NPC npc = NPCUtil.createNPC(player.getName(), player.getName(), player.getLocation());
		npc.setProtected(false);
		for (int i = 0; i < new Random().nextInt(30) + 70; i++) {
			if (new Random().nextDouble() < 0.33)
				player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation().clone().add(
						new Random().nextInt(3) * (new Random().nextBoolean() ? 1 : -1), new Random().nextInt(3),
						new Random().nextInt(3) * (new Random().nextBoolean() ? 1 : -1)), 1, 0, 0, 0, 0);
		}
		npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.BOOTS, player.getEquipment().getBoots());
		npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.CHESTPLATE, player.getEquipment().getChestplate());
		npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.LEGGINGS, player.getEquipment().getLeggings());
		npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HELMET, player.getEquipment().getHelmet());

		npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HAND, player.getEquipment().getItemInMainHand());
		npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.OFF_HAND, player.getEquipment().getItemInOffHand());

		Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), new DecoyRunnable(npc, player), 0);
	}

	private class DecoyRunnable implements Runnable {

		NPC npc;
		Player player;
		long started;
		ItemStack[] equipment;

		public DecoyRunnable(NPC npc, Player player) {
			this.npc = npc;
			this.player = player;
			started = new Date().getTime();
			equipment = player.getEquipment().getArmorContents();
			player.getEquipment().clear();
		}

		@Override
		public void run() {
			if (TimeUnit.SECONDS.convert(new Date().getTime() - started, TimeUnit.MILLISECONDS) < 10
					&& npc.isSpawned()) {
				Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
					player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1, false, true, false));
				}, 0);

				Bukkit.getScheduler().runTaskLaterAsynchronously(Utils.getPlugin(), this, 0);
				return;
			}
			Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), () -> {
				npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HAND, new ItemStack(Material.TOTEM_OF_UNDYING, 1));
				npc.despawn();
				npc.destroy();
				player.getEquipment().setArmorContents(equipment);
			}, 0);

		}

	}

}

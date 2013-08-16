package com.gmail.emertens.nodropsatspawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import us.talabrek.ultimateskyblock.uSkyBlock;

/**
 * Plug-in for restricting item drops and item pickups at the spawn point.
 * @author Eric Mertens <emertens@gmail.com>
 *
 */
public final class NoDropsAtSpawn extends JavaPlugin implements Listener {

	private uSkyBlock uSkyBlockPlugin;

	/**
	 * Optional message sent to player when item drop is denied.
	 */
	private String noDropMsg;

	/**
	 * Optional message sent to player when item pickup is denied.
	 */
	private String noPickupMsg;

	/**
	 * Bukkit permission for overriding this plug-in
	 */
	private static final String OVERRIDE_PERMISSION = "nodropsatspawn.override";

	/**
	 * Event handler for restricting item drops
	 * @param event Item drop event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		restrictEvent(event, noDropMsg);
	}

	/**
	 * Event handler for restricting item pickups
	 * @param event Item pickup event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		restrictEvent(event, noPickupMsg);
	}

	/**
	 * Cancel an event if the given player is in the spawn island and does not have
	 * override permission.
	 * @param event An event thrown by a player that can be cancelled
	 * @param msg Message to send if event is restricted, ignored if null
	 */
	private <T extends PlayerEvent & Cancellable>
	void restrictEvent(final T event, final String msg) {

		final Player player = event.getPlayer();

		// Leave players alone on their own islands
		if (uSkyBlockPlugin.playerIsOnIsland(player)) {
			return;
		}

		// Ignore privileged players
		if (player.hasPermission(OVERRIDE_PERMISSION)) {
			return;
		}

		// Notify player and cancel event
		if (msg != null) { player.sendMessage(msg); }
		event.setCancelled(true);
		return;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		loadSettings();
		uSkyBlockPlugin = (uSkyBlock)Bukkit.getPluginManager().getPlugin("uSkyBlock");
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	/**
	 * Retrieve a colored string from the configuration file.
	 * @param key Configuration entry key
	 * @return Color translated value if key is found, null otherwise
	 */
	private String loadMessage(final String key) {
		final String val = getConfig().getString(key);
		return val == null ? null : ChatColor.translateAlternateColorCodes('&', val);
	}

	/**
	 * Populate plug-in settings from the configuration file.
	 */
	private void loadSettings() {
		noDropMsg = loadMessage("item-drop-message");
		noPickupMsg = loadMessage("item-pickup-message");
	}
}

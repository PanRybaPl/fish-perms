package pl.panryba.mc.permissions.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.panryba.mc.permissions.PluginApi;

public class PlayerListener implements Listener {

    private final PluginApi api;

    public PlayerListener(PluginApi api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.api.playerJoined(e.getPlayer());
    }

    // Keep HIGHEST priority so player permissions are still set when player quits and dies in hardcore
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        this.api.playerQuit(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent e) {
        this.api.playerKick(e.getPlayer());
    }
}

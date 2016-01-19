package pl.panryba.mc.permissions.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author PanRyba.pl
 */
public class PlayerGroupsChangedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public PlayerGroupsChangedEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }
}

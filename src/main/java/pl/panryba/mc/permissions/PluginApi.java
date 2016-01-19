package pl.panryba.mc.permissions;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import pl.panryba.mc.permissions.events.PlayerGroupsChangedEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PluginApi {
    private final Plugin plugin;
    private final EbeanServer db;

    private Permissions permissions;
    private Map<Player, PermissionAttachment> attachments;

    public PluginApi(EbeanServer db, Plugin plugin) {
        this.db = db;
        this.plugin = plugin;
        this.attachments = new HashMap<>();

        readConfig();
    }

    private void readConfig() {
        FileConfiguration config = this.plugin.getConfig();

        PluginConfig pluginConfig = new PluginConfig();
        pluginConfig.setDefaultGroup(config.getString("default.group"));

        this.permissions = new Permissions(db, pluginConfig);

        reset();
        for(Player player : Bukkit.getOnlinePlayers()) {
            loadPlayerPermissions(player);
        }
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.readConfig();
    }

    public Set<Map.Entry<String, Boolean>> getEffectivePlayerPermissions(Player player) {
        return this.permissions.getEffectivePlayerPermissions(player.getName());
    }

    public Map<String, Boolean> getPlayerPermissions(String playerName) {
        return this.permissions.getPlayerPermissions(playerName);
    }

    public Set<String> getPlayerGroups(String playerName) {
        return this.permissions.getPlayerGroups(playerName);
    }

    public boolean addPlayerToGroup(String playerName, String groupName) {
        return addPlayerToGroup(playerName, groupName, null);
    }

    public boolean addPlayerToGroup(String playerName, String groupName, Date validity) {
        boolean result = this.permissions.addPlayerToGroup(playerName, groupName, validity);

        if (result) {
            reloadPlayerPermissions(playerName);
            notifyPlayerGroupsChanged(playerName);
        }

        return result;
    }

    private void notifyPlayerGroupsChanged(String playerName) {
        try {
            Player player = Bukkit.getPlayerExact(playerName);
            if(player != null) {
                PlayerGroupsChangedEvent event = new PlayerGroupsChangedEvent(player);
                player.getServer().getPluginManager().callEvent(event);
            }
        } catch (Exception ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reloadPlayerPermissions(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if(player == null) {
            return;
        }

        removePlayerAttachment(player);
        loadPlayerPermissions(player);
    }

    public boolean removePlayerFromGroup(String playerName, String groupName) {
        boolean result = this.permissions.removePlayerFromGroup(playerName, groupName);

        if (result) {
            reloadPlayerPermissions(playerName);
            notifyPlayerGroupsChanged(playerName);
        }

        return result;
    }

    public boolean setPlayerPermission(String playerName, String permissionName, Boolean allowed) {
        return this.permissions.setPlayerPermission(playerName, permissionName, allowed);
    }

    public boolean setGroupPermission(String groupName, String permissionName, Boolean allowed) {
        return this.permissions.setGroupPermission(groupName, permissionName, allowed);
    }

    public boolean updateGroupParent(String groupName, String parentName, boolean isParent) {
        return this.permissions.updateGroupParent(groupName, parentName, isParent);
    }

    public void playerJoined(Player player) {
        loadPlayerPermissions(player);
    }

    private void loadPlayerPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(this.plugin);
        attachments.put(player, attachment);

        PluginManager manager = this.plugin.getServer().getPluginManager();
        Set<Map.Entry<String, Boolean>> perms = getEffectivePlayerPermissions(player);

        for(Map.Entry<String, Boolean> perm : perms) {
            if(perm.getValue() != null) {
                attachment.setPermission(perm.getKey(), perm.getValue());
            } else {
                attachment.unsetPermission(perm.getKey());
            }
        }

        player.recalculatePermissions();
    }

    public void playerQuit(Player player) {
        playerLeft(player);
    }

    private void playerLeft(Player player) {
        this.permissions.unloadPlayer(player);
        removePlayerAttachment(player);
    }

    public void playerKick(Player player) {
        playerLeft(player);
    }

    private void removePlayerAttachment(Player player) {
        PermissionAttachment attachment = attachments.remove(player);
        if(attachment == null) {
            return;
        }

        player.removeAttachment(attachment);
    }

    public void reset() {
        for(PermissionAttachment info : this.attachments.values()) {
            info.remove();
        }

        this.attachments.clear();
    }

    public void reloadGroupPlayersPermissions(String groupName) {
        for(String playerName : this.permissions.getGroupAndDerivedPlayers(groupName)) {
            reloadPlayerPermissions(playerName);
        }
    }
}

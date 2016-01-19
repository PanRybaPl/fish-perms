package pl.panryba.mc.permissions;

import com.avaje.ebean.EbeanServer;
import org.bukkit.entity.Player;
import pl.panryba.mc.permissions.entities.*;

import java.util.*;

public class Permissions {
    private final PluginConfig config;
    private final PermissionsCache cache;

    public Permissions(EbeanServer db, PluginConfig config) {
        this.config = config;
        this.cache = new PermissionsCache(db);
    }

    public Set<Map.Entry<String, Boolean>> getEffectivePlayerPermissions(String playerName) {
        PermsPlayer permsPlayer = this.cache.getOrCreatePlayer(playerName);

        if(permsPlayer == null) {
            return new HashSet<>();
        }

        PermissionSet playerSet = new PermissionSet();
        Date now = new Date();

        Set<PermsGroup> groups = getPlayerGroups(permsPlayer, now);

        for(PermsGroup group : groups) {
            Set<GroupPermission> groupPermissions = group.getPermissions();

            for(GroupPermission groupPermission : groupPermissions) {
                playerSet.set(groupPermission.getName(), groupPermission.isAllowed());
            }
        }

        for(PlayerPermission permission : permsPlayer.getPermissions()) {
            playerSet.set(permission.getName(), permission.isAllowed());
        }

        return playerSet.getPermissions();
    }

    private Set<PermsGroup> getPlayerGroups(PermsPlayer permsPlayer, Date now) {
        Set<PermsGroup> groups = new LinkedHashSet<>();

        for(PlayerGroup playerGroup : permsPlayer.getGroups()) {
            if(!playerGroup.isValid(now)) {
                continue;
            }

            Set<PermsGroup> parentGroups = new LinkedHashSet<>();
            addGroups(playerGroup.getGroup(), parentGroups);

            PermsGroup[] parentGroupsArray = new PermsGroup[parentGroups.size()];
            parentGroups.toArray(parentGroupsArray);

            for(int i = parentGroupsArray.length - 1; i >= 0; --i) {
                groups.add(parentGroupsArray[i]);
            }
        }

        if(groups.isEmpty()) {
            String defaultGroupName = this.config.getDefaultGroup();
            if(defaultGroupName != null && !defaultGroupName.isEmpty()) {
                PermsGroup defaultGroup = this.cache.getGroup(defaultGroupName);

                if (defaultGroup != null) {
                    groups.add(defaultGroup);
                }
            }
        }

        return groups;
    }

    private void addGroups(PermsGroup group, Set<PermsGroup> groupNames) {
        groupNames.add(group);

        for(PermsGroupParent parent : group.getParents()) {
            addGroups(parent.getParent(), groupNames);
        }
    }

    public Map<String, Boolean> getPlayerPermissions(String playerName) {
        Map<String, Boolean> result = new LinkedHashMap<>();

        PermsPlayer player = this.cache.getOrCreatePlayer(playerName);

        if(player == null) {
            return result;
        }

        for(PlayerPermission permission : player.getPermissions()) {
            result.put(permission.getName(), permission.isAllowed());
        }

        return result;
    }

    public Set<String> getPlayerGroups(String playerName) {
        PermsPlayer player = this.cache.getOrCreatePlayer(playerName);
        Set<PermsGroup> playerGroups = getPlayerGroups(player, new Date());

        Set<String> names = new HashSet<>();
        for(PermsGroup group : playerGroups) {
            names.add(group.getName().toLowerCase());
        }

        return names;
    }

    public boolean addPlayerToGroup(String playerName, String groupName, Date validity) {
        PermsGroup group = this.cache.getOrCreateGroup(groupName);
        PermsPlayer player = this.cache.getOrCreatePlayer(playerName);

        this.cache.addPlayerToGroup(player, group, validity);
        return true;
    }

    public boolean removePlayerFromGroup(String playerName, String groupName) {
        PermsPlayer permsPlayer = this.cache.getOrCreatePlayer(playerName);

        if(permsPlayer == null) {
            return false;
        }

        PermsGroup group = this.cache.getGroup(groupName);

        if(group == null) {
            return false;
        }

        this.cache.removePlayerFromGroup(permsPlayer, group);
        return true;
    }

    public boolean setPlayerPermission(String playerName, String permissionName, Boolean allowed) {
        PermsPlayer player = this.cache.getOrCreatePlayer(playerName);

        if(allowed == null) {
            this.cache.removePlayerPermission(player, permissionName);
        } else {
            this.cache.setPlayerPermission(player, permissionName, allowed.booleanValue());
        }
        return true;
    }

    public boolean updateGroupParent(String groupName, String parentName, boolean isParent) {
        PermsGroup group = this.cache.getOrCreateGroup(groupName);
        PermsGroup parent = this.cache.getOrCreateGroup(parentName);

        if(isParent) {
            this.cache.addGroupParent(group, parent);
        } else {
            this.cache.removeGroupParent(group, parent);
        }

        return true;
    }

    public boolean setGroupPermission(String groupName, String permissionName, Boolean allowed) {
        PermsGroup group = this.cache.getOrCreateGroup(groupName);

        if(allowed == null) {
            this.cache.removeGroupPermission(group, permissionName);
        } else {
            this.cache.setGroupPermission(group, permissionName, allowed.booleanValue());
        }
        return true;
    }

    public Set<String> getGroupAndDerivedPlayers(String groupName) {
        Set<String> result = new HashSet<>();

        PermsGroup permsGroup = this.cache.getGroup(groupName);
        if(permsGroup == null) {
            return result;
        }

        Set<PermsGroup> thisAndDerivedGroups = this.cache.getDerivedGroups(permsGroup);
        thisAndDerivedGroups.add(permsGroup);

        for(PermsGroup group : thisAndDerivedGroups) {
            Set<PlayerGroup> playerGroups = this.cache.getPlayersGroups(group);

            for (PlayerGroup playerGroup : playerGroups) {
                result.add(playerGroup.getPlayer().getName());
            }
        }

        return result;
    }

    public void unloadPlayer(Player player) {
        this.cache.unloadPlayer(player.getName());
    }
}

package pl.panryba.mc.permissions;

import com.avaje.ebean.EbeanServer;
import pl.panryba.mc.permissions.entities.*;

import java.util.*;

public class PermissionsCache {
    private EbeanServer db;
    private Map<String, PermsGroup> groups;
    private Map<String, PermsPlayer> players;

    public PermissionsCache(EbeanServer db) {
        this.db = db;

        this.groups = new HashMap<>();
        this.players = new HashMap<>();

        for(PermsGroup group : this.db.find(PermsGroup.class).findSet()) {
            this.groups.put(group.getName().toLowerCase(), group);
        }
    }

    public PermsPlayer getOrCreatePlayer(String name) {
        String lowerName = name.toLowerCase();
        PermsPlayer player = this.players.get(lowerName);

        if(player == null) {
            player = this.db.find(PermsPlayer.class).where().eq("name", name).findUnique();

            if(player == null) {
                player = new PermsPlayer();
                player.setName(name);

                this.db.save(player);
            }

            this.players.put(lowerName, player);
        }

        return player;
    }

    public PermsGroup getOrCreateGroup(String name) {
        String lowerName = name.toLowerCase();
        PermsGroup group = this.groups.get(lowerName);

        if(group == null) {
            group = new PermsGroup();
            group.setName(name);

            this.db.save(group);
            this.groups.put(lowerName, group);
        }

        return group;
    }

    public PermsGroup getGroup(String name) {
        return this.groups.get(name.toLowerCase());
    }

    public PermsPlayer unloadPlayer(String name) {
        return this.players.remove(name.toLowerCase());
    }

    public void addPlayerToGroup(PermsPlayer player, PermsGroup group, Date validity) {
        PlayerGroup playerGroup = player.findGroup(group);

        if(playerGroup == null) {
            playerGroup = new PlayerGroup();
            playerGroup.setGroup(group);
            playerGroup.setPlayer(player);

            player.addGroup(playerGroup);
        }

        playerGroup.setValidity(validity);
        this.db.save(playerGroup);
    }

    public void removePlayerFromGroup(PermsPlayer player, PermsGroup group) {
        PlayerGroup toRemove = player.findGroup(group);
        if(toRemove == null) {
            return;
        }

        player.removeGroup(toRemove);
        this.db.delete(toRemove);
    }

    public void setPlayerPermission(PermsPlayer player, String permissionName, boolean allowed) {
        PlayerPermission permission = player.findPermission(permissionName);

        if(permission == null) {
            permission = new PlayerPermission();
            permission.setPlayer(player);
            permission.setAllowed(allowed);
            permission.setName(permissionName);

            player.addPermission(permission);
        }

        permission.setAllowed(allowed);
        this.db.save(permission);
    }

    public void removePlayerPermission(PermsPlayer player, String permissionName) {
        PlayerPermission permission = player.findPermission(permissionName);

        if(permission == null) {
            // Already removedRealizowane
            return;
        }

        player.removePermission(permission);
        this.db.delete(permission);
    }

    public void addGroupParent(PermsGroup group, PermsGroup parent) {
        PermsGroupParent groupParent = group.findParent(parent);

        if(groupParent != null) {
            return;
        }

        groupParent = new PermsGroupParent();
        groupParent.setParent(parent);
        groupParent.setGroup(group);

        this.db.save(groupParent);
        group.addParent(groupParent);
    }

    public void removeGroupParent(PermsGroup group, PermsGroup parent) {
        PermsGroupParent groupParent = group.findParent(parent);

        if(groupParent == null) {
            return;
        }

        group.removeParent(groupParent);
        this.db.delete(groupParent);
    }

    public void setGroupPermission(PermsGroup group, String permissionName, boolean allowed) {
        GroupPermission permission = group.findPermission(permissionName);

        if(permission == null) {
            permission = new GroupPermission();
            permission.setGroup(group);
            permission.setAllowed(allowed);
            permission.setName(permissionName);

            group.addPermission(permission);
        }

        permission.setAllowed(allowed);
        this.db.save(permission);
    }

    public void removeGroupPermission(PermsGroup group, String permissionName) {
        GroupPermission permission = group.findPermission(permissionName);

        if(permission == null) {
            // Already removed
            return;
        }

        group.removePermission(permission);
        this.db.delete(permission);
    }

    public Set<PlayerGroup> getPlayersGroups(PermsGroup permsGroup) {
        return this.db.find(PlayerGroup.class)
                .where()
                .eq("group_id", permsGroup.getId())
                .findSet();
    }

    public Set<PermsGroup> getDerivedGroups(PermsGroup permsGroup) {
        Set<PermsGroup> result = new HashSet<>();

        for(PermsGroup group : this.groups.values()) {
           if(isDerivedFrom(group, permsGroup)) {
               result.add(group);
           }
        }

        return  result;
    }

    private boolean isDerivedFrom(PermsGroup group, PermsGroup permsGroup) {
        for(PermsGroupParent parent : group.getParents()) {
            PermsGroup parentGroup = parent.getParent();

            if(parentGroup.getId().equals(permsGroup.getId())) {
                return true;
            }

            if(isDerivedFrom(parentGroup, permsGroup)) {
                return true;
            }
        }

        return false;
    }
}

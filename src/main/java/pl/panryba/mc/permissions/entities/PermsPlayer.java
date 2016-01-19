package pl.panryba.mc.permissions.entities;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "perms_players")
public class PermsPlayer {
    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<PlayerGroup> groups;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<PlayerPermission> permissions;

    @Version
    private Timestamp lastUpdate;

    public PermsPlayer() {
        this.groups = new LinkedHashSet<>();
        this.permissions = new LinkedHashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PlayerGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<PlayerGroup> groups) {
        this.groups = groups;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isInGroup(PermsGroup group) {
        for (PlayerGroup playerGroup : this.getGroups()) {
            if (playerGroup.getGroup().getId().equals(group.getId()))
                return true;
        }

        return false;
    }

    public Set<PlayerPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PlayerPermission> permissions) {
        this.permissions = permissions;
    }

    public void addGroup(PlayerGroup playerGroup) {
        this.getGroups().add(playerGroup);
    }

    public void removeGroup(PlayerGroup playerGroup) {
        this.getGroups().remove(playerGroup);
    }

    public void removePermission(PlayerPermission permission) {
        this.getPermissions().remove(permission);
    }

    public PlayerPermission findPermission(String permissionName) {
        for (PlayerPermission playerPermission : this.getPermissions()) {
            if (playerPermission.getName().equals(permissionName)) {
                return playerPermission;
            }
        }

        return null;
    }

    public void addPermission(PlayerPermission permission) {
        this.getPermissions().add(permission);
    }

    public PlayerGroup findGroup(PermsGroup group) {
        for(PlayerGroup playerGroup : this.getGroups()) {
            if(playerGroup.getGroup().getId().equals(group.getId())) {
                return playerGroup;
            }
        }

        return null;
    }
}

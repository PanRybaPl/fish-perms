package pl.panryba.mc.permissions.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "perms_groups")
public class PermsGroup  {
    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<GroupPermission> permissions;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<PermsGroupParent> parents;

    @Version
    private Timestamp lastUpdate;

    public PermsGroup() {
        this.permissions = new LinkedHashSet<>();
        this.parents = new LinkedHashSet<>();
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

    public Set<GroupPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<GroupPermission> permissions) {
        this.permissions = permissions;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<PermsGroupParent> getParents() {
        return parents;
    }

    public void setParents(Set<PermsGroupParent> parents) {
        this.parents = parents;
    }

    public void addParent(PermsGroupParent permsParent) {
        this.getParents().add(permsParent);
    }

    public void removeParent(PermsGroupParent groupParent) {
        this.getParents().remove(groupParent);
    }

    public PermsGroupParent findParent(PermsGroup parent) {
        for(PermsGroupParent groupParent : this.getParents()) {
            if(groupParent.getParent().getId().equals(parent.getId())) {
                return groupParent;
            }
        }

        return null;
    }

    public void removePermission(GroupPermission permission) {
        this.getPermissions().remove(permission);
    }

    public GroupPermission findPermission(String permissionName) {
        for (GroupPermission groupPermission : this.getPermissions()) {
            if (groupPermission.getName().equals(permissionName)) {
                return groupPermission;
            }
        }

        return null;
    }

    public void addPermission(GroupPermission permission) {
        this.getPermissions().add(permission);
    }
}

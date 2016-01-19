package pl.panryba.mc.permissions.entities;


import javax.persistence.*;

@Entity
@Table(name = "perms_group_permissions")
public class GroupPermission extends PermissionBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "group_id", nullable = false)
    private PermsGroup group;

    public PermsGroup getGroup() {
        return group;
    }
    public void setGroup(PermsGroup group) {
        this.group = group;
    }
}

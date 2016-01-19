package pl.panryba.mc.permissions.entities;

import javax.persistence.*;

@Entity
@Table(name = "perms_group_parents")
public class PermsGroupParent {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @Column(name = "group_id", nullable = false)
    private PermsGroup group;

    @ManyToOne(fetch = FetchType.EAGER)
    @Column(name = "parent_id", nullable = false)
    private PermsGroup parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PermsGroup getGroup() {
        return group;
    }

    public void setGroup(PermsGroup group) {
        this.group = group;
    }

    public PermsGroup getParent() {
        return parent;
    }

    public void setParent(PermsGroup parent) {
        this.parent = parent;
    }
}

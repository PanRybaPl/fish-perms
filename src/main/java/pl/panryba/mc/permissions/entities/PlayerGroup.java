package pl.panryba.mc.permissions.entities;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "perms_player_groups")
public class PlayerGroup {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "player_id", nullable = false)
    private PermsPlayer player;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "group_id", nullable = false)
    private PermsGroup group;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "validity", nullable = true)
    private Date validity;

    @Version
    private Timestamp lastUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PermsPlayer getPlayer() {
        return player;
    }

    public void setPlayer(PermsPlayer player) {
        this.player = player;
    }

    public PermsGroup getGroup() {
        return group;
    }

    public void setGroup(PermsGroup group) {
        this.group = group;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public boolean isValid(Date now) {
        return getValidity() == null || getValidity().getTime() >= now.getTime();
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

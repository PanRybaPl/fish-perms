package pl.panryba.mc.permissions.entities;

import javax.persistence.*;

@Entity
@Table(name = "perms_player_permissions")
public class PlayerPermission extends  PermissionBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "player_id", nullable = false)
    private PermsPlayer player;

    public PermsPlayer getPlayer() {
        return player;
    }
    public void setPlayer(PermsPlayer player) {
        this.player = player;
    }
}

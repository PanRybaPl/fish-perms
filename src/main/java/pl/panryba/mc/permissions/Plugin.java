package pl.panryba.mc.permissions;

import com.avaje.ebean.EbeanServer;
import pl.panryba.mc.db.FishDbPlugin;
import pl.panryba.mc.permissions.commands.PGCommand;
import pl.panryba.mc.permissions.commands.PPCommand;
import pl.panryba.mc.permissions.commands.PermCommand;
import pl.panryba.mc.permissions.entities.*;
import pl.panryba.mc.permissions.listeners.PlayerListener;

import java.util.List;

public class Plugin extends FishDbPlugin {
    PluginApi api;

    @Override
    public void onEnable() {
        super.onEnable();

        EbeanServer db = getCustomDatabase();
        api = new PluginApi(db, this);

        PermissionsManager.setup(api);

        getCommand("perm").setExecutor(new PermCommand(api));
        getCommand("pp").setExecutor(new PPCommand(api));
        getCommand("pg").setExecutor(new PGCommand(api));

        getServer().getPluginManager().registerEvents(new PlayerListener(api), this);
    }

    public static void fillDatabaseClasses(List<Class<?>> list) {
        list.add(PlayerPermission.class);
        list.add(GroupPermission.class);
        list.add(PermsGroup.class);
        list.add(PermsPlayer.class);
        list.add(PlayerGroup.class);
        list.add(PermsGroupParent.class);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = super.getDatabaseClasses();
        Plugin.fillDatabaseClasses(list);
        return list;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        api.reset();
    }
}

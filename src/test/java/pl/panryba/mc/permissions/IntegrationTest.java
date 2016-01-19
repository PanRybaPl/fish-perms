package pl.panryba.mc.permissions;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationTest {

    @Test
    public void testIntegration() {
        ServerConfig config = new ServerConfig();
        config.setName("h2test");

        DataSourceConfig db = new DataSourceConfig();
        db.setDriver("org.h2.Driver");
        db.setUsername("sa");
        db.setPassword("");
        db.setUrl("jdbc:h2:mem:tests;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2");

        config.setDataSourceConfig(db);

        config.setDdlGenerate(true);
        config.setDdlRun(true);
        config.setDefaultServer(false);
        config.setRegister(false);

        List<Class<?>> list = new ArrayList<>();
        Plugin.fillDatabaseClasses(list);
        for(Class<?> c : list) {
            config.addClass(c);
        }

        EbeanServer server = EbeanServerFactory.create(config);
        PluginConfig pluginConfig = new PluginConfig();
        pluginConfig.setDefaultGroup("default");

        Permissions pdb = new Permissions(server, pluginConfig);

        pdb.setGroupPermission("group1", "test.group.perm", true);
        pdb.setGroupPermission("default", "default.perm", true);

        pdb.addPlayerToGroup("player", "group1", null);
        pdb.addPlayerToGroup("player", "group3", new Date(new Date().getTime() - 1000));

        Set<String> groups = pdb.getPlayerGroups("player");
        assertEquals(1, groups.size());
        assertTrue(groups.contains("group1"));

        Set<String> players = pdb.getGroupAndDerivedPlayers("group1");
        assertEquals(1, players.size());
        assertTrue(players.contains("player"));

        pdb.setPlayerPermission("player", "test.permission", true);
        pdb.setGroupPermission("group2", "test.group2.perm", true);

        assertEquals(0, pdb.getGroupAndDerivedPlayers("group2").size());
        pdb.updateGroupParent("group1", "group2", true);
        assertEquals(1, pdb.getGroupAndDerivedPlayers("group2").size());

        Set<Map.Entry<String, Boolean>> perms = pdb.getEffectivePlayerPermissions("player");
        assertEquals(3, perms.size());

        groups = pdb.getPlayerGroups("player");
        assertEquals(2, groups.size());
        assertTrue(groups.contains("group1"));
        assertTrue(groups.contains("group2"));

        pdb.updateGroupParent("group1", "group2", false);
        groups = pdb.getPlayerGroups("player");
        assertEquals(1, groups.size());
        assertTrue(groups.contains("group1"));

        pdb.removePlayerFromGroup("player", "group1");
        groups = pdb.getPlayerGroups("player");
        assertEquals(1, groups.size());        //pdb.addPlayerToGroup("player", "group2", null);
        assertEquals(2, pdb.getEffectivePlayerPermissions("player").size());

        pdb.setPlayerPermission("player", "test.permission", false);
        assertEquals(2, pdb.getEffectivePlayerPermissions("player").size());

        pdb.setPlayerPermission("player", "test.permission", null);
        assertEquals(1, pdb.getEffectivePlayerPermissions("player").size());

        pdb.setGroupPermission("default", "default.perm", false);
        assertEquals(1, pdb.getEffectivePlayerPermissions("player").size());

        pluginConfig.setDefaultGroup(null);
        assertEquals(0, pdb.getEffectivePlayerPermissions("player").size());

        pdb = new Permissions(server, pluginConfig);
        pdb.setGroupPermission("default", "default.perm", null);
        assertEquals(0, pdb.getEffectivePlayerPermissions("player").size());

        groups = pdb.getPlayerGroups("player");
        assertEquals(0, groups.size());
    }
}

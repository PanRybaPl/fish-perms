package pl.panryba.mc.permissions;

/**
 * @author PanRyba.pl
 */
public class PermissionsManager {
    static PluginApi instance;

    static void setup(PluginApi api) {
        PermissionsManager.instance = api;
    }

    public static PluginApi getInstance() {
        return PermissionsManager.instance;
    }
}

package pl.panryba.mc.permissions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PermissionSet {
    private Map<String, Boolean> permissions;

    public PermissionSet() {
        this.permissions = new LinkedHashMap<>();
    }

    public Set<Map.Entry<String, Boolean>> getPermissions() {
        return this.permissions.entrySet();
    }

    public void set(String permission, boolean enabled) {
        this.permissions.put(permission, enabled);
    }
}

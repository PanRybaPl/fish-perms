package pl.panryba.mc.permissions.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import pl.panryba.mc.permissions.PluginApi;
import pl.panryba.mc.permissions.StringUtils;

import java.util.*;

public class PermissionsCommandHandler {
    private final PluginApi api;

    public PermissionsCommandHandler(PluginApi api) {
        this.api = api;
    }

    boolean handleReload(CommandSender commandSender) {
        this.api.reload();
        commandSender.sendMessage("Perms reloaded");

        return true;
    }

    boolean handleGroup(CommandSender commandSender, String[] strings) {
        if(strings.length < 2) {
            return false;
        }

        String[] args = new String[strings.length - 2];
        System.arraycopy(strings, 2, args, 0, args.length);

        switch(strings[1]) {
            case "parent":
                return handleGroupParent(commandSender, strings[0], args);
            case "enable":
                return handleGroupEnable(commandSender, strings[0], args);
            case "disable":
                return handleGroupDisable(commandSender, strings[0], args);
            case "remove":
                return handleGroupRemove(commandSender, strings[0], args);
        }

        return false;
    }

    private boolean handleGroupParent(CommandSender commandSender, String groupName, String[] strings) {
        if(strings.length < 2) {
            return false;
        }

        switch(strings[0]) {
            case "add":
                return handleGroupParentAdd(commandSender, groupName, strings[1]);
            case "remove":
                return handleGroupParentRemove(commandSender, groupName, strings[1]);
        }

        return false;
    }

    private boolean handleGroupParentRemove(CommandSender commandSender, String groupName, String parentName) {
        if(!this.api.updateGroupParent(groupName, parentName, false)) {
            commandSender.sendMessage("Failed to remove group parent");
        } else {
            commandSender.sendMessage("Group parent removed");
            this.api.reloadGroupPlayersPermissions(groupName);
        }

        return true;
    }

    private boolean handleGroupParentAdd(CommandSender commandSender, String groupName, String parentName) {
        if(!this.api.updateGroupParent(groupName, parentName, true)) {
            commandSender.sendMessage("Failed to add group parent");
        } else {
            commandSender.sendMessage("Group parent added");
            this.api.reloadGroupPlayersPermissions(groupName);
        }

        return true;
    }

    private boolean handleGroupEnable(CommandSender commandSender, String groupName, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        if(!api.setGroupPermission(groupName, strings[0], true)) {
            commandSender.sendMessage("Failed to enable permission for group");
        } else {
            commandSender.sendMessage("Permission enabled for group");
            this.api.reloadGroupPlayersPermissions(groupName);
        }

        return true;
    }

    private boolean handleGroupDisable(CommandSender commandSender, String groupName, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        if(!api.setGroupPermission(groupName, strings[0], false)) {
            commandSender.sendMessage("Failed to disable permission for group");
        } else {
            commandSender.sendMessage("Permission disabled for group");
            this.api.reloadGroupPlayersPermissions(groupName);
        }

        return true;
    }


    private boolean handleGroupRemove(CommandSender commandSender, String groupName, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        if(!api.setGroupPermission(groupName, strings[0], null)) {
            commandSender.sendMessage("Failed to remove permission from group");
        } else {
            commandSender.sendMessage("Permission removed from group");
            this.api.reloadGroupPlayersPermissions(groupName);
        }

        return true;
    }

    boolean handleUser(CommandSender commandSender, String[] strings) {
        if(strings.length == 1 ) {
            return handleUserInfo(commandSender, strings[0]);
        }

        if(strings.length < 2) {
            return false;
        }

        String[] args = new String[strings.length - 2];
        System.arraycopy(strings, 2, args, 0, args.length);

        switch(strings[1]) {
            case "groups":
                return handleUserGroups(commandSender, strings[0], args);
            case "perms":
                return handleUserPerms(commandSender, strings[0], args);
            case "group":
                return handleUserGroup(commandSender, strings[0], args);
            case "enable":
                return handleUserEnable(commandSender, strings[0], args);
            case "disable":
                return handleUserDisable(commandSender, strings[0], args);
            case "remove":
                return handleUserRemove(commandSender, strings[0], args);
        }

        return false;
    }

    private boolean handleUserInfo(CommandSender commandSender, String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) {
            commandSender.sendMessage("Player not found");
            return true;
        }

        List<String> msgs = new ArrayList<>();
        msgs.add("Player " + player.getName() + " effective permissions:");

        for(PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if(info.getValue()) {
                msgs.add("[+] " + info.getPermission());
            } else {
                msgs.add("[-] " + info.getPermission());
            }
        }

        String[] arr = new String[msgs.size()];
        msgs.toArray(arr);

        commandSender.sendMessage(arr);
        return true;
    }

    private boolean handleUserGroups(CommandSender commandSender, String playerName, String[] strings) {
        if(strings.length != 0) {
            return false;
        }

        Set<String> playerGroups = this.api.getPlayerGroups(playerName);
        if(playerGroups.isEmpty()) {
            commandSender.sendMessage("Player " + playerName + " has no groups");
        } else {
            String groupsStrings = StringUtils.join(playerGroups, ", ");
            commandSender.sendMessage("Player " + playerName + " groups: " + groupsStrings);
        }

        return true;
    }

    private boolean handleUserPerms(CommandSender commandSender, String playerName, String[] strings) {
        if(strings.length != 0) {
            return false;
        }

        Map<String, Boolean> perms = this.api.getPlayerPermissions(playerName);

        if(perms.isEmpty()) {
            commandSender.sendMessage("Player " + playerName + " has no specific permissions");
        } else {
            List<String> permStrings = new ArrayList<>();
            permStrings.add("Player " + playerName + " specific permissions:");

            for(Map.Entry<String, Boolean> entry : perms.entrySet()) {
                if(entry.getValue().booleanValue()) {
                    permStrings.add("[+] " + entry.getKey());
                } else {
                    permStrings.add("[-] " + entry.getKey());
                }
            }

            String[] arr = new String[permStrings.size()];
            permStrings.toArray(arr);

            commandSender.sendMessage(arr);
        }

        return true;
    }

    private boolean handleUserEnable(CommandSender commandSender, String playerName, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        if(!api.setPlayerPermission(playerName, strings[0], true)) {
            commandSender.sendMessage("Failed to enable permission for player");
        } else {
            commandSender.sendMessage("Permission enabled for player");
            this.api.reloadPlayerPermissions(playerName);
        }

        return true;
    }

    private boolean handleUserDisable(CommandSender commandSender, String playerName, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        if(!api.setPlayerPermission(playerName, strings[0], false)) {
            commandSender.sendMessage("Failed to disable permission for player");
        } else {
            commandSender.sendMessage("Permission disabled for player");
            this.api.reloadPlayerPermissions(playerName);
        }

        return true;
    }


    private boolean handleUserRemove(CommandSender commandSender, String playerName, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        if(!api.setPlayerPermission(playerName, strings[0], null)) {
            commandSender.sendMessage("Failed to remove permission from player");
        } else {
            commandSender.sendMessage("Permission removed from player");
            this.api.reloadPlayerPermissions(playerName);
        }

        return true;
    }

    private boolean handleUserGroup(CommandSender commandSender, String playerName, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        String[] args = new String[strings.length - 1];
        System.arraycopy(strings, 1, args, 0, args.length);

        switch(strings[0]) {
            case "add":
                return handleUserGroupAdd(commandSender, playerName, args);
            case "remove":
                return handleUserGroupRemove(commandSender, playerName, args);
        }

        return false;
    }

    private boolean handleUserGroupAdd(CommandSender commandSender, String playerName, String[] args) {
        if(args.length == 0) {
            return false;
        }

        switch(args.length) {
            case 1:
                if (!this.api.addPlayerToGroup(playerName, args[0])) {
                    commandSender.sendMessage("Failed to add player to group");
                } else {
                    commandSender.sendMessage("Permanently added player to group");
                }
                break;

            case 2:
                Date validity = parseFutureDate(args[1]);
                if(!this.api.addPlayerToGroup(playerName, args[0], validity)) {
                    commandSender.sendMessage("Failed to add player to group");
                } else {
                    commandSender.sendMessage("Added player to group until " + validity.toString());
                }
                break;

            default:
                commandSender.sendMessage("Unsupported number of arguments");
                break;
        }

        return true;
    }

    private Date parseFutureDate(String value) {
        if(value.endsWith("d")) {
            int days = Integer.parseInt(value.substring(0, value.length() - 1));
            Date date = new Date(new Date().getTime() + 24 * 60 * 60 * 1000 * days);

            return date;
        }

        throw new IllegalArgumentException("invalid value: " + value);
    }

    private boolean handleUserGroupRemove(CommandSender commandSender, String playerName, String[] args) {
        if(args.length == 0) {
            return false;
        }

        if(!this.api.removePlayerFromGroup(playerName, args[0])) {
            commandSender.sendMessage("Failed to remove player from group");
            return true;
        }

        commandSender.sendMessage("Removed player from group");
        return true;
    }

}

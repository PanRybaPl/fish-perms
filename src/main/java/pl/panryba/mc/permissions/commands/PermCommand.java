package pl.panryba.mc.permissions.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import pl.panryba.mc.permissions.PluginApi;
import pl.panryba.mc.permissions.StringUtils;

import java.util.*;

public class PermCommand implements CommandExecutor {
    private final PermissionsCommandHandler handler;

    public PermCommand(PluginApi api) {
        this.handler = new PermissionsCommandHandler(api);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        String[] args = new String[strings.length - 1];
        System.arraycopy(strings, 1, args, 0, args.length);

        switch(strings[0]) {
            case "player":
                return handler.handleUser(commandSender, args);

            case "group":
                return handler.handleGroup(commandSender, args);

            case "reload":
                return handler.handleReload(commandSender);
        }

        return true;
    }

}

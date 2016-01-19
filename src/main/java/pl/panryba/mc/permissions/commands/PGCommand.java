package pl.panryba.mc.permissions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.panryba.mc.permissions.PluginApi;

public class PGCommand implements CommandExecutor {
    private final PermissionsCommandHandler handler;

    public PGCommand(PluginApi api) {
        this.handler = new PermissionsCommandHandler(api);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return handler.handleGroup(commandSender, strings);
    }
}

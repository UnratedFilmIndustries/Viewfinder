
package de.unratedfilms.viewfinder.main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.quartercode.quarterbukkit.api.command.CommandExecutor;
import de.unratedfilms.viewfinder.command.HelpCommand;
import de.unratedfilms.viewfinder.command.InfoCommand;
import de.unratedfilms.viewfinder.command.StartCommand;
import de.unratedfilms.viewfinder.command.StopCommand;
import de.unratedfilms.viewfinder.logic.SpectateManager;

public class ViewfinderPluginExecutor {

    public static void onEnable(ViewfinderPlugin plugin) {

        new ExceptionListener(plugin);

        CommandExecutor commandExecutor = new CommandExecutor(plugin);
        plugin.getCommand("viewfinder").setExecutor(commandExecutor);
        plugin.getCommand("viewfinder").setTabCompleter(commandExecutor);

        commandExecutor.addCommandHandler(new HelpCommand());
        commandExecutor.addCommandHandler(new InfoCommand());
        commandExecutor.addCommandHandler(new StartCommand());
        commandExecutor.addCommandHandler(new StopCommand());

        SpectateManager.startSpectateTask(plugin);

        plugin.getLogger().info("Plugin successfully enabled.");
    }

    public static void onDisable(ViewfinderPlugin plugin) {

        for (Player spectator : SpectateManager.getAllSpectators()) {
            SpectateManager.stopSpectating(spectator);
            spectator.sendMessage(ChatColor.DARK_RED + "You were forced to stop spectating because the plugin is disabled.");
        }

        SpectateManager.stopSpectateTask();

        plugin.getLogger().info("Plugin successfully disabled.");
    }

}

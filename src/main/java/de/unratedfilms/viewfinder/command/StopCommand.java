
package de.unratedfilms.viewfinder.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.quartercode.quarterbukkit.api.command.Command;
import com.quartercode.quarterbukkit.api.command.CommandHandler;
import com.quartercode.quarterbukkit.api.command.CommandInfo;
import de.unratedfilms.viewfinder.api.SpectateManager;

public class StopCommand implements CommandHandler {

    @Override
    public CommandInfo getInfo() {

        return new CommandInfo(true, null, "Takes you out of spectate mode.", "viewfinder.command.stop", "stop");
    }

    @Override
    public void execute(Command command) {

        if (! (command.getSender() instanceof Player)) {
            command.getSender().sendMessage(ChatColor.DARK_RED + "This command must be executed by a player.");
            return;
        }
        Player sender = (Player) command.getSender();

        if (!SpectateManager.isSpectating(sender)) {
            sender.sendMessage(ChatColor.DARK_RED + "Currently, you are not spectating anyone.");
        }

        Player target = SpectateManager.getTarget(sender);
        SpectateManager.stopSpectating(sender);
        sender.sendMessage(ChatColor.DARK_GREEN + "You have stopped spectating " + ChatColor.DARK_AQUA + target.getName() + ChatColor.DARK_GREEN + ".");
    }

}

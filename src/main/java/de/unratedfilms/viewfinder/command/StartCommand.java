
package de.unratedfilms.viewfinder.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.quartercode.quarterbukkit.api.command.Command;
import com.quartercode.quarterbukkit.api.command.CommandHandler;
import com.quartercode.quarterbukkit.api.command.CommandInfo;
import de.unratedfilms.viewfinder.api.SpectateManager;
import de.unratedfilms.viewfinder.util.Utils;

public class StartCommand implements CommandHandler {

    @Override
    public CommandInfo getInfo() {

        return new CommandInfo(true, "<Player>", "Puts you into spectate mode and lets you see what the target sees.", "viewfinder.command.start", "start");
    }

    @Override
    public void execute(Command command) {

        if (! (command.getSender() instanceof Player)) {
            command.getSender().sendMessage(ChatColor.DARK_RED + "This command must be executed by a player.");
            return;
        }
        Player sender = (Player) command.getSender();

        String[] arguments = command.getArguments();
        if (arguments.length != 1) {
            sender.sendMessage(ChatColor.DARK_RED + "This command requires exactly 1 argument.");
            return;
        }

        Player targetPlayer = Utils.getPlayerExact(arguments[0]);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Player " + arguments[0] + " is not online.");
            return;
        }
        if (sender.getName().equals(targetPlayer.getName())) {
            sender.sendMessage(ChatColor.DARK_RED + "Did you really just try to spectate yourself?");
            return;
        }
        if (SpectateManager.isSpectating(sender) && targetPlayer.getName().equals(SpectateManager.getTarget(sender).getName())) {
            sender.sendMessage(ChatColor.DARK_RED + "You are already spectating " + targetPlayer.getName() + ".");
            return;
        }
        if (SpectateManager.isSpectating(targetPlayer)) {
            sender.sendMessage(ChatColor.DARK_RED + "You can't spectate " + targetPlayer.getName() + " while that player is also spectating someone.");
            return;
        }
        if (targetPlayer.isDead()) {
            sender.sendMessage(ChatColor.DARK_RED + "You can't spectate dead people, creep.");
            return;
        }

        SpectateManager.startSpectating(sender, targetPlayer);
        sender.sendMessage(ChatColor.DARK_GREEN + "You have started spectating " + ChatColor.DARK_AQUA + SpectateManager.getTarget(sender).getName() + ChatColor.DARK_GREEN + ".");
    }

}

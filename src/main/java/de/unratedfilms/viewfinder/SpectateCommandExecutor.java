package de.unratedfilms.viewfinder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommandExecutor implements CommandExecutor {

    Spectate plugin;

    public SpectateCommandExecutor(Spectate plugin) {

        this.plugin = plugin;

    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spectate")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                showHelp(sender);
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't execute this command from the console.");
                return true;
            }
            Player cmdsender = (Player) sender;
            if (args[0].equalsIgnoreCase("off")) {
                if (!Spectate.getAPI().isSpectating(cmdsender)) {
                    cmdsender.sendMessage(ChatColor.GRAY + "You are not currently spectating anyone.");
                    return true;
                }
                cmdsender.sendMessage(ChatColor.GRAY + "You have stopped spectating " + Spectate.getAPI().getTarget(cmdsender).getName() + ".");
                Spectate.getAPI().stopSpectating(cmdsender, true);
                return true;
            }
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                cmdsender.sendMessage(ChatColor.RED + "Error: Player is not online.");
                return true;
            }
            if (cmdsender.getName().equals(targetPlayer.getName())) {
                cmdsender.sendMessage(ChatColor.GRAY + "Did you really just try to spectate yourself?");
                return true;
            }
            if (Spectate.getAPI().isSpectating(cmdsender)) {
                if (targetPlayer.getName().equals(Spectate.getAPI().getTarget(cmdsender).getName())) {
                    cmdsender.sendMessage(ChatColor.GRAY + "You are already spectating them.");
                    return true;
                }
            }
            if (Spectate.getAPI().isSpectating(targetPlayer)) {
                cmdsender.sendMessage(ChatColor.GRAY + "They are currently spectating someone.");
                return true;
            }
            if (targetPlayer.isDead()) {
                cmdsender.sendMessage(ChatColor.GRAY + "They are currently dead.");
                return true;
            }
            if (!Spectate.getAPI().isSpectating(cmdsender)) {
                Spectate.getAPI().savePlayerState(cmdsender);
            }
            Spectate.getAPI().startSpectating(cmdsender, targetPlayer);
            return true;
        }
        return true;
    }

    public void showHelp(CommandSender cmdsender) {
        cmdsender.sendMessage(ChatColor.RED + "Commands for Spectate:");
        cmdsender.sendMessage(ChatColor.RED + "/spectate [PlayerName] : " + ChatColor.GRAY + "Puts you into spectate mode and lets you see what the target sees.");
        cmdsender.sendMessage(ChatColor.RED + "/spectate off : " + ChatColor.GRAY + "Takes you out of spectate mode.");
        cmdsender.sendMessage(ChatColor.RED + "/spectate help : " + ChatColor.GRAY + "Shows this help page.");
    }

}

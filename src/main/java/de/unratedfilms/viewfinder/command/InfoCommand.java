
package de.unratedfilms.viewfinder.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import com.quartercode.quarterbukkit.api.command.Command;
import com.quartercode.quarterbukkit.api.command.CommandHandler;
import com.quartercode.quarterbukkit.api.command.CommandInfo;

public class InfoCommand implements CommandHandler {

    @Override
    public CommandInfo getInfo() {

        return new CommandInfo(true, null, "Shows an info page.", "viewfinder.command.info", "info");
    }

    @Override
    public void execute(Command command) {

        CommandSender sender = command.getSender();
        PluginDescriptionFile pdf = Bukkit.getPluginManager().getPlugin("Viewfinder").getDescription();

        sender.sendMessage(ChatColor.GREEN + "==========[ Viewfinder Info ]==========");
        sender.sendMessage(ChatColor.AQUA + "This is Viewfinder version " + ChatColor.GOLD + pdf.getVersion() + ChatColor.AQUA + ".");
        sender.sendMessage(ChatColor.AQUA + pdf.getDescription());
        sender.sendMessage(ChatColor.AQUA + "Credits: " + ChatColor.GOLD + StringUtils.join(pdf.getAuthors(), ChatColor.AQUA + ", " + ChatColor.GOLD));
    }

}


package de.unratedfilms.viewfinder;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import de.unratedfilms.viewfinder.api.SpectateManager;

public class Spectate extends JavaPlugin {

    private static SpectateManager manager;

    @Override
    public void onEnable() {

        manager = new SpectateManager(this);

        getServer().getPluginManager().registerEvents(new SpectateListener(this), this);
        getCommand("spectate").setExecutor(new SpectateCommandExecutor(this));
        getAPI().startSpectateTask();
    }

    @Override
    public void onDisable() {

        for (Player p : getAPI().getSpectatingPlayers()) {
            getAPI().stopSpectating(p, true);
            p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because of a server reload.");
        }
        getAPI().stopSpectateTask();
    }

    public static SpectateManager getAPI() {

        return manager;
    }

}

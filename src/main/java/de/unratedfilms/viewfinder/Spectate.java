package de.unratedfilms.viewfinder;

import de.unratedfilms.viewfinder.api.SpectateManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectate extends JavaPlugin {

    //TODO: Control command?

    /* Submit this to spigot in the CraftPlayer class in the teleport method \/
    if (!this.getHandle().activeContainer.a(this.getHandle())) {
        if (getHandle().activeContainer != this.getHandle().defaultContainer) {
            this.getHandle().closeInventory();
        }
    }
     */

    //TODO: Figure out why the crafting bench behaves weird and make a pull request for that too

    //TODO: Fix projectiles launched by a player getting stopped by the spectator
    
    //TODO: Set the player's skin to the person they're spectating

    private static SpectateManager Manager;

    public void onEnable() {
        Manager = new SpectateManager(this);

        getServer().getPluginManager().registerEvents(new SpectateListener(this), this);
        getCommand("spectate").setExecutor(new SpectateCommandExecutor(this));
        getAPI().startSpectateTask();
    }

    public void onDisable() {
        for (Player p : getAPI().getSpectatingPlayers()) {
            getAPI().stopSpectating(p, true);
            p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because of a server reload.");
        }
        getAPI().stopSpectateTask();
    }

    public static SpectateManager getAPI() {
        return Manager;
    }

    public boolean multiverseInvEnabled() {
        return getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && getServer().getPluginManager().getPlugin("Multiverse-Inventories").isEnabled();
    }

}

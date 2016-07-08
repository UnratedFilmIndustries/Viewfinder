
package de.unratedfilms.viewfinder.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.quartercode.quarterbukkit.QuarterBukkitIntegration;
import de.unratedfilms.viewfinder.Spectate;

public class ViewfinderPlugin extends JavaPlugin {

    private boolean quarterBukkitInstalled;

    @Override
    public void onEnable() {

        // QuarterBukkit
        if (!QuarterBukkitIntegration.integrate(this)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        quarterBukkitInstalled = true;
        Spectate.onEnable(this);
    }

    @Override
    public void onDisable() {

        if (quarterBukkitInstalled) {
            Spectate.onDisable(this);
        }
    }

}

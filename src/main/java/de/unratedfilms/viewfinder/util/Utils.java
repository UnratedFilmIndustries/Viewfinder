
package de.unratedfilms.viewfinder.util;

import java.text.DecimalFormat;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Utils {

    public static double roundTwoDecimals(double d) {

        try {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.valueOf(twoDForm.format(d));
        } catch (NumberFormatException e) {
            return d;
        }
    }

    // Have to make a custom teleport method thanks to Acrobot
    public static void teleport(Player p, Location location) {

        EntityPlayer entity = ((CraftPlayer) p).getHandle();

        if (entity.dead) {
            return;
        }

        if (entity.playerConnection == null || entity.playerConnection.isDisconnected()) {
            return;
        }

        if (entity.passenger != null) {
            return;
        }

        Location from = p.getLocation();
        Location to = location;

        entity.mount(null);

        WorldServer fromWorld = ((CraftWorld) from.getWorld()).getHandle();
        WorldServer toWorld = ((CraftWorld) to.getWorld()).getHandle();

        if (fromWorld == toWorld) {
            entity.playerConnection.teleport(to);
        } else {
            ((CraftServer) Bukkit.getServer()).getHandle().moveToWorld(entity, toWorld.dimension, true, to, true);
        }
    }

    private Utils() {

    }

}

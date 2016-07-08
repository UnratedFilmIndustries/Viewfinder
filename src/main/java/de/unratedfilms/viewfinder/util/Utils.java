
package de.unratedfilms.viewfinder.util;

import java.text.DecimalFormat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    @SuppressWarnings ("deprecation")
    public static Player getPlayerExact(String name) {

        return Bukkit.getPlayerExact(name);
    }

    // Have to make a custom teleport method thanks to Acrobot
    public static void teleportPlayer(Player player, Location location) {

        player.teleport(location);

        // EntityPlayer entity = ((CraftPlayer) player).getHandle();
        //
        // if (entity.dead) {
        // return;
        // }
        //
        // if (entity.playerConnection == null || entity.playerConnection.isDisconnected()) {
        // return;
        // }
        //
        // if (entity.passenger != null) {
        // return;
        // }
        //
        // Location from = player.getLocation();
        // Location to = location;
        //
        // entity.mount(null);
        //
        // WorldServer fromWorld = ((CraftWorld) from.getWorld()).getHandle();
        // WorldServer toWorld = ((CraftWorld) to.getWorld()).getHandle();
        //
        // if (fromWorld == toWorld) {
        // entity.playerConnection.teleport(to);
        // } else {
        // ((CraftServer) Bukkit.getServer()).getHandle().moveToWorld(entity, toWorld.dimension, true, to, true);
        // }
    }

    public static void setPlayerVisible(Player player, boolean visible) {

        for (Player otherPlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (visible) {
                otherPlayer.showPlayer(player);
            } else {
                otherPlayer.hidePlayer(player);
            }
        }
    }

    private Utils() {

    }

}

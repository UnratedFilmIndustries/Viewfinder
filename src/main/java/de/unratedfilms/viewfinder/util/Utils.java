
package de.unratedfilms.viewfinder.util;

import java.text.DecimalFormat;
import org.bukkit.Bukkit;
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

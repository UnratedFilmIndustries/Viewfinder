
package de.unratedfilms.viewfinder.logic;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerState {

    public Player       source;

    public Location     location;
    public GameMode     gameMode;
    public boolean      allowFlight;
    public boolean      isFlying;
    public List<Player> vanishedFrom = new ArrayList<>();

    public PlayerState(Player source) {

        this.source = source;

        location = source.getLocation();
        gameMode = source.getGameMode();
        allowFlight = source.getAllowFlight();
        isFlying = source.isFlying();

        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            if (players != source) {
                if (!players.canSee(source)) {
                    vanishedFrom.add(players);
                }
            }
        }
    }

    public void apply(Player player) {

        player.teleport(location);
        player.setGameMode(gameMode);
        player.setAllowFlight(allowFlight);
        player.setFlying(isFlying);

        for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
            if (!vanishedFrom.contains(onlinePlayers)) {
                onlinePlayers.showPlayer(player);
            }
        }
    }

}

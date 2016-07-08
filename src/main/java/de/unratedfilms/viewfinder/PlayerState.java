package de.unratedfilms.viewfinder;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerState {

    public Player player;
    public boolean allowFlight;
    public boolean isFlying;
    public GameMode mode;
    public Location location;

    public ArrayList<Player> vanishedFrom = new ArrayList<Player>();

    public PlayerState(Player p) {
        player = p;
        allowFlight = p.getAllowFlight();
        isFlying = p.isFlying();
        mode = p.getGameMode();
        location = p.getLocation();
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            if (players != p) {
                if (!players.canSee(p)) {
                    vanishedFrom.add(players);
                }
            }
        }
    }

}

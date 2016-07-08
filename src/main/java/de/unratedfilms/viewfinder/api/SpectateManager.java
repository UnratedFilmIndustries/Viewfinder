
package de.unratedfilms.viewfinder.api;

import static de.unratedfilms.viewfinder.util.Utils.roundTwoDecimals;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import de.unratedfilms.viewfinder.PlayerState;
import de.unratedfilms.viewfinder.Spectate;
import de.unratedfilms.viewfinder.util.Utils;

public class SpectateManager {

    private final Spectate                           plugin;
    private int                                      spectateTask        = -1;

    private final HashMap<Player, ArrayList<Player>> targetsToSpectators = new HashMap<>();
    private final HashMap<Player, Player>            spectatorsToTargets = new HashMap<>();

    private final HashMap<Player, PlayerState>       states              = new HashMap<>();

    public SpectateManager(Spectate plugin) {

        this.plugin = plugin;
    }

    private void updateSpectators() {

        spectateTask = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {

                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (isSpectating(p)) {
                        Player target = getTarget(p);
                        if (roundTwoDecimals(p.getLocation().getX()) != roundTwoDecimals(target.getLocation().getX())
                                || roundTwoDecimals(p.getLocation().getY()) != roundTwoDecimals(target.getLocation().getY())
                                || roundTwoDecimals(p.getLocation().getZ()) != roundTwoDecimals(target.getLocation().getZ())
                                || roundTwoDecimals(p.getLocation().getYaw()) != roundTwoDecimals(target.getLocation().getYaw())
                                || roundTwoDecimals(p.getLocation().getPitch()) != roundTwoDecimals(target.getLocation().getPitch())) {
                            Utils.teleport(p, target.getLocation());
                        }
                        if (target.isFlying()) {
                            if (!p.isFlying()) {
                                p.setFlying(true);
                            }
                        }
                        p.setGameMode(target.getGameMode());
                    }
                }
            }
        }, 0L, 1L);
    }

    public void startSpectateTask() {

        if (spectateTask == -1) {
            updateSpectators();
        }
    }

    public void stopSpectateTask() {

        if (spectateTask != -1) {
            plugin.getServer().getScheduler().cancelTask(spectateTask);
            spectateTask = -1;
        }
    }

    public void startSpectating(Player p, Player target, boolean saveState) {

        if (saveState) {
            savePlayerState(p);
        }
        startSpectating(p, target);
    }

    public void startSpectating(Player p, Player target) {

        for (Player player1 : plugin.getServer().getOnlinePlayers()) {
            player1.hidePlayer(p);
        }

        String playerListName = p.getPlayerListName();

        if (isSpectating(p)) {
            p.showPlayer(getTarget(p));
            removeSpectator(getTarget(p), p);
        }

        p.hidePlayer(target);

        p.setPlayerListName(playerListName);
        p.teleport(target);

        spectatorsToTargets.put(p, target);
        addSpectator(target, p);

        p.setAllowFlight(true);

        p.sendMessage(ChatColor.GRAY + "You are now spectating " + target.getName() + ".");
    }

    public void stopSpectating(Player p, boolean loadState) {

        removeSpectator(getTarget(p), p);
        for (PotionEffect e : p.getActivePotionEffects()) {
            p.removePotionEffect(e.getType());
        }
        if (loadState) {
            loadPlayerState(p);
        }
        p.setItemOnCursor(null);
        p.showPlayer(getTarget(p));
        spectatorsToTargets.remove(p);
    }

    public Player getTarget(Player p) {

        return spectatorsToTargets.get(p);
    }

    public boolean isSpectating(Player p) {

        return spectatorsToTargets.containsKey(p);
    }

    public boolean isBeingSpectated(Player p) {

        return targetsToSpectators.containsKey(p);
    }

    private void addSpectator(Player p, Player spectator) {

        if (targetsToSpectators.get(p) == null) {
            ArrayList<Player> newSpectators = new ArrayList<>();
            newSpectators.add(spectator);
            targetsToSpectators.put(p, newSpectators);
        } else {
            targetsToSpectators.get(p).add(spectator);
        }
    }

    private void removeSpectator(Player p, Player spectator) {

        if (targetsToSpectators.get(p) != null) {
            if (targetsToSpectators.get(p).size() == 1) {
                targetsToSpectators.remove(p);
            } else {
                targetsToSpectators.get(p).remove(spectator);
            }
        }
    }

    public ArrayList<Player> getSpectators(Player p) {

        return targetsToSpectators.get(p) == null ? new ArrayList<Player>() : targetsToSpectators.get(p);
    }

    public ArrayList<Player> getSpectatingPlayers() {

        ArrayList<Player> spectatingPlayers = new ArrayList<>();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (isSpectating(p)) {
                spectatingPlayers.add(p);
            }
        }
        return spectatingPlayers;
    }

    public PlayerState getPlayerState(Player p) {

        return states.get(p);
    }

    public void savePlayerState(Player p) {

        PlayerState playerstate = new PlayerState(p);
        states.put(p, playerstate);
    }

    public void loadPlayerState(Player toPlayer) {

        loadPlayerState(toPlayer, toPlayer);
    }

    public void loadPlayerState(Player fromState, Player toPlayer) {

        loadFinalState(getPlayerState(fromState), toPlayer);
        states.remove(fromState);
    }

    private void loadFinalState(PlayerState state, Player toPlayer) {

        toPlayer.teleport(state.location);

        toPlayer.setAllowFlight(state.allowFlight);
        toPlayer.setFlying(state.isFlying);
        toPlayer.setGameMode(state.mode);

        for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {
            if (!state.vanishedFrom.contains(onlinePlayers)) {
                onlinePlayers.showPlayer(toPlayer);
            }
        }
    }

}

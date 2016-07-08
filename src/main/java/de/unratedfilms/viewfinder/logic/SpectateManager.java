
package de.unratedfilms.viewfinder.logic;

import static de.unratedfilms.viewfinder.util.Utils.roundTwoDecimals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import com.quartercode.quarterbukkit.api.scheduler.ScheduleTask;
import de.unratedfilms.viewfinder.main.ViewfinderPlugin;
import de.unratedfilms.viewfinder.util.Utils;

public class SpectateManager {

    private static ScheduleTask                    spectateTask;

    private static final Map<Player, Player>       spectatorsToTargets       = new HashMap<>();
    private static final Map<Player, List<Player>> targetsToSpectators       = new HashMap<>();

    // The states the spectating players were in before they started spectating
    private static final Map<Player, PlayerState>  preSpectatingPlayerStates = new HashMap<>();

    public static void startSpectateTask(ViewfinderPlugin plugin) {

        if (spectateTask == null) {
            spectateTask = new ScheduleTask(plugin) {

                @Override
                public void run() {

                    for (Player spectator : getAllSpectators()) {
                        Player target = getTarget(spectator);

                        if (roundTwoDecimals(spectator.getLocation().getX()) != roundTwoDecimals(target.getLocation().getX())
                                || roundTwoDecimals(spectator.getLocation().getY()) != roundTwoDecimals(target.getLocation().getY())
                                || roundTwoDecimals(spectator.getLocation().getZ()) != roundTwoDecimals(target.getLocation().getZ())
                                || roundTwoDecimals(spectator.getLocation().getYaw()) != roundTwoDecimals(target.getLocation().getYaw())
                                || roundTwoDecimals(spectator.getLocation().getPitch()) != roundTwoDecimals(target.getLocation().getPitch())) {
                            spectator.teleport(target.getLocation());
                        }

                        spectator.setGameMode(target.getGameMode());

                        if (target.isFlying() && !spectator.isFlying()) {
                            spectator.setFlying(true);
                        }
                    }
                }

            }.run(true, 0, 1);
        }
    }

    public static void stopSpectateTask() {

        if (spectateTask != null) {
            spectateTask.cancel();
            spectateTask = null;
        }
    }

    public static void startSpectating(Player spectator, Player target) {

        // If the spectator was already spectating, stop that
        stopSpectating(spectator);

        // Save the current state of the spectator
        preSpectatingPlayerStates.put(spectator, new PlayerState(spectator));

        // Make the spectator invisible, hide the target from him, make him able to fly
        Utils.setPlayerVisible(spectator, false);
        spectator.hidePlayer(target);
        spectator.setAllowFlight(true);

        // Save that the spectator is spectating the target
        addSpectatingRelationship(spectator, target);
    }

    private static void addSpectatingRelationship(Player spectator, Player target) {

        spectatorsToTargets.put(spectator, target);

        if (targetsToSpectators.get(target) == null) {
            List<Player> newSpectators = new ArrayList<>();
            newSpectators.add(spectator);
            targetsToSpectators.put(target, newSpectators);
        } else {
            targetsToSpectators.get(target).add(spectator);
        }
    }

    public static void stopSpectating(Player spectator) {

        Player target = getTarget(spectator);

        if (target != null) {
            // Save that the spectator is no longer spectating the target
            removeSpectatingRelationship(spectator, target);

            // Show the target to the spectator again
            spectator.showPlayer(target);

            // Apply the state the spectator had before he started spectating
            preSpectatingPlayerStates.get(spectator).apply(spectator);
            preSpectatingPlayerStates.remove(spectator);
        }
    }

    private static void removeSpectatingRelationship(Player spectator, Player target) {

        spectatorsToTargets.remove(spectator);

        if (targetsToSpectators.get(target) != null) {
            if (targetsToSpectators.get(target).size() == 1) {
                targetsToSpectators.remove(target);
            } else {
                targetsToSpectators.get(target).remove(spectator);
            }
        }
    }

    public static Player getTarget(Player spectator) {

        return spectatorsToTargets.get(spectator);
    }

    public static boolean isSpectating(Player spectator) {

        return spectatorsToTargets.containsKey(spectator);
    }

    public static boolean isBeingSpectated(Player target) {

        return targetsToSpectators.containsKey(target);
    }

    public static List<Player> getSpectators(Player player) {

        return targetsToSpectators.get(player) == null ? Collections.<Player> emptyList() : targetsToSpectators.get(player);
    }

    public static List<Player> getAllSpectators() {

        return new ArrayList<>(spectatorsToTargets.keySet());
    }

    private SpectateManager() {

    }

}

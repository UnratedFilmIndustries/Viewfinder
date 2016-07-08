package de.unratedfilms.viewfinder.api;

import de.unratedfilms.viewfinder.PlayerState;
import de.unratedfilms.viewfinder.Spectate;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SpectateManager {

    private Spectate plugin;
    private int spectateTask = -1;

    private HashMap<Player, ArrayList<Player>> spectators = new HashMap<Player, ArrayList<Player>>();
    private HashMap<Player, Player> target = new HashMap<Player, Player>();

    private ArrayList<String> cantClick = new ArrayList<String>();

    private HashMap<Player, PlayerState> states = new HashMap<Player, PlayerState>();
    private HashMap<Player, PlayerState> multiInvStates = new HashMap<Player, PlayerState>();

    private ArrayList<String> inventoryOff = new ArrayList<String>();

    public SpectateManager(Spectate plugin) {
        this.plugin = plugin;
    }

    private void updateSpectators() {
        spectateTask = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (isSpectating(p)) {
                        if (plugin.multiverseInvEnabled()) {
                            if (!p.getWorld().getName().equals(getTarget(p).getWorld().getName())) {
                                p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because the person you were spectating switched worlds.");
                                stopSpectating(p, true);
                                continue;
                            }
                        }
                        if (roundTwoDecimals(p.getLocation().getX()) != roundTwoDecimals(getTarget(p).getLocation().getX()) || roundTwoDecimals(p.getLocation().getY()) != roundTwoDecimals(getTarget(p).getLocation().getY()) || roundTwoDecimals(p.getLocation().getZ()) != roundTwoDecimals(getTarget(p).getLocation().getZ()) || roundTwoDecimals(p.getLocation().getYaw()) != roundTwoDecimals(getTarget(p).getLocation().getYaw()) || roundTwoDecimals(p.getLocation().getPitch()) != roundTwoDecimals(getTarget(p).getLocation().getPitch())) {
                            teleport(p, getTarget(p));
                        }
                        if (!inventoryOff.contains(p.getName())) {
                            p.getInventory().setContents(getTarget(p).getInventory().getContents());
                            p.getInventory().setArmorContents(getTarget(p).getInventory().getArmorContents());
                            p.setItemOnCursor(getTarget(p).getItemOnCursor());
                        }
                        if (getTarget(p).getMaxHealth() != p.getMaxHealth()) {
                            p.setMaxHealth(getTarget(p).getMaxHealth());
                        }
                        if (getTarget(p).getHealth() == 0) {
                            p.setHealth(1);
                        } else {
                            if (getTarget(p).getHealth() < p.getHealth()) {
                                double difference = p.getHealth() - getTarget(p).getHealth();
                                p.damage(difference);
                            } else if (getTarget(p).getHealth() > p.getHealth()) {
                                p.setHealth(getTarget(p).getHealth());
                            }
                        }
                        p.setLevel(getTarget(p).getLevel());
                        p.setExp(getTarget(p).getExp());
                        for (PotionEffect e : p.getActivePotionEffects()) {
                            boolean foundPotion = false;
                            for (PotionEffect e1 : getTarget(p).getActivePotionEffects()) {
                                if (e1.getType() == e.getType()) {
                                    foundPotion = true;
                                    break;
                                }
                            }
                            if (!foundPotion) {
                                p.removePotionEffect(e.getType());
                            }
                        }
                        for (PotionEffect e : getTarget(p).getActivePotionEffects()) {
                            p.addPotionEffect(e);
                        }
                        if (!inventoryOff.contains(p.getName())) {
                            p.getInventory().setHeldItemSlot(getTarget(p).getInventory().getHeldItemSlot());
                        }
                        if (getTarget(p).isFlying()) {
                            if (!p.isFlying()) {
                                p.setFlying(true);
                            }
                        }
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
        p.setMaxHealth(target.getMaxHealth());
        p.setHealth(target.getHealth());
        p.teleport(target);

        for (PotionEffect e : p.getActivePotionEffects()) {
            p.removePotionEffect(e.getType());
        }

        setTarget(p, target);
        addSpectator(target, p);

        p.setGameMode(target.getGameMode());
        p.setFoodLevel(target.getFoodLevel());

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
        target.remove(p);
    }

    public ArrayList<Player> getSpectateablePlayers() {
        ArrayList<Player> spectateablePlayers = new ArrayList<Player>();
        for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayers.isDead()) {
                continue;
            }
            if (isSpectating(onlinePlayers)) {
                continue;
            }
            spectateablePlayers.add(onlinePlayers);
        }
        return spectateablePlayers;
    }

    private void setTarget(Player p, Player ptarget) {
        target.put(p, ptarget);
    }

    public Player getTarget(Player p) {
        return target.get(p);
    }

    public boolean isSpectating(Player p) {
        return target.containsKey(p);
    }

    public boolean isBeingSpectated(Player p) {
        return spectators.containsKey(p);
    }

    private void addSpectator(Player p, Player spectator) {
        if (spectators.get(p) == null) {
            ArrayList<Player> newSpectators = new ArrayList<Player>();
            newSpectators.add(spectator);
            spectators.put(p, newSpectators);
        } else {
            spectators.get(p).add(spectator);
        }
    }

    private void removeSpectator(Player p, Player spectator) {
        if (spectators.get(p) != null) {
            if (spectators.get(p).size() == 1) {
                spectators.remove(p);
            } else {
                spectators.get(p).remove(spectator);
            }
        }
    }

    public ArrayList<Player> getSpectators(Player p) {
        return (spectators.get(p) == null ? new ArrayList<Player>() : spectators.get(p));
    }

    public ArrayList<Player> getSpectatingPlayers() {
        ArrayList<Player> spectatingPlayers = new ArrayList<Player>();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (isSpectating(p)) {
                spectatingPlayers.add(p);
            }
        }
        return spectatingPlayers;
    }

    public void setModifyInventory(Player p, boolean modify) {
        if (modify) {
            if (inventoryOff.contains(p.getName())) {
                inventoryOff.remove(p.getName());
            }
        } else {
            if (!inventoryOff.contains(p.getName())) {
                inventoryOff.add(p.getName());
            }
        }
    }

    public PlayerState getPlayerState(Player p) {
        return states.get(p);
    }

    public void savePlayerState(Player p) {
        PlayerState playerstate = new PlayerState(p);
        states.put(p, playerstate);
    }

    public void saveMultiInvState(Player p, Player target) {
        if (!p.getWorld().getName().equals(target.getWorld().getName())) {
            p.teleport(target.getWorld().getSpawnLocation());
            multiInvStates.put(p, new PlayerState(p));
        }
    }

    public void loadPlayerState(Player toPlayer) {
        loadPlayerState(toPlayer, toPlayer);
    }

    public void loadPlayerState(Player fromState, Player toPlayer) {
        if (plugin.multiverseInvEnabled() && multiInvStates.get(fromState) != null) {
            loadFinalState(multiInvStates.get(fromState), toPlayer);
            multiInvStates.remove(fromState);
        }
        loadFinalState(getPlayerState(fromState), toPlayer);
        states.remove(fromState);
    }

    private void loadFinalState(PlayerState state, Player toPlayer) {
        toPlayer.teleport(state.location);

        toPlayer.getInventory().setContents(state.inventory);
        toPlayer.getInventory().setArmorContents(state.armor);
        toPlayer.setFoodLevel(state.hunger);
        toPlayer.setMaxHealth(state.maxHealth);
        toPlayer.setHealth(state.health);
        toPlayer.setLevel(state.level);
        toPlayer.setExp(state.exp);
        toPlayer.getInventory().setHeldItemSlot(state.slot);
        toPlayer.setAllowFlight(state.allowFlight);
        toPlayer.setFlying(state.isFlying);
        toPlayer.setGameMode(state.mode);

        for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {
            if (!state.vanishedFrom.contains(onlinePlayers)) {
                onlinePlayers.showPlayer(toPlayer);
            }
        }

        for (PotionEffect e : state.potions) {
            toPlayer.addPotionEffect(e);
        }
    }

    public double roundTwoDecimals(double d) {
        try {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.valueOf(twoDForm.format(d));
        } catch (NumberFormatException e) {
            return d;
        }
    }

    private void teleport(Player p, Entity e) {
        teleport(p, e.getLocation());
    }

    //Have to make a custom teleport method thanks to Acrobot
    private void teleport(Player p, Location location) {
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
            ((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(entity, toWorld.dimension, true, to, true);
        }
    }

}

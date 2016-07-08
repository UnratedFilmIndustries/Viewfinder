
package de.unratedfilms.viewfinder;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import de.unratedfilms.viewfinder.api.SpectateManager;

public class SpectateListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        for (Player p : SpectateManager.getAllSpectators()) {
            event.getPlayer().hidePlayer(p);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (SpectateManager.isSpectating(event.getPlayer())) {
            SpectateManager.stopSpectating(event.getPlayer());
        } else if (SpectateManager.isBeingSpectated(event.getPlayer())) {
            for (Player spectator : SpectateManager.getSpectators(event.getPlayer())) {
                SpectateManager.stopSpectating(spectator);
                spectator.sendMessage(ChatColor.DARK_RED + "You were forced to stop spectating because the person you were spectating disconnected.");
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (SpectateManager.isBeingSpectated(event.getEntity())) {
            for (Player spectator : SpectateManager.getSpectators(event.getEntity())) {
                SpectateManager.stopSpectating(spectator);
                spectator.sendMessage(ChatColor.DARK_RED + "You were forced to stop spectating because the person you were spectating died.");
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        if (! (event.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getEntity();
        if (SpectateManager.isSpectating(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {
            if (SpectateManager.isSpectating((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    // TODO: Throwable objects don't work, arrows do.
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        if (event.getEntity().getShooter() instanceof Player) {
            if (SpectateManager.isBeingSpectated((Player) event.getEntity().getShooter())) {
                Location fromLocation = event.getEntity().getLocation();
                Vector toLocation = fromLocation.clone().getDirection().multiply(0.5);
                Location finalLoc = fromLocation.clone().add(toLocation);
                event.getEntity().teleport(finalLoc);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!event.isCancelled()) {
                if (SpectateManager.isBeingSpectated(player)) {
                    for (Player p : SpectateManager.getSpectators(player)) {
                        p.setFoodLevel(event.getFoodLevel());
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {

        if (!event.isCancelled()) {
            if (SpectateManager.isBeingSpectated(event.getPlayer())) {
                for (Player p : SpectateManager.getSpectators(event.getPlayer())) {
                    p.setGameMode(event.getNewGameMode());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        if (! (event.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getPlayer();
        if (SpectateManager.isBeingSpectated(p)) {
            for (Player spectators : SpectateManager.getSpectators(p)) {
                spectators.openInventory(event.getInventory());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (! (event.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getPlayer();
        if (SpectateManager.isBeingSpectated(p)) {
            for (Player spectators : SpectateManager.getSpectators(p)) {
                spectators.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (! (event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getWhoClicked();
        if (SpectateManager.isSpectating(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (SpectateManager.isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        if (SpectateManager.isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (SpectateManager.isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (SpectateManager.isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event) {

        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (SpectateManager.isSpectating(p)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {

        if (SpectateManager.isSpectating(event.getPlayer())) {
            SpectateManager.getTarget(event.getPlayer()).giveExp(event.getAmount());
            event.setAmount(0);
        }
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {

        if (event.getEntity() instanceof Monster) {
            if (event.getTarget() instanceof Player) {
                if (SpectateManager.isSpectating((Player) event.getTarget())) {
                    event.setCancelled(true);
                }
            }
        }
    }

}

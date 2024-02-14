package com.kliminskyi.ffregions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerActionListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Optional<Region> regionFrom = Database.getInstance().getRegionByLocation(e.getFrom());
        Optional<Region> region = Database.getInstance().getRegionByLocation(e.getTo());

        if (regionFrom.equals(region)) {
            return;
        }

        if (region.isEmpty()) {
            e.getPlayer().sendTitle(
                String.format("%sUntaken territory%s", ChatColor.GRAY, ChatColor.RESET),
                String.format("%sPublic land%s", ChatColor.DARK_GRAY, ChatColor.RESET),
                1,
                1,
                60
            );
        }
        else if (!region.get().isMemberOrOwner(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendTitle(
                String.format("%s%s%s%s", ChatColor.RED, ChatColor.BOLD, region.get().getName(), ChatColor.RESET),
                String.format("%sPrivate land%s", ChatColor.RED, ChatColor.RESET),
                1,
                1,
                60
            );
        }
        else {
            e.getPlayer().sendTitle(
                String.format("%s%s%s%s", ChatColor.GREEN, ChatColor.BOLD, region.get().getName(), ChatColor.RESET),
                String.format("%sPrivate land%s", ChatColor.GREEN, ChatColor.RESET),
                1,
                1,
                60
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!isPlayerIntrusionAllowed(e.getBlock().getLocation(), e.getPlayer())) {
            denyIntrusion(e, e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurnEvent(BlockBurnEvent e) {
        if (!isAnonymousIntrusionAllowed(e.getBlock().getLocation())) {
            e.getBlock().breakNaturally();
            denyIntrusion(e, null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgniteEvent(BlockIgniteEvent e) {
        if (!isAnonymousIntrusionAllowed(e.getBlock().getLocation())) {
            denyIntrusion(e, null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent e) {
        if (!isAnonymousIntrusionAllowed(e.getBlock().getLocation())) {
            denyIntrusion(e, null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (!isPlayerIntrusionAllowed(e.getBlock().getLocation(), e.getPlayer())) {
            denyIntrusion(e, e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent e) {
        List<Block> blocks = e.getBlocks();
        if (!isPistonMovementAllowed(blocks, e)) {
            denyIntrusion(e, null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPistonRetractEvent(BlockPistonRetractEvent e) {
        List<Block> blocks = e.getBlocks();
        if (!isPistonMovementAllowed(blocks, e)) {
            denyIntrusion(e, null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplodeEvent(EntityExplodeEvent e) {
        Optional<Player> player = Optional.empty();
        if (e.getEntity() instanceof Player) {
            player = Optional.of((Player)e.getEntity());
        }

        List<Block> blockList = e.blockList();
        for (int i = 0; i < blockList.size(); i++) {
            if (player.isPresent()) {
                if (!isPlayerIntrusionAllowed(blockList.get(i).getLocation(), player.get())) {
                    blockList.remove(i--);
                }
            }
            else {
                if (!isAnonymousIntrusionAllowed(blockList.get(i).getLocation())) {
                    blockList.remove(i--);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent e) {
        if (!isPlayerIntrusionAllowed(e.getBed().getLocation(), e.getPlayer())) {
            denyIntrusion(e, e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }

        if (!isPlayerIntrusionAllowed(e.getClickedBlock().getLocation(), e.getPlayer())) {
            denyIntrusion(e, e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
        if (!isPlayerIntrusionAllowed(e.getRightClicked().getLocation(), e.getPlayer())) {
            denyIntrusion(e, e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteractEvent(EntityInteractEvent e) {
        if (e.getEntityType() != EntityType.DROPPED_ITEM) {
            return;
        }

        UUID itemThrowerUUID = ((Item)e.getEntity()).getThrower();
        if (itemThrowerUUID == null) {
            return;
        }

        if (!isPlayerIntrusionAllowed(e.getBlock().getLocation(), itemThrowerUUID)) {
            denyIntrusion(e, null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        Optional<Region> damageeRegion = Database.getInstance().getRegionByLocation(e.getEntity().getLocation());

        if (damageeRegion.isEmpty()) {
            return;
        }
    
        if (e.getCause() == DamageCause.BLOCK_EXPLOSION || e.getCause() == DamageCause.ENTITY_EXPLOSION) {
            if (!(e.getEntity() instanceof Player)) {
                denyIntrusion(e, null);
                return;
            }
            
            Player damageePlayer = (Player)e.getEntity();
            if (!damageeRegion.get().isMemberOrOwner(damageePlayer.getUniqueId())) {
                return;
            }

            denyIntrusion(e, null);
            return;
        }
        else if (e.getCause() == DamageCause.PROJECTILE) {
            Projectile projectile = (Projectile)e.getDamager();
            ProjectileSource source = projectile.getShooter();

            if (!isProjectileHitAllowed(projectile, e.getEntity().getLocation())) {
                if (source instanceof Player) {
                    denyIntrusion(e, (Player)source);
                }
                else {
                    denyIntrusion(e, null);
                }
            }
            
            return;
        }

        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Player damagerPlayer = (Player)e.getDamager();
        if (!damageeRegion.get().isMemberOrOwner(damagerPlayer.getUniqueId())) {
            denyIntrusion(e, damagerPlayer);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHitEvent(ProjectileHitEvent e) {
        Location hitLocation = e.getHitBlock() == null ? e.getHitEntity().getLocation() : e.getHitBlock().getLocation();

        if (!isProjectileHitAllowed(e.getEntity(), hitLocation)) {
            if (e.getEntity().getShooter() instanceof Player) {
                denyIntrusion(e, (Player)e.getEntity().getShooter());
            }
            else {
                denyIntrusion(e, null);
            }
        }
    }

    private boolean isProjectileHitAllowed(Projectile projectile, Location hitLocation) {
        Optional<Region> hitRegion = Database.getInstance().getRegionByLocation(hitLocation);
        if (hitRegion.isEmpty()) {
            return true;
        }

        if (projectile.getShooter() instanceof Player) {
            Player shooter = (Player)projectile.getShooter();
            if (!isPlayerIntrusionAllowed(hitLocation, shooter)) {
                return false;
            }
        }
        else if (projectile.getShooter() instanceof BlockProjectileSource) {
            Location shooterLocation = ((BlockProjectileSource)projectile.getShooter()).getBlock().getLocation();
            Optional<Region> shooterRegion = Database.getInstance().getRegionByLocation(shooterLocation);

            if (shooterRegion.isEmpty()) {
                return false;
            }

            if (shooterRegion.get() != hitRegion.get()) {
                return false;
            }
        }
        else if (projectile.getShooter() instanceof Entity) {
            Location shooterLocation = ((Entity)projectile.getShooter()).getLocation();
            Optional<Region> shooterRegion = Database.getInstance().getRegionByLocation(shooterLocation);

            if (shooterRegion.isEmpty()) {
                return false;
            }

            if (shooterRegion.get() != hitRegion.get()) {
                return false;
            }
        }
        else {
            return false;
        }

        return true;
    }

    private boolean isPistonMovementAllowed(List<Block> blocks, BlockPistonEvent e) {
        double directionCoeff = e instanceof BlockPistonRetractEvent ? -1.0 : 1.0;

        for (Block block : blocks) {
            Location from = block.getLocation();
            Location to = from.clone();
            to.setX(to.getX() + e.getDirection().getDirection().getX() * directionCoeff);
            to.setY(to.getY() + e.getDirection().getDirection().getY() * directionCoeff);
            to.setZ(to.getZ() + e.getDirection().getDirection().getZ() * directionCoeff);

            Optional<Region> pistonRegion = Database.getInstance().getRegionByLocation(e.getBlock().getLocation());
            Optional<Region> fromRegion = Database.getInstance().getRegionByLocation(from);
            Optional<Region> toRegion = Database.getInstance().getRegionByLocation(to);

            if (!(pistonRegion.equals(fromRegion) && pistonRegion.equals(toRegion))) {
                return false;
            }
        }

        return true;
    }

    private boolean isPlayerIntrusionAllowed(Location intrusionLocation, UUID playerUUID) {
        Optional<Region> region = Database.getInstance().getRegionByLocation(intrusionLocation);
        if (region.isEmpty()) {
            return true;
        }

        return region.get().isMemberOrOwner(playerUUID);
    }

    private boolean isPlayerIntrusionAllowed(Location intrusionLocation, Player player) {
        return isPlayerIntrusionAllowed(intrusionLocation, player.getUniqueId());
    }

    private boolean isAnonymousIntrusionAllowed(Location intrusionLocation) {
        Optional<Region> region = Database.getInstance().getRegionByLocation(intrusionLocation);
        return region.isEmpty();
    }

    private void denyIntrusion(Cancellable e, Player player) {
        if (player != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("%sIntrusion to this region is not allowed.%s", ChatColor.RED, ChatColor.RESET)));
        }

        e.setCancelled(true);
        if (e instanceof ProjectileHitEvent) {
            ((ProjectileHitEvent)e).getEntity().remove();
        }
    }
}

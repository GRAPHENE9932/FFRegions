package com.kliminskyi.ffregions;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandLsRegion implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.getServer().getLogger().warning("Only players can send the mkregion command.");
            return false;
        }

        Player player = (Player)sender;

        Inventory inventory = Bukkit.createInventory(null, 45, "Chunks nearby");
        fillInventory(inventory, player);
        player.openInventory(inventory);

        return true;
    }

    private void fillInventory(Inventory inventory, Player player, Direction direction) {
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 5; z++) {
                Chunk currentChunk = new Chunk(player.getLocation());

                int offset_x;
                int offset_z;
                if (direction == Direction.NEG_Z) {
                    offset_x = x - 4;
                    offset_z = z - 2;
                }
                else if (direction == Direction.POS_Z) {
                    offset_x = x - 4;
                    offset_z = -z + 2;
                }
                else if (direction == Direction.NEG_X) {
                    offset_x = z - 2;
                    offset_z = -x + 4;
                }
                else {
                    offset_x = -z + 2;
                    offset_z = x - 4;
                }

                currentChunk.x += offset_x;
                currentChunk.z += offset_z;

                inventory.setItem(z * 9 + x, createItemStackForChunk(currentChunk, player));
            }
        }
    }

    private void fillInventory(Inventory inventory, Player player) {
        Direction dir = calculateDirection(player.getLocation());
        fillInventory(inventory, player, dir);
    }

    private ItemStack createItemStackForChunk(Chunk chunk, Player player) {
        Optional<Region> region = Database.getInstance().getRegionByChunk(chunk);
        if (region.isEmpty()) {
            ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("Untaken");
            stack.setItemMeta(meta);
            return stack;
        }
        else if (!region.get().isMemberOrOwner(player.getUniqueId())) {
            ItemStack stack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(region.get().getName());
            stack.setItemMeta(meta);
            return stack;
        }
        else {
            ItemStack stack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(region.get().getName());
            stack.setItemMeta(meta);
            return stack;
        }
    }

    private float normalizeAngle(float source) {
        if (source < 0.0f) {
            return source + 360.0f;
        }
        else {
            return source;
        }
    }

    private enum Direction { POS_Z, NEG_Z, POS_X, NEG_X }
    private Direction calculateDirection(Location location) {
        float yaw = normalizeAngle(location.getYaw());
        if (yaw >= 315.0f || yaw < 45.0f) {
            return Direction.POS_Z;
        }
        else if (yaw >= 45.0f && yaw < 135.0f) {
            return Direction.NEG_X;
        }
        else if (yaw >= 135.0f && yaw < 225.0f) {
            return Direction.NEG_Z;
        }
        else {
            return Direction.POS_X;
        }
    }
}

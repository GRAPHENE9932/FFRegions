package com.kliminskyi.ffregions;

import java.util.Optional;

import org.bukkit.Bukkit;
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
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 5; z++) {
                Chunk currentChunk = new Chunk(player.getLocation());
                currentChunk.x += x - 4;
                currentChunk.z += z - 2;
                inventory.setItem(z * 9 + x, createItemStackForChunk(currentChunk, player));
            }
        }
        player.openInventory(inventory);

        return true;
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
}

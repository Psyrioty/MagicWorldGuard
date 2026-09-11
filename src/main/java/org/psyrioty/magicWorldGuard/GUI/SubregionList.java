package org.psyrioty.magicWorldGuard.GUI;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicWorldGuard.MagicWorldGuard;

import java.util.HashSet;
import java.util.Set;

import static org.psyrioty.magicWorldGuard.Utils.SubregionChecker.checkSubregionForParentRegion;

public class SubregionList implements InventoryHolder {
    Inventory inventory;
    Player player;
    Set<ProtectedRegion> regions;
    World world;
    ProtectedRegion parentRegion;

    public SubregionList(
            Player player,
            World world,
            ProtectedRegion parentRegion
    ){
        Bukkit.getScheduler().runTaskAsynchronously(MagicWorldGuard.getPlugin(),()-> {
            this.world = world;
            this.player = player;
            this.parentRegion = parentRegion;
            createGUI(world);
            openGUI();
        });
    }

    private void createGUI(World world){
        inventory = Bukkit.createInventory(this, 54);

        RegionManager manager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        if (manager != null) {

            int regionIterator = 9;

            regions = new HashSet<>();

            for (ProtectedRegion region : manager.getRegions().values()) {
                if (checkSubregionForParentRegion(player, region, parentRegion)) {
                    regions.add(region);

                    ItemStack itemStack = new ItemStack(Material.PAPER);
                    ItemMeta meta = itemStack.getItemMeta();

                    FileConfiguration config = MagicWorldGuard.getPlugin().getDefaultConfig();

                    String prefix = config.getString("regionPrefix");
                    String suffix = config.getString("regionSuffix");

                    Component name = MiniMessage.miniMessage().deserialize(
                            prefix + region.getId() + suffix
                    );

                    meta.displayName(name);
                    itemStack.setItemMeta(meta);

                    inventory.setItem(regionIterator, itemStack);

                    regionIterator++;
                }
            }
        }
    }

    public World getWorld() {
        return world;
    }

    public Set<ProtectedRegion> getRegions() {
        return regions;
    }

    private void openGUI(){
        Bukkit.getScheduler().runTask(MagicWorldGuard.getPlugin(), () -> {
            try {
                player.openInventory(inventory);
                MagicWorldGuard.getPlugin().getSubregionListHashMap().put(player, this);
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicWorldGuard error SubregionList.java in openGUI() " + exception.getMessage());
            }
        });
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}

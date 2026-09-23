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

import java.util.*;

import static org.psyrioty.magicWorldGuard.Utils.Other.createButton;
import static org.psyrioty.magicWorldGuard.Utils.RegionCreator.createRegionClick;
import static org.psyrioty.magicWorldGuard.Utils.SubregionChecker.checkSubregion;

public class SelectRegion implements InventoryHolder {
    Inventory inventory;
    Player player;
    List<ProtectedRegion> regions;
    World world;
    InventoryHolder back;

    public SelectRegion(
            Player player,
            World world,
            InventoryHolder back
    ){
        Bukkit.getScheduler().runTaskAsynchronously(MagicWorldGuard.getPlugin(),()-> {
            this.world = world;
            this.player = player;
            this.back = back;
            createGUI(world);
            openGUI();
        });
    }

    private void createGUI(World world){
        inventory = Bukkit.createInventory(this, 54, "Список регионов");


        FileConfiguration config = MagicWorldGuard.getPlugin().getDefaultConfig();
        String back = config.getString("back");
        int backCustomModelData = config.getInt("backCustomModelData");
        createButton(0, back, Material.ARROW, backCustomModelData, false, inventory);

        RegionManager manager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        String createRegionName = config.getString("createRegionName");
        int createRegionCustomModelData = config.getInt("createRegionCustomModelData");
        createButton(4, createRegionName, Material.WOODEN_AXE, createRegionCustomModelData, false, inventory);


        if (manager != null) {

            int regionIterator = 9;

            regions = new ArrayList<>();

            for (ProtectedRegion region : manager.getRegions().values()) {
                if (region.getOwners().contains(player.getUniqueId())) {
                    if(checkSubregion(player, region)){
                        continue;
                    }

                    regions.add(region);

                    ItemStack itemStack = new ItemStack(Material.PAPER);
                    ItemMeta meta = itemStack.getItemMeta();

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

    public List<ProtectedRegion> getRegions() {
        return regions;
    }

    private void openGUI(){
        Bukkit.getScheduler().runTask(MagicWorldGuard.getPlugin(), () -> {
            try {
                player.openInventory(inventory);
                MagicWorldGuard.getPlugin().getSelectRegionHashMap().put(player, this);
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicWorldGuard error SelectRegion.java in openGUI() " + exception.getMessage());
            }
        });
    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ///////////НАЖАТИЯ НА КНОПКУ////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    public void click(Player player, int slot){
        int regionIterator = 0;

        if(slot == 0){
            player.openInventory(back.getInventory());
            return;
        }

        if(slot == 4){
            createRegionClick(player);
            return;
        }

        for(ProtectedRegion protectedRegion: regions){
            if(slot - 9 == regionIterator){
                new RegionSettings(player, world, protectedRegion, this);
                return;
            }

            regionIterator++;
        }
    }
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}

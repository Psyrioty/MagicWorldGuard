package org.psyrioty.magicWorldGuard.GUI;

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

public class SubregionSettings implements InventoryHolder {
    private Inventory inventory;
    private Player player;
    private World world;
    private ProtectedRegion region;

    public SubregionSettings(
            Player player,
            World world,
            ProtectedRegion region
    ){
        Bukkit.getScheduler().runTaskAsynchronously(MagicWorldGuard.getPlugin(), () -> {
            this.player = player;
            this.world = world;
            this.region = region;

            createGUI();
            openGUI(player);
        });
    }

    private void openGUI(Player player){
        Bukkit.getScheduler().runTask(MagicWorldGuard.getPlugin(), () -> {
            try {
                player.openInventory(inventory);
                MagicWorldGuard.getPlugin().getSubregionSettingsHashMap().put(player, this);
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicWorldGuard error SubregionSettings.java in openGUI() " + exception.getMessage());
            }
        });
    }

    private void createGUI(){
        inventory = Bukkit.createInventory(this, 27);

        FileConfiguration config = MagicWorldGuard.getPlugin().getDefaultConfig();

        String deleteRegionName = config.getString("regionSettings.deleteRegionName");
        int deleteRegionNameCustomModelData = config.getInt("regionSettings.deleteRegionNameCustomModelData");
        createButton(0, deleteRegionName, Material.PAPER, deleteRegionNameCustomModelData);

        String regionNamePrefix = config.getString("regionSettings.regionNamePrefix");
        String regionNameSuffix = config.getString("regionSettings.regionNameSuffix");
        String regionName = regionNamePrefix + region.getId() + regionNameSuffix;
        int regionNameCustomModelData = config.getInt("regionSettings.regionNameCustomModelData");
        createButton(4, regionName, Material.PAPER, regionNameCustomModelData);

        String openDoor = config.getString("regionSettings.openDoor");
        int openDoorCustomModelData = config.getInt("regionSettings.openDoorCustomModelData");
        createButton(10, openDoor, Material.PAPER, openDoorCustomModelData);

        String memberList = config.getString("regionSettings.memberList");
        int memberListCustomModelData = config.getInt("regionSettings.memberListCustomModelData");
        createButton(13, memberList, Material.PAPER, memberListCustomModelData);

        String pvpOff = config.getString("regionSettings.pvpOff");
        int pvpOffCustomModelData = config.getInt("regionSettings.pvpOffCustomModelData");
        createButton(16, pvpOff, Material.PAPER, pvpOffCustomModelData);

        String back = config.getString("back");
        int backCustomModelData = config.getInt("backCustomModelData");
        createButton(18, back, Material.PAPER, backCustomModelData);

        String openChest = config.getString("regionSettings.openChest");
        int openChestCustomModelData = config.getInt("regionSettings.openChestCustomModelData");
        createButton(19, openChest, Material.PAPER, openChestCustomModelData);

        String mobSpawnDeny = config.getString("regionSettings.mobSpawnDeny");
        int mobSpawnDenyCustomModelData = config.getInt("regionSettings.mobSpawnDenyCustomModelData");
        createButton(22, mobSpawnDeny, Material.PAPER, mobSpawnDenyCustomModelData);

        String enderPearlAndChorusFruitDeny = config.getString("regionSettings.enderPearlAndChorusFruitDeny");
        int enderPearlAndChorusFruitDenyCustomModelData = config.getInt("regionSettings.enderPearlAndChorusFruitDenyCustomModelData");
        createButton(25, enderPearlAndChorusFruitDeny, Material.PAPER, enderPearlAndChorusFruitDenyCustomModelData);
    }

    private void createButton(
            int slot,
            String text,
            Material material,
            int customModelData
    ){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        Component name = MiniMessage.miniMessage().deserialize(
                text
        );
        itemMeta.displayName(name);
        itemMeta.setCustomModelData(customModelData);

        itemStack.setItemMeta(itemMeta);

        inventory.setItem(slot, itemStack);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}

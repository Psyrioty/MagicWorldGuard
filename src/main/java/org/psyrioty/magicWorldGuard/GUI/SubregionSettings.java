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

import static org.psyrioty.magicWorldGuard.Utils.RegionSettingsFlags.*;
import static org.psyrioty.magicWorldGuard.Utils.RegionSettingsFlags.enderPearlAndChorusFruit;
import static org.psyrioty.magicWorldGuard.Utils.RegionSettingsFlags.mobSpawn;
import static org.psyrioty.magicWorldGuard.Utils.RegionSettingsFlags.openChest;

public class SubregionSettings implements InventoryHolder {
    private Inventory inventory;
    private Player player;
    private World world;
    private ProtectedRegion region;
    private InventoryHolder back;

    public SubregionSettings(
            Player player,
            World world,
            ProtectedRegion region,
            InventoryHolder back
    ){
        Bukkit.getScheduler().runTaskAsynchronously(MagicWorldGuard.getPlugin(), () -> {
            this.player = player;
            this.world = world;
            this.region = region;
            this.back = back;

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

    private void createGUI() {
        inventory = Bukkit.createInventory(this, 27, "Настройка дочернего региона");

        FileConfiguration config = MagicWorldGuard.getPlugin().getDefaultConfig();

        // 🗑 Удаление региона — BARRIER (классическая иконка «удалить/запрещено»)
        String deleteRegionName = config.getString("regionSettings.deleteRegionName");
        int deleteRegionNameCustomModelData = config.getInt("regionSettings.deleteRegionNameCustomModelData");
        createButton(18, deleteRegionName, Material.BARRIER, deleteRegionNameCustomModelData, false);

        // 📛 Название региона — NAME_TAG
        String regionNamePrefix = config.getString("regionSettings.regionNamePrefix");
        String regionNameSuffix = config.getString("regionSettings.regionNameSuffix");
        String regionName = regionNamePrefix + region.getId() + regionNameSuffix;
        int regionNameCustomModelData = config.getInt("regionSettings.regionNameCustomModelData");
        createButton(4, regionName, Material.NAME_TAG, regionNameCustomModelData, false);

        // 🚪 Открытие дверей — OAK_DOOR (или IRON_DOOR для «только для своих»)
        String openDoor = config.getString("regionSettings.openDoor");
        int openDoorCustomModelData = config.getInt("regionSettings.openDoorCustomModelData");
        createButton(10, openDoor, Material.OAK_DOOR, openDoorCustomModelData, openDoorCheck(region));

        // 👥 Список участников — PLAYER_HEAD
        String memberList = config.getString("regionSettings.memberList");
        int memberListCustomModelData = config.getInt("regionSettings.memberListCustomModelData");
        createButton(13, memberList, Material.PLAYER_HEAD, memberListCustomModelData, false);

        // ⚔ PvP — IRON_SWORD (или NETHERITE_SWORD для «вкл», SHIELD для «выкл»)
        String pvpOff = config.getString("regionSettings.pvpOff");
        int pvpOffCustomModelData = config.getInt("regionSettings.pvpOffCustomModelData");
        createButton(16, pvpOff, Material.SHIELD, pvpOffCustomModelData, pvpCheck(region));

        // ⬅ Назад — ARROW
        String back = config.getString("back");
        int backCustomModelData = config.getInt("backCustomModelData");
        createButton(0, back, Material.ARROW, backCustomModelData, false);

        // 📦 Открытие сундуков — CHEST
        String openChest = config.getString("regionSettings.openChest");
        int openChestCustomModelData = config.getInt("regionSettings.openChestCustomModelData");
        createButton(19, openChest, Material.CHEST, openChestCustomModelData, openChestCheck(region));

        // 🐛 Спавн мобов — ZOMBIE_HEAD (или SPIDER_EYE / WITHER_SKELETON_SKULL)
        String mobSpawnDeny = config.getString("regionSettings.mobSpawnDeny");
        int mobSpawnDenyCustomModelData = config.getInt("regionSettings.mobSpawnDenyCustomModelData");
        createButton(22, mobSpawnDeny, Material.ZOMBIE_HEAD, mobSpawnDenyCustomModelData, mobSpawnCheck(region));

        // 🔮 Эндер-перл / хорус — ENDER_PEARL (или CHORUS_FRUIT)
        String enderPearlAndChorusFruitDeny = config.getString("regionSettings.enderPearlAndChorusFruitDeny");
        int enderPearlAndChorusFruitDenyCustomModelData = config.getInt("regionSettings.enderPearlAndChorusFruitDenyCustomModelData");
        createButton(25, enderPearlAndChorusFruitDeny, Material.ENDER_PEARL, enderPearlAndChorusFruitDenyCustomModelData, enderPearlAndChorusFruitCheck(region));
    }

    private void createButton(
            int slot,
            String text,
            Material material,
            int customModelData,
            boolean check
    ){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        Component name = MiniMessage.miniMessage().deserialize(
                text
        );
        itemMeta.displayName(name);
        itemMeta.setCustomModelData(customModelData);
        itemMeta.setEnchantmentGlintOverride(check);

        itemStack.setItemMeta(itemMeta);

        inventory.setItem(slot, itemStack);
    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ///////////НАЖАТИЯ НА КНОПКУ////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    public void click(Player player, int slot){
        switch (slot){
            //удалить регион
            case 18:
                deleteRegion(player, world, region);
                player.closeInventory();
                break;
            //вкл/выкл открытие дверей
            case 10:
                setActive(slot, openDoor(player, region));
                break;
            //список участников привата
            case 13:
                new RegionMembers(inventory.getHolder(), player, region);
                break;
            //вкл/выкл PvP
            case 16:
                setActive(slot, pvp(player, region));
                break;
            //назад
            case 0:
                player.openInventory(back.getInventory());
                break;
            //вкл/выкл открытие сундуков
            case 19:
                setActive(slot, openChest(player, region));
                break;
            //вкл/выкл запрет спавна мобов
            case 22:
                setActive(slot, mobSpawn(player, region));
                break;
            //вкл/выкл запрет перлов и хоруса
            case 25:
                setActive(slot, enderPearlAndChorusFruit(player, region));
                break;
        }
    }

    private void setActive(int slot, boolean isActive){
        ItemStack itemStack = inventory.getItem(slot);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setEnchantmentGlintOverride(isActive);
        itemStack.setItemMeta(itemMeta);
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

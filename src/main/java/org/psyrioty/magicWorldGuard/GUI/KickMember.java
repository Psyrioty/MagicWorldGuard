package org.psyrioty.magicWorldGuard.GUI;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicWorldGuard.MagicWorldGuard;

import java.util.Set;

import static org.psyrioty.magicWorldGuard.Utils.RegionCreator.kickMember;

public class KickMember implements InventoryHolder {
    private Inventory inventory;
    private InventoryHolder regionSettings;
    private Player player;
    private ProtectedRegion protectedRegion;
    private OfflinePlayer member;

    public KickMember(
            InventoryHolder regionSettings,
            Player player,
            ProtectedRegion protectedRegion,
            OfflinePlayer member
    ){
        this.player = player;
        this.regionSettings = regionSettings;
        this.protectedRegion = protectedRegion;
        this.member = member;

        createGUI();
        openGUI();
    }

    private void createGUI(){
        inventory = Bukkit.createInventory(this, 27, "Выгнать игрока");

        FileConfiguration config = MagicWorldGuard.getPlugin().getDefaultConfig();

        String prefixUsername = config.getString("regionMembers.prefixUsername");
        String suffixUsername = config.getString("regionMembers.suffixUsername");

        String username = prefixUsername + member.getName() + suffixUsername;

        Component memberUsername = MiniMessage.miniMessage().deserialize(
                username
        );

        ItemStack memberButton = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) memberButton.getItemMeta();
        meta.displayName(memberUsername);
        memberButton.setItemMeta(meta);

        inventory.setItem(4, memberButton);
        //////////////////////////////////////////////////////////////////
        ItemStack acceptButton = new ItemStack(Material.LIME_DYE);
        ItemMeta acceptButtonMeta = acceptButton.getItemMeta();

        String acceptButtonName = config.getString("acceptButton");

        Component acceptButtonComponent = MiniMessage.miniMessage().deserialize(
                acceptButtonName
        );

        acceptButtonMeta.displayName(acceptButtonComponent);
        acceptButton.setItemMeta(acceptButtonMeta);
        inventory.setItem(10, acceptButton);
        /////////////////////////////////////////////////////////////////

        ItemStack cancelButton = new ItemStack(Material.RED_DYE);
        ItemMeta cancelButtonMeta = cancelButton.getItemMeta();

        String cancelButtonName = config.getString("cancelButton");

        Component cancelButtonComponent = MiniMessage.miniMessage().deserialize(
                cancelButtonName
        );

        cancelButtonMeta.displayName(cancelButtonComponent);
        cancelButton.setItemMeta(cancelButtonMeta);
        inventory.setItem(16, cancelButton);
    }

    private void openGUI(){
        try {
            player.openInventory(inventory);
            MagicWorldGuard.getPlugin().getKickMemberHashMap().put(player, this);
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicWorldGuard error KickMember.java in openGUI() " + exception.getMessage());
        }
    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ///////////НАЖАТИЯ НА КНОПКУ////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    public void click(Player player, int slot){
        switch (slot){
            case 10:
                kickMember(player, member, protectedRegion);
                break;
            case 16:
                player.openInventory(regionSettings.getInventory());
                break;
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

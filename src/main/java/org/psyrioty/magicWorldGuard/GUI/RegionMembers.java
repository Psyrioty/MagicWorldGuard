package org.psyrioty.magicWorldGuard.GUI;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.data.type.Switch;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicWorldGuard.MagicWorldGuard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.psyrioty.magicWorldGuard.Utils.Other.createPlayerHead;
import static org.psyrioty.magicWorldGuard.Utils.RegionSettingsFlags.getRegionMembers;
import static org.psyrioty.magicWorldGuard.Utils.SubregionChecker.checkSubregion;

public class RegionMembers implements InventoryHolder {
    private Inventory inventory;
    private InventoryHolder back;
    private Player player;
    private ProtectedRegion protectedRegion;
    private int page = 0;
    private int maxPage = 0;

    public RegionMembers(
                InventoryHolder back,
                Player player,
                ProtectedRegion protectedRegion
    ){
        this.player = player;
        this.back = back;
        this.protectedRegion = protectedRegion;

        createGUI();
        openGUI();
    }

    private void createGUI(){
        inventory = Bukkit.createInventory(this, 54, "Список участников");

        Set<OfflinePlayer> offlinePlayers = getRegionMembers(protectedRegion);

        maxPage = offlinePlayers.size() / (inventory.getSize() - 9);

        FileConfiguration config = MagicWorldGuard.getPlugin().getDefaultConfig();

        showMembers(0);

        String nextButtonName = config.getString("regionMembers.nextButton");

        Component nextButtonNameFinal = MiniMessage.miniMessage().deserialize(
                nextButtonName
        );

        ItemStack nextButton = new ItemStack(Material.ARROW);
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        nextButtonMeta.displayName(nextButtonNameFinal);
        nextButton.setItemMeta(nextButtonMeta);

        inventory.setItem(7, nextButton);

        String backButtonName = config.getString("regionMembers.backButton");

        Component backButtonNameFinal = MiniMessage.miniMessage().deserialize(
                backButtonName
        );

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.displayName(backButtonNameFinal);
        backButton.setItemMeta(backButtonMeta);

        inventory.setItem(1, backButton);
        //////////////////////
        String addMemberName = config.getString("regionMembers.addMember");

        Component addMemberNameFinal = MiniMessage.miniMessage().deserialize(
                addMemberName
        );
        ItemStack addMember = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta addMemberMeta = addMember.getItemMeta();
        addMemberMeta.displayName(addMemberNameFinal);
        addMember.setItemMeta(addMemberMeta);

        inventory.setItem(4, addMember);
    }

    private void openGUI(){
        try {
            player.openInventory(inventory);
            MagicWorldGuard.getPlugin().getRegionMembersHashMap().put(player, this);
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicWorldGuard error RegionMembers.java in openGUI() " + exception.getMessage());
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private void showMembers(
            int page
    ){
        Set<OfflinePlayer> offlinePlayers = getRegionMembers(protectedRegion);
        FileConfiguration config = MagicWorldGuard.getPlugin().getDefaultConfig();

        int i = 9 + (5 * 9 * page);
        for(OfflinePlayer offlinePlayer: offlinePlayers){
            if(inventory.getSize() <= i){
                break;
            }

            ItemStack head = createPlayerHead(offlinePlayer);
            SkullMeta meta = (SkullMeta) head.getItemMeta();


            String prefixUsername = config.getString("regionMembers.prefixUsername");
            String suffixUsername = config.getString("regionMembers.suffixUsername");
            String text = prefixUsername + offlinePlayer.getName() + suffixUsername;

            Component name = MiniMessage.miniMessage().deserialize(
                    text
            );

            meta.displayName(
                    name
            );

            meta.lore(null);

            head.setItemMeta(meta);

            inventory.setItem(i, head);

            i++;
        }
    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ///////////НАЖАТИЯ НА КНОПКУ////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    public void click(Player player, int slot){
        switch(slot){
            case 1:
                clickBackMembers();
                break;
            case 4:
                clickAddMember();
                break;
            case 7:
                clickNextMembers();
                break;
            default:
                int clickedMember = (5 * 9 * page) + slot - 9;
                if(clickedMember < inventory.getSize() - 9 && clickedMember >= 0){
                    Set<OfflinePlayer> members = getRegionMembers(protectedRegion);
                    OfflinePlayer member = members.stream()
                            .skip(clickedMember)
                            .findFirst()
                            .orElse(null);

                    if(member == null){
                        return;
                    }

                    new KickMember(this, player, protectedRegion, member);
                }
                break;
        }
    }

    private void clickAddMember() {
        // Закрываем инвентарь
        player.closeInventory();

        // Подсказка с кликабельной командой
        String regionName = protectedRegion.getId();
        String suggestion = "/region addmember " + regionName + " ";

        Component message = MiniMessage.miniMessage().deserialize(
                "<yellow>Введите в чат команду: <click:suggest_command:'" + suggestion + "'>" +
                        "<green><hover:show_text:'<gray>Нажми, чтобы подставить команду'>" +
                        "/region addmember <регион> <ник></hover></green></click>"
        );

        player.sendMessage(message);
    }

    private void clickBackMembers(){
        if(page <= 0){
            player.openInventory(back.getInventory());
            return;
        }
        page--;
        showMembers(page);
    }

    private void clickNextMembers(){
        if(page >= maxPage){
            return;
        }
        page++;
        showMembers(page);
    }
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
}

package org.psyrioty.magicWorldGuard.Listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.psyrioty.magicWorldGuard.GUI.*;
import org.psyrioty.magicWorldGuard.MagicWorldGuard;

import java.util.HashMap;

import static org.psyrioty.magicWorldGuard.Utils.RegionSettingsFlags.*;

public class GUIEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void OnClick(InventoryClickEvent event){
        Inventory inventory = event.getInventory();

        if(isGUI(inventory)){
            event.setCancelled(true);
        }

        HumanEntity player = event.getWhoClicked();
        int slot = event.getSlot();

        clickSelectWorld(inventory, player, slot);
        clickSelectRegion(inventory, player, slot);
        clickRegionSettings(inventory, player, slot);
        clickSubregionList(inventory, player, slot);
        clickKickMember(inventory, player, slot);
        clickRegionMembers(inventory, player, slot);
        clickSubregionSettings(inventory, player, slot);
    }

    private void clickSubregionList(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof SubregionList)){
            return;
        }

        SubregionList subregionList = getNeedSubregionList(player);

        if(subregionList == null){
            return;
        }

        subregionList.click((Player) player, slot);
    }

    private void clickKickMember(Inventory inventory, HumanEntity player, int slot) {
        if (!(inventory.getHolder() instanceof KickMember)) {
            return;
        }

        KickMember kickMember = getNeedKickMember(player);

        if(kickMember == null){
            return;
        }

        kickMember.click((Player) player, slot);
    }

    private void clickRegionMembers(Inventory inventory, HumanEntity player, int slot) {
        if (!(inventory.getHolder() instanceof RegionMembers)) {
            return;
        }

        RegionMembers regionMembers = getNeedRegionMembers(player);

        if(regionMembers == null){
            return;
        }

        regionMembers.click((Player) player, slot);
    }

    private void clickSelectWorld(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof SelectWorld)){
            return;
        }

        SelectWorld selectWorld = getNeedSelectWorld(player);

        if(selectWorld == null){
            return;
        }

        selectWorld.click((Player) player, slot);
    }

    private void clickSelectRegion(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof SelectRegion)){
            return;
        }

        SelectRegion selectRegion = getNeedSelectRegion(player);

        if(selectRegion == null){
            return;
        }

        selectRegion.click((Player) player, slot);
    }

    private void clickRegionSettings(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof RegionSettings)){
            return;
        }

        RegionSettings regionSettings = getNeedRegionSettings(player);

        if(regionSettings == null){
            return;
        }

        regionSettings.click((Player) player, slot);
    }

    private void clickSubregionSettings(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof SubregionSettings)){
            return;
        }

        SubregionSettings subregionSettings = getNeedSubregionSettings(player);

        if(subregionSettings == null){
            return;
        }

        subregionSettings.click((Player) player, slot);
    }


    /*@EventHandler(priority = EventPriority.LOWEST)
    private void CloseInventory(InventoryCloseEvent event){
        Inventory inventory = event.getInventory();

        if(!isGUI(inventory)){
            return;
        }

        HumanEntity player = event.getPlayer();

        SelectWorld selectWorld = getNeedSelectWorld(player);

        if(selectWorld == null){
            return;
        }

        MagicWorldGuard.getPlugin().getSelectWorldHashMap().remove((Player) player);
    }*/

    @EventHandler(priority = EventPriority.LOWEST)
    private void PlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();

        MagicWorldGuard.getPlugin().getKickMemberHashMap().remove(player);
        MagicWorldGuard.getPlugin().getSelectWorldHashMap().remove(player);
        MagicWorldGuard.getPlugin().getSelectRegionHashMap().remove(player);
        MagicWorldGuard.getPlugin().getRegionSettingsHashMap().remove(player);
        MagicWorldGuard.getPlugin().getSubregionSettingsHashMap().remove(player);
        MagicWorldGuard.getPlugin().getSubregionListHashMap().remove(player);
        MagicWorldGuard.getPlugin().getRegionMembersHashMap().remove(player);
    }

    private SelectWorld getNeedSelectWorld(HumanEntity player){
        SelectWorld selectWorld = null;
        HashMap<Player, SelectWorld> selectWorldHashMap = MagicWorldGuard.getPlugin().getSelectWorldHashMap();

        for(Player playerInventory: selectWorldHashMap.keySet()){
            if(player.getUniqueId().equals(playerInventory.getUniqueId())){
                selectWorld = selectWorldHashMap.get(playerInventory);

                return selectWorld;
            }
        }

        return null;
    }

    private SubregionList getNeedSubregionList(HumanEntity player){
        SubregionList subregionList = null;
        HashMap<Player, SubregionList> subregionListHashMap = MagicWorldGuard.getPlugin().getSubregionListHashMap();

        for(Player playerInventory: subregionListHashMap.keySet()){
            if(player.getUniqueId().equals(playerInventory.getUniqueId())){
                subregionList = subregionListHashMap.get(playerInventory);

                return subregionList;
            }
        }

        return null;
    }

    private SelectRegion getNeedSelectRegion(HumanEntity player){
        SelectRegion selectRegion = null;
        HashMap<Player, SelectRegion> selectRegionHashMap = MagicWorldGuard.getPlugin().getSelectRegionHashMap();

        for(Player playerInventory: selectRegionHashMap.keySet()){
            if(player.getUniqueId().equals(playerInventory.getUniqueId())){
                selectRegion = selectRegionHashMap.get(playerInventory);

                return selectRegion;
            }
        }

        return null;
    }

    private KickMember getNeedKickMember(HumanEntity player){
        KickMember kickMember = null;
        HashMap<Player, KickMember> kickMemberHashMap = MagicWorldGuard.getPlugin().getKickMemberHashMap();

        for(Player playerInventory: kickMemberHashMap.keySet()){
            if(player.getUniqueId().equals(playerInventory.getUniqueId())){
                kickMember = kickMemberHashMap.get(playerInventory);

                return kickMember;
            }
        }

        return null;
    }

    private RegionMembers getNeedRegionMembers(HumanEntity player){
        RegionMembers regionMembers = null;
        HashMap<Player, RegionMembers> regionMembersHashMap = MagicWorldGuard.getPlugin().getRegionMembersHashMap();

        for(Player playerInventory: regionMembersHashMap.keySet()){
            if(player.getUniqueId().equals(playerInventory.getUniqueId())){
                regionMembers = regionMembersHashMap.get(playerInventory);

                return regionMembers;
            }
        }

        return null;
    }

    private RegionSettings getNeedRegionSettings(HumanEntity player){
        RegionSettings regionSettings = null;
        HashMap<Player, RegionSettings> selectRegionHashMap = MagicWorldGuard.getPlugin().getRegionSettingsHashMap();

        for(Player playerInventory: selectRegionHashMap.keySet()){
            if(player.getUniqueId().equals(playerInventory.getUniqueId())){
                regionSettings = selectRegionHashMap.get(playerInventory);

                return regionSettings;
            }
        }

        return null;
    }

    private SubregionSettings getNeedSubregionSettings(HumanEntity player){
        SubregionSettings subregionSettings = null;
        HashMap<Player, SubregionSettings> selectRegionHashMap = MagicWorldGuard.getPlugin().getSubregionSettingsHashMap();

        for(Player playerInventory: selectRegionHashMap.keySet()){
            if(player.getUniqueId().equals(playerInventory.getUniqueId())){
                subregionSettings = selectRegionHashMap.get(playerInventory);

                return subregionSettings;
            }
        }

        return null;
    }

    private boolean isGUI(Inventory inventory){
        InventoryHolder inventoryHolder = inventory.getHolder();

        if(
                inventoryHolder instanceof SelectWorld ||
                inventoryHolder instanceof SelectRegion ||
                inventoryHolder instanceof RegionSettings ||
                inventoryHolder instanceof SubregionList ||
                inventoryHolder instanceof SubregionSettings ||
                inventoryHolder instanceof RegionMembers ||
                inventoryHolder instanceof KickMember
        ){
            return true;
        }

        return false;
    }
}

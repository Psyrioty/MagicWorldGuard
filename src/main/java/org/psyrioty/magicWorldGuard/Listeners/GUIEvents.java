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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.psyrioty.magicWorldGuard.GUI.*;
import org.psyrioty.magicWorldGuard.MagicWorldGuard;

import java.util.HashMap;

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
    }

    private void clickSubregionList(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof SubregionList)){
            return;
        }

        Bukkit.getLogger().info("0");

        SubregionList subregionList = getNeedSubregionList(player);

        if(subregionList == null){
            return;
        }
        Bukkit.getLogger().info("1");

        int regionIterator = 0;

        for(ProtectedRegion protectedRegion: subregionList.getRegions()){
            if(slot - 9 == regionIterator){
                Bukkit.getLogger().info("2");
                new SubregionSettings((Player) player, subregionList.getWorld(), protectedRegion);
                return;
            }

            regionIterator++;
        }
    }

    private void clickSelectWorld(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof SelectWorld)){
            return;
        }

        SelectWorld selectWorld = getNeedSelectWorld(player);

        if(selectWorld == null){
            return;
        }

        int worldIterator = 0;

        for(World world: selectWorld.getWorldList()){
            if(slot - 9 == worldIterator){
                new SelectRegion((Player) player, world);
                return;
            }

            worldIterator++;
        }
    }

    private void clickSelectRegion(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof SelectRegion)){
            return;
        }

        SelectRegion selectRegion = getNeedSelectRegion(player);

        if(selectRegion == null){
            return;
        }

        int regionIterator = 0;

        for(ProtectedRegion protectedRegion: selectRegion.getRegions()){
            if(slot - 9 == regionIterator){
                new RegionSettings((Player) player, selectRegion.getWorld(), protectedRegion);
                return;
            }

            regionIterator++;
        }
    }

    private void clickRegionSettings(Inventory inventory, HumanEntity player, int slot){
        if(!(inventory.getHolder() instanceof RegionSettings)){
            return;
        }

        RegionSettings regionSettings = getNeedRegionSettings(player);

        if(regionSettings == null){
            return;
        }

        switch (slot){
            //удалить регион
            case 0:
                break;
            //список подприватов
            case 8:
                new SubregionList((Player) player, regionSettings.getWorld(), regionSettings.getRegion());


                break;
            //вкл/выкл открытие дверей
            case 10:
                break;
            //список участников привата
            case 13:
                break;
            //вкл/выкл PvP
            case 16:
                break;
            //назад
            case 18:
                break;
            //вкл/выкл открытие сундуков
            case 19:
                break;
            //вкл/выкл запрет спавна мобов
            case 22:
                break;
            //вкл/выкл запрет перлов и хоруса
            case 25:
                break;
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
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

    private boolean isGUI(Inventory inventory){
        InventoryHolder inventoryHolder = inventory.getHolder();

        if(
                inventoryHolder instanceof SelectWorld ||
                inventoryHolder instanceof SelectRegion ||
                inventoryHolder instanceof RegionSettings ||
                inventoryHolder instanceof SubregionList ||
                inventoryHolder instanceof SubregionSettings
        ){
            return true;
        }

        return false;
    }
}

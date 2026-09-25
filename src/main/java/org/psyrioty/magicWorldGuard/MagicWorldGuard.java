package org.psyrioty.magicWorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.psyrioty.magicWorldGuard.Commands.Region;
import org.psyrioty.magicWorldGuard.GUI.*;
import org.psyrioty.magicWorldGuard.Listeners.GUIEvents;
import org.psyrioty.magicWorldGuard.Listeners.WorldGuardEvents;

import java.util.HashMap;

public final class MagicWorldGuard extends JavaPlugin {
    PluginManager pm;
    private static MagicWorldGuard plugin;
    private FileConfiguration defaultConfig;

    private HashMap<Player, SelectWorld> selectWorldHashMap;
    private HashMap<Player, SelectRegion> selectRegionHashMap;
    private HashMap<Player, RegionSettings> regionSettingsHashMap;
    private HashMap<Player, SubregionSettings> subregionSettingsHashMap;
    private HashMap<Player, SubregionList> subregionListHashMap;
    private HashMap<Player, RegionMembers> regionMembersHashMap;
    private HashMap<Player, KickMember> kickMemberHashMap;

    @Override
    public void onEnable() {
        plugin = this;
        pm = Bukkit.getServer().getPluginManager();

        pm.registerEvents(new GUIEvents(), this);
        pm.registerEvents(new WorldGuardEvents(), this);

        /*Bukkit.getScheduler().runTaskLater(this, () -> {
            PluginCommand existing = getCommand("region");
            if (existing == null) {
                getCommand("region").setExecutor(new Region());
            } else {
                existing.setExecutor(new Region());
            }
        }, 1L);*/

        loadConfig();

        selectWorldHashMap = new HashMap<>();
        selectRegionHashMap = new HashMap<>();
        regionSettingsHashMap = new HashMap<>();
        subregionSettingsHashMap = new HashMap<>();
        subregionListHashMap = new HashMap<>();
        regionMembersHashMap = new HashMap<>();
        kickMemberHashMap = new HashMap<>();
    }

    public HashMap<Player, SelectWorld> getSelectWorldHashMap() {
        return selectWorldHashMap;
    }

    public HashMap<Player, SelectRegion> getSelectRegionHashMap() {
        return selectRegionHashMap;
    }

    public HashMap<Player, RegionSettings> getRegionSettingsHashMap() {
        return regionSettingsHashMap;
    }

    private void loadConfig(){
        saveDefaultConfig();
        defaultConfig = getConfig();
    }

    public HashMap<Player, SubregionSettings> getSubregionSettingsHashMap() {
        return subregionSettingsHashMap;
    }

    public FileConfiguration getDefaultConfig() {
        return defaultConfig;
    }

    public HashMap<Player, SubregionList> getSubregionListHashMap() {
        return subregionListHashMap;
    }

    public HashMap<Player, KickMember> getKickMemberHashMap() {
        return kickMemberHashMap;
    }

    @Override
    public void onDisable() {
        for(SelectWorld selectWorld: selectWorldHashMap.values()){
            selectWorld.getInventory().close();
        }

        for(SelectRegion selectRegion: selectRegionHashMap.values()){
            selectRegion.getInventory().close();
        }

        for(RegionSettings regionSettings: regionSettingsHashMap.values()){
            regionSettings.getInventory().close();
        }

        for(SubregionSettings subregionSettings: subregionSettingsHashMap.values()){
            subregionSettings.getInventory().close();
        }

        for(SubregionList subregionList: subregionListHashMap.values()){
            subregionList.getInventory().close();
        }

        for(RegionMembers regionMembers: regionMembersHashMap.values()){
            regionMembers.getInventory().close();
        }

        for(KickMember kickMember: kickMemberHashMap.values()){
            kickMember.getInventory().close();
        }
    }

    public static MagicWorldGuard getPlugin() {
        return plugin;
    }

    public HashMap<Player, RegionMembers> getRegionMembersHashMap() {
        return regionMembersHashMap;
    }
}

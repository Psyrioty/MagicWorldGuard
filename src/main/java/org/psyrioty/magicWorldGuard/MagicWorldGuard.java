package org.psyrioty.magicWorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.psyrioty.magicWorldGuard.Commands.MWG;
import org.psyrioty.magicWorldGuard.GUI.*;
import org.psyrioty.magicWorldGuard.Listeners.GUIEvents;

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

    @Override
    public void onEnable() {
        plugin = this;
        pm = Bukkit.getServer().getPluginManager();

        pm.registerEvents(new GUIEvents(), this);

        this.getCommand("mwg").setExecutor(new MWG());

        loadConfig();

        selectWorldHashMap = new HashMap<>();
        selectRegionHashMap = new HashMap<>();
        regionSettingsHashMap = new HashMap<>();
        subregionSettingsHashMap = new HashMap<>();
        subregionListHashMap = new HashMap<>();
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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MagicWorldGuard getPlugin() {
        return plugin;
    }
}

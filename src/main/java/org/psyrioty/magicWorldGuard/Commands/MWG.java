package org.psyrioty.magicWorldGuard.Commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicWorldGuard.GUI.SelectWorld;

public class MWG implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        //RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        new SelectWorld((Player) sender);


        //World worldEditWorld = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        //container.get(worldEditWorld).getRegions();
        return true;
    }
}

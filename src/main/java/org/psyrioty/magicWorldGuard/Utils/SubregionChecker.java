package org.psyrioty.magicWorldGuard.Utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public class SubregionChecker {
    public static boolean checkSubregion(Player player, ProtectedRegion region) {
        RegionManager manager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));

        if (manager == null) {
            return false;
        }

        for (ProtectedRegion parent : manager.getRegions().values()) {

            // Сам с собой не сравниваем
            if (parent.getId().equalsIgnoreCase(region.getId())) {
                continue;
            }

            // Игрок должен владеть подрегионом
            if (!region.getOwners().contains(player.getUniqueId())) {
                continue;
            }

            // Игрок должен владеть родительским регионом
            if (!parent.getOwners().contains(player.getUniqueId())) {
                continue;
            }

            // Подрегион должен полностью находиться внутри родительского
            if (parent.contains(region.getMinimumPoint())
                    && parent.contains(region.getMaximumPoint())) {

                return true;
            }
        }

        return false;
    }

    public static boolean checkSubregionForParentRegion(
            Player player,
            ProtectedRegion region,
            ProtectedRegion parentRegion
    ) {
        // Сам с собой
        if (region.getId().equalsIgnoreCase(parentRegion.getId())) {
            return false;
        }

        // Игрок должен владеть подрегионом
        if (!region.getOwners().contains(player.getUniqueId())) {
            return false;
        }

        // Игрок должен владеть родительским регионом
        if (!parentRegion.getOwners().contains(player.getUniqueId())) {
            return false;
        }

        // Подрегион должен полностью находиться внутри родительского региона
        return parentRegion.contains(region.getMinimumPoint())
                && parentRegion.contains(region.getMaximumPoint());
    }
}

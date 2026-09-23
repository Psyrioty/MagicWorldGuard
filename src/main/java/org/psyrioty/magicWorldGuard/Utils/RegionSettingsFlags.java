package org.psyrioty.magicWorldGuard.Utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RegionSettingsFlags {
    public static boolean mobSpawn(Player owner, ProtectedRegion protectedRegion) {
        StateFlag.State current = protectedRegion.getFlag(Flags.MOB_SPAWNING);

        StateFlag.State newState = (current == StateFlag.State.DENY)
                ? StateFlag.State.ALLOW
                : StateFlag.State.DENY;

        protectedRegion.setFlag(Flags.MOB_SPAWNING, newState);

        if (newState == StateFlag.State.ALLOW) {
            owner.sendMessage("§aСпавн мобов в регионе \"" + protectedRegion.getId() + "\" §aвключён.");
        } else {
            owner.sendMessage("§cСпавн мобов в регионе \"" + protectedRegion.getId() + "\" §cвыключен.");
        }

        return newState == StateFlag.State.ALLOW;
    }

    public static boolean enderPearlAndChorusFruit(Player owner, ProtectedRegion protectedRegion) {
        StateFlag.State pvp = protectedRegion.getFlag(Flags.PVP);

        if (pvp != StateFlag.State.DENY) {
            owner.sendMessage("§cНельзя изменить эндер-перлы и хорус, пока включён PvP.");
            return protectedRegion.getFlag(Flags.ENDERPEARL) == StateFlag.State.ALLOW;
        }

        StateFlag.State current = protectedRegion.getFlag(Flags.ENDERPEARL);

        StateFlag.State newState = (current == StateFlag.State.DENY)
                ? StateFlag.State.ALLOW
                : StateFlag.State.DENY;

        protectedRegion.setFlag(Flags.ENDERPEARL, newState);
        protectedRegion.setFlag(Flags.CHORUS_TELEPORT, newState);

        if (newState == StateFlag.State.ALLOW) {
            owner.sendMessage("§aЭндер-перлы и хорус в регионе \"" + protectedRegion.getId() + "\" §aразрешены.");
        } else {
            owner.sendMessage("§cЭндер-перлы и хорус в регионе \"" + protectedRegion.getId() + "\" §cзапрещены.");
        }

        return newState == StateFlag.State.ALLOW;
    }

    public static boolean pvp(Player owner, ProtectedRegion protectedRegion) {
        StateFlag.State currentPvp = protectedRegion.getFlag(Flags.PVP);

        // Если PVP выключен — включаем его
        if (currentPvp == StateFlag.State.DENY) {
            protectedRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);

            // При включении PVP обязательно включаем перлы/хорус
            if (protectedRegion.getFlag(Flags.ENDERPEARL) == StateFlag.State.DENY) {
                protectedRegion.setFlag(Flags.ENDERPEARL, StateFlag.State.ALLOW);
                protectedRegion.setFlag(Flags.CHORUS_TELEPORT, StateFlag.State.ALLOW);
            }

            owner.sendMessage("§aPvP в регионе \"" + protectedRegion.getId() + "\" §aвключён. Эндер-перлы и хорус автоматически разрешены.");
            return true;
        }

        // Если PVP включён или не установлен — выключаем
        protectedRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
        owner.sendMessage("§cPvP в регионе \"" + protectedRegion.getId() + "\" §cвыключен.");
        return false;
    }

    public static boolean openChest(Player owner, ProtectedRegion protectedRegion) {
        StateFlag.State current = protectedRegion.getFlag(Flags.CHEST_ACCESS);

        StateFlag.State newState = (current == StateFlag.State.DENY)
                ? StateFlag.State.ALLOW
                : StateFlag.State.DENY;

        protectedRegion.setFlag(Flags.CHEST_ACCESS, newState);

        if (newState == StateFlag.State.ALLOW) {
            owner.sendMessage("§aДоступ к сундукам в регионе \"" + protectedRegion.getId() + "\" §aразрешён.");
        } else {
            owner.sendMessage("§cДоступ к сундукам в регионе \"" + protectedRegion.getId() + "\" §cзапрещён.");
        }

        return newState == StateFlag.State.ALLOW;
    }

    public static boolean openDoor(Player owner, ProtectedRegion protectedRegion) {
        StateFlag.State current = protectedRegion.getFlag(Flags.USE);

        StateFlag.State newState = (current == StateFlag.State.DENY)
                ? StateFlag.State.ALLOW
                : StateFlag.State.DENY;

        protectedRegion.setFlag(Flags.USE, newState);

        if (newState == StateFlag.State.ALLOW) {
            owner.sendMessage("§aИспользование дверей/кнопок в регионе \"" + protectedRegion.getId() + "\" §aразрешено.");
        } else {
            owner.sendMessage("§cИспользование дверей/кнопок в регионе \"" + protectedRegion.getId() + "\" §cзапрещено.");
        }

        return newState == StateFlag.State.ALLOW;
    }

    public static void deleteRegion(Player owner, World world, ProtectedRegion protectedRegion) {
        RegionManager manager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        if (manager == null) {
            owner.sendMessage("§cНе удалось загрузить регионы мира \"" + world.getName() + "\".");
            return;
        }

        String regionId = protectedRegion.getId();
        manager.removeRegion(regionId);

        owner.sendMessage("§aРегион \"" + regionId + "\" §aудалён.");
    }

    public static Set<OfflinePlayer> getRegionMembers(ProtectedRegion region) {
        Set<OfflinePlayer> members = new HashSet<>();

        for (UUID uuid : region.getMembers().getUniqueIds()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            members.add(player);
        }

        return members;
    }

    public static boolean mobSpawnCheck(ProtectedRegion protectedRegion) {
        return protectedRegion.getFlag(Flags.MOB_SPAWNING) != StateFlag.State.DENY;
    }

    public static boolean enderPearlAndChorusFruitCheck(ProtectedRegion protectedRegion) {
        return protectedRegion.getFlag(Flags.ENDERPEARL) != StateFlag.State.DENY;
    }

    public static boolean pvpCheck(ProtectedRegion protectedRegion) {
        return protectedRegion.getFlag(Flags.PVP) != StateFlag.State.DENY;
    }

    public static boolean openChestCheck(ProtectedRegion protectedRegion) {
        return protectedRegion.getFlag(Flags.CHEST_ACCESS) != StateFlag.State.DENY;
    }

    public static boolean openDoorCheck(ProtectedRegion protectedRegion) {
        return protectedRegion.getFlag(Flags.USE) != StateFlag.State.DENY;
    }
}

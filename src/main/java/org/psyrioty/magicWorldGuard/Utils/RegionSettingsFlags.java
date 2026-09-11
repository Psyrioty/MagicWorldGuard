package org.psyrioty.magicWorldGuard.Utils;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionSettingsFlags {
    public static void mobSpawn(ProtectedRegion protectedRegion) {
        StateFlag.State current = protectedRegion.getFlag(Flags.MOB_SPAWNING);

        if (current == StateFlag.State.DENY) {
            protectedRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
        } else {
            protectedRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
        }
    }

    public static void enderPearlAndChorusFruit(ProtectedRegion protectedRegion) {

        // Перлы/хорус можно выключать только при выключенном PVP
        StateFlag.State pvp = protectedRegion.getFlag(Flags.PVP);

        if (pvp != StateFlag.State.DENY) {
            return;
        }

        StateFlag.State current = protectedRegion.getFlag(Flags.ENDERPEARL);

        protectedRegion.setFlag(
                Flags.ENDERPEARL,
                current == StateFlag.State.DENY
                        ? StateFlag.State.ALLOW
                        : StateFlag.State.DENY
        );
    }

    public static void pvp(ProtectedRegion protectedRegion) {

        StateFlag.State currentPvp = protectedRegion.getFlag(Flags.PVP);

        // Если PVP выключен — включаем его
        if (currentPvp == StateFlag.State.DENY) {

            protectedRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);

            // При включении PVP обязательно включаем перлы/хорус
            if (protectedRegion.getFlag(Flags.ENDERPEARL) == StateFlag.State.DENY) {
                protectedRegion.setFlag(Flags.ENDERPEARL, StateFlag.State.ALLOW);
            }

            return;
        }

        // Если PVP включен или не установлен — выключаем
        protectedRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
    }

    public static void openChest(ProtectedRegion protectedRegion) {
        StateFlag.State current = protectedRegion.getFlag(Flags.CHEST_ACCESS);

        protectedRegion.setFlag(
                Flags.CHEST_ACCESS,
                current == StateFlag.State.DENY
                        ? StateFlag.State.ALLOW
                        : StateFlag.State.DENY
        );
    }

    public static void openDoor(ProtectedRegion protectedRegion) {
        StateFlag.State current = protectedRegion.getFlag(Flags.USE);

        protectedRegion.setFlag(
                Flags.USE,
                current == StateFlag.State.DENY
                        ? StateFlag.State.ALLOW
                        : StateFlag.State.DENY
        );
    }
}

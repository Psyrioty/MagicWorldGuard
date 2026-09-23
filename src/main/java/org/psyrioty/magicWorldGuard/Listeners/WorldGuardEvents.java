package org.psyrioty.magicWorldGuard.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.psyrioty.magicWorldGuard.GUI.SelectWorld;

import static org.psyrioty.magicWorldGuard.Utils.RegionCreator.createRegionOrSubregion;

public class WorldGuardEvents implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().trim().split("\\s+");

        if (args.length == 0) {
            return;
        }

        // /rg или /region
        String command = args[0].toLowerCase();

        if (!command.equals("/rg") && !command.equals("/region")) {
            return;
        }

        Player player = event.getPlayer();

        switch (args.length) {
            case 1:
                event.setCancelled(true);
                new SelectWorld(player);
                break;

            case 3:
                if (args[1].equalsIgnoreCase("claim")) {
                    event.setCancelled(true);
                    createRegionOrSubregion(player, args[2]);
                }
                break;
        }
    }
}

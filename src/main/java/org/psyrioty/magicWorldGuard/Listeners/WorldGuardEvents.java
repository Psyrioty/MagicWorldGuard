package org.psyrioty.magicWorldGuard.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.psyrioty.magicWorldGuard.GUI.SelectWorld;

import static org.psyrioty.magicWorldGuard.Utils.RegionCreator.addMember;
import static org.psyrioty.magicWorldGuard.Utils.RegionCreator.createRegionOrSubregion;

public class WorldGuardEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        message = message.replace("/", "");
        String[] args = message.split(" ");

        if(
                !args[0].startsWith("rg") &&
                !args[0].startsWith("region") &&
                !args[0].startsWith("regions")
        ){
            return;
        }

        /*if(player.isOp()){
            return;
        }*/

        switch (args.length){
            case 1:
                new SelectWorld(player);
                break;
            case 3:
                switch (args[1]){
                    case "claim":
                        createRegionOrSubregion(player, args[2]);
                        break;
                }
                break;
            ///region addmember <регион> <ник>
            case 4:
                if(args[1].equals("addmember")){
                    OfflinePlayer member = Bukkit.getOfflinePlayer(args[3]);

                    if(member == null){
                        return;
                    }

                    addMember(
                            player,
                            member,
                            args[2]
                    );
                }
                break;
        }

        event.setCancelled(true);
    }
}

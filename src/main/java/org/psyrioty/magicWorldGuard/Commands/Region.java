package org.psyrioty.magicWorldGuard.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicWorldGuard.GUI.SelectWorld;

import static org.psyrioty.magicWorldGuard.Utils.RegionCreator.*;

public class Region implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender.isOp()){
            return true;
        }

        switch (args.length){
            case 0:
                new SelectWorld((Player) sender);
                break;
            case 2:
                switch (args[0]){
                    case "claim":
                        createRegionOrSubregion((Player) sender, args[1]);
                        break;
                }
                break;
            ///region addmember <регион> <ник>
            case 3:
                if(args[0].equals("addmember")){
                    OfflinePlayer member = Bukkit.getOfflinePlayer(args[2]);

                    if(member == null){
                        return true;
                    }

                    addMember(
                            (Player) sender,
                            member,
                            args[1]
                    );
                }
                break;
        }
        return true;
    }
}

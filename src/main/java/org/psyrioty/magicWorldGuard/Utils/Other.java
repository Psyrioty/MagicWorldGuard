package org.psyrioty.magicWorldGuard.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

public class Other {
    public static ItemStack createPlayerHead(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            PlayerProfile profile = Bukkit.createPlayerProfile(
                    player.getUniqueId(),
                    player.getName()
            );

            profile.update().thenAccept(updatedProfile -> {
                meta.setPlayerProfile((com.destroystokyo.paper.profile.PlayerProfile) updatedProfile);
                head.setItemMeta(meta);
            });
        }

        return head;
    }

    public static void createButton(
            int slot,
            String text,
            Material material,
            int customModelData,
            boolean check,
            Inventory inventory
    ){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        Component name = MiniMessage.miniMessage().deserialize(
                text
        );
        itemMeta.displayName(name);
        itemMeta.setCustomModelData(customModelData);
        itemMeta.setEnchantmentGlintOverride(check);

        itemStack.setItemMeta(itemMeta);

        inventory.setItem(slot, itemStack);
    }
}

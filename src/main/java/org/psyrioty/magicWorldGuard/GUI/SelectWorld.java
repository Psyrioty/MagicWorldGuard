package org.psyrioty.magicWorldGuard.GUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicWorldGuard.MagicWorldGuard;

import java.util.ArrayList;
import java.util.List;

public class SelectWorld implements InventoryHolder {
    Inventory inventory;
    Player player;
    List<World> worldList = new ArrayList<>();

    public SelectWorld(
            Player player
    ){
        Bukkit.getScheduler().runTaskAsynchronously(MagicWorldGuard.getPlugin(),()-> {
            this.player = player;
            createGUI();
            openGUI();
        });
    }

    private void createGUI(){
        inventory = Bukkit.createInventory(this, 27, "Список миров");

        int worldIterator = 9;
        for(org.bukkit.World world: Bukkit.getWorlds()){
            worldList.add(world);

            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta meta = itemStack.getItemMeta();

            FileConfiguration config = MagicWorldGuard.getPlugin().getDefaultConfig();
            String worldName = config.getString("worldsName." + world.getName());

            Component name;

            if(worldName.isEmpty()){
                name = MiniMessage.miniMessage().deserialize(
                        "<gradient:#ff0000:#00ffff><bold>" + world.getName() + "</bold></gradient>"
                );
            }else{
                name = MiniMessage.miniMessage().deserialize(
                        worldName
                );
            }

            meta.displayName(name);
            itemStack.setItemMeta(meta);

            inventory.setItem(worldIterator, itemStack);

            worldIterator++;
        }
    }

    private void openGUI(){
        Bukkit.getScheduler().runTask(MagicWorldGuard.getPlugin(), () -> {
            try {
                player.openInventory(inventory);
                MagicWorldGuard.getPlugin().getSelectWorldHashMap().put(player, this);
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicWorldGuard error SelectWorld.java in openGUI() " + exception.getMessage());
            }
        });
    }

    public List<World> getWorldList() {
        return worldList;
    }


    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ///////////НАЖАТИЯ НА КНОПКУ////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    public void click(Player player, int slot){
        int worldIterator = 0;

        for(World world: worldList){
            if(slot - 9 == worldIterator){
                new SelectRegion(player, world, this);
                return;
            }

            worldIterator++;
        }
    }
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}

package hgplus.corelogic;

import hgplus.HungerGamesPlus2;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class lootlogic {
    public void ClearAndRefilChests() {
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        Collection<ArmorStand> armorStand = Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")).getEntitiesByClass(ArmorStand.class);

        //these need to be seperated loops as when chest.update is called it breaks a lot between the loot table and chest.clear
        for(ArmorStand currentArmorStand : armorStand) {
            Block chestBlock = currentArmorStand.getWorld().getBlockAt(currentArmorStand.getLocation());
            chestBlock.setType(Material.CHEST);
            Chest chest = (Chest) chestBlock.getState();
            chest.getBlockInventory().clear();
        }

        for(ArmorStand currentArmorStand : armorStand){
            Chest chest = (Chest) currentArmorStand.getWorld().getBlockAt(currentArmorStand.getLocation()).getState();
            if(currentArmorStand.getScoreboardTags().contains("Common")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","commonbox")));
                chest.setCustomName("§0§lCommon Crate");
            }
            else if(currentArmorStand.getScoreboardTags().contains("Uncommon")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","uncommon")));
                chest.setCustomName("§a§lUncommon Crate");
            }
            else if(currentArmorStand.getScoreboardTags().contains("Rare")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","rarefoodbox")));
                chest.setCustomName("§5§lFood Crate");
            }
            else if(currentArmorStand.getScoreboardTags().contains("Epic")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","epicbox")));
                chest.setCustomName("§b§lEpic Crate");
            }
            else if(currentArmorStand.getScoreboardTags().contains("Legendary")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","legendarybox")));
                chest.setCustomName("§e§lLegendary Crate");
            }
            chest.update(true);
        }
    }
    public void ClearAndRefillCustomChests() {
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
        List<String> chestNames = savedlocations.getStringList("chests.names");
        for(String currentChestName : chestNames){
            Location OGChest = new Location(Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")),
                    savedlocations.getInt("chests.chests." + currentChestName + ".OGx"),
                    savedlocations.getInt("chests.chests." + currentChestName + ".OGy"),
                    savedlocations.getInt("chests.chests." + currentChestName + ".OGz"));

            Location NEWChest = new Location(Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")),
                    savedlocations.getInt("chests.chests." + currentChestName + ".NEWx"),
                    savedlocations.getInt("chests.chests." + currentChestName + ".NEWy"),
                    savedlocations.getInt("chests.chests." + currentChestName + ".NEWz"));


            Container container = (Chest) OGChest.getBlock().getState();
            ItemStack[] lootContentsToAdd = container.getInventory().getContents();

            ((Chest) NEWChest.getBlock().getState()).getInventory().setContents(lootContentsToAdd);

        }



    }
    public void registerCustomChest(String[] args, Player player){
        World world = player.getWorld();

        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
        List<String> names = savedlocations.getStringList("chests.names");
        names.add(String.format(args[2]));
        savedlocations.set("chests.names", names);
        savedlocations.set("chests.chests." + String.format(args[2]) + ".OGx", Integer.valueOf(args[3]));//Og is the chest to coppy from
        savedlocations.set("chests.chests." + String.format(args[2]) + ".OGy", Integer.valueOf(args[4]));
        savedlocations.set("chests.chests." + String.format(args[2]) + ".OGz", Integer.valueOf(args[5]));
        savedlocations.set("chests.chests." + String.format(args[2]) + ".NEWx", Integer.valueOf(args[6]));//New is the chest to copy to.
        savedlocations.set("chests.chests." + String.format(args[2]) + ".NEWy", Integer.valueOf(args[7]));
        savedlocations.set("chests.chests." + String.format(args[2]) + ".NEWz", Integer.valueOf(args[8]));
        savedlocations.set("chests.chests." + String.format(args[2]) + ".world", world.getName());//chests must be in the same world
        HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().saveCustomConfig();
    }
    public void deleteCustomChest(String name){
        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
        List<String> names = savedlocations.getStringList("chests.names");
        names.remove(name);
        savedlocations.set("chests.names", names);
        savedlocations.set("chests.chests." + name, null);
        HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().saveCustomConfig();
    }
}

package hgplus.setup;

import com.sun.nio.sctp.SendFailedNotification;
import hgplus.HungerGamesPlus2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class setup {
    public void setLobby(CommandSender sender) {
        Player player = (Player) sender;
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        config.set("lobby-spawn-position.spawn-world", player.getLocation().getWorld().getName());
        config.set("lobby-spawn-position.spawn-x", Double.valueOf(String.format("%,.2f", player.getLocation().getX())));
        config.set("lobby-spawn-position.spawn-y", Double.valueOf(String.format("%,.2f", player.getLocation().getY())));
        config.set("lobby-spawn-position.spawn-z", Double.valueOf(String.format("%,.2f", player.getLocation().getZ())));
        HungerGamesPlus2.getInstance().saveConfig();

        sender.sendMessage("§7[§3HGPlus§7]§f Successfully set §2lobby§f to §2" +
                String.format("%,.2f", player.getLocation().getX()) + "§f, §2" +
                String.format("%,.2f", player.getLocation().getY()) + "§f, §2" +
                String.format("%,.2f", player.getLocation().getZ()) + "§f in §2" +
                player.getLocation().getWorld().getName());
    }
    public void setLobbyRegion(String[] args){
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        config.set("lobby-region.x", Integer.valueOf(args[2]));
        config.set("lobby-region.y", Integer.valueOf(args[3]));
        config.set("lobby-region.z", Integer.valueOf(args[4]));
        config.set("lobby-region.x2", Integer.valueOf(args[5]));
        config.set("lobby-region.y2", Integer.valueOf(args[6]));
        config.set("lobby-region.z2", Integer.valueOf(args[7]));
        HungerGamesPlus2.getInstance().saveConfig();

    }
    public void showCrateNames() {
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        Collection<ArmorStand> armorStand = Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")).getEntitiesByClass(ArmorStand.class);

        for(ArmorStand currentArmorStand : armorStand) {
            if(currentArmorStand.getScoreboardTags().contains("Common") ||
                    currentArmorStand.getScoreboardTags().contains("Uncommon") ||
                    currentArmorStand.getScoreboardTags().contains("Rare") ||
                    currentArmorStand.getScoreboardTags().contains("Epic") ||
                    currentArmorStand.getScoreboardTags().contains("Legendary")) {
                currentArmorStand.setVisible(true);
                currentArmorStand.setCustomNameVisible(true);
            }
        }
    }
    public void hideCrateNames() {
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        Collection<ArmorStand> armorStand = Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")).getEntitiesByClass(ArmorStand.class);

        for(ArmorStand currentArmorStand : armorStand) {
            if(currentArmorStand.getScoreboardTags().contains("Common") ||
                    currentArmorStand.getScoreboardTags().contains("Uncommon") ||
                    currentArmorStand.getScoreboardTags().contains("Rare") ||
                    currentArmorStand.getScoreboardTags().contains("Epic") ||
                    currentArmorStand.getScoreboardTags().contains("Legendary")){
                currentArmorStand.setVisible(false);
                currentArmorStand.setCustomNameVisible(false);
            }
        }
    }

    public void registerDoor(String[] args, Player player){ //hgplus registerdoor name x y z x2 y2 z2
        World world = player.getWorld();

        //saving
        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
        List<String> names = savedlocations.getStringList("doors.names");
        names.add(String.format(args[2]));
        savedlocations.set("doors.names", names);
        savedlocations.set("doors.doors." + String.format(args[2]) + ".x", Integer.valueOf(args[3]));
        savedlocations.set("doors.doors." + String.format(args[2]) + ".y", Integer.valueOf(args[4]));
        savedlocations.set("doors.doors." + String.format(args[2]) + ".z", Integer.valueOf(args[5]));
        savedlocations.set("doors.doors." + String.format(args[2]) + ".x2", Integer.valueOf(args[6]));
        savedlocations.set("doors.doors." + String.format(args[2]) + ".y2", Integer.valueOf(args[7]));
        savedlocations.set("doors.doors." + String.format(args[2]) + ".z2", Integer.valueOf(args[8]));
        savedlocations.set("doors.doors." + String.format(args[2]) + ".world", world.getName());
        HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().saveCustomConfig();


        //making display block in world
        Location location = new Location(player.getWorld(), Double.valueOf(args[3]), Double.valueOf(args[4]), Double.valueOf(args[5]));

        Vector3f scale = new Vector3f(Math.abs( Integer.valueOf(args[3]) -  Integer.valueOf(args[6])) + 1,
                Math.abs( Integer.valueOf(args[4]) -  Integer.valueOf(args[7])) + 1,
                Math.abs( Integer.valueOf(args[5]) -  Integer.valueOf(args[8])) + 1);

        BlockDisplay displayblock = (BlockDisplay) world.spawnEntity(location, EntityType.BLOCK_DISPLAY);
        displayblock.setBlock(Material.WHITE_CONCRETE.createBlockData());
        displayblock.setTransformation(new Transformation(new Vector3f(0,0,0), new AxisAngle4f(0,0,0,1), scale, new AxisAngle4f(0,0,0,1)));
        displayblock.addScoreboardTag(String.format(args[2]));
    }

    public void unregisterdoor(String name) {
        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();

        //remove entity
        getDoorBlockDisplayByName(name).remove();

        //remove blocks
        fillRegionBetweenPoints(
                savedlocations.getInt("doors.doors." + name + ".x"),
                savedlocations.getInt("doors.doors." + name + ".y"),
                savedlocations.getInt("doors.doors." + name + ".z"),
                savedlocations.getInt("doors.doors." + name + ".x2"),
                savedlocations.getInt("doors.doors." + name + ".y2"),
                savedlocations.getInt("doors.doors." + name + ".z2"),
                Bukkit.getWorld(savedlocations.getString("doors.doors." + name + ".world")),
                Material.AIR);

        //saving
        List<String> names = savedlocations.getStringList("doors.names");
        names.remove(name);
        savedlocations.set("doors.names", names);
        savedlocations.set("doors.doors." + name, null);
        HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().saveCustomConfig();
    }

    public void openDoor(String name, int animDuration) {
        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
        BlockDisplay blockDisplay = getDoorBlockDisplayByName(name);


        double distanceToMoveDoor = Math.abs( Double.parseDouble(savedlocations.getString("doors.doors." + name + ".y")) - Double.parseDouble(savedlocations.getString("doors.doors." + name + ".y2"))) + 1.01;

        Vector3f scale = new Vector3f(
                (Math.abs( Float.parseFloat(savedlocations.getString("doors.doors." + name + ".x")) - Float.parseFloat(savedlocations.getString("doors.doors." + name + ".x2"))) + 1),
                (Math.abs( Float.parseFloat(savedlocations.getString("doors.doors." + name + ".y")) - Float.parseFloat(savedlocations.getString("doors.doors." + name + ".y2"))) + 1),
                (Math.abs( Float.parseFloat(savedlocations.getString("doors.doors." + name + ".z")) - Float.parseFloat(savedlocations.getString("doors.doors." + name + ".z2"))) + 1));

        //animation
        blockDisplay.setInterpolationDelay(0);
        blockDisplay.setInterpolationDuration(animDuration);
        blockDisplay.setTransformation(new Transformation(new Vector3f(0,-1 * ((float) distanceToMoveDoor),0), new AxisAngle4f(0,0,0,1), scale, new AxisAngle4f(0,0,0,1)));

        //remove blocks
        fillRegionBetweenPoints(
                savedlocations.getInt("doors.doors." + name + ".x"),
                savedlocations.getInt("doors.doors." + name + ".y"),
                savedlocations.getInt("doors.doors." + name + ".z"),
                savedlocations.getInt("doors.doors." + name + ".x2"),
                savedlocations.getInt("doors.doors." + name + ".y2"),
                savedlocations.getInt("doors.doors." + name + ".z2"),
                Bukkit.getWorld(savedlocations.getString("doors.doors." + name + ".world")),
                Material.AIR);
    }
    public void closeDoor(String name, int animDuration) {
        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
        BlockDisplay blockDisplay = getDoorBlockDisplayByName(name);

        Vector3f scale = new Vector3f(
                (Math.abs( Float.parseFloat(savedlocations.getString("doors.doors." + name + ".x")) - Float.parseFloat(savedlocations.getString("doors.doors." + name + ".x2"))) + 1),
                (Math.abs( Float.parseFloat(savedlocations.getString("doors.doors." + name + ".y")) - Float.parseFloat(savedlocations.getString("doors.doors." + name + ".y2"))) + 1),
                (Math.abs( Float.parseFloat(savedlocations.getString("doors.doors." + name + ".z")) - Float.parseFloat(savedlocations.getString("doors.doors." + name + ".z2"))) + 1));

        //animation
        blockDisplay.setInterpolationDelay(0);
        blockDisplay.setInterpolationDuration(animDuration);
        blockDisplay.setTransformation(new Transformation(new Vector3f(0,0,0), new AxisAngle4f(0,0,0,1), scale, new AxisAngle4f(0,0,0,1)));

        //remove blocks
        fillRegionBetweenPoints(
                savedlocations.getInt("doors.doors." + name + ".x"),
                savedlocations.getInt("doors.doors." + name + ".y"),
                savedlocations.getInt("doors.doors." + name + ".z"),
                savedlocations.getInt("doors.doors." + name + ".x2"),
                savedlocations.getInt("doors.doors." + name + ".y2"),
                savedlocations.getInt("doors.doors." + name + ".z2"),
                Bukkit.getWorld(savedlocations.getString("doors.doors." + name + ".world")),
                Material.BARRIER);
    }


    BlockDisplay getDoorBlockDisplayByName(String name){
        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
        Collection<BlockDisplay> blockDisplays = Bukkit.getWorld(savedlocations.getString("doors.doors." + name + ".world")).getEntitiesByClass(BlockDisplay.class);
        for(BlockDisplay currentBlockDisplay : blockDisplays) {
            if (currentBlockDisplay.getScoreboardTags().contains(name)) {
                return currentBlockDisplay;

            }
        }
        return null;
    }

    public void openStartDoors(){
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        List<String> startdoornames = config.getStringList("startdoornames");
        for(String currentdoorname : startdoornames){
            openDoor(currentdoorname, 10);
        }
    }
    public void closeStartDoors(){
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        List<String> startdoornames = config.getStringList("startdoornames");
        for(String currentdoorname : startdoornames){
            closeDoor(currentdoorname, 10);
        }
    }
    public boolean checkIfPlayerIsInRegion(int x, int y, int z, int x2, int y2, int z2, Player player){
        int minX = Math.min(x, x2);
        int minY = Math.min(y, y2);
        int minZ = Math.min(z, z2);
        int maxX = Math.max(x, x2);
        int maxY = Math.max(y, y2);
        int maxZ = Math.max(z, z2);
        Location playerloc = player.getLocation();
        if (playerloc.getX() >= minX && playerloc.getX() <= maxX &&
                playerloc.getY() >= minY && playerloc.getY() <= maxY &&
                playerloc.getZ() >= minZ && playerloc.getZ() <= maxZ){
            return true;
        }
        return false;
    }
    public void fillRegionBetweenPoints(int x, int y, int z, int x2, int y2, int z2, World world, Material mat){
        int minX = Math.min(x, x2);
        int minY = Math.min(y, y2);
        int minZ = Math.min(z, z2);
        int maxX = Math.max(x, x2);
        int maxY = Math.max(y, y2);
        int maxZ = Math.max(z, z2);

        for (int currentX = minX; currentX <= maxX; currentX++) {//gx
            for (int currentY = minY; currentY <= maxY; currentY++) {//y
                for (int currentZ = minZ; currentZ <= maxZ; currentZ++) {//z
                    Location blockpos = new Location(world, currentX, currentY, currentZ);
                    blockpos.getBlock().setType(mat);
                }
            }
        }
    }
}
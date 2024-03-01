package hgplus.commands;

import hgplus.HungerGamesPlus2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class hgplus implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if(!(sender instanceof Player)) {
//            sender.sendMessage("§c§l[!]§c Only players can execute this command.");
//            return true;
//        }
        int gameStatus = HungerGamesPlus2.getGameLogic().gameStatus;

        if (command.getName().equalsIgnoreCase("hgplus")) {
            if (args.length == 0) {
                sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                return false;
                // could probably have this show like a help page that shows all the available arguments for this command
            }
            if (args[0].equalsIgnoreCase("DEBUG")) {
                Player player = (Player) sender;
                for (ItemStack item : player.getInventory().getContents()) {
                    if(item == null) {continue;}
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    player.getInventory().removeItem(item);
                }

                player.getInventory().clear();
            }


            if (args[0].equalsIgnoreCase("customchests")) {
                if (args.length == 1) {
                    sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                    return false;
                }
                if (args[1].equalsIgnoreCase("createcustomchest")) {//hgplus chests createcustomchest x y z x2 y2 z2
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Only players can execute this command.");
                        return false;
                    }
                    if (args.length <= 8) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                        return false;
                    }
                    FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
                    List<String> names = savedlocations.getStringList("chests.names");
                    if (names.contains(args[2])) {
                        sender.sendMessage("§7[§3HGPlus§7]§c " + (args[2]) + " already Exists!");
                        return false;
                    }
                    Player player = (Player) sender;
                    HungerGamesPlus2.getLootLogic().registerCustomChest(args, player);
                    return true;
                }
                if (args[1].equalsIgnoreCase("listcustomchests")) {
                    sender.sendMessage("§7[§3HGPlus§7]§f Currently registered chests: §a" + HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig().getStringList("chests.names"));
                    return true;
                }
                if (args[1].equalsIgnoreCase("deletecustomchest")) {
                    if (args.length <= 2) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                        return false;
                    }
                    HungerGamesPlus2.getLootLogic().deleteCustomChest(args[2]);
                    return true;

                }
            }


            if (args[0].equalsIgnoreCase("doors")) {
                if (args.length == 1) {
                    sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                    return false;
                }
                if (args[1].equalsIgnoreCase("createdoor")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Only players can execute this command.");
                        return false;
                    }
                    if (args.length <= 8) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                        return false;
                    }
                    FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
                    List<String> names = savedlocations.getStringList("doors.names");
                    if (names.contains(args[2])) {
                        sender.sendMessage("§7[§3HGPlus§7]§c " + (args[2]) + " already Exists!");
                        return false;
                    }
                    Player player = (Player) sender;
                    HungerGamesPlus2.getSetup().registerDoor(args, player);
                    return true;
                }
                if (args[1].equalsIgnoreCase("listdoors")){
                    sender.sendMessage("§7[§3HGPlus§7]§f Currently registered doors: §a" + HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig().getStringList("doors.names"));
                    return true;
                }
                if (args[1].equalsIgnoreCase("deletedoor")){
                    if (args.length <= 2) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                        return false;
                    }

                    HungerGamesPlus2.getSetup().unregisterdoor(args[2]);
                    return true;
                }
                if (args[1].equalsIgnoreCase("opendoor")){
                    if (args.length == 2) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Please specify a door name!");
                        return false;
                    }
                    if (args.length == 3) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Please specify a durration!");
                        return false;
                    }
                    HungerGamesPlus2.getSetup().openDoor(args[2], Integer.valueOf(args[3]));
                    return true;
                }
                if (args[1].equalsIgnoreCase("closedoor")){
                    if (args.length <= 2) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Please specify a door name!");
                        return false;
                    }
                    if (args.length <= 3) {
                        sender.sendMessage("§7[§3HGPlus§7]§c Please specify a durration!");
                        return false;
                    }
                    HungerGamesPlus2.getSetup().closeDoor(args[2], Integer.valueOf(args[3]));
                    return true;
                }
            }


            if (args[0].equalsIgnoreCase("refillchests")) {
                FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();

                sender.sendMessage("§7[§3HGPlus§7]§f Attempting to refill chests...");
                sender.sendMessage("§7[§3HGPlus§7]§f Randomized Loot value set to:" + config.getBoolean("randomized-loot"));
                HungerGamesPlus2.getLootLogic().ClearAndRefilChests();
                HungerGamesPlus2.getLootLogic().ClearAndRefillCustomChests();
                sender.sendMessage("§7[§3HGPlus§7]§f Chests Refilled!");
                return true;
            }
            if (args[0].equalsIgnoreCase("hidecratenames")) {
                sender.sendMessage("§7[§3HGPlus§7]§f Debug crate mode §cdisabled§f, No players can see the names of crates anymore");
                HungerGamesPlus2.getSetup().hideCrateNames();
                return true;
            }
            if (args[0].equalsIgnoreCase("showcratenames")) {
                sender.sendMessage("§7[§3HGPlus§7]§f Debug crate mode §aenabled§f, All players can see the names of crates now");
                HungerGamesPlus2.getSetup().showCrateNames();
                return true;
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (gameStatus != -1) {
                    sender.sendMessage("§7[§3HGPlus§7]§c There is already a game active. End that one before starting another.");
                } else {
                    HungerGamesPlus2.getGameLogic().StartGame(sender);
                }
            } else if (args[0].equalsIgnoreCase("end")) {
                if (gameStatus == -1) {
                    sender.sendMessage("§7[§3HGPlus§7]§c There is no active game to end.");
                } else {
                    gameStatus = -1;
                    Bukkit.broadcastMessage("§7[§3HGPlus§7]§f Ending Game...");
                    HungerGamesPlus2.getGameLogic().EndGame();
                    Bukkit.broadcastMessage("§7[§3HGPlus§7]§f Game ended manually by " + sender.getName());

                }
            } else if (args[0].equalsIgnoreCase("players")) {
                if (args.length == 1) {
                    sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                    return false;
                }
                if (args[1].equalsIgnoreCase("list")) {
                    sender.sendMessage("§7[§3HGPlus§7]§c Active Players: (Command not setup yet)");
                }
            } else if (args[0].equalsIgnoreCase("config")) {
                if (args.length == 1) {
                    sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                    return false;
                }
                if (gameStatus == -1) {
                    if (args[1].equalsIgnoreCase("randomizedloot")) {
                        if (args.length == 2) {
                            sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                            return false;
                        }

                        if(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")){
                            FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
                            sender.sendMessage("§7[§3HGPlus§7]§f Set randomized-loot to: " + args[2]);
                            config.set("randomized-loot", Boolean.valueOf(args[2]));
                            HungerGamesPlus2.getInstance().saveConfig();
                            return true;
                        }
                        sender.sendMessage("§7[§3HGPlus§7]§c Invalid input, use: true, false!");
                        return false;
                    }
                    if (args[1].equalsIgnoreCase("setlobby")) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage("§7[§3HGPlus§7]§c Only players can execute this command.");
                            return false;
                        }
                        HungerGamesPlus2.getSetup().setLobby(sender); //Logic
                    }
                    if (args[1].equalsIgnoreCase("setlobbyregion")) {
                        if (args.length <= 7) {
                            sender.sendMessage("§7[§3HGPlus§7]§c Please specify a sub command!");
                            return true;
                        }
                        HungerGamesPlus2.getSetup().setLobbyRegion(args); //Logic
                    }
                    else if (args[1].equalsIgnoreCase("reload")) {
                        HungerGamesPlus2.getInstance().reloadConfig();
                        sender.sendMessage("§7[§3HGPlus§7]§f Config reloaded!");
                    }
                    else {
                        sender.sendMessage("§7[§3HGPlus§7]§c Subcommand not recognized.");
                    }

                } else {
                    sender.sendMessage("§7[§3HGPlus§7]§c Cannot change config during game.");
                }

            }
            else {
                sender.sendMessage("§7[§3HGPlus§7]§c That command was not recognized.");
            }

        }
        else {
            Bukkit.getLogger().info("§7[§3HGPlus§7]§c An error has occured.");
        }
        return false;
    }
}

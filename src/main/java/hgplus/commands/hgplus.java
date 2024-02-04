package hgplus.commands;

import hgplus.HungerGamesPlus2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                sender.sendMessage(ChatColor.RED + "Please specify a sub command!");
                return false;
                // could probably have this show like a help page that shows all the available arguments for this command
            }


            if (args[0].equalsIgnoreCase("DEBUG")) {
                HungerGamesPlus2.getGameLogic().ClearAndRefilChests();
                return false;
            }

            if (args[0].equalsIgnoreCase("start")) {
                if (gameStatus != -1) {
                    sender.sendMessage("There is already a game active. End that one before starting another.");
                } else {
                    HungerGamesPlus2.getGameLogic().StartGame(sender);
                }
            } else if (args[0].equalsIgnoreCase("end")) {
                if (gameStatus == -1) {
                    sender.sendMessage("There is no active game to end.");
                } else {
                    gameStatus = -1;
                    Bukkit.broadcastMessage("Ending Game...");
                    HungerGamesPlus2.getGameLogic().EndGame();
                    Bukkit.broadcastMessage("Game ended manually by " + sender.getName());

                }
            } else if (args[0].equalsIgnoreCase("players")) {
                if (args[1].equalsIgnoreCase("list")) {
                    sender.sendMessage("Active Players: (Command not setup yet)");
                }
            } else if (args[0].equalsIgnoreCase("config")) {
                if (gameStatus == -1) {
                    if (args[1].equalsIgnoreCase("setspawn")) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage("§c§l[!]§c Only players can execute this command.");
                            return false;
                        }
                        Player player = (Player) sender;
                        HungerGamesPlus2.getInstance().getConfig().set("spawn-position.spawn-world", player.getLocation().getWorld().getName());
                        HungerGamesPlus2.getInstance().getConfig().set("spawn-position.spawn-x", Double.valueOf(String.format("%,.2f", player.getLocation().getX())));
                        HungerGamesPlus2.getInstance().getConfig().set("spawn-position.spawn-y", Double.valueOf(String.format("%,.2f", player.getLocation().getY())));
                        HungerGamesPlus2.getInstance().getConfig().set("spawn-position.spawn-z", Double.valueOf(String.format("%,.2f", player.getLocation().getZ())));
                        HungerGamesPlus2.getInstance().saveConfig();

                        sender.sendMessage("Successfully set spawn to " + String.format("%,.2f", player.getLocation().getX()) + ", " + String.format("%,.2f", player.getLocation().getY()) + ", " + String.format("%,.2f", player.getLocation().getZ()) + " in \"" + player.getLocation().getWorld().getName() + "\"");

                    } else if (args[1].equalsIgnoreCase("reload")) {
                        HungerGamesPlus2.getInstance().reloadConfig();
                        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
                    } else {
                        sender.sendMessage("Subcommand not recognized.");
                    }

                } else {
                    sender.sendMessage("Cannot change config during game.");
                }

            } else {
                sender.sendMessage("That command was not recognized.");
            }

        } else {
            Bukkit.getLogger().info("§c§l[!]§c An error has occured.");
        }


        return false;

    }
}

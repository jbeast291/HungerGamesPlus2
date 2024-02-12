package hgplus.commands;

import hgplus.HungerGamesPlus2;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class hgplustabcompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){


        if (command.getName().equalsIgnoreCase("hgplus")) {
            if (args.length == 1) {
                List<String> ListToReturn = new ArrayList<>();
                ListToReturn.add("refillchests");
                ListToReturn.add("hidecratenames");
                ListToReturn.add("showcratenames");
                ListToReturn.add("start");
                ListToReturn.add("end");
                ListToReturn.add("players");
                ListToReturn.add("config");
                ListToReturn.add("doors");
                ListToReturn.add("customchests");
                return ListToReturn;
            }
            else if (args[0].equalsIgnoreCase("customchests")) {
                if (args.length == 2) {
                    List<String> ListToReturn = new ArrayList<>();
                    ListToReturn.add("createcustomchest");
                    ListToReturn.add("listcustomchests");
                    ListToReturn.add("deletecustomchest");
                    return ListToReturn;
                }
                if (args[1].equalsIgnoreCase("createcustomchest")) { // name x y z 2x 2y 2z
                    if (args.length == 3) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(Name)");
                        return ListToReturn;
                    } else if (args.length == 4) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(OGx)");
                        return ListToReturn;
                    } else if (args.length == 5) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(OGy)");
                        return ListToReturn;
                    } else if (args.length == 6) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(OGz)");
                        return ListToReturn;
                    } else if (args.length == 7) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(NEWx)");
                        return ListToReturn;
                    } else if (args.length == 8) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(NEWy)");
                        return ListToReturn;
                    } else if (args.length == 9) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(NEW2)");
                        return ListToReturn;
                    }
                } else if (args[1].equalsIgnoreCase("listcustomchests")) {
                    return new ArrayList<>();
                } else if (args[1].equalsIgnoreCase("deletecustomchest")) {
                    if (args.length == 3) {
                        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
                        List<String> names = savedlocations.getStringList("chests.names");
                        return names;
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("doors")) {
                if (args.length == 2) {
                    List<String> ListToReturn = new ArrayList<>();
                    ListToReturn.add("createdoor");
                    ListToReturn.add("listdoors");
                    ListToReturn.add("deletedoor");
                    ListToReturn.add("opendoor");
                    ListToReturn.add("closedoor");
                    return ListToReturn;
                }
                if (args[1].equalsIgnoreCase("createdoor")){ // name x y z 2x 2y 2z
                    if (args.length == 3) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(Name)");
                        return ListToReturn;
                    }
                    else if (args.length == 4) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(x)");
                        return ListToReturn;
                    }
                    else if (args.length == 5) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(y)");
                        return ListToReturn;
                    }
                    else if (args.length == 6) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(z)");
                        return ListToReturn;
                    }
                    else if (args.length == 7) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(x2)");
                        return ListToReturn;
                    }
                    else if (args.length == 8) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(y2)");
                        return ListToReturn;
                    }
                    else if (args.length == 9) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(z2)");
                        return ListToReturn;
                    }
                }
                else if (args[1].equalsIgnoreCase("listdoors")){
                    return new ArrayList<>();
                }
                else if (args[1].equalsIgnoreCase("deletedoor")){
                    if (args.length == 3) {
                        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
                        List<String> names = savedlocations.getStringList("doors.names");
                        return names;
                    }
                }
                else if (args[1].equalsIgnoreCase("opendoor")){
                    if (args.length == 3) {
                        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
                        List<String> names = savedlocations.getStringList("doors.names");
                        return names;
                    }
                    if (args.length == 4) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(animdurration)");
                        return ListToReturn;
                    }
                }
                else if (args[1].equalsIgnoreCase("closedoor")){
                    if (args.length == 3) {
                        FileConfiguration savedlocations = HungerGamesPlus2.getInstance().getSaveDataHandlerinstance().getConfig();
                        List<String> names = savedlocations.getStringList("doors.names");
                        return names;
                    }
                    if (args.length == 4) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(animdurration)");
                        return ListToReturn;
                    }
                }
                return new ArrayList<>();
            }
            else if (args[0].equalsIgnoreCase("refillchests") && args.length == 2) {
                return new ArrayList<>();
            }
            else if (args[0].equalsIgnoreCase("hidecratenames") && args.length == 2) {
                return new ArrayList<>();
            }
            else if (args[0].equalsIgnoreCase("showcratenames") && args.length == 2) {
                return new ArrayList<>();
            }
            else if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                return new ArrayList<>();
            }
            else if (args[0].equalsIgnoreCase("end") && args.length == 2) {
                return new ArrayList<>();
            }
            else if (args[0].equalsIgnoreCase("players") && args.length == 2) {
                List<String> ListToReturn = new ArrayList<>();
                ListToReturn.add("list");
                return ListToReturn;
            }
            else if (args[0].equalsIgnoreCase("config")) {
                if(args.length == 2){
                    List<String> ListToReturn = new ArrayList<>();
                    ListToReturn.add("setlobby");
                    ListToReturn.add("setlobbyregion");
                    ListToReturn.add("reload");
                    return ListToReturn;
                }
                if (args[1].equalsIgnoreCase("setlobbyregion")){ // name x y z 2x 2y 2z
                    if (args.length == 3) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(x)");
                        return ListToReturn;
                    }
                    else if (args.length == 4) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(y)");
                        return ListToReturn;
                    }
                    else if (args.length == 5) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(z)");
                        return ListToReturn;
                    }
                    else if (args.length == 6) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(x2)");
                        return ListToReturn;
                    }
                    else if (args.length == 7) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(y2)");
                        return ListToReturn;
                    }
                    else if (args.length == 8) {
                        List<String> ListToReturn = new ArrayList<>();
                        ListToReturn.add("(z2)");
                        return ListToReturn;
                    }
                }
            }
        }
        return new ArrayList<>();
    }
}

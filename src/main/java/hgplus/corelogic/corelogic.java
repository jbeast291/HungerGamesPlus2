package hgplus.corelogic;

import hgplus.HungerGamesPlus2;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.BlockDisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class corelogic {

    //-2: game over/10s victory intermission thing
    //-1: no game/off
    // 0: starting up
    // 1: game active
    public int gameStatus = -1;
    private int startCountdown = 10;
    private int endCountdown = 10;
    public BossBar bossBar;
    private Chest chest;
    private double initialWorldBorderSize = 299;
    private List<Integer> gameEventTaskIDs = new ArrayList<Integer>();
    private int playerPositionCounter = 0;

    // private static Location spawnpoint = new Location(Bukkit.getWorld("world"), -65.5,98,225.5);

    public Location getSpawnLocation() {
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        Location spawnLocation = new Location(Bukkit.getWorld(
                config.getString("lobby-spawn-position.spawn-world")),
                config.getDouble("lobby-spawn-position.spawn-x"),
                config.getDouble("lobby-spawn-position.spawn-y"),
                config.getDouble("lobby-spawn-position.spawn-z"));


        return spawnLocation;
    }

    private void addWorldborderEvent(int delay, double size, int time) { //delay is how long before it this is called, size is blocks for border, time is how long it takes to get to that size
        try {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    gameEventTaskIDs.remove(gameEventTaskIDs.indexOf(getTaskId()));
                    FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
                    Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")).getWorldBorder().setSize(size, time);
                    Bukkit.broadcastMessage("§7[§3HGPlus§7]§e§l World Border shrinking to §n" + size + "§n§l blocks over §n" + time + "§n§l seconds.");
                }
            }.runTaskLater(HungerGamesPlus2.getInstance(), delay);
            gameEventTaskIDs.add(task.getTaskId());
            Bukkit.getLogger().info("[HGPlus] Added " + task.getTaskId() + " to task ids. New size: " + gameEventTaskIDs.size());
        } catch (UnsupportedOperationException e) {
            // Log a warning message
            Bukkit.getLogger().warning("[HGPlus] Failed to schedule game start task: " + e.getMessage());
        }
    }

    private int startupTask;

    public List<UUID> activePlayers = new ArrayList<UUID>();

    public List<UUID> activePlayersBeforeLanding = new ArrayList<UUID>();//Elytra exploit fix

    public List<UUID> initialPlayers = new ArrayList<UUID>();

    public List<UUID> lateSpectatorPlayers = new ArrayList<UUID>();




    public void broadcastAllActionbarMessage(BaseComponent[] message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
        });
    }

    public void broadcastActiveActionbarMessage(BaseComponent[] message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
        });
    }
    public void StartGame(CommandSender sender) {
        gameEventTaskIDs.clear();
        gameStatus = 0;
        activePlayers.clear();
        activePlayersBeforeLanding.clear();
        initialPlayers.clear();
        sender.sendMessage("§7[§3HGPlus§7]§f Starting...");
        if (bossBar == null) {
            bossBar = HungerGamesPlus2.getInstance().getServer().createBossBar(ChatColor.YELLOW + "Loading players... ", BarColor.GREEN, BarStyle.SEGMENTED_10);
        } else {
            bossBar.setTitle("§eLoading players... ");
            bossBar.setColor(BarColor.GREEN);
            bossBar.setStyle(BarStyle.SEGMENTED_10);
        }
        bossBar.setVisible(false);
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            bossBar.addPlayer(player);
            if (!player.hasPermission("hungergamesplus.nonplayer")) {
                activePlayers.add(player.getUniqueId());
                activePlayersBeforeLanding.add(player.getUniqueId());//Elytra exploit fix
                initialPlayers.add(player.getUniqueId());
            } else {
                Bukkit.getLogger().info("§7[§3HGPlus§7]§f " + player.getName() + " is not playing in the game.");
            }
        });
        if (activePlayers.size() < 2) {
            Bukkit.broadcastMessage("§7[§3HGPlus§7]§c Start cancelled. Need at least two players to begin.");
            bossBar.removeAll();
            gameStatus = -1;
            initialPlayers.clear();
            activePlayersBeforeLanding.clear();
            activePlayers.clear();
            return;
        }
        Bukkit.broadcastMessage("§7[§3HGPlus§7]§f " + activePlayers.size() + " Players loaded.");
        startCountdown = 10;
        bossBar.setVisible(true);

        Bukkit.broadcastMessage("§7[§3HGPlus§7]§f Refilling Chests...");
        HungerGamesPlus2.getLootLogic().ClearAndRefilChests();
        HungerGamesPlus2.getLootLogic().ClearAndRefillCustomChests();
        Bukkit.broadcastMessage("§7[§3HGPlus§7]§f Chests Refilled!");

        Bukkit.broadcastMessage("§7[§3HGPlus§7]§f Clearing ground items!");
        HungerGamesPlus2.getSetup().clearGroundItems();

        activePlayers.forEach(pUUID -> {

            Player player = Bukkit.getPlayer(pUUID);

            if (player.isDead()) {
                player.spigot().respawn();
            }

            player.setGameMode(GameMode.SURVIVAL);
            player.teleportAsync(getSpawnLocation());
            player.getInventory().clear();
            player.getEquipment().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGlowing(false);
        });

        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")).getWorldBorder().setSize(initialWorldBorderSize, 5);

        //announce to discord
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"discordbroadcast hglogs `[HGPlus]` A game was just started at **" + HungerGamesPlus2.getSetup().getCurrentTimeAsString() + "**");




        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                bossBar.setTitle("§7[§3HGPlus§7]§f Starting in: §6" + startCountdown);
                if (startCountdown == 0) {
                    startupTask = -1;
                    cancel();
                    bossBar.setStyle(BarStyle.SOLID);
                    broadcastActiveActionbarMessage(TextComponent.fromLegacyText("§a§lJump!"));
                    //set game status to active
                    gameStatus = 1;
                    //open doors
                    HungerGamesPlus2.getSetup().openStartDoors();
                    //setup players
                    activePlayers.forEach(pUUID -> {
                        Player player = Bukkit.getPlayer(pUUID);
                        player.getInventory().clear();
                        player.getEquipment().clear();
                        player.getEquipment().setChestplate(new ItemStack(Material.ELYTRA));
                        player.sendMessage("§7[§3HGPlus§7]§f Your §aelytra§f has been automatically equipped!");
                        player.setHealth(20);
                        player.setFoodLevel(20);
                        player.setExp(0);
                        player.setExperienceLevelAndProgress(0);
                        player.setRespawnLocation(getSpawnLocation());
                    });


                    bossBar.setProgress(Double.valueOf(initialPlayers.size()) / Double.valueOf(activePlayers.size()));
                    bossBar.setTitle("§7[§3HGPlus§7]§f Players Remaining: §6" + String.valueOf(activePlayers.size()));

                    //for positions in discord
                    playerPositionCounter = initialPlayers.size();

                    //send initial player list to discord
                    List<String> initialPlayerNames = new ArrayList<>();
                    for(UUID currentPlayerUUID : initialPlayers) {
                        initialPlayerNames.add(Bukkit.getOfflinePlayer(currentPlayerUUID).getName());
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"discordbroadcast hglogs Initial players in this game: **" + initialPlayerNames + "**");


                    //worldborder events
                    addWorldborderEvent(2400, 150, 60); //2 min wait
                    addWorldborderEvent(4800, 20, 60);//4 min wait

                    // REMOVE AND KILL PLAYERS IF THEY DON'T JUMP IN 30s
                    try {
                        BukkitTask task = new BukkitRunnable() {
                            @Override
                            public void run() {
                                gameEventTaskIDs.remove(gameEventTaskIDs.indexOf(getTaskId()));
                                Bukkit.broadcastMessage("§7[§3HGPlus§7]§f Closing lobby doors! Players that have §cnot§f jummped will be removed");
                                HungerGamesPlus2.getSetup().closeStartDoors();

                                List<UUID> playersMarkedForDeath = new ArrayList<UUID>();

                                activePlayers.forEach(pUUID -> {
                                    Player player = Bukkit.getPlayer(pUUID);
                                    if (player == null) return;
                                    if(HungerGamesPlus2.getSetup().checkIfPlayerIsInRegion(config.getInt("looby-region.x"),
                                            config.getInt("lobby-region.y"),
                                            config.getInt("lobby-region.z"),
                                            config.getInt("lobby-region.x2"),
                                            config.getInt("lobby-region.y2"),
                                            config.getInt("lobby-region.z2"),
                                            player)){
                                        playersMarkedForDeath.add(pUUID);
                                    }
                                });
                                playersMarkedForDeath.forEach(pUUID -> {
                                    Player player = Bukkit.getPlayer(pUUID);
                                    if (gameStatus == 1) {
                                        player.setHealth(0);
                                    }
                                });
                            }
                        }.runTaskLater(HungerGamesPlus2.getInstance(), 600);//30s
                        gameEventTaskIDs.add(task.getTaskId());
                        Bukkit.getLogger().info("[HGPlus] Added " + task.getTaskId() + " to task ids. New size: " + gameEventTaskIDs.size());
                    }
                    catch (UnsupportedOperationException e) {
                        // Log a warning message
                        Bukkit.getLogger().warning("[HGPlus] Failed to schedule game start task: " + e.getMessage());
                    }

                    // ADD GLOW TO ALL PLAYERS AFTER CERTAIN AMOUNT OF TIME (OPTIONAL)
                    try {
                        BukkitTask task = new BukkitRunnable() {
                            @Override
                            public void run() {
                                gameEventTaskIDs.remove(gameEventTaskIDs.indexOf(getTaskId()));
                                Bukkit.broadcastMessage("§7[§3HGPlus§7]§f Applied glow effect to all players");
                                activePlayers.forEach(pUUID -> {
                                    Player player = Bukkit.getPlayer(pUUID);
                                    player.setGlowing(true);

                                });
                            }
                        }.runTaskLater(HungerGamesPlus2.getInstance(), 4800);//3
                        gameEventTaskIDs.add(task.getTaskId());
                        Bukkit.getLogger().info("[HGPlus] Added " + task.getTaskId() + " to task ids. New size: " + gameEventTaskIDs.size());
                    }
                    catch (UnsupportedOperationException e) {
                        // Log a warning message
                        Bukkit.getLogger().warning("[HGPlus] Failed to schedule game start task: " + e.getMessage());
                    }


                } else {
                    //Bukkit.broadcastMessage("Starting in: " + String.valueOf(startCountdown));
                    broadcastActiveActionbarMessage(TextComponent.fromLegacyText("§a§lStarting in: §f§l" + startCountdown));
                    bossBar.setProgress(Double.valueOf(startCountdown) / 10);
                    startCountdown--;

                }
            }
        };

        try {
            task.runTaskTimer(HungerGamesPlus2.getInstance(), 0, 20);
            startupTask = task.getTaskId();
        } catch (UnsupportedOperationException e) {
            // Log a warning message
            Bukkit.getLogger().warning("§7[§3HGPlus§7]§c Failed to schedule game start task: " + e.getMessage());
        }
    }

    public void removePlayerFromEvent(UUID pUUID, UUID killerUUID, boolean died, boolean leftGame) {
        if (!activePlayers.contains(pUUID)) return;
        Player player = Bukkit.getPlayer(pUUID);
        Player killer;

        //remove the player if they left just before the game started
        if (gameStatus == 0){
            initialPlayers.remove(pUUID);
        }

        //get ref to killer
        if (killerUUID != null) {
            killer = Bukkit.getPlayer(killerUUID);
        } else {
            killer = null;
        }

        //remove the player from the event list and elytra list
        activePlayers.remove(pUUID);
        activePlayersBeforeLanding.remove(pUUID);

        player.setGlowing(false);

        Player winner = Bukkit.getPlayer(activePlayers.get(0));

        bossBar.setProgress(new Double(activePlayers.size()) / new Double(initialPlayers.size()));

        if (died) {//set spectator etc
            if (Bukkit.getOnlinePlayers().contains(player)) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }

        if (activePlayers.size() <= 1) {

            if (leftGame){
                Bukkit.broadcastMessage("§7[§3HGPlus§7]§c§l " + player.getName() + "§f left the game and was eliminated!");
            }
            else if (killer == null) {
                Bukkit.broadcastMessage("§7[§3HGPlus§7]§c§l " + player.getName() + "§f was eliminated!");
            }
            else {
                Bukkit.broadcastMessage("§7[§3HGPlus§7]§c§l " + player.getName() + "§f was eliminated by §c§l" + killer.getName() + "§f!");
            }

            Bukkit.broadcastMessage("§7[§3HGPlus§7]§f Game over! The winner is §6" + winner.getName() + "§f!");

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"discordbroadcast hglogs  **" + player.getName() + "** Position: `" + playerPositionCounter + "`!");

            //announce to discord
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"discordbroadcast hglogs  **"+ winner.getName() + "** just won a game!");

            //end the game
            EndGame();
        } else {
            //update bossbar
            bossBar.setTitle("§7[§3HGPlus§7]§f Players Remaining: §6" + String.valueOf(activePlayers.size()));
            if (leftGame){
                Bukkit.broadcastMessage("§7[§3HGPlus§7]§c§l " + player.getName() + "§f left the game and was eliminated!");
            }
            else if (killer == null) {
                Bukkit.broadcastMessage("§7[§3HGPlus§7]§c§l " + player.getName() + "§f was eliminated! §c§l" + activePlayers.size() + "§f players remaining!");
            } else {
                Bukkit.broadcastMessage("§7[§3HGPlus§7]§c§l " + player.getName() + "§f was eliminated by §c§l" + killer.getName() + "§f! §c§l" + activePlayers.size() + "§f players remaining!");
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"discordbroadcast hglogs  **" + player.getName() + "** Position: `" + playerPositionCounter + "`!");
            playerPositionCounter--;
        }
    }
    public void EndGame() {
        if (startupTask != -1) {
            Bukkit.getScheduler().cancelTask(startupTask);
        }

        //send the time the game ended to discord
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"discordbroadcast hglogs `[HGPlus]` Time game ended: **" + HungerGamesPlus2.getSetup().getCurrentTimeAsString() + "**");

        Bukkit.getLogger().info("Ending game!");

        //close doors if they havnt been already
        HungerGamesPlus2.getSetup().closeStartDoors();

        gameEventTaskIDs.forEach(e -> {
            Bukkit.getServer().getScheduler().cancelTask(e);
        });

        gameEventTaskIDs.clear();
        gameStatus = -2;

        Player winner = Bukkit.getPlayer(activePlayers.get(0));

        bossBar.setTitle("§a§lWinner: §f§l" + winner.getName() + "!");
        bossBar.setProgress(1);
        bossBar.setStyle(BarStyle.SOLID);


        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                gameStatus = -1;
                bossBar.setVisible(false);
                winner.setGlowing(false);

                //clear ground items
                HungerGamesPlus2.getSetup().clearGroundItems();

                activePlayers.forEach(pUUID -> {
                    Player player = Bukkit.getPlayer(pUUID);
                    player.getInventory().clear();
                    player.getEquipment().clear();
                });

                activePlayers.clear();

                initialPlayers.forEach(pUUID -> {
                    Player player = Bukkit.getPlayer(pUUID);
                    if (player == null) return;

                    if (player.isDead()) {
                        player.spigot().respawn();
                    }

                    player.setGameMode(GameMode.SURVIVAL);

                    player.teleportAsync(getSpawnLocation());//teleport players

                    player.setFireTicks(0);
                    player.setHealth(20);
                    player.setFoodLevel(20);

                    //just in case
                    player.getInventory().clear();
                    player.getEquipment().clear();

                });

                initialPlayers.clear();

                lateSpectatorPlayers.forEach(pUUID ->{
                    Player player = Bukkit.getPlayer(pUUID);

                    if (player == null) return;

                    if (player.isDead()) {
                        player.spigot().respawn();
                    }

                    player.setGameMode(GameMode.SURVIVAL);

                    player.teleportAsync(getSpawnLocation());//teleport players

                    player.setFireTicks(0);
                    player.setHealth(20);
                    player.setFoodLevel(20);

                    //just in case
                    player.getInventory().clear();
                    player.getEquipment().clear();
                });

                lateSpectatorPlayers.clear();

                bossBar.removeAll();

            }
        };

        try {
            task.runTaskLater(HungerGamesPlus2.getInstance(), 150);
        } catch (UnsupportedOperationException e) {
            // Log a warning message
            Bukkit.getLogger().warning("Failed to schedule game start task: " + e.getMessage());
        }

    }
}


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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class corelogic {

    //-2: game over/10s victory intermission thing
    //-1: no game/off
    // 0: starting up
    // 1: game active
    public int gameStatus = -1;
    private int startCountdown = 10;
    private int endCountdown = 10;
    private BossBar bossBar;
    private Chest chest;
    private double initialWorldBorderSize = 100;
    private List<Integer> gameEventTaskIDs = new ArrayList<Integer>();

    // private static Location spawnpoint = new Location(Bukkit.getWorld("world"), -65.5,98,225.5);

    private Location getSpawnLocation() {
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        Location spawnLocation = new Location(Bukkit.getWorld(
                config.getString("lobby-spawn-position.spawn-world")),
                config.getDouble("lobby-spawn-position.spawn-x"),
                config.getDouble("lobby-spawn-position.spawn-y"),
                config.getDouble("lobby-spawn-position.spawn-z"));

        return spawnLocation;
    }
    private Location getStartLocation() {
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        Location startLocation = new Location(Bukkit.getWorld(config.getString("start-position.spawn-world")),
                config.getDouble("start-position.spawn-x"),
                config.getDouble("start-position.spawn-y"),
                config.getDouble("start-position.spawn-z"));

        return startLocation;
    }

    public void ClearAndRefilChests() {
        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        Collection<ArmorStand> armorStand = Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")).getEntitiesByClass(ArmorStand.class);
        for(ArmorStand currentArmorStand : armorStand){
            Block chestBlock = currentArmorStand.getWorld().getBlockAt(currentArmorStand.getLocation());
            chestBlock.setType(Material.CHEST);
            Chest chest = (Chest) chestBlock.getState();
            chest.getSnapshotInventory().clear();
            if(currentArmorStand.getScoreboardTags().contains("Common")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","commonbox")));
                chest.setCustomName("§l§0Common Crate");
            }
            else if(currentArmorStand.getScoreboardTags().contains("Uncommon")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","uncommon")));
                chest.setCustomName("§l§aUncommon Crate");
            }
            else if(currentArmorStand.getScoreboardTags().contains("Rare")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","rarefoodbox")));
                chest.setCustomName("§l§9Food Crate");
            }
            else if(currentArmorStand.getScoreboardTags().contains("Epic")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","epicbox")));
                chest.setCustomName("§l§bEpic Crate");
            }
            else if(currentArmorStand.getScoreboardTags().contains("Legendary")){
                chest.setLootTable(Bukkit.getServer().getLootTable(new NamespacedKey("hungergamesloottables","legendarybox")));
                chest.setCustomName("§l§eLegendary Crate");
            }
            chest.update(true);
        }
    }




    private void addWorldborderEvent(int delay, double size, int time) {
        try {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    gameEventTaskIDs.remove(gameEventTaskIDs.indexOf(getTaskId()));
                    FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
                    Bukkit.getWorld(config.getString("lobby-spawn-position.spawn-world")).getWorldBorder().setSize(size, time);
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "World Border shrinking to " + ChatColor.UNDERLINE + size + ChatColor.YELLOW + ChatColor.BOLD + " blocks over " + ChatColor.UNDERLINE + time + ChatColor.YELLOW + ChatColor.BOLD + " seconds.");
                }
            }.runTaskLater(HungerGamesPlus2.getInstance(), delay);
            gameEventTaskIDs.add(task.getTaskId());
            Bukkit.getLogger().info("Added " + task.getTaskId() + " to task ids. New size: " + gameEventTaskIDs.size());
        } catch (UnsupportedOperationException e) {
            // Log a warning message
            Bukkit.getLogger().warning("Failed to schedule game start task: " + e.getMessage());
        }
    }

    public void EndGame() {
        if (startupTask != -1) {
            Bukkit.getScheduler().cancelTask(startupTask);
        }

        Bukkit.getLogger().info("Ending game!");

        gameEventTaskIDs.forEach(e -> {
            Bukkit.getServer().getScheduler().cancelTask(e);
        });

        gameEventTaskIDs.clear();
        gameStatus = -2;

        Player winner = Bukkit.getPlayer(activePlayers.get(0));

        bossBar.setTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Winner: " + winner.getName() + "!");
        bossBar.setProgress(1);
        bossBar.setStyle(BarStyle.SOLID);


        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                gameStatus = -1;
                bossBar.setVisible(false);
                winner.setGlowing(false);

                activePlayers.forEach(pUUID -> {
                    Player player = Bukkit.getPlayer(pUUID);
                    player.getInventory().clear();
                    player.getEquipment().clear();
                });

                activePlayers.clear();

                initialPlayers.forEach(pUUID -> {
                    Player player = Bukkit.getPlayer(pUUID);

                    if (player.isDead()) {
                        player.spigot().respawn();
                    }

                    player.setGameMode(GameMode.SURVIVAL);

                    player.teleportAsync(getSpawnLocation());//teleport players

                    player.setHealth(20);
                    player.setFoodLevel(20);

                });

                initialPlayers.clear();
                bossBar.removeAll();

            }
        };

        try {
            task.runTaskLater(HungerGamesPlus2.getInstance(), 60);
        } catch (UnsupportedOperationException e) {
            // Log a warning message
            Bukkit.getLogger().warning("Failed to schedule game start task: " + e.getMessage());
        }

    }

    private int startupTask;

    public List<UUID> activePlayers = new ArrayList<UUID>();

    public List<UUID> activePlayersBeforeLanding = new ArrayList<UUID>();//Elytra exploit fix

    public List<UUID> initialPlayers = new ArrayList<UUID>();




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
            bossBar.setTitle(ChatColor.YELLOW + "Loading players... ");
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


        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                bossBar.setTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "Starting in: " + ChatColor.WHITE + ChatColor.BOLD + startCountdown);
                if (startCountdown == 0) {
                    startupTask = -1;
                    cancel();
                    bossBar.setStyle(BarStyle.SOLID);
                    broadcastActiveActionbarMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "" + ChatColor.BOLD + "Start!"));
                    gameStatus = 1;
                    activePlayers.forEach(pUUID -> {
                        Player player = Bukkit.getPlayer(pUUID);
                        player.getInventory().clear();
                        player.getEquipment().clear();
                        player.getEquipment().setChestplate(new ItemStack(Material.ELYTRA));
                        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Your elytra has been automatically equipped!");
                        player.setHealth(20);
                        player.setFoodLevel(20);
                        player.setGliding(true);
                        player.teleportAsync(getStartLocation());

                    });
                    bossBar.setProgress(Double.valueOf(initialPlayers.size()) / Double.valueOf(activePlayers.size()));
                    bossBar.setTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players Remaining: " + String.valueOf(activePlayers.size()));

                    addWorldborderEvent(200, 90, 50);
                    addWorldborderEvent(400, 75, 50);

                    // ADD GLOW TO ALL PLAYERS AFTER CERTAIN AMOUNT OF TIME (OPTIONAL)
                    try {
                        BukkitTask task = new BukkitRunnable() {
                            @Override
                            public void run() {
                                gameEventTaskIDs.remove(gameEventTaskIDs.indexOf(getTaskId()));
                                Bukkit.broadcastMessage("Applied glow effect to all players");
                                activePlayers.forEach(pUUID -> {
                                    Player player = Bukkit.getPlayer(pUUID);
                                    player.setGlowing(true);

                                });
                            }
                        }.runTaskLater(HungerGamesPlus2.getInstance(), 600);
                        gameEventTaskIDs.add(task.getTaskId());
                        Bukkit.getLogger().info("Added " + task.getTaskId() + " to task ids. New size: " + gameEventTaskIDs.size());
                    } catch (UnsupportedOperationException e) {
                        // Log a warning message
                        Bukkit.getLogger().warning("Failed to schedule game start task: " + e.getMessage());
                    }


                } else {
                    //Bukkit.broadcastMessage("Starting in: " + String.valueOf(startCountdown));
                    broadcastActiveActionbarMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + "" + ChatColor.BOLD + "Starting in: " + ChatColor.WHITE + ChatColor.BOLD + startCountdown));
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
            Bukkit.getLogger().warning("Failed to schedule game start task: " + e.getMessage());
        }
    }

    public void removePlayerFromEvent(UUID pUUID, UUID killerUUID) {
        if (!activePlayers.contains(pUUID)) return;

        Player player = Bukkit.getPlayer(pUUID);

        Player killer;

        if (killerUUID != null) {
            killer = Bukkit.getPlayer(killerUUID);
        } else {
            killer = null;
        }

        activePlayers.remove(pUUID);
        activePlayersBeforeLanding.remove(pUUID);
        player.setGlowing(false);

        Player winner = Bukkit.getPlayer(activePlayers.get(0));

        bossBar.setProgress(new Double(activePlayers.size()) / new Double(initialPlayers.size()));

        if (activePlayers.size() <= 1) {

            Location tpSpot = player.getLocation();

            if (Bukkit.getOnlinePlayers().contains(player)) {
                player.setGameMode(GameMode.SPECTATOR);

//                                        if(player.isDead()) {
//                            player.spigot().respawn();
//                        }
//                        player.teleport(tpSpot);

                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isDead()) {
                            player.spigot().respawn();
                        }
                        player.teleport(tpSpot);


                    }
                };

                try {
                    task.runTaskLater(HungerGamesPlus2.getInstance(), 20);
                } catch (UnsupportedOperationException e) {
                    // Log a warning message
                    Bukkit.getLogger().warning("Failed to schedule game start task: " + e.getMessage());
                }
            }

            if (killer == null) {
                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " was eliminated!");
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " was eliminated by " + killer.getName() + "!");
            }

            Bukkit.broadcastMessage("Game over! The winner is " + winner.getName() + "!");
            EndGame();
        } else {
            bossBar.setTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players Remaining: " + String.valueOf(activePlayers.size()));

            if (killer == null) {
                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " was eliminated! " + activePlayers.size() + " players remaining!");
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " was eliminated by " + killer.getName() + "! " + activePlayers.size() + " players remaining!");
            }

        }
    }
}


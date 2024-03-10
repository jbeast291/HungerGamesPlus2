package hgplus.handlers;

import hgplus.HungerGamesPlus2;
import hgplus.corelogic.corelogic;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerEventHandler implements Listener {
    public PlayerEventHandler(HungerGamesPlus2 plugin) { Bukkit.getPluginManager().registerEvents(this, plugin); }
    corelogic CoreLogic = HungerGamesPlus2.getGameLogic();

    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if(CoreLogic.gameStatus != 1) return;

        if(event.isGliding()) return;
        Player player = (Player) event.getEntity();
        if(!CoreLogic.activePlayers.contains(player.getUniqueId())) return;

        FileConfiguration config = HungerGamesPlus2.getInstance().getConfig();
        if(HungerGamesPlus2.getSetup().checkIfPlayerIsInRegion(config.getInt("lobby-region.x"),
                config.getInt("lobby-region.y"),
                config.getInt("lobby-region.z"),
                config.getInt("lobby-region.x2"),
                config.getInt("lobby-region.y2"),
                config.getInt("lobby-region.z2"),
                player)) return;

        if((player.getInventory().getChestplate() != null) && (player.getInventory().getChestplate().getType() == Material.ELYTRA)) {
            player.getEquipment().setChestplate(null);
            CoreLogic.activePlayersBeforeLanding.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        if (CoreLogic.gameStatus != 1) return;
        Player player = event.getEntity();
        Player killer = player.getKiller();

        player.getWorld().strikeLightningEffect(player.getLocation());

        //drop player items as the death event is cancelled and items will not drop
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }
            if (item.getType() == Material.ELYTRA) {
                continue;
            }
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.getInventory().removeItem(item);
        }
        //clear their inv just in case
        player.getInventory().clear();

        //cancel the player dying
        event.setCancelled(true);

        //remove player from the event
        if (killer != null) {
            CoreLogic.removePlayerFromEvent(player.getUniqueId(), killer.getUniqueId(), true, false);
        } else {
            CoreLogic.removePlayerFromEvent(player.getUniqueId(), null, true, false);
        }
        //remove the vinilla message
        event.deathMessage(null);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if(CoreLogic.gameStatus != -1) {
            Player player = event.getPlayer();
            if (CoreLogic.activePlayers.contains(player.getUniqueId()))
            {
                for (ItemStack item : player.getInventory().getContents()) {
                    if(item == null) {continue;}
                    if (item.getType() == Material.ELYTRA) {
                        continue;
                    }
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    player.getInventory().removeItem(item);
                }

                player.getInventory().clear();
            }
            CoreLogic.removePlayerFromEvent(event.getPlayer().getUniqueId(), null, false, true);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Bukkit.getLogger().info("test");
        Player player = event.getPlayer();
        if(CoreLogic.gameStatus != -1){
            player.setGameMode(GameMode.SPECTATOR);
            player.teleportAsync(HungerGamesPlus2.getGameLogic().getSpawnLocation());
            player.sendMessage("§7[§3HGPlus§7]§f A game is currently running. You have been set to spectator and can join the next game when this one ends.");
            player.sendActionBar(TextComponent.fromLegacyText("You are now spectating a game"));
            CoreLogic.bossBar.addPlayer(player);
            CoreLogic.bossBar.setVisible(true);
            CoreLogic.lateSpectatorPlayers.add(player.getUniqueId());
        }
        else if(CoreLogic.gameStatus == -1){
            player.teleportAsync(HungerGamesPlus2.getGameLogic().getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setSaturation(20);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event)//fixes an exploit were someone could remove their elytra before landing and keep it
    {
        if(CoreLogic.gameStatus != 1) return;
        Player player = (Player) event.getWhoClicked();
        if(!CoreLogic.activePlayersBeforeLanding.contains(player.getUniqueId())) return;
        if(event.getSlot() == 38) {
            event.setCancelled(true);
        }
    }

    //block blocks like trapdoors and fence gates
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null)
        {
            return;
        }
        if (e.getPlayer().hasPermission("hungergamesplus.nonplayer")){
            return;
        }
        if (IsProtectedBlock(e.getClickedBlock().getType())){
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cYou cannot interact with this!");
        }
    }

    boolean IsProtectedBlock(Material material){
        if (material.equals(Material.OAK_FENCE_GATE) ||
                material.equals(Material.SPRUCE_FENCE_GATE) ||
                material.equals(Material.BIRCH_FENCE_GATE) ||
                material.equals(Material.JUNGLE_FENCE_GATE) ||
                material.equals(Material.ACACIA_FENCE_GATE) ||
                material.equals(Material.DARK_OAK_FENCE_GATE) ||
                material.equals(Material.MANGROVE_FENCE_GATE) ||
                material.equals(Material.CHERRY_FENCE_GATE) ||
                material.equals(Material.BAMBOO_FENCE_GATE) ||
                material.equals(Material.CRIMSON_FENCE_GATE) ||
                material.equals(Material.WARPED_FENCE_GATE) ||

                material.equals(Material.OAK_TRAPDOOR) ||
                material.equals(Material.SPRUCE_TRAPDOOR) ||
                material.equals(Material.BIRCH_TRAPDOOR) ||
                material.equals(Material.JUNGLE_TRAPDOOR) ||
                material.equals(Material.ACACIA_TRAPDOOR) ||
                material.equals(Material.DARK_OAK_TRAPDOOR) ||
                material.equals(Material.MANGROVE_TRAPDOOR) ||
                material.equals(Material.CHERRY_TRAPDOOR) ||
                material.equals(Material.BAMBOO_TRAPDOOR) ||
                material.equals(Material.CRIMSON_TRAPDOOR) ||
                material.equals(Material.WARPED_TRAPDOOR) ||
                material.equals(Material.IRON_TRAPDOOR) ||

                material.equals(Material.GLOW_ITEM_FRAME) ||
                material.equals(Material.ITEM_FRAME) ||

                material.equals(Material.FURNACE) ||
                material.equals(Material.BLAST_FURNACE) ||
                material.equals(Material.SMOKER) ||
                material.equals(Material.BARREL) ||
                material.equals(Material.SMITHING_TABLE) ||

                material.equals(Material.WHITE_BED) ||
                material.equals(Material.GRAY_BED) ||
                material.equals(Material.LIGHT_GRAY_BED) ||
                material.equals(Material.BLACK_BED) ||
                material.equals(Material.BROWN_BED) ||
                material.equals(Material.RED_BED) ||
                material.equals(Material.ORANGE_BED) ||
                material.equals(Material.YELLOW_BED) ||
                material.equals(Material.GREEN_BED) ||
                material.equals(Material.LIME_BED) ||
                material.equals(Material.CYAN_BED) ||
                material.equals(Material.LIGHT_BLUE_BED) ||
                material.equals(Material.BLUE_BED) ||
                material.equals(Material.PURPLE_BED) ||
                material.equals(Material.MAGENTA_BED) ||
                material.equals(Material.PINK_BED)
                ) {

            return true;
        }
        return false;
    }

}

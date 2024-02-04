package hgplus.handlers;

import hgplus.HungerGamesPlus2;
import hgplus.corelogic.corelogic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

        if((player.getInventory().getChestplate() != null) && (player.getInventory().getChestplate().getType() == Material.ELYTRA)) {
            player.getEquipment().setChestplate(null);
            CoreLogic.activePlayersBeforeLanding.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if(CoreLogic.gameStatus != 1) return;
        Player player = event.getEntity();
        Player killer = player.getKiller();

        player.getWorld().strikeLightningEffect(player.getLocation());

        List<ItemStack> list = event.getDrops();
        list.removeIf(item -> item.getType() == Material.ELYTRA);

        if(killer != null) {
            CoreLogic.removePlayerFromEvent(player.getUniqueId(), killer.getUniqueId());
        } else {
            CoreLogic.removePlayerFromEvent(player.getUniqueId(), null);
        }

        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if(CoreLogic.gameStatus != 1) return;
        CoreLogic.removePlayerFromEvent(event.getPlayer().getUniqueId(), null);
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

}

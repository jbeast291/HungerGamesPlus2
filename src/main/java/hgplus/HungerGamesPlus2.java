package hgplus;

import hgplus.commands.hgplus;
import hgplus.corelogic.corelogic;
import hgplus.handlers.PlayerEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HungerGamesPlus2 extends JavaPlugin {

    private static HungerGamesPlus2 instance;
    private static corelogic CoreLogic;

    @Override
    public void onEnable() {

        //class instances
        instance = this;
        CoreLogic = new corelogic();

        saveDefaultConfig();

        // Plugin startup logic
        Bukkit.getLogger().info("HungerGamesPlus starting up! Created by Camilo Sanchez (@coolspee).");

        getCommand("hgplus").setExecutor(new hgplus());


        new PlayerEventHandler(this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        // Plugin shutdown logic
        Bukkit.getLogger().info("HungerGamesPlus shutting down.");
    }

    public static corelogic getGameLogic() {
        return CoreLogic;
    }
    public static HungerGamesPlus2 getInstance() {
        return instance;
    }


}

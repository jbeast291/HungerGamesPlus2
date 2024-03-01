package hgplus;

import hgplus.commands.hgplus;
import hgplus.commands.hgplustabcompleter;
import hgplus.corelogic.corelogic;
import hgplus.corelogic.lootlogic;
import hgplus.savedatahandle.SaveDataHandler;
import hgplus.setup.setup;
import hgplus.handlers.PlayerEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public final class HungerGamesPlus2 extends JavaPlugin {

    private static HungerGamesPlus2 instance;
    private static corelogic CoreLogic;
    private static setup Setup;
    private static lootlogic LootLogic;
    private SaveDataHandler SaveDataHandlerinstance;

    @Override
    public void onEnable() {

        //class instances
        instance = this;
        SaveDataHandlerinstance = new SaveDataHandler();
        CoreLogic = new corelogic();
        Setup = new setup();
        LootLogic = new lootlogic();

        SaveDataHandlerinstance.saveDefaultCustomConfig();
        saveDefaultConfig();

        // Plugin startup logic
        Bukkit.getLogger().info("HungerGamesPlus starting up! Created by @coolspee and @Jbeast291.");

        getCommand("hgplus").setExecutor(new hgplus());
        getCommand("hgplus").setTabCompleter(new hgplustabcompleter());

        new PlayerEventHandler(this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        // Plugin shutdown logic
        Bukkit.getLogger().info("HungerGamesPlus shutting down.");
    }

    public static HungerGamesPlus2 getInstance() {
        return instance;
    }
    public SaveDataHandler getSaveDataHandlerinstance() {
        return SaveDataHandlerinstance;
    }
    public static corelogic getGameLogic() {
        return CoreLogic;
    }
    public static setup getSetup() { return Setup; }
    public static lootlogic getLootLogic() { return LootLogic; }


}

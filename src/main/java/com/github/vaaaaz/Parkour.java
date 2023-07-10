package com.github.vaaaaz;

import com.github.vaaaaz.commands.ParkourCommand;
import com.github.vaaaaz.events.ParkourEvents;
import com.github.vaaaaz.files.Loc;
import com.github.vaaaaz.mysql.SQLconnection;
import com.github.vaaaaz.utils.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Parkour extends JavaPlugin {

    private static Parkour instance;
    private static SQLconnection sqLconnection;
    private static Loc loc;
    public HashMap<String, InventoryAPI> hashinv = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        registerCommands();
        registerEvents();
        loadConfig();

        sqLconnection = new SQLconnection(getConfig().getString("mysql.user"), getConfig().getString("mysql.host"), getConfig().getString("mysql.password"), getConfig().getInt("mysql.port"), getConfig().getString("mysql.db"));

        PluginDescriptionFile description = getDescription();
        String version = description.getVersion();
        loc = new Loc();

        Bukkit.getConsoleSender().sendMessage(new String[]{
                "",
                "§fO §bParkour §ffoi iniciado com sucesso!",
                "              §fVersão: §b" + version + "",
                ""});
    }

    @Override
    public void onDisable() {
    }


    void loadConfig() {
        getConfig().options().copyDefaults(false);
        saveDefaultConfig();
    }

    void registerCommands() {
        getCommand("parkour").setExecutor(new ParkourCommand());
    }

    void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new ParkourEvents(), this);
    }

    public static SQLconnection getSqLconnection() {
        return sqLconnection;
    }

    public static Loc getLoc() {
        return loc;
    }

    public static Parkour getInstance() {
        return instance;
    }
}

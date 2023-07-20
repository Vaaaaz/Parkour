package com.github.vaaaaz;

import com.github.vaaaaz.commands.ParkourCommand;
import com.github.vaaaaz.events.ParkourEvents;
import com.github.vaaaaz.files.Loc;
import com.github.vaaaaz.mysql.SQLconnection;
import com.github.vaaaaz.utils.ConfigUtils;
import com.github.vaaaaz.utils.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Parkour extends JavaPlugin {

    private static Parkour instance;
    private static SQLconnection sqLconnection;
    private static Loc loc;


    public static ConfigUtils config;

    public HashMap<String, InventoryAPI> hashinv = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        loc = new Loc();


        sqLconnection = new SQLconnection(config.getConfig().getString("MySQL.user"), config.getConfig().getString("MySQL.host"), config.getConfig().getString("MySQL.password"), config.getConfig().getInt("MySQL.port"), config.getConfig().getString("MySQL.db"));

        Bukkit.getConsoleSender().sendMessage(new String[]{
                "",
                "     §bParkour - 1.0",
                "  §aIniciado com sucesso!",
                ""});

        registerCommands();
        registerEvents();
        archives();

    }

    @Override
    public void onDisable() {}


    void registerCommands() {
        getCommand("parkour").setExecutor(new ParkourCommand());
    }
    void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new ParkourEvents(), this);
    }

    void archives() {

        config = new ConfigUtils(this, "config.yml");

        config.saveDefaultConfig();

        createFile(this, "", false);

    }
    public void createFile(Parkour main, String fileName, boolean isFile) {
        try {
            File file = new File(main.getDataFolder() + File.separator + fileName);
            if (isFile) file.createNewFile();
            else if (!file.exists()) file.mkdirs();
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
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

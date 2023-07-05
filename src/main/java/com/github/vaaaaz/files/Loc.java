package com.github.vaaaaz.files;

import com.github.vaaaaz.Parkour;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Loc {

    private File file;
    private FileConfiguration fileConfiguration;

    public Loc() {
        file = new File(Parkour.getInstance().getDataFolder(), "Loc.yml");
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
                loadConfig();
            } catch (Exception e) {
                System.out.println("§cOcorreu um erro ao criar o arquivo " + file.getName());
                e.printStackTrace();
            }
        }
    }


    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return fileConfiguration;
    }

    public void saveConfig() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        fileConfiguration.createSection("Inicio");
        fileConfiguration.createSection("Inicio.x");
        fileConfiguration.createSection("Inicio.y");
        fileConfiguration.createSection("Inicio.z");
        fileConfiguration.createSection("Inicio.yaw");
        fileConfiguration.createSection("Inicio.pitch");

        fileConfiguration.createSection("Fim.x");
        fileConfiguration.createSection("Fim.y");
        fileConfiguration.createSection("Fim.z");
        fileConfiguration.createSection("Fim.yaw");
        fileConfiguration.createSection("Fim.pitch");

        fileConfiguration.createSection("Spawn.x");
        fileConfiguration.createSection("Spawn.y");
        fileConfiguration.createSection("Spawn.z");
        fileConfiguration.createSection("Spawn.yaw");
        fileConfiguration.createSection("Spawn.pitch");


        fileConfiguration.createSection("Spawnpoints");
        saveConfig();
    }

    public void reloadConfig() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }
}

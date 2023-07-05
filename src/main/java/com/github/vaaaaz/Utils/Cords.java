package com.github.vaaaaz.Utils;

import com.github.vaaaaz.Parkour;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public interface Cords {
    default Location spawn(Player p){
        FileConfiguration file = Parkour.getLoc().getConfig();
        return new Location(p.getWorld(), file.getDouble("Spawn.x"),file.getDouble("Spawn.y"),file.getDouble("Spawn.z"),(float)file.getDouble("Spawn.yaw"),(float)file.getDouble("Spawn.pitch"));
    }
}
